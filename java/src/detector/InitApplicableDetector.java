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

public class InitApplicableDetector implements OnlinePropertyDetectorInterface {
	
	@Override
	public Set<OperatorSet> detectPropertyOnline(
			Collection<Operator> allOperators,
			Map<Integer, SearchState> stateMap, SearchState relevantState,
			SearchState iParent, int analyzedAgentID) {
		
		OperatorSet opSet = new OperatorSet(EnumPrivacyProperty.INIT_APPLICBLE,true);
		if(relevantState.senderID == analyzedAgentID && relevantState.iparentID == SearchState.UNDEFINED_STATE_ID ){
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
	public boolean isGroundTruthProperty(Operator op, Set<String> privatePropertyIDs) {
		// TODO Auto-generated method stub
		return true;
	}

	

	

}
