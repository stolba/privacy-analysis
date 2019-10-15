package detector;

import java.util.Set;

import analysis.EnumPrivacyProperty;
import tree.Operator;
import tree.SearchState;
import tree.SearchTree;

public interface PropertyDetectorInterface {
	
	public Set<Operator> detectProperty(SearchTree tree, SearchState relevantState);
	
	
	public EnumPrivacyProperty getPrivacyProperty();
	
	
	public boolean isApplicableOnline();

}
