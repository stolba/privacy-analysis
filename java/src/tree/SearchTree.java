package tree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SearchTree {
	
	private final int analyzedAgentID;

	private Map<Integer,Variable> varMap = new HashMap<>();
	private Map<Integer,Operator> opMap = new HashMap<>();
	private Map<Integer,SearchState> sentStateMap = new HashMap<>();
	private Map<Integer,SearchState> receivedStateMap = new HashMap<>();
	
	private int numOfPublicVariables = 0;
	
	private SearchState previousReceivedState = null;
	
	
	
	public SearchTree(int analyzedAgentID) {
		super();
		this.analyzedAgentID = analyzedAgentID;
	}

	public void addVariable(Variable var){
		varMap.put(var.varID, var);
		
		if(!var.isPrivate) ++numOfPublicVariables;
	}
	
	public void addOperator(Operator op){
		// We are interested in projected operators o
		if(analyzedAgentID == op.ownerID) return;
		opMap.put(op.opID, op);
		
		op.preMask = new int[numOfPublicVariables];
		op.effMask = new int[numOfPublicVariables];
		
		for(int var = 0; var < numOfPublicVariables; var++){
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
			//find all possibly responsible operators
			for(Operator op : opMap.values()){
//				System.out.println("match " + op.opName + ":"+Arrays.toString(op.preMask)+ "->"+Arrays.toString(op.effMask)+" on "+ Arrays.toString(iparent.values)+" --> "+ Arrays.toString(state.values)+"?");
				if(op.matchTransition(iparent, state)){
					op.matchingTransitions.add(state);
					state.responsibleOperators.add(op);
				}
				
			}
			
			//detect whether all successors of the current i-parent were received
			//TODO: this has to be turned off when not using GBFS
			if(previousReceivedState !=null && previousReceivedState.iparentID != state.iparentID){
				sentStateMap.get(previousReceivedState.iparentID).allSuccessorsReceived = true;
				System.out.println("all successors of state "+previousReceivedState.iparentID+" received");
			}
			previousReceivedState = state;
		}
		
		
		
		
	}
	
}
