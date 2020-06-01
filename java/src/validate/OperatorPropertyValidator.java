package validate;

import input.SearchTraceInputInterface;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tree.Operator;
import tree.SearchState;
import tree.Variable;
import analysis.EnumPrivacyProperty;
import analysis.OperatorSet;
import detector.OfflinePropertyDetectorInterface;
import detector.OnlinePropertyDetectorInterface;

public class OperatorPropertyValidator implements SearchTraceInputInterface{
	
	private final int analyzedAgentID;
	
	private final Map<Integer,Set<EnumPrivacyProperty>> groundTruthOpPrivacyProperties = new HashMap<>();
	
	private final List<OnlinePropertyDetectorInterface> onlinePropertyDetectors = new LinkedList<>();
	private final List<OfflinePropertyDetectorInterface> offlinePropertyDetectors = new LinkedList<>();
	
	private final Set<String> privateVarIDs = new HashSet<>();
	
	private Map<Integer,Operator> opMap = new HashMap<>();
	
	
	public OperatorPropertyValidator(int analyzedAgentID){
		this.analyzedAgentID = analyzedAgentID;
	}
	
	public void addPropertyDetector(OnlinePropertyDetectorInterface detector){
		onlinePropertyDetectors.add(detector);
	}
	
	public void addPropertyDetector(OfflinePropertyDetectorInterface detector){
		offlinePropertyDetectors.add(detector);
	}
	
	public boolean validateOperators(Set<OperatorSet> operatorPropertiesSet){
		boolean valid = true;
		
		for(OperatorSet ops : operatorPropertiesSet){
			EnumPrivacyProperty property = ops.privacyProperty;
			
			//TODO: we need to handle the label non-preserving abstraction and test all IDs in originalOpIDs
			for(Operator op : ops){
				if(!groundTruthOpPrivacyProperties.containsKey(op.opID)){
					System.out.println("WARN: operator "+op.opName + " was not part of the processed search trace.");
				}else if (!groundTruthOpPrivacyProperties.get(op.opID).contains(property)){
					System.out.println("WARN: operator "+op.opName + " has the property " + property + ", but it should not be the case according to the ground truth!");
					valid = false;
				}
			}
		}
		
		return valid;
	}

	@Override
	public void addVariable(Variable var) {
		if(analyzedAgentID != var.agentID) return;
		
		if(var.isPrivate){
			privateVarIDs.add(Integer.toString(var.varID));
		}
		
	}

	//TODO: we need to consider label non-preserving projection here as well!
	@Override
	public void addOperator(Operator op) {
		if(analyzedAgentID != op.ownerID) return;
		
		op.generatePublicLabelAndHash();
		
		if(opMap.containsKey(op.hash)){
			Operator existingOp = opMap.get(op.hash);
			
			existingOp.addOriginalOp(op);
			
			System.out.println("GT: op " + op.opName + " added to op " + existingOp.opName + " with preMask="+Arrays.toString(op.preMask)+ ",effMask="+Arrays.toString(op.effMask));
			
		}else{
			opMap.put(op.hash, op);
			System.out.println("GT: op " + op.opName + " preMask="+Arrays.toString(op.preMask)+ ",effMask="+Arrays.toString(op.effMask));
		}
		
		groundTruthOpPrivacyProperties.put(op.opID,new HashSet<EnumPrivacyProperty>());
	}

	@Override
	public void addStateSequential(SearchState state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterAllStatesProcessed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterAllVariablesProcessed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterAllOperatorsProcessed() {
		System.out.println("GT: afterAllOperatorsProcessed...");
		for(Operator op : opMap.values()){
			for(OnlinePropertyDetectorInterface detector : onlinePropertyDetectors){
				if(detector.isGroundTruthProperty(op,privateVarIDs)){
					groundTruthOpPrivacyProperties.putIfAbsent(op.opID, new HashSet<EnumPrivacyProperty>());
					groundTruthOpPrivacyProperties.get(op.opID).add(detector.getPrivacyProperty());
				}
			}
			for(OfflinePropertyDetectorInterface detector : offlinePropertyDetectors){
				if(detector.isGroundTruthProperty(op,privateVarIDs)){
					groundTruthOpPrivacyProperties.putIfAbsent(op.opID, new HashSet<EnumPrivacyProperty>());
					groundTruthOpPrivacyProperties.get(op.opID).add(detector.getPrivacyProperty());
				}
			}
		}
		
	}

}
