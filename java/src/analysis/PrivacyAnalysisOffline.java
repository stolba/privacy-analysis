package analysis;

import input.InputJSONReader;
import input.SearchTraceInputInterface;

import java.io.File;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lp.LPPropertiecComputation;
import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import tree.Variable;
import validate.OperatorPropertyValidator;
import detector.InitApplicableDetector;
import detector.NotInitApplicableDetector;
import detector.OfflinePropertyDetectorInterface;
import detector.OnlinePropertyDetectorInterface;
import detector.PrivatelyDependentDetector;
import detector.PrivatelyDeterministicDetector;
import detector.PrivatelyDifferentStateDetectorInterface;
import detector.PrivatelyIndependentDetector;
import detector.PrivatelyNondeterministicDetector;
import detector.ProjectedHeuristicPrivatelyDifferentDetector;

public class PrivacyAnalysisOffline {

	public static void main(String[] args) {
		//set up assumptions
		EnumSet<EnumAlgorithmAssumptions> assumptions = EnumSet.of(
				EnumAlgorithmAssumptions.ASSUME_PROJECTED_HEURISTIC,
				EnumAlgorithmAssumptions.ASSUME_STATES_SENT_AFTER_EXPANSION
				);
		
		//prepare detectors
		List<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors = new LinkedList<>();
		privatelyDifferentStateDetectors.add(new ProjectedHeuristicPrivatelyDifferentDetector(assumptions));
		
		List<OnlinePropertyDetectorInterface> onlinePropertyDetectors = new LinkedList<>();
		onlinePropertyDetectors.add(new PrivatelyDependentDetector());
		onlinePropertyDetectors.add(new InitApplicableDetector());
		onlinePropertyDetectors.add(new PrivatelyNondeterministicDetector());
		onlinePropertyDetectors.add(new PrivatelyIndependentDetector(privatelyDifferentStateDetectors));
		
		List<OfflinePropertyDetectorInterface> offlinePropertyDetectors = new LinkedList<>();
		offlinePropertyDetectors.add(new NotInitApplicableDetector());
		offlinePropertyDetectors.add(new PrivatelyDeterministicDetector(privatelyDifferentStateDetectors));
		
		//prepare the search tree structure
		int analyzedAgentID = Integer.parseInt(args[1]);
		
		final SearchTree tree = new SearchTree(analyzedAgentID);
		final Algorithm algorithm = new Algorithm(
				tree,
				analyzedAgentID,
				assumptions,
				onlinePropertyDetectors,
				offlinePropertyDetectors,
				privatelyDifferentStateDetectors
				);
		
		//check the trace files
		int numOfTraces = 0;
		String traceDirectory = args[0];
		for(String fileName : new File(traceDirectory).list()){
			if(fileName.startsWith("agent") && fileName.endsWith(".json")) numOfTraces++;
		}
		
		//read all the traces and process online
		InputJSONReader reader = new InputJSONReader();
		
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
		
		for(EnumPrivacyProperty prop : EnumPrivacyProperty.values()){
			for(OperatorSet os : algorithm.getOperatorPropertiesSet(prop)){
				System.out.println("operators "+os.privacyProperty+": " + os);
				
			}
		}
		
		//validation
		System.out.println("read for validation...");
		reader = new InputJSONReader();
		OperatorPropertyValidator validator = new OperatorPropertyValidator(analyzedAgentID);
		
		//TODO: this could be done better
		validator.addOnlinePropertyDetectors(onlinePropertyDetectors);
		validator.addOfflinePropertyDetectors(offlinePropertyDetectors);
		
		reader.readJSONFileOffline(traceDirectory+"/"+"agent"+analyzedAgentID+".json",validator);
		
		System.out.println("validate...");
		boolean validAll = true;
		for(EnumPrivacyProperty prop : EnumPrivacyProperty.values()){
			boolean valid = validator.validateOperators(algorithm.getOperatorPropertiesSet(prop));
			if(!valid) validAll = false;
		}
		
		if(validAll){
			System.out.println("The found properties are VALID");
		}else{
			System.out.println("The found properties are NOT VALID!");
		}
		
		System.out.println("compute LP...");
		LPPropertiecComputation lpComputation = new LPPropertiecComputation(tree);
		
		for(EnumPrivacyProperty prop : EnumPrivacyProperty.values()){
			if(!algorithm.getOperatorPropertiesSet(prop).isEmpty()){
				System.out.println("\ncompute LP for " + prop);
				
				lpComputation.prepareLP(algorithm.getOperatorPropertiesSet(prop));
				Set<String> operators = lpComputation.computeLP();
				
				System.out.println(prop + " operators: " + operators);
				System.out.println(prop + " value = " + operators.size());
			}
		}

	}

}
