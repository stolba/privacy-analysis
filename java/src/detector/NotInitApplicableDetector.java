package detector;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tree.Operator;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

//TODO: not-init-applicable - to hold, the action must be applicable in the public part of the initial state, otherwise it does not make sense!
public class NotInitApplicableDetector implements OfflinePropertyDetectorInterface {
	
	SearchTree tree;

	@Override
	public Set<OperatorSet> detectPropertyOffline(
			SearchTree tree,
			Map<EnumPrivacyProperty,Set<OperatorSet>> operatorPropertiesMap
			) {
		
		this.tree = tree;
		
		Set<OperatorSet> operatorPropertiesSet = operatorPropertiesMap.get(EnumPrivacyProperty.INIT_APPLICBLE);
		
		OperatorSet resultOpSet = new OperatorSet(EnumPrivacyProperty.NOT_INIT_APPLICABLE,false);
		
		for(Operator op : tree.getAllOperators()){
			
			if(op.applicable(tree.getInitialState())){
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
	public boolean isGroundTruthProperty(Operator op, Set<String> privatePropertyIDs) {
		return !op.applicable(tree.getInitialState());
	}




	
	

}
