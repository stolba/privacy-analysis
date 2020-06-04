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

public class NotInitApplicableDetector implements OfflinePropertyDetectorInterface {
	
	

	@Override
	public Set<OperatorSet> detectPropertyOffline(
			Collection<Operator> allOperators,
			Set<OperatorSet> operatorPropertiesSet) {
		
		if(operatorPropertiesSet == null) return new HashSet<OperatorSet>();

		OperatorSet resultOpSet = new OperatorSet(EnumPrivacyProperty.NOT_INIT_APPLICABLE,false);
		
		for(Operator op : allOperators){
			boolean nia = true;
			
			for(OperatorSet opSet : operatorPropertiesSet){
				if(opSet.privacyProperty == EnumPrivacyProperty.INIT_APPLICBLE && opSet.contains(op)){
					nia = false;
					break;
				}
			}
			
			if(nia){
				resultOpSet.add(op);
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
		// TODO Auto-generated method stub
		return true;
	}




	
	

}
