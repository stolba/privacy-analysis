package analysis;

import tree.SearchTree;
import input.InputJSONReader;

public class PrivacyAnalysisOffline {

	public static void main(String[] args) {
		
		//TODO: read the analyzed agent ID from cmd
		SearchTree tree = new SearchTree(1);
		
		//TODO: we are going to need to read al the files for all adversary agents which will most probably prevent the Online processing anyway!
		InputJSONReader reader = new InputJSONReader();
		reader.readJSONFileOffline(args[0],tree);
		
		
		for(OperatorSet os : tree.getOperatorPropertiesSet()){
			System.out.println("operators "+os.privacyProperty+": " + os);
			
		}

	}

}
