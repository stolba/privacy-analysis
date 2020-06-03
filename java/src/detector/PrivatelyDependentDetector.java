package detector;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public class PrivatelyDependentDetector implements OnlinePropertyDetectorInterface {
	
	@Override
	public Set<OperatorSet> detectPropertyOnline(
			Collection<Operator> allOperators,
			Map<Integer, SearchState> stateMap, SearchState relevantState,
			SearchState iParent, int analyzedAgentID) {
		

		Set<OperatorSet> result =  new HashSet<OperatorSet>();
		
		//We must make sure all successors were already received!
		if(relevantState.allSuccessorsReceived){
			
			OperatorSet opSet = new OperatorSet(EnumPrivacyProperty.PRIVATELY_DEPENDENT,false);
			
		
			for(Operator op : allOperators){
				if(op.applicable(relevantState)){
					
					boolean noSuccessor = true;
					
					for(SearchState state : relevantState.successors){
						if(op.matchEffects(state)){
							noSuccessor = false;
							break;
						}
						
					}
					
					
					
					if(noSuccessor){
						System.out.println(op + " is PD because it is publicly applicable but not aplied on " + relevantState);
						//op is pd
						opSet.add(op);
					}
				}
			}
			
			if(!opSet.isEmpty()){
				result.add(opSet);
			}
		}
		
		
		
		return result;
	}

	
	

	

	@Override
	public EnumPrivacyProperty getPrivacyProperty() {
		return EnumPrivacyProperty.PRIVATELY_DEPENDENT;
	}

	
	@Override
	public boolean isGroundTruthProperty(Operator op, Set<String> privateVarIDs) {
		//find a private variable for which there is exactly one precondition value 
		for(String var : privateVarIDs){
			Set<Integer> opValues = new HashSet<>();
			
			for(Operator origOp : op.getOriginalOps()){
				if(origOp.pre.containsKey(var)) opValues.add(origOp.pre.get(var));
			}
			
			if(opValues.size() == 1){
				System.out.println("GT op " + op.opName + " is privately-dependent in " + var);
				return true;
			}
			
		}
		return false;
		
	}

	

}
