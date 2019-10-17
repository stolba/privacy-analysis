package detector;

import java.util.HashSet;
import java.util.Set;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public class NotInitApplicableDetector implements PropertyDetectorInterface {

	@Override
	public Set<OperatorSet> detectProperty(SearchTree tree, SearchState relevantState) {
		
		OperatorSet resultOpSet = new OperatorSet(EnumPrivacyProperty.NOT_INIT_APPLICABLE,false);
		
		for(Operator op : tree.getAllOperators()){
			boolean nia = true;
			
			for(OperatorSet opSet : tree.getOperatorPropertiesSet()){
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isApplicableOnline() {
		// TODO Auto-generated method stub
		return false;
	}

}
