package detector;

import java.util.Set;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public interface OnlinePropertyDetectorInterface {
	
	public Set<OperatorSet> detectPropertyOnline(SearchState relevantState, SearchTree tree);
	
	public boolean isGroundTruthProperty(Operator op, Set<String> privateVarIDs);
	
	public EnumPrivacyProperty getPrivacyProperty();
	

}
