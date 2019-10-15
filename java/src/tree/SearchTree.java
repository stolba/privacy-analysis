package tree;

import java.util.HashMap;
import java.util.Map;

public class SearchTree {
	
	private final int analyzedAgentID;

	private Map<Integer,Variable> varMap = new HashMap<>();
	private Map<Integer,Operator> opMap = new HashMap<>();
	private Map<Integer,SearchState> stateMap = new HashMap<>();
	
	
	
	public SearchTree(int analyzedAgentID) {
		super();
		this.analyzedAgentID = analyzedAgentID;
	}

	public void addVariable(Variable var){
		varMap.put(var.varID, var);
	}
	
	public void addOperator(Operator op){
		// We are interested in projected operators o
		if(op.agentID == op.ownerID) return;
		opMap.put(op.opID, op);
	}
	
	public void addStateSequential(SearchState state){
		//TODO: we need to retain the order somehow
		//TODO: we'll probably do all the processing here
		stateMap.put(state.stateID, state);
	}
	
}
