package tree;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Operator {
	public int agentID;
	public int ownerID;
	public String opName;
	public boolean isPrivate;
	public int opID;
	public int cost;
	
	public Map<String,Integer> pre;
	public Map<String,Integer> eff;
	
	public int[] preMask;
	public int[] effMask;
	
	public Set<SearchState> matchingTransitions = new HashSet<>();
	
	
	public boolean matchTransition(SearchState parent, SearchState state){
		for(int i = 0; i < preMask.length; i++){
			if(preMask[i] != SearchState.UNDEFINED_VALUE){
				if(parent.values[i] != preMask[i]) return false;
			}
			if(effMask[i] != SearchState.UNDEFINED_VALUE){
				if(state.values[i] != effMask[i]) return false;
			}
		}
		return true;
	}
}
