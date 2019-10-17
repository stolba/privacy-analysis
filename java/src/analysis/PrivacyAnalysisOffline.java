package analysis;

import input.InputJSONReader;

import java.util.LinkedList;
import java.util.List;

import tree.SearchTree;
import detector.PrivatelyDifferentStateDetectorInterface;
import detector.ProjectedHeuristicPrivatelyDifferentDetector;

public class PrivacyAnalysisOffline {

	public static void main(String[] args) {
		
		
		List<PrivatelyDifferentStateDetectorInterface> privatelyDifferentStateDetectors = new LinkedList<>();
		
		privatelyDifferentStateDetectors.add(new ProjectedHeuristicPrivatelyDifferentDetector());
		
		//TODO: read the analyzed agent ID from cmd
		SearchTree tree = new SearchTree(1,privatelyDifferentStateDetectors);
		
		//TODO: we are going to need to read al the files for all adversary agents which will most probably prevent the Online processing anyway!
		InputJSONReader reader = new InputJSONReader();
		reader.readJSONFileOffline(args[0],tree);
		
		
		for(OperatorSet os : tree.getOperatorPropertiesSet()){
			System.out.println("operators "+os.privacyProperty+": " + os);
			
		}

	}

}
