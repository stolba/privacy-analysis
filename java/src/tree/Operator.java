package tree;

import java.util.Arrays;
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
	
	public String label;
	public int hash;
	
	public int[] preMask;
	public int[] effMask;
	
	private Set<Integer> originalOpIDs = new HashSet<>();
	private Set<Operator> originalOps = new HashSet<>();
	
	public Set<SearchState> matchingTransitions = new HashSet<>();
	
	public Operator(){
		addOriginalOp(this);
	}
	
	public void generatePublicLabelAndHash(){
		preMask = new int[SearchState.numOfPublicVariables];
		effMask = new int[SearchState.numOfPublicVariables];
		
		for(int var = 0; var < SearchState.numOfPublicVariables; var++){
			Integer val = pre.get(Integer.toString(var));
			if(val != null){
				preMask[var] = val;
			}else{
				preMask[var] = SearchState.UNDEFINED_VALUE;
			}
			
			val = eff.get(Integer.toString(var));
			if(val != null){
				effMask[var] = val;
			}else{
				effMask[var] = SearchState.UNDEFINED_VALUE;
			}
		}
		
		label = Arrays.toString(preMask)+ "->"+Arrays.toString(effMask);
		hash = label.hashCode();
	}
	
	
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
	
	
	public boolean applicable(SearchState state){
		for(int i = 0; i < preMask.length; i++){
			if(preMask[i] != SearchState.UNDEFINED_VALUE){
				if(state.values[i] != preMask[i]) return false;
			}
		}
		return true;
	}
	
	public boolean matchEffects(SearchState state){
		for(int i = 0; i < effMask.length; i++){
			if(effMask[i] != SearchState.UNDEFINED_VALUE){
				if(state.values[i] != effMask[i]) return false;
			}
		}
		return true;
	}
	
	
	
	public boolean isOpID(Integer opID){
		return originalOpIDs.contains(opID);
	}
	
	public void addOriginalOp(Operator op){
		originalOps.add(op);
		originalOpIDs.add(op.opID);
	}
	
	public Set<Operator> getOriginalOps(){
		return originalOps;
	}


	@Override
	public String toString() {
		return "Operator [opName=" + opName + "]";
	}
	
	
}
