package analysis;

import input.InputJSONReader;

import java.util.LinkedList;
import java.util.List;

import tree.SearchTree;
import validate.OperatorPropertyValidator;
import detector.PrivatelyDifferentStateDetectorInterface;
import detector.ProjectedHeuristicPrivatelyDifferentDetector;
import detector.PropertyDetectorInterface;

public class PrivacyAnalysisOffline {

	public static void main(String[] args) {
		
		
		List<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors = new LinkedList<>();
		
		privatelyDifferentStateDetectors.add(new ProjectedHeuristicPrivatelyDifferentDetector());
		
		//TODO: read the analyzed agent ID from cmd
		int analyzedAgentID = 1;
		SearchTree tree = new SearchTree(analyzedAgentID,privatelyDifferentStateDetectors);
		
		//TODO: we are going to need to read al the files for all adversary agents which will most probably prevent the Online processing anyway!
		InputJSONReader reader = new InputJSONReader();
		reader.readJSONFileOffline(args[0],tree);
		
		
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
		
		reader.readJSONFileOffline(args[1],validator);
		
		System.out.println("validate...");
		
		
		validator.validateOperators(tree.getOperatorPropertiesSet());

	}

}
