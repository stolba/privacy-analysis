package detector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public class PrivatelyIndependentDetector implements PropertyDetectorInterface {
	
	private final Collection<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors;
	
	

	public PrivatelyIndependentDetector(
			Collection<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors) {
		super();
		this.privatelyDifferentStateDetectors = privatelyDifferentStateDetectors;
	}

	@Override
	public Set<OperatorSet> detectProperty(SearchTree tree, SearchState relevantState) {
		Set<OperatorSet> result =  new HashSet<OperatorSet>();
		
		
		SearchState iparent = tree.getSentStateMap().get(relevantState.iparentID);
		
		for(SearchState s : tree.getSentStateMap().values()){
			if(iparent.publiclyEquivalent(s)){
				if(iparent.privatelyDifferent(s, privatelyDifferentStateDetectors)){
					OperatorSet opSet = new OperatorSet(EnumPrivacyProperty.PRIVATELY_INDEPENDENT,false);
					opSet.addAll(relevantState.responsibleOperators);
					opSet.retainAll(s.responsibleOperators);
					
					if(!opSet.isEmpty()){
						result.add(opSet);
					}
				}
			}
		}
		
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
