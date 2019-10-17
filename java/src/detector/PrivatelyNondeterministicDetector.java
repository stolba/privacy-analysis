package detector;

import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public class PrivatelyNondeterministicDetector implements
		PropertyDetectorInterface {

	@Override
	public OperatorSet detectProperty(SearchTree tree, SearchState relevantState) {
		OperatorSet opSet = new OperatorSet(EnumPrivacyProperty.PRIVATELY_NONDETERMINISTIC,true);
		
		//TODO: optimize
		for(SearchState s1 : relevantState.successors){
			for(SearchState s2 : relevantState.successors){
				if(s1.stateID != s2.stateID){
					opSet.addAll(s1.responsibleOperators);
					opSet.retainAll(s2.responsibleOperators);
					
					if(!opSet.isEmpty()){
						//TODO: we'll need to return all of the nonempty sets!
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
