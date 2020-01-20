package detector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import tree.Operator;
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
		
		return EnumPrivacyProperty.PRIVATELY_INDEPENDENT;
	}

	@Override
	public boolean isApplicableOnline() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isGroundTruthProperty(Operator op, Set<String> privateVarIDs) {
		//find a private variable for which there is none or more than one precondition value 
		for(String var : privateVarIDs){
			Set<Integer> opValues = new HashSet<>();
			
			for(Operator origOp : op.getOriginalOps()){
				if(origOp.pre.containsKey(var)) opValues.add(origOp.pre.get(var));
			}
			
			if(opValues.size() != 1 ){
				System.out.println("GT op " + op.opName + " is privately-independent in " + var);
				return true;
			}
			
		}
		return false;
		
	}

}
