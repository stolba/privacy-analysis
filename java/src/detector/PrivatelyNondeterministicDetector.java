package detector;

import java.util.HashSet;
import java.util.Set;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public class PrivatelyNondeterministicDetector implements
		OnlinePropertyDetectorInterface {

	@Override
	public Set<OperatorSet> detectPropertyOnline(
			SearchState relevantState,
			SearchTree tree) {
		

		Set<OperatorSet> result =  new HashSet<OperatorSet>();
		
		//TODO: optimize
		for(SearchState s1 : relevantState.successors){
			for(SearchState s2 : relevantState.successors){
				if(s1.stateID != s2.stateID){
					OperatorSet opSet = new OperatorSet(EnumPrivacyProperty.PRIVATELY_NONDETERMINISTIC,true);
					
					opSet.addAll(s1.responsibleOperators);
					opSet.retainAll(s2.responsibleOperators);
					
					if(!opSet.isEmpty()){
						result.add(opSet);
					}
				}
				
			}
		}
		
		return result;
	}

	

	@Override
	public EnumPrivacyProperty getPrivacyProperty() {
		
		return EnumPrivacyProperty.PRIVATELY_NONDETERMINISTIC;
	}

	
	@Override
	public boolean isGroundTruthProperty(Operator op, Set<String> privateVarIDs, SearchState initState) {
		//find a private variable for which there is more than one effect value 
		//or more then one private variable in the effect
		int numOfDistinctPrivateVarEffects = 0;
		
		for(String var : privateVarIDs){
			Set<Integer> opValues = new HashSet<>();
			
			for(Operator origOp : op.getOriginalOps()){
				if(origOp.eff.containsKey(var)) opValues.add(origOp.eff.get(var));
			}
			
			if(opValues.size() > 1 ){
				System.out.println("GT op " + op.opName + " is privately-deterministic in " + var);
				return true;
			}
			
			if(opValues.size() == 1 ) numOfDistinctPrivateVarEffects++;
			
		}
		
		if(numOfDistinctPrivateVarEffects > 1){
			return true;
		}else{
			return false;
		}
		
	}

	

}
