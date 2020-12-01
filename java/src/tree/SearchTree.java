package tree;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;



public class SearchTree{
	
	public final int analyzedAgentID;

	private Map<Integer,Variable> varMap = new HashMap<>();
	private Map<Integer,Operator> opMap = new HashMap<>();
	private Map<Integer,SearchState> sentStateMap = new HashMap<>();
	private Map<Integer,SearchState> receivedStateMap = new HashMap<>();
	
	private Map<Integer,SearchState> previousReceivedState = new HashMap<>();
	private SearchState initState = null;
	
	
	
	public SearchTree(int analyzedAgentID) {
		super();
		this.analyzedAgentID = analyzedAgentID;
		
	}

	
	public void addVariable(Variable var){
		varMap.put(var.varID, var);
		
		if(!var.isPrivate) ++SearchState.numOfPublicVariables;
	}
	
	
	
	public void addOperator(Operator op){
		// We are interested in projected operators of analyzedAgentID
		if(analyzedAgentID != op.ownerID) return;
		
		if(op.isPrivate) return;
		
		if(op.pre == null) op.pre = new HashMap<>();
		if(op.eff == null) op.eff = new HashMap<>();
		
		op.generatePublicLabelAndHash();
		
		Operator existingOp = opMap.get(op.hash);
		
		if(existingOp != null){
			
			existingOp.addOriginalOp(op);
			
			System.out.println("op " + op.opName + ", owner="+op.ownerID + " added to op " + existingOp.opName + " with preMask="+Arrays.toString(op.publicPreMask)+ ",effMask="+Arrays.toString(op.publicEffMask)+ ", op count " + existingOp.getOriginalOps().size());
			
		}else{
			opMap.put(op.hash, op);
			System.out.println("op " + op.opName + ", owner="+op.ownerID + " created with preMask="+Arrays.toString(op.publicPreMask)+ ",effMask="+Arrays.toString(op.publicEffMask));
		}
		
		
		
	}
	
	
	public void addState(SearchState state){
		
		
		if(state.iparentID == SearchState.UNDEFINED_STATE_ID){
			//sent state
			sentStateMap.put(state.stateID, state);
			
			if(state.parentID == SearchState.UNDEFINED_STATE_ID){
				initState = state;
			}
		}else{
			//received state
			receivedStateMap.put(state.stateID, state);
			
			SearchState iparent = sentStateMap.get(state.iparentID);
			
			iparent.successors.add(state);
			
			//find all possibly responsible operators
			for(Operator op : opMap.values()){
//				System.out.println("match " + op.opName + ":"+Arrays.toString(op.preMask)+ "->"+Arrays.toString(op.effMask)+" on "+ Arrays.toString(iparent.values)+" --> "+ Arrays.toString(state.values)+"?");
				if(op.matchPublicTransition(iparent, state)){
					op.matchingTransitions.add(state);
					state.responsibleOperators.add(op);
				}
				
			}
			
			
			
		}
	}
	
	public void setPreviousReceivedState(SearchState state) {
		if(state.iparentID != SearchState.UNDEFINED_STATE_ID){
			previousReceivedState.put(state.senderID, state);
		}
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
	
	public SearchState getIParent(SearchState state) {
		return receivedStateMap.get(state.iparentID);
	}
	
	public SearchState getReceivedState(int id) {
		return receivedStateMap.get(id);
	}
	
	public SearchState getSentState(int id) {
		return sentStateMap.get(id);
	}
	
	public SearchState getPreviousReceivedState(int senderID) {
		return previousReceivedState.get(senderID);
	}
	
	public SearchState getInitialState(){
		return initState;
	}


	public Collection<SearchState> getSentStates() {
		return sentStateMap.values();
	}
	
	public Collection<SearchState> getReceivedStates() {
		return receivedStateMap.values();
	}


	


	

	
}
