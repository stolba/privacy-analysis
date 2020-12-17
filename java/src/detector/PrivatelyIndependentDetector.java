package detector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public class PrivatelyIndependentDetector implements OnlinePropertyDetectorInterface {
	
	private final Collection<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors;
	
	

	public PrivatelyIndependentDetector(
			Collection<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors) {
		super();
		this.privatelyDifferentStateDetectors = privatelyDifferentStateDetectors;
	}
	
	@Override
	public Set<OperatorSet> detectPropertyOnline(
			SearchState relevantState,
			SearchTree tree) {
		

		Set<OperatorSet> result =  new HashSet<OperatorSet>();
		
		
		SearchState iparent = tree.getSentState(relevantState.iparentID);
		
		if(iparent == null) return result;
		
		for(SearchState s : tree.getReceivedStates()){
			SearchState otherIParent = tree.getSentState(s.iparentID);
			if(otherIParent == null) continue;
			
			if(iparent.publiclyEquivalent(otherIParent)){
				if(iparent.privatelyDifferent(otherIParent, privatelyDifferentStateDetectors)){
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
		
		return EnumPrivacyProperty.PRIVATELY_INDEPENDENT;
	}

	
	@Override
	public boolean isGroundTruthProperty(Operator op, Set<String> privateVarIDs, SearchState initState) {
		//find a private variable for which there is none or more than one precondition value 
		Set<String> opValues = new HashSet<>();
		
		for(String var : privateVarIDs){
			
			for(Operator origOp : op.getOriginalOps()){
				if(origOp.pre.containsKey(var)){
					opValues.add(var + "-"+origOp.pre.get(var));
				}
			}
			
			
			
		}

		if(opValues.size() != 1 ){
			//op has multiple precondition values for var
//			System.out.println("GT op " + op.opName + " is privately-independent in " + var);
			return true;
		}else{
			return false;
		}
		
	}

	

}
