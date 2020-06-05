package detector;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

//TODO: not-init-applicable - to hold, the action must be applicable in the public part of the initial state, otherwise it does not make sense!
public class NotInitApplicableDetector implements OfflinePropertyDetectorInterface {
	

	@Override
	public Set<OperatorSet> detectPropertyOffline(
			SearchTree tree,
			Map<EnumPrivacyProperty,Set<OperatorSet>> operatorPropertiesMap
			) {
		
		
		Set<OperatorSet> operatorPropertiesSet = operatorPropertiesMap.get(EnumPrivacyProperty.INIT_APPLICBLE);
		
		OperatorSet resultOpSet = new OperatorSet(EnumPrivacyProperty.NOT_INIT_APPLICABLE,false);
		
		for(Operator op : tree.getAllOperators()){
			
			if(op.publicApplicable(tree.getInitialState())){
				boolean nia = true;
			
				if(operatorPropertiesSet != null){
					for(OperatorSet opSet : operatorPropertiesSet){
						if(opSet.privacyProperty == EnumPrivacyProperty.INIT_APPLICBLE && opSet.contains(op)){
							nia = false;
							break;
						}
					}
				}
				
				if(nia){
					resultOpSet.add(op);
				}
			
			}
			
		}

		Set<OperatorSet> result =  new HashSet<OperatorSet>();
		result.add(resultOpSet);
		return result;
	}

	

	@Override
	public EnumPrivacyProperty getPrivacyProperty() {
		return EnumPrivacyProperty.NOT_INIT_APPLICABLE;
	}


	@Override
	public boolean isGroundTruthProperty(Operator op, Set<String> privateVarIDs, SearchState initState) {
		//TODO: this needs to be extended to any states which are reachable from the initial state by agent's private actions!
		
		
		boolean match = true;
		
		for(String var : privateVarIDs){
			if(op.pre.containsKey(var)){
				int preValue = op.pre.get(var);
				int varOrd = Integer.parseInt(var);
				int stateVal = initState.values[varOrd];
				
				if(preValue != stateVal){
					match = false;
					break;
				}
			}
		}
		
		
		return !match;
	}




	
	

}
