package detector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public class PubliclyDeterministicDetector implements PropertyDetectorInterface {
	
	private final Collection<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors;
	
	

	public PubliclyDeterministicDetector(
			Collection<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors) {
		super();
		this.privatelyDifferentStateDetectors = privatelyDifferentStateDetectors;
	}

	@Override
	public Set<OperatorSet> detectProperty(SearchTree tree,SearchState relevantState) {
		OperatorSet opSet = new OperatorSet(EnumPrivacyProperty.PRIVATELY_DETERMINISTIC,false);
		
		for(Operator op : tree.getAllOperators()){
			boolean opIsPubliclyDeterministic = true;
			
			for(SearchState s1 : op.matchingTransitions){
				for(SearchState s2 : op.matchingTransitions){
					if(!s1.publiclyEquivalent(s2)) continue;
					
					if(!s1.privatelyDifferent(s2, privatelyDifferentStateDetectors)){
						opIsPubliclyDeterministic = false;
						break;
					}
					
					if(!opIsPubliclyDeterministic) break;
					
				}
				
				if(opIsPubliclyDeterministic){
					opSet.add(op);
				}
			}
			
		}

		Set<OperatorSet> result =  new HashSet<OperatorSet>();
		result.add(opSet);
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
