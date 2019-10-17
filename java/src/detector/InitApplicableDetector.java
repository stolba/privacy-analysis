package detector;

import java.util.HashSet;
import java.util.Set;

import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public class InitApplicableDetector implements PropertyDetectorInterface {

	@Override
	public Set<OperatorSet> detectProperty(SearchTree tree, SearchState relevantState) {
		OperatorSet opSet = new OperatorSet(EnumPrivacyProperty.INIT_APPLICBLE,true);
		if(relevantState.senderID == tree.analyzedAgentID && relevantState.iparentID == SearchState.UNDEFINED_STATE_ID ){
			opSet.addAll(relevantState.responsibleOperators);
		}
		Set<OperatorSet> result =  new HashSet<OperatorSet>();
		result.add(opSet);
		return result;
	}

	@Override
	public EnumPrivacyProperty getPrivacyProperty() {
		return EnumPrivacyProperty.INIT_APPLICBLE;
	}

	@Override
	public boolean isApplicableOnline() {
		return true;
	}

}
