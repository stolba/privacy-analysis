package detector;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public interface OnlinePropertyDetectorInterface {
	
	public Set<OperatorSet> detectPropertyOnline(Collection<Operator> allOperators, Map<Integer,SearchState> stateMap, SearchState relevantState, SearchState iParent, int analyzedAgentID);
	
	public boolean isGroundTruthProperty(Operator op, Set<String> privateVarIDs);
	
	public EnumPrivacyProperty getPrivacyProperty();
	

}
