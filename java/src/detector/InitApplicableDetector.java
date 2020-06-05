package detector;

import java.util.HashSet;
import java.util.Set;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public class InitApplicableDetector implements OnlinePropertyDetectorInterface {
	
	
	@Override
	public Set<OperatorSet> detectPropertyOnline(
			SearchState relevantState,
			SearchTree tree) {
		
		
		OperatorSet opSet = new OperatorSet(EnumPrivacyProperty.INIT_APPLICBLE,true);
		
		
		if(relevantState.senderID == tree.analyzedAgentID ){
			//TODO: we want states which are a result of application of a sequence of agent's actions on the initial state. Is the 0 correct? then create a constant out of it.
			if(relevantState.iparentID == SearchState.UNDEFINED_STATE_ID || relevantState.iparentID == SearchState.INITIAL_STATE_ID){
				opSet.addAll(relevantState.responsibleOperators);
			}
		}
		Set<OperatorSet> result =  new HashSet<OperatorSet>();
		result.add(opSet);
		return result;
	}

	


	@Override
	public EnumPrivacyProperty getPrivacyProperty() {
		return EnumPrivacyProperty.INIT_APPLICBLE;
	}

	

	@Override
	public boolean isGroundTruthProperty(Operator op, Set<String> privateVarIDs, SearchState initState) {
		//TODO: this needs to be extended to any states which are reachable from the initial state by agent's private actions!
		
		
		boolean match = true;
		
		for(String var : privateVarIDs){
			if(op.pre.containsKey(var)){
				int preValue = op.pre.get(var);
				int varOrd = Integer.parseInt(var);
				int stateVal = initState.values[varOrd];
				
				if(preValue != stateVal){
					match = false;
					break;
				}
			}
		}
		
		
		return match;
	}





	

	

}
