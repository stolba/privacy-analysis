package detector;

import java.util.Collection;

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
	public OperatorSet detectProperty(SearchTree tree, SearchState relevantState) {
		OperatorSet opSet = new OperatorSet(EnumPrivacyProperty.PRIVATELY_INDEPENDENT,true);
		
		SearchState iparent = tree.getSentStateMap().get(relevantState.iparentID);
		
		for(SearchState s : tree.getSentStateMap().values()){
			if(iparent.publiclyEquivalent(s)){
				if(iparent.privatelyDifferent(s, privatelyDifferentStateDetectors)){
					opSet.addAll(relevantState.responsibleOperators);
					opSet.retainAll(s.responsibleOperators);
					
					//TODO: we will need to retur ALL non-empty sets, not just the first one!
					if(!opSet.isEmpty()){
						return opSet;
					}
				}
			}
		}
		
		return opSet;
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
