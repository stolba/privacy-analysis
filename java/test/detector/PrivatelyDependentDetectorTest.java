package detector;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import input.InputJSONReader;

import org.junit.Test;

import analysis.OperatorSet;
import tree.Operator;
import tree.SearchState;
import tree.Variable;

public class PrivatelyDependentDetectorTest {
	
	InputJSONReader reader = new InputJSONReader();

	@Test
	public void test() {
		PrivatelyDependentDetector detector = new PrivatelyDependentDetector();
		
		SearchState.numOfPublicVariables = 1;
		
		Variable varPub0 = reader.readVariable("{\"agentID\":1,\"varID\":0,\"varName\":\"varPub0\",\"isPrivate\":false,\"range\":2,\"vals\": {\"0\":\"T\",\"1\":\"F\"}}");
		Variable varPriv0 = reader.readVariable("{\"agentID\":1,\"varID\":1,\"varName\":\"varPriv0\",\"isPrivate\":true,\"range\":2,\"vals\": {\"0\":\"T\",\"1\":\"F\"}}");
		Variable varPriv1 = reader.readVariable("{\"agentID\":1,\"varID\":2,\"varName\":\"varPriv1\",\"isPrivate\":true,\"range\":2,\"vals\": {\"0\":\"T\",\"1\":\"F\"}}");
		
		Operator PDOperator = reader.readOperator("{\"agentID\":1,\"ownerID\":0,\"opName\":\"PDOp \",\"isPrivate\":false,\"opID\":0,\"cost\":1,\"pre\": {\"1\":1},\"eff\": {\"0\":1}}");
		PDOperator.generatePublicLabelAndHash();
		Operator PIOperator1 = reader.readOperator("{\"agentID\":1,\"ownerID\":0,\"opName\":\"PIOp1 \",\"isPrivate\":false,\"opID\":0,\"cost\":1,\"pre\": {\"1\":1,\"1\":0},\"eff\": {\"0\":0}}");
		PIOperator1.generatePublicLabelAndHash();
		Operator PIOperator2 = reader.readOperator("{\"agentID\":1,\"ownerID\":0,\"opName\":\"PIOp2 \",\"isPrivate\":false,\"opID\":0,\"cost\":1,\"pre\": {},\"eff\": {\"0\":0}}");
		PIOperator2.generatePublicLabelAndHash();
		
		Set<Operator> allOperators = new HashSet<>();
		allOperators.add(PDOperator);
		allOperators.add(PIOperator1);
		allOperators.add(PIOperator2);
		
		SearchState relevantState = reader.readSearchState("{\"agentID\":1,\"senderID\":0,\"stateID\":1,\"parentID\":-1,\"iparentID\":-1,\"cost\":1,\"heuristic\":1,\"privateIDs\": [1,0],\"values\": [1,1,1],\"context\":\"received\"}");
		
		//Cannot detect PD if all successors were not received
		relevantState.allSuccessorsReceived = false;	
		Set<OperatorSet> result = detector.detectPropertyOnline(allOperators, null, relevantState, null, 0);
		assertTrue(result.isEmpty());
		
		//if all successors are received but no successors, all actions are PD
		relevantState.allSuccessorsReceived = true;	
		result = detector.detectPropertyOnline(allOperators, null, relevantState, null, 0);
		assertFalse(result.isEmpty());
		assertTrue(result.contains(allOperators));
		
		//add successor of the PI actions, PD action should be detected correctly 
		relevantState.successors.add(reader.readSearchState("{\"agentID\":1,\"senderID\":0,\"stateID\":2,\"parentID\":-1,\"iparentID\":1,\"cost\":1,\"heuristic\":1,\"privateIDs\": [1,0],\"values\": [0,1,1],\"context\":\"received\"}"));
		result = detector.detectPropertyOnline(allOperators, null, relevantState, null, 0);
		assertFalse(result.isEmpty());
		for(OperatorSet opSet : result){
			assertTrue(opSet.contains(PDOperator));
			assertTrue(opSet.size() == 1);
		}
		//TODO: ground truth test
		Set<String> privateVarIDs = new HashSet<>();
		privateVarIDs.add("1");
		privateVarIDs.add("2");
		
		assertTrue(detector.isGroundTruthProperty(PDOperator, privateVarIDs));
//		assertFalse(detector.isGroundTruthProperty(PIOperator1, privateVarIDs)); //TODO: this does not work because the operator is not created properly
		assertFalse(detector.isGroundTruthProperty(PIOperator2, privateVarIDs));
	}

	

}
