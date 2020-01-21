package analysis;

import input.InputJSONReader;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import tree.SearchTree;
import validate.OperatorPropertyValidator;
import detector.PrivatelyDifferentStateDetectorInterface;
import detector.ProjectedHeuristicPrivatelyDifferentDetector;

public class PrivacyAnalysisOffline {

	public static void main(String[] args) {
		
		
		List<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors = new LinkedList<>();
		
		privatelyDifferentStateDetectors.add(new ProjectedHeuristicPrivatelyDifferentDetector());
		
		String traceDirectory = args[0];
		int analyzedAgentID = Integer.parseInt(args[1]);
		
		SearchTree tree = new SearchTree(analyzedAgentID,privatelyDifferentStateDetectors);
		
		InputJSONReader reader = new InputJSONReader();
		
		int numOfTraces = 0;
		for(String fileName : new File(traceDirectory).list()){
			if(fileName.startsWith("agent") && fileName.endsWith(".json")) numOfTraces++;
		}
		
		
		for(int i = 0; i < numOfTraces; ++i){
			if(i == analyzedAgentID) continue;
			
			reader.readJSONFileOffline(traceDirectory+"/"+"agent"+i+".json",tree);
		}
		
		
		for(OperatorSet os : tree.getOperatorPropertiesSet()){
			System.out.println("operators "+os.privacyProperty+": " + os);
			
		}
		
		//validation
		System.out.println("read for validation...");
		reader = new InputJSONReader();
		OperatorPropertyValidator validator = new OperatorPropertyValidator(analyzedAgentID);
		
		//TODO: this could be done better
		validator.addPropertyDetector(tree.deDetector);
		validator.addPropertyDetector(tree.iaDetector);
		validator.addPropertyDetector(tree.niaDetector);
		validator.addPropertyDetector(tree.noDetector);
		validator.addPropertyDetector(tree.pdDetector);
		validator.addPropertyDetector(tree.piDetector);
		
		reader.readJSONFileOffline(traceDirectory+"/"+"agent"+analyzedAgentID+".json",validator);
		
		System.out.println("validate...");
		
		
		boolean valid = validator.validateOperators(tree.getOperatorPropertiesSet());
		
		if(valid){
			System.out.println("The found properties are VALID");
		}else{
			System.out.println("The found properties are NOT VALID!");
		}

	}

}
