package tree;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SearchState {
	
	public static int UNDEFINED_VALUE = -1;
	public static int UNDEFINED_STATE_ID = -1;
	
	public int agentID;
	public int senderID;
	public int stateID;
	public int parentID;
	public int iparentID;
	
	public int cost;
	public int heuristic;
	public int[] privateIDs;
	public int[] values;
	
	public String context;
	
	
	public Set<Operator> responsibleOperators = new HashSet<>();
	
	public Set<SearchState> successors = new HashSet<>();
	public boolean allSuccessorsReceived = false;
	
	
	@Override
	public String toString() {
		return "SearchState [agentID=" + agentID + ", senderID=" + senderID
				+ ", stateID=" + stateID + ", iparentID=" + iparentID
				+ ", values=" + Arrays.toString(values) + ", context="
				+ context + "]";
	}
	
	
	
	

}
