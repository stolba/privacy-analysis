package analysis;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tree.SearchState;
import tree.SearchTree;
import analysis.OperatorSet;
import detector.InitApplicableDetector;
import detector.NotInitApplicableDetector;
import detector.OfflinePropertyDetectorInterface;
import detector.OnlinePropertyDetectorInterface;
import detector.PrivatelyDependentDetector;
import detector.PrivatelyDifferentStateDetectorInterface;
import detector.PrivatelyIndependentDetector;
import detector.PrivatelyNondeterministicDetector;
import detector.PrivatelyDeterministicDetector;

//TODO: it is probably good idea to disentangle the search tree structure and its creation from the actual analysis algorithm. The online approach if viable at all can be done using the building primitives
public class Algorithm{
	
	public final int analyzedAgentID;

	private final EnumSet<EnumAlgorithmAssumptions> assumptions;
	
	private Map<EnumPrivacyProperty,Set<OperatorSet>> operatorPropertiesMap = new HashMap<>();
	
	private final Collection<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors;
	private final Collection<OnlinePropertyDetectorInterface> onlinePropertyDetectors;
	private final Collection<OfflinePropertyDetectorInterface> offlinePropertyDetectors;
	
	
	
	
	
	public SearchTree tree;
	
	
	
	public Algorithm(
			SearchTree tree, 
			int analyzedAgentID,
			EnumSet<EnumAlgorithmAssumptions> assumptions,
			Collection<OnlinePropertyDetectorInterface> onlinePropertyDetectors,
			Collection<OfflinePropertyDetectorInterface> offlinePropertyDetectors,
			Collection<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors) {
		super();
		this.analyzedAgentID = analyzedAgentID;
		
		this.assumptions = assumptions;
		
		this.onlinePropertyDetectors = onlinePropertyDetectors;
		this.offlinePropertyDetectors = offlinePropertyDetectors;
		this.privatelyDifferentStateDetectors = privatelyDifferentStateDetectors;
		
		
		
		this.tree = tree;
	}

	
	
	
	
	
	
	public void processStateOnline(SearchState state){
		
		//process received state
		if(state.senderID == analyzedAgentID){
			
			if(PrivacyAnalysisOffline.VERBOSE) System.out.println("Algorithm processing state: "+state);
//			System.out.println("Algorithm processing state: "+state);
			
			//detect whether all successors of the current i-parent were received
			if(assumptions.contains(EnumAlgorithmAssumptions.ASSUME_STATES_SENT_AFTER_EXPANSION) && assumptions.contains(EnumAlgorithmAssumptions.ASSUME_NO_PRIVATE_ACTIONS)){
				
				SearchState previousReceivedState = tree.getPreviousReceivedState(state.senderID);
				
				if(PrivacyAnalysisOffline.VERBOSE) System.out.println("previous received state: " + previousReceivedState);
				if(PrivacyAnalysisOffline.VERBOSE) System.out.println("state: " + state);
				

				
				
//				System.out.println("state: " + state.stateID);
//				System.out.println("state.iparentID: " + state.iparentID);
//				
				
				if(previousReceivedState !=null){
//					System.out.println("previous received state: " + previousReceivedState.stateID);
//					System.out.println("previousReceivedState.iparentID: " + previousReceivedState.iparentID);
//					
					if(PrivacyAnalysisOffline.VERBOSE) System.out.println(state.senderID +": " +previousReceivedState.iparentID + "->" + previousReceivedState.stateID + "," +state.iparentID + "->" + state.stateID);
					
				}
				
				//&& previousReceivedState.iparentID != -1 && state.iparentID != -1
				if(previousReceivedState !=null && previousReceivedState.senderID == state.senderID && previousReceivedState.iparentID != state.iparentID){
					SearchState stateWithAllSuccessorsReceived = tree.getSentStateMap().get(previousReceivedState.iparentID);
					if(stateWithAllSuccessorsReceived != null){
						stateWithAllSuccessorsReceived.allSuccessorsReceived = true;
						
						if(PrivacyAnalysisOffline.VERBOSE) System.out.println("stateWithAllSuccessorsReceived: " + stateWithAllSuccessorsReceived.stateID);
					}
				}
			}
			
			for(OnlinePropertyDetectorInterface detector : onlinePropertyDetectors){
				addOpSet(detector.detectPropertyOnline( state, tree));
			}
			
		}
	}
	
	private void addOpSet(Set<OperatorSet> osSet){
		for(OperatorSet os : osSet){
			if(!os.isEmpty()){
				if(PrivacyAnalysisOffline.VERBOSE) System.out.println("Algorithm add operator set: "+os);
				
				if(!operatorPropertiesMap.containsKey(os.privacyProperty)) operatorPropertiesMap.put(os.privacyProperty, new HashSet<OperatorSet>());
				operatorPropertiesMap.get(os.privacyProperty).add(os);
			}
		}
	}
	
	
	public void processStatesOffline(){
		for(OfflinePropertyDetectorInterface detector : offlinePropertyDetectors){
			addOpSet(detector.detectPropertyOffline(tree, operatorPropertiesMap));
			
		}
		
	}
	
	
	
	
	
	public Set<OperatorSet> getOperatorPropertiesSet(EnumPrivacyProperty property){
		return operatorPropertiesMap.getOrDefault(property, new HashSet<OperatorSet>());
	}

	
}
