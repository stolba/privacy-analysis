package detector;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public interface OfflinePropertyDetectorInterface {
	
	public Set<OperatorSet> detectPropertyOffline(SearchTree tree, Map<EnumPrivacyProperty,Set<OperatorSet>> operatorPropertiesMap);
	
	
	public boolean isGroundTruthProperty(Operator op, Set<String> privateVarIDs, SearchState initState);
	
	
	public EnumPrivacyProperty getPrivacyProperty();
	
}
