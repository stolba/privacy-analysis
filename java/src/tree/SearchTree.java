package tree;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import analysis.OperatorSet;
import detector.InitApplicableDetector;
import detector.PrivatelyDependentDetector;
import detector.PrivatelyDifferentStateDetectorInterface;
import detector.PrivatelyIndependentDetector;
import detector.PrivatelyNondeterministicDetector;

//TODO: it is probably good idea to disentangle the search tree structure and its creation from the actual analysis algorithm. The online approach if viable at all can be done using the building primitives
public class SearchTree {
	
	public final int analyzedAgentID;

	private Map<Integer,Variable> varMap = new HashMap<>();
	private Map<Integer,Operator> opMap = new HashMap<>();
	private Map<Integer,SearchState> sentStateMap = new HashMap<>();
	private Map<Integer,SearchState> receivedStateMap = new HashMap<>();
	
	
	private SearchState previousReceivedState = null;
	
	private Set<OperatorSet> operatorPropertiesSet = new HashSet<>();
	
	
	private PrivatelyDependentDetector pdDetector = new PrivatelyDependentDetector();
	private InitApplicableDetector iaDetector = new InitApplicableDetector();
	private PrivatelyIndependentDetector piDetector;
	private PrivatelyNondeterministicDetector noDetector = new PrivatelyNondeterministicDetector();
	
	
	
	public SearchTree(int analyzedAgentID,Collection<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors) {
		super();
		this.analyzedAgentID = analyzedAgentID;
		
		piDetector = new PrivatelyIndependentDetector(privatelyDifferentStateDetectors);
	}

	public void addVariable(Variable var){
		varMap.put(var.varID, var);
		
		if(!var.isPrivate) ++SearchState.numOfPublicVariables;
	}
	
	
	//TODO: I need to create label-non-preserving projection!
	public void addOperator(Operator op){
		// We are interested in projected operators of analyzedAgentID
		if(analyzedAgentID != op.ownerID) return;
		
		
		op.preMask = new int[SearchState.numOfPublicVariables];
		op.effMask = new int[SearchState.numOfPublicVariables];
		
		for(int var = 0; var < SearchState.numOfPublicVariables; var++){
			Integer val = op.pre.get(Integer.toString(var));
			if(val != null){
				op.preMask[var] = val;
			}else{
				op.preMask[var] = SearchState.UNDEFINED_VALUE;
			}
			
			val = op.eff.get(Integer.toString(var));
			if(val != null){
				op.effMask[var] = val;
			}else{
				op.effMask[var] = SearchState.UNDEFINED_VALUE;
			}
		}
		
		op.label = Arrays.toString(op.preMask)+ "->"+Arrays.toString(op.effMask);
		op.hash = op.label.hashCode();
		
		if(opMap.containsKey(op.hash)){
			String existingName = opMap.get(op.hash).opName;
			
			String newName = "";
			
			for(int i = 0; i < Math.min(existingName.length(), op.opName.length()); i++){
				if(existingName.charAt(i) == op.opName.charAt(i) && existingName.charAt(i) != ' '){
					newName += existingName.charAt(i);
				}else{
					break;
				}
			}
			
			op.opName = newName;
		}
		opMap.put(op.hash, op);
		
		System.out.println("op " + op.opName + " preMask="+Arrays.toString(op.preMask)+ ",effMask="+Arrays.toString(op.effMask));
		
	}
	
	public void addStateSequential(SearchState state){
		//TODO: we need to retain the order somehow
		//TODO: we'll probably do all the processing here
		
		
		if(state.iparentID == SearchState.UNDEFINED_STATE_ID){
			//sent state
			sentStateMap.put(state.stateID, state);
		}else{
			//received state
			receivedStateMap.put(state.stateID, state);
			
			SearchState iparent = sentStateMap.get(state.iparentID);
			
			iparent.successors.add(state);
			
			//find all possibly responsible operators
			for(Operator op : opMap.values()){
//				System.out.println("match " + op.opName + ":"+Arrays.toString(op.preMask)+ "->"+Arrays.toString(op.effMask)+" on "+ Arrays.toString(iparent.values)+" --> "+ Arrays.toString(state.values)+"?");
				if(op.matchTransition(iparent, state)){
					op.matchingTransitions.add(state);
					state.responsibleOperators.add(op);
				}
				
			}
			
			addOpSet(iaDetector.detectProperty(this, state));
			
			addOpSet(piDetector.detectProperty(this, state));
			
			addOpSet(noDetector.detectProperty(this, iparent));
			
			//detect whether all successors of the current i-parent were received
			//TODO: this has to be turned off when not using GBFS
			if(previousReceivedState !=null && previousReceivedState.iparentID != state.iparentID){
				SearchState stateWithAllSuccessorsReceived = sentStateMap.get(previousReceivedState.iparentID);
				stateWithAllSuccessorsReceived.allSuccessorsReceived = true;
				System.out.println("all successors of state "+stateWithAllSuccessorsReceived+" received");
				
				addOpSet(pdDetector.detectProperty(this, stateWithAllSuccessorsReceived));
			}
			previousReceivedState = state;
		}
	}
	
	private void addOpSet(Set<OperatorSet> osSet){
		for(OperatorSet os : osSet){
			if(!os.isEmpty()){
				operatorPropertiesSet.add(os);
			}
		}
	}
	
	public void afterAllStatesProcessed(){
		
	}
	
	
	
	public Collection<Operator> getAllOperators(){
		return opMap.values();
	}
	
	public Map<Integer,SearchState> getSentStateMap(){
		return sentStateMap;
	}
	
	public Map<Integer,SearchState> getReceivedStateMap(){
		return receivedStateMap;
	}
	
	public Set<OperatorSet> getOperatorPropertiesSet(){
		return operatorPropertiesSet;
	}
}
