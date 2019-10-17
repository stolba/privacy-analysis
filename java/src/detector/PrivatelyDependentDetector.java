package detector;

import java.util.HashSet;
import java.util.Set;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public class PrivatelyDependentDetector implements PropertyDetectorInterface {

	@Override
	public Set<OperatorSet> detectProperty(SearchTree tree, SearchState relevantState) {
		
		OperatorSet opSet = new OperatorSet(EnumPrivacyProperty.PRIVATELY_DEPENDENT,false);
		
		for(Operator op : tree.getAllOperators()){
			if(op.applicable(relevantState)){
				
				boolean noSuccessor = true;
				
				for(SearchState state : relevantState.successors){
					if(op.matchEffects(state)) noSuccessor = false;
					break;
				}
				
				if(noSuccessor){
					System.out.println(op + " is PD because it is publicly applicable but not aplied on " + relevantState);
					//op is pd
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
		return EnumPrivacyProperty.PRIVATELY_DEPENDENT;
	}

	@Override
	public boolean isApplicableOnline() {
		return true;
	}

}
