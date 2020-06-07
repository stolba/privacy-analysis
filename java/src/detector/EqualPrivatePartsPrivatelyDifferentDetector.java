package detector;

import tree.SearchState;

public class EqualPrivatePartsPrivatelyDifferentDetector implements
		PrivatelyDifferentStateDetectorInterface {
	
	
	public EqualPrivatePartsPrivatelyDifferentDetector(){
		
	}
			

	@Override
	public boolean privatelyDifferent(SearchState s1, SearchState s2) {
		
		for(int i = 0; i < s1.values.length; i++){
			if(s1.values[i] != s2.values[i]) return false;
		}
		
		if(s1.heuristic != s2.heuristic || s1.cost != s2.cost){
			return true;
		}else{
			return false;
		}
		
	}

}
