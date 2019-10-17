package detector;

import java.util.Set;

import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public interface PropertyDetectorInterface {
	
	public Set<OperatorSet> detectProperty(SearchTree tree, SearchState relevantState);
	
	
	public EnumPrivacyProperty getPrivacyProperty();
	
	
	public boolean isApplicableOnline();

}
