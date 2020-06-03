package detector;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import input.InputJSONReader;

import org.junit.Test;

import analysis.OperatorSet;
import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import tree.Variable;

public class PrivatelyDependentDetectorTest {
	
	InputJSONReader reader = new InputJSONReader();

	@Test
	public void test() {
		PrivatelyDependentDetector detector = new PrivatelyDependentDetector();
		
		SearchTree tree = new SearchTree(0);
		
		tree.addVariable(reader.readVariable("{\"agentID\":1,\"varID\":0,\"varName\":\"varPub0\",\"isPrivate\":false,\"range\":2,\"vals\": {\"0\":\"T\",\"1\":\"F\"}}"));
		tree.addVariable(reader.readVariable("{\"agentID\":1,\"varID\":1,\"varName\":\"varPriv0\",\"isPrivate\":true,\"range\":2,\"vals\": {\"0\":\"T\",\"1\":\"F\"}}"));
		tree.addVariable(reader.readVariable("{\"agentID\":1,\"varID\":2,\"varName\":\"varPriv1\",\"isPrivate\":true,\"range\":2,\"vals\": {\"0\":\"T\",\"1\":\"F\"}}"));
		
		Operator PDOperator = reader.readOperator("{\"agentID\":1,\"ownerID\":0,\"opName\":\"PDOp \",\"isPrivate\":false,\"opID\":0,\"cost\":1,\"pre\": {\"1\":1},\"eff\": {\"0\":1}}");
		tree.addOperator(PDOperator);
		
		Operator PIOperator1 = reader.readOperator("{\"agentID\":1,\"ownerID\":0,\"opName\":\"PIOp1 \",\"isPrivate\":false,\"opID\":0,\"cost\":1,\"pre\": {\"1\":1,\"1\":0},\"eff\": {\"0\":0}}");
		tree.addOperator(PIOperator1);
		
		Operator PIOperator2 = reader.readOperator("{\"agentID\":1,\"ownerID\":0,\"opName\":\"PIOp2 \",\"isPrivate\":false,\"opID\":0,\"cost\":1,\"pre\": {},\"eff\": {\"0\":0}}");
		tree.addOperator(PIOperator2);
		
		
		SearchState relevantState = reader.readSearchState("{\"agentID\":1,\"senderID\":0,\"stateID\":1,\"parentID\":-1,\"iparentID\":-1,\"cost\":1,\"heuristic\":1,\"privateIDs\": [1,0],\"values\": [1,1,1],\"context\":\"received\"}");
		
		//Cannot detect PD if all successors were not received
		relevantState.allSuccessorsReceived = false;	
		Set<OperatorSet> result = detector.detectPropertyOnline(relevantState,tree);
		assertTrue(result.isEmpty());
		
		
		//add successor of the PI actions, PD action should be detected correctly 
		relevantState.allSuccessorsReceived = true;
		relevantState.successors.add(reader.readSearchState("{\"agentID\":1,\"senderID\":0,\"stateID\":2,\"parentID\":-1,\"iparentID\":1,\"cost\":1,\"heuristic\":1,\"privateIDs\": [1,0],\"values\": [0,1,1],\"context\":\"received\"}"));
		result = detector.detectPropertyOnline(relevantState,tree);
		assertFalse(result.isEmpty());
		for(OperatorSet opSet : result){
			assertTrue(opSet.contains(PDOperator));
			assertTrue(opSet.size() == 1);
		}
		
		//ground truth test
		Set<String> privateVarIDs = new HashSet<>();
		privateVarIDs.add("1");
		privateVarIDs.add("2");
		
		assertTrue(detector.isGroundTruthProperty(PDOperator, privateVarIDs));
//		assertFalse(detector.isGroundTruthProperty(PIOperator1, privateVarIDs)); //TODO: this does not work because the operator is not created properly
		assertFalse(detector.isGroundTruthProperty(PIOperator2, privateVarIDs));
	}

	

}
