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
	
	public int[] publicPreMask;
	public int[] publicEffMask;
	
	private Set<Integer> originalOpIDs = new HashSet<>();
	private Set<Operator> originalOps = new HashSet<>();
	
	public Set<SearchState> matchingTransitions = new HashSet<>();
	
	public Operator(){
		addOriginalOp(this);
	}
	
	public void generatePublicLabelAndHash(){
		publicPreMask = new int[SearchState.numOfPublicVariables];
		publicEffMask = new int[SearchState.numOfPublicVariables];
		
		for(int var = 0; var < SearchState.numOfPublicVariables; var++){
			Integer val = pre.get(Integer.toString(var));
			if(val != null){
				publicPreMask[var] = val;
			}else{
				publicPreMask[var] = SearchState.UNDEFINED_VALUE;
			}
			
			val = eff.get(Integer.toString(var));
			if(val != null){
				publicEffMask[var] = val;
			}else{
				publicEffMask[var] = SearchState.UNDEFINED_VALUE;
			}
		}
		
		label = Arrays.toString(publicPreMask)+ "->"+Arrays.toString(publicEffMask);
		hash = label.hashCode();
	}
	
	
	public boolean matchPublicTransition(SearchState parent, SearchState state){
		for(int i = 0; i < publicPreMask.length; i++){
			if(publicPreMask[i] != SearchState.UNDEFINED_VALUE){
				if(parent.values[i] != publicPreMask[i]) return false;
			}
			if(publicEffMask[i] != SearchState.UNDEFINED_VALUE){
				if(state.values[i] != publicEffMask[i]) return false;
			}
		}
		return true;
	}
	
	
	public boolean publicApplicable(SearchState state){
		for(int i = 0; i < publicPreMask.length; i++){
			if(publicPreMask[i] != SearchState.UNDEFINED_VALUE){
				if(state.values[i] != publicPreMask[i]) return false;
			}
		}
		return true;
	}
	
	public boolean matchPublicEffects(SearchState state){
		for(int i = 0; i < publicEffMask.length; i++){
			if(publicEffMask[i] != SearchState.UNDEFINED_VALUE){
				if(state.values[i] != publicEffMask[i]) return false;
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
		return "Operator [opName=" + opName + ", originalOps="+ originalOps.size() +"]";
	}
	
	
}
