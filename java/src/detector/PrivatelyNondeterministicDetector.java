package detector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public class PrivatelyNondeterministicDetector implements
		OfflinePropertyDetectorInterface {
	
	private final Collection<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors;
	
	public PrivatelyNondeterministicDetector(
			Collection<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors) {
		super();
		this.privatelyDifferentStateDetectors = privatelyDifferentStateDetectors;
	}

	
	@Override
	public Set<OperatorSet> detectPropertyOffline(SearchTree tree,
			Map<EnumPrivacyProperty, Set<OperatorSet>> operatorPropertiesMap) {
		
		
		Set<OperatorSet> result =  new HashSet<OperatorSet>();
		
		for(SearchState state : tree.getSentStates()){
			for(SearchState s1 : state.successors){
				for(SearchState s2 : state.successors){
					
					if(s1.publiclyEquivalent(s2) && s1.privatelyDifferent(s2, privatelyDifferentStateDetectors)){
						OperatorSet opSet = new OperatorSet(EnumPrivacyProperty.PRIVATELY_NONDETERMINISTIC,true);
						
						opSet.addAll(s1.responsibleOperators);
						opSet.retainAll(s2.responsibleOperators);
						
						if(!opSet.isEmpty()){
							result.add(opSet);
						}
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
				if(origOp.eff.containsKey(var)){
					opValues.add(origOp.eff.get(var));
				}else{
					//It is important to represent empty value, because not changing the value and changing the value 
					//to a single value together creates privately-nondeterministic behaviour
					opValues.add(-1);
				}
			}
			
			if(opValues.size() > 1 ){
//				System.out.println("GT op " + op.opName + " is privately-nondeterministic in " + var);
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
