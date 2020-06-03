package detector;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import input.InputJSONReader;

import org.junit.Test;

import analysis.OperatorSet;
import tree.Operator;
import tree.SearchState;
import tree.Variable;

public class PrivatelyIndependentDetectorTest {
	
	InputJSONReader reader = new InputJSONReader();

	@Test
	public void test() {
		List<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors = new LinkedList<>();
		
		privatelyDifferentStateDetectors.add(new ProjectedHeuristicPrivatelyDifferentDetector());
		
		PrivatelyIndependentDetector detector = new PrivatelyIndependentDetector(privatelyDifferentStateDetectors);
		
		SearchState.numOfPublicVariables = 1;
		
		Variable varPub0 = reader.readVariable("{\"agentID\":1,\"varID\":0,\"varName\":\"varPub0\",\"isPrivate\":false,\"range\":2,\"vals\": {\"0\":\"T\",\"1\":\"F\"}}");
		Variable varPriv0 = reader.readVariable("{\"agentID\":1,\"varID\":1,\"varName\":\"varPriv0\",\"isPrivate\":true,\"range\":2,\"vals\": {\"0\":\"T\",\"1\":\"F\"}}");
		Variable varPriv1 = reader.readVariable("{\"agentID\":1,\"varID\":2,\"varName\":\"varPriv1\",\"isPrivate\":true,\"range\":2,\"vals\": {\"0\":\"T\",\"1\":\"F\"}}");
		
		Operator PDOperator = reader.readOperator("{\"agentID\":1,\"ownerID\":0,\"opName\":\"PDOp \",\"isPrivate\":false,\"opID\":0,\"cost\":1,\"pre\": {\"1\":1},\"eff\": {\"0\":0}}");
		PDOperator.generatePublicLabelAndHash();
		Operator PIOperator1 = reader.readOperator("{\"agentID\":1,\"ownerID\":0,\"opName\":\"PIOp1 \",\"isPrivate\":false,\"opID\":0,\"cost\":1,\"pre\": {\"1\":1,\"1\":0},\"eff\": {\"0\":0}}");
		PIOperator1.generatePublicLabelAndHash();
		Operator PIOperator2 = reader.readOperator("{\"agentID\":1,\"ownerID\":0,\"opName\":\"PIOp2 \",\"isPrivate\":false,\"opID\":0,\"cost\":1,\"pre\": {},\"eff\": {\"0\":0}}");
		PIOperator2.generatePublicLabelAndHash();
		
		Set<Operator> allOperators = new HashSet<>();
		allOperators.add(PDOperator);
		allOperators.add(PIOperator1);
		allOperators.add(PIOperator2);
		
		SearchState iParent = reader.readSearchState("{\"agentID\":1,\"senderID\":0,\"stateID\":1,\"parentID\":-1,\"iparentID\":-1,\"cost\":1,\"heuristic\":1,\"privateIDs\": [1,0],\"values\": [1,1,1],\"context\":\"received\"}");
		SearchState iParentPrivatelyDifferent = reader.readSearchState("{\"agentID\":1,\"senderID\":0,\"stateID\":2,\"parentID\":-1,\"iparentID\":-1,\"cost\":1,\"heuristic\":2,\"privateIDs\": [1,1],\"values\": [1,0,1],\"context\":\"received\"}");
		
		SearchState state3 = reader.readSearchState("{\"agentID\":1,\"senderID\":0,\"stateID\":3,\"parentID\":-1,\"iparentID\":1,\"cost\":1,\"heuristic\":1,\"privateIDs\": [1,0],\"values\": [0,1,1],\"context\":\"received\"}");
		SearchState state4 = reader.readSearchState("{\"agentID\":1,\"senderID\":0,\"stateID\":4,\"parentID\":-1,\"iparentID\":2,\"cost\":1,\"heuristic\":1,\"privateIDs\": [1,0],\"values\": [0,0,1],\"context\":\"received\"}");
		
		state3.responsibleOperators.add(PDOperator);
		state3.responsibleOperators.add(PIOperator1);
		state3.responsibleOperators.add(PIOperator2);
		
		state4.responsibleOperators.add(PIOperator1);
		state4.responsibleOperators.add(PIOperator2);
		
		Map<Integer,SearchState> stateMap = new HashMap<>();
		stateMap.put(1, iParent);
		stateMap.put(2, iParentPrivatelyDifferent);
		stateMap.put(3, state3);
		stateMap.put(4, state4);
 		
		
		Set<OperatorSet> result = detector.detectPropertyOnline(allOperators, stateMap, state3, iParent, 0);
		System.out.println(result);
		
		assertFalse(result.isEmpty());
		
		for(OperatorSet opSet : result){
			assertTrue(opSet.contains(PIOperator1));
			assertTrue(opSet.contains(PIOperator2));
			assertTrue(opSet.size() == 2);
		}
		
	}

	

}
