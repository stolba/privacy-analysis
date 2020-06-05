package detector;

import java.util.HashSet;
import java.util.Set;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public class PrivatelyDependentDetector implements OnlinePropertyDetectorInterface {
	
	@Override
	public Set<OperatorSet> detectPropertyOnline(
			SearchState relevantState,
			SearchTree tree) {
		
		Set<OperatorSet> result =  new HashSet<OperatorSet>();
		
		if(tree.getPreviousReceivedState() !=null){
			SearchState stateWithAllSuccessorsReceived = tree.getSentStateMap().get(tree.getPreviousReceivedState().iparentID);
			
			if(stateWithAllSuccessorsReceived != null && stateWithAllSuccessorsReceived.allSuccessorsReceived == true){
			
				OperatorSet opSet = new OperatorSet(EnumPrivacyProperty.PRIVATELY_DEPENDENT,false);
				
				
				for(Operator op : tree.getAllOperators()){
					if(op.publicApplicable(stateWithAllSuccessorsReceived)){
						
						boolean noSuccessor = true;
						
						for(SearchState state : stateWithAllSuccessorsReceived.successors){
							if(op.matchPublicEffects(state)){
								noSuccessor = false;
								break;
							}
							
						}
						
						
						
						if(noSuccessor){
							System.out.println(op + " is PD because it is publicly applicable but not aplied on " + stateWithAllSuccessorsReceived);
							//op is pd
							opSet.add(op);
						}
					}
				}
				
				if(!opSet.isEmpty()){
					result.add(opSet);
				}
			
			}
			
		}
		

		
		return result;
	}

	
	

	

	@Override
	public EnumPrivacyProperty getPrivacyProperty() {
		return EnumPrivacyProperty.PRIVATELY_DEPENDENT;
	}

	
	@Override
	public boolean isGroundTruthProperty(Operator op, Set<String> privateVarIDs, SearchState initState) {
		//find a private variable for which there is exactly one precondition value 
		
		
		
		for(String var : privateVarIDs){
			
			Set<Integer> preValues = new HashSet<>();
			
			boolean emptyPrecondition = false;
			
			for(Operator origOp : op.getOriginalOps()){
				if(origOp.pre.containsKey(var)){
					preValues.add(origOp.pre.get(var));
				}else{
					//preOp without var in preconditions - applicable in any value
					emptyPrecondition = true;
				}
			}
			
			if(!emptyPrecondition && preValues.size() == 1){
				System.out.println("GT op " + op.opName + " is privately-dependent in " + var);
				return true;
			}
			
			
			
		}
		return false;
		
	}

	

}
