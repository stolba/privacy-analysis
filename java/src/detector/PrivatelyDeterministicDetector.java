package detector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;

public class PrivatelyDeterministicDetector implements OfflinePropertyDetectorInterface {
	
	private final Collection<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors;
	
	

	public PrivatelyDeterministicDetector(
			Collection<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors) {
		super();
		this.privatelyDifferentStateDetectors = privatelyDifferentStateDetectors;
	}
	
	

	@Override
	public Set<OperatorSet> detectPropertyOffline(
			SearchTree tree,
			Map<EnumPrivacyProperty,Set<OperatorSet>> operatorPropertiesMap) {
		

		OperatorSet opSet = new OperatorSet(EnumPrivacyProperty.PRIVATELY_DETERMINISTIC,false);
		
		for(Operator op : tree.getAllOperators()){
			boolean opIsPubliclyDeterministic = true;
			
			for(SearchState s1 : op.matchingTransitions){
				for(SearchState s2 : op.matchingTransitions){
					if(!s1.publiclyEquivalent(s2)) continue;
					
					if(!s1.privatelyDifferent(s2, privatelyDifferentStateDetectors)){
						opIsPubliclyDeterministic = false;
						break;
					}
					
					if(!opIsPubliclyDeterministic) break;
					
				}
				
				if(opIsPubliclyDeterministic){
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
		
		return EnumPrivacyProperty.PRIVATELY_DETERMINISTIC;
	}

	
	@Override
	public boolean isGroundTruthProperty(Operator op, Set<String> privateVarIDs, SearchState initState) {
		//find a private variable for which there is exactly one effect value 
		for(String var : privateVarIDs){
			Set<Integer> opValues = new HashSet<>();
			
			for(Operator origOp : op.getOriginalOps()){
				if(origOp.eff.containsKey(var)) opValues.add(origOp.eff.get(var));
			}
			
			if(opValues.size() == 1 ){
				System.out.println("GT op " + op.opName + " is privately-deterministic in " + var);
				return true;
			}
			
		}
		return false;
		
	}

	

}
