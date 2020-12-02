package validate;

import input.SearchTraceInputInterface;

import java.util.Arrays;
import java.util.Collection;
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
import analysis.Result;
import detector.OfflinePropertyDetectorInterface;
import detector.OnlinePropertyDetectorInterface;

public class OperatorPropertyValidator implements SearchTraceInputInterface{
	
	private final int analyzedAgentID;
	
	private final Map<Integer,Set<EnumPrivacyProperty>> groundTruthOpPrivacyProperties = new HashMap<>();
	
	private final List<OnlinePropertyDetectorInterface> onlinePropertyDetectors = new LinkedList<>();
	private final List<OfflinePropertyDetectorInterface> offlinePropertyDetectors = new LinkedList<>();
	
	private final Set<String> privateVarIDs = new HashSet<>();
	
	private Map<Integer,Operator> opMap = new HashMap<>();
	
	private SearchState initState = null;
	

	public int privateActions=0;
	
	
	public OperatorPropertyValidator(int analyzedAgentID){
		this.analyzedAgentID = analyzedAgentID;
	}
	
	public void addOnlinePropertyDetectors(Collection<OnlinePropertyDetectorInterface> detectors){
		onlinePropertyDetectors.addAll(detectors);
	}
	
	public void addOfflinePropertyDetectors(Collection<OfflinePropertyDetectorInterface> detectors){
		offlinePropertyDetectors.addAll(detectors);
	}
	
	public boolean validateOperators(Set<OperatorSet> operatorPropertiesSet){
		boolean valid = true;
		
		for(OperatorSet ops : operatorPropertiesSet){
			EnumPrivacyProperty property = ops.privacyProperty;
			
			for(Operator op : ops){
				boolean atLeastOneOriginalOpValid = false;
				
				for(Operator originalOp : op.getOriginalOps()){
					if(!groundTruthOpPrivacyProperties.containsKey(originalOp.opID)){
						System.out.println("WARN: operator "+originalOp.opName + " was not part of the processed search trace.");
					}else if (groundTruthOpPrivacyProperties.get(originalOp.opID).contains(property)){
						atLeastOneOriginalOpValid = true;
					}
				}
				
				if(!atLeastOneOriginalOpValid){
					System.out.println("WARN: operator "+op + " has the property " + property + ", but it should not be the case according to the ground truth!");
					valid = false;
					ops.setInvalid();
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

	
	@Override
	public void addOperator(Operator op) {
		
		if(op.isPrivate){
			privateActions++;
		}
		
		if(analyzedAgentID != op.ownerID) return;
		
		op.generatePublicLabelAndHash();
		
		if(opMap.containsKey(op.hash)){
			Operator existingOp = opMap.get(op.hash);
			
			existingOp.addOriginalOp(op);
			
			System.out.println("GT: op " + op.opName + " added to op " + existingOp.opName + " with preMask="+Arrays.toString(op.publicPreMask)+ ",effMask="+Arrays.toString(op.publicEffMask));
			
		}else{
			opMap.put(op.hash, op);
			System.out.println("GT: op " + op.opName + " preMask="+Arrays.toString(op.publicPreMask)+ ",effMask="+Arrays.toString(op.publicEffMask));
		}
		
		groundTruthOpPrivacyProperties.put(op.opID,new HashSet<EnumPrivacyProperty>());
	}

	@Override
	public void addStateSequential(SearchState state) {
		if(state.stateID == SearchState.INITIAL_STATE_ID){
			
			initState = state;
			
		}
		
	}

	@Override
	public void afterAllStatesProcessed() {
		//TODO: we should not process all states!
		
		System.out.println("GT: afterAllOperatorsProcessed...");
		for(Operator projectedOp : opMap.values()){
			Operator op = projectedOp;
//			for(Operator op : projectedOp.getOriginalOps()){
				for(OnlinePropertyDetectorInterface detector : onlinePropertyDetectors){
					if(detector.isGroundTruthProperty(op,privateVarIDs,initState)){
						groundTruthOpPrivacyProperties.putIfAbsent(op.opID, new HashSet<EnumPrivacyProperty>());
						groundTruthOpPrivacyProperties.get(op.opID).add(detector.getPrivacyProperty());
					}
				}
				for(OfflinePropertyDetectorInterface detector : offlinePropertyDetectors){
					if(detector.isGroundTruthProperty(op,privateVarIDs,initState)){
						groundTruthOpPrivacyProperties.putIfAbsent(op.opID, new HashSet<EnumPrivacyProperty>());
						groundTruthOpPrivacyProperties.get(op.opID).add(detector.getPrivacyProperty());
					}
				}
//			}
		}
		
	}

	@Override
	public void afterAllVariablesProcessed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterAllOperatorsProcessed() {
		
		
	}
	
	public void writeGroundTruthResults(Result result){
		for(Integer opID: groundTruthOpPrivacyProperties.keySet()){
			for(EnumPrivacyProperty prop : groundTruthOpPrivacyProperties.get(opID)){
				result.increaseGroundTruthCount(prop);
			}
		}
	}

}
