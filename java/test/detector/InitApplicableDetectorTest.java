package detector;

import static org.junit.Assert.*;

import java.util.Set;

import input.InputJSONReader;

import org.junit.Test;

import analysis.OperatorSet;
import tree.Operator;
import tree.SearchState;

public class InitApplicableDetectorTest {
	
	InputJSONReader reader = new InputJSONReader();
	

	@Test
	public void testDetectPropertyOnline() {
		InitApplicableDetector detector = new InitApplicableDetector();
		
		SearchState relevantState = reader.readSearchState("{\"agentID\":1,\"senderID\":0,\"stateID\":1,\"parentID\":-1,\"iparentID\":-1,\"cost\":1,\"heuristic\":1,\"privateIDs\": [1,0],\"values\": [1,0,1,1],\"context\":\"received\"}");
		Operator testOperator = new Operator();
		
		relevantState.responsibleOperators.add(testOperator);
		
		Set<OperatorSet> operatorSetSet = detector.detectPropertyOnline(null, null, relevantState, null, 0);
		
		assertTrue(operatorSetSet.size() == 1);
		
		for(OperatorSet opSet : operatorSetSet){
			assertTrue(opSet.contains(testOperator));
		}
	}

	@Test
	public void testIsGroundTruthProperty() {
		fail("Not yet implemented");
		
		InitApplicableDetector detector = new InitApplicableDetector();
		
//		Operator testOperator = reader.readOperator(input)
//		
//		detector.isGroundTruthProperty(op, privatePropertyIDs)
		
		//TODO
	}

}
