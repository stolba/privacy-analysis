package analysis;

import tree.SearchTree;
import input.InputJSONReader;

public class PrivacyAnalysisOffline {

	public static void main(String[] args) {
		
		//TODO: read the analyzed agent ID from cmd
		SearchTree tree = new SearchTree(0);
		
		
		InputJSONReader reader = new InputJSONReader();
		reader.readJSONFileOffline(args[0],tree);

	}

}
