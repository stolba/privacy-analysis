package tree;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import detector.PrivatelyDifferentStateDetectorInterface;

public class SearchState {
	
	public static final int UNDEFINED_VALUE = -1;
	public static final int UNDEFINED_STATE_ID = -1;
	
	public static int numOfPublicVariables = 0;
	
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
	
	
	public boolean publiclyEquivalent(SearchState state){
		if(this.stateID == state.stateID) return false;
		
		for(int v=0; v < numOfPublicVariables; v++){
			if(this.values[v] != UNDEFINED_VALUE && state.values[v] != UNDEFINED_VALUE && this.values[v] != state.values[v]){
				return false;
			}
		}
		
		return true;
	}
	
	public boolean privatelyDifferent(SearchState state, Collection<PrivatelyDifferentStateDetectorInterface> detectors){
		
		for(PrivatelyDifferentStateDetectorInterface detector : detectors){
			boolean privatelyDifferent = detector.privatelyDifferent(this, state);
			if(privatelyDifferent) return true;
		}
		
		
		return false;
	}
	
	
	@Override
	public String toString() {
		return "SearchState [agentID=" + agentID + ", senderID=" + senderID
				+ ", stateID=" + stateID + ", iparentID=" + iparentID
				+ ", values=" + Arrays.toString(values) + ", context="
				+ context +", allSuccessorsReceived="+allSuccessorsReceived+ "]";
	}
	
	
	
	

}
