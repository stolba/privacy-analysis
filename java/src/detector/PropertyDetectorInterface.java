package detector;

import java.util.Set;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public interface PropertyDetectorInterface {
	
	public Set<OperatorSet> detectProperty(SearchTree tree, SearchState relevantState);
	
	
	public boolean isGroundTruthProperty(Operator op, Set<String> privateVarIDs);
	
	
	public EnumPrivacyProperty getPrivacyProperty();
	
	
	public boolean isApplicableOnline();

}
