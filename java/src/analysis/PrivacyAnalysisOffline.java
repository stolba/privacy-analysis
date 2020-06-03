package analysis;

import input.InputJSONReader;
import input.SearchTraceInputInterface;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import tree.Variable;
import validate.OperatorPropertyValidator;
import detector.PrivatelyDifferentStateDetectorInterface;
import detector.ProjectedHeuristicPrivatelyDifferentDetector;

public class PrivacyAnalysisOffline {

	public static void main(String[] args) {
		
		
		List<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors = new LinkedList<>();
		
		privatelyDifferentStateDetectors.add(new ProjectedHeuristicPrivatelyDifferentDetector());
		
		String traceDirectory = args[0];
		int analyzedAgentID = Integer.parseInt(args[1]);
		
		final SearchTree tree = new SearchTree(analyzedAgentID);
		final Algorithm algorithm = new Algorithm(tree,analyzedAgentID,privatelyDifferentStateDetectors);
		
		InputJSONReader reader = new InputJSONReader();
		
		int numOfTraces = 0;
		for(String fileName : new File(traceDirectory).list()){
			if(fileName.startsWith("agent") && fileName.endsWith(".json")) numOfTraces++;
		}
		
		
		for(int i = 0; i < numOfTraces; ++i){
			if(i == analyzedAgentID) continue;
			
			reader.readJSONFileOffline(traceDirectory+"/"+"agent"+i+".json",new SearchTraceInputInterface() {
				
				@Override
				public void afterAllVariablesProcessed() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterAllStatesProcessed() {
					algorithm.processStatesOffline();
					
				}
				
				@Override
				public void afterAllOperatorsProcessed() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void addVariable(Variable var) {
					tree.addVariable(var);
				}
				
				@Override
				public void addStateSequential(SearchState state) {
					tree.addState(state);
					algorithm.processStateOnline(state);
				}
				
				@Override
				public void addOperator(Operator op) {
					tree.addOperator(op);
					
				}
			});
		}
		
		
		for(OperatorSet os : algorithm.getOperatorPropertiesSet()){
			System.out.println("operators "+os.privacyProperty+": " + os);
			
		}
		
		//validation
		System.out.println("read for validation...");
		reader = new InputJSONReader();
		OperatorPropertyValidator validator = new OperatorPropertyValidator(analyzedAgentID);
		
		//TODO: this could be done better
		validator.addPropertyDetector(algorithm.deDetector);
		validator.addPropertyDetector(algorithm.iaDetector);
		validator.addPropertyDetector(algorithm.niaDetector);
		validator.addPropertyDetector(algorithm.noDetector);
		validator.addPropertyDetector(algorithm.pdDetector);
		validator.addPropertyDetector(algorithm.piDetector);
		
		reader.readJSONFileOffline(traceDirectory+"/"+"agent"+analyzedAgentID+".json",validator);
		
		System.out.println("validate...");
		
		
		boolean valid = validator.validateOperators(algorithm.getOperatorPropertiesSet());
		
		if(valid){
			System.out.println("The found properties are VALID");
		}else{
			System.out.println("The found properties are NOT VALID!");
		}

	}

}
