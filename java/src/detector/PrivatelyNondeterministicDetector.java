package detector;

import java.util.HashSet;
import java.util.Set;

import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public class PrivatelyNondeterministicDetector implements
		PropertyDetectorInterface {

	@Override
	public Set<OperatorSet> detectProperty(SearchTree tree, SearchState relevantState) {
		Set<OperatorSet> result =  new HashSet<OperatorSet>();
		
		//TODO: optimize
		for(SearchState s1 : relevantState.successors){
			for(SearchState s2 : relevantState.successors){
				if(s1.stateID != s2.stateID){
					OperatorSet opSet = new OperatorSet(EnumPrivacyProperty.PRIVATELY_NONDETERMINISTIC,true);
					
					opSet.addAll(s1.responsibleOperators);
					opSet.retainAll(s2.responsibleOperators);
					
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
