package analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tree.SearchState;
import tree.SearchTree;
import analysis.OperatorSet;
import detector.InitApplicableDetector;
import detector.NotInitApplicableDetector;
import detector.PrivatelyDependentDetector;
import detector.PrivatelyDifferentStateDetectorInterface;
import detector.PrivatelyIndependentDetector;
import detector.PrivatelyNondeterministicDetector;
import detector.PrivatelyDeterministicDetector;

//TODO: it is probably good idea to disentangle the search tree structure and its creation from the actual analysis algorithm. The online approach if viable at all can be done using the building primitives
public class Algorithm{
	
	public final int analyzedAgentID;

	
	private SearchState previousReceivedState = null;
	
	private Map<EnumPrivacyProperty,Set<OperatorSet>> operatorPropertiesMap = new HashMap<>();
	
	
	
	public PrivatelyDependentDetector pdDetector = new PrivatelyDependentDetector();
	public InitApplicableDetector iaDetector = new InitApplicableDetector();
	public PrivatelyIndependentDetector piDetector;
	public PrivatelyNondeterministicDetector noDetector = new PrivatelyNondeterministicDetector();
	public NotInitApplicableDetector niaDetector = new NotInitApplicableDetector();
	public PrivatelyDeterministicDetector deDetector;
	
	public SearchTree tree;
	
	
	
	public Algorithm(SearchTree tree, int analyzedAgentID,Collection<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors) {
		super();
		this.analyzedAgentID = analyzedAgentID;
		
		piDetector = new PrivatelyIndependentDetector(privatelyDifferentStateDetectors);
		deDetector = new PrivatelyDeterministicDetector(privatelyDifferentStateDetectors);
		
		this.tree = tree;
	}

	
	
	
	
	
	
	public void processStateOnline(SearchState state){
		
		if(state.iparentID == SearchState.UNDEFINED_STATE_ID){
			//sent state
			
		}else{
			//received state
			
			SearchState iparent = tree.getIParent(state);
			
			addOpSet(iaDetector.detectPropertyOnline( state, tree));
			
			addOpSet(piDetector.detectPropertyOnline(state, tree));
			
			addOpSet(noDetector.detectPropertyOnline(state, tree));
			
			//detect whether all successors of the current i-parent were received
			//TODO: this has to be turned off when not using GBFS
			if(previousReceivedState !=null && previousReceivedState.iparentID != state.iparentID){
				SearchState stateWithAllSuccessorsReceived = tree.getSentStateMap().get(previousReceivedState.iparentID);
				stateWithAllSuccessorsReceived.allSuccessorsReceived = true;
				System.out.println("all successors of state "+stateWithAllSuccessorsReceived+" received");
				
				addOpSet(pdDetector.detectPropertyOnline(stateWithAllSuccessorsReceived,tree));
			}
			previousReceivedState = state;
		}
	}
	
	private void addOpSet(Set<OperatorSet> osSet){
		for(OperatorSet os : osSet){
			if(!os.isEmpty()){
				if(!operatorPropertiesMap.containsKey(os.privacyProperty)) operatorPropertiesMap.put(os.privacyProperty, new HashSet<OperatorSet>());
				operatorPropertiesMap.get(os.privacyProperty).add(os);
			}
		}
	}
	
	
	public void processStatesOffline(){
		addOpSet(niaDetector.detectPropertyOffline(tree.getAllOperators(), getOperatorPropertiesSet(EnumPrivacyProperty.INIT_APPLICBLE)));
		addOpSet(deDetector.detectPropertyOffline(tree.getAllOperators(), null));
	}
	
	
	
	
	
	public Set<OperatorSet> getOperatorPropertiesSet(EnumPrivacyProperty property){
		return operatorPropertiesMap.getOrDefault(property, new HashSet<OperatorSet>());
	}

	
}
