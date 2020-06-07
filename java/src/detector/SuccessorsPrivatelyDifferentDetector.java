package detector;

import java.util.EnumSet;

import analysis.EnumAlgorithmAssumptions;
import tree.SearchState;

public class SuccessorsPrivatelyDifferentDetector implements
		PrivatelyDifferentStateDetectorInterface {
	
	
	public SuccessorsPrivatelyDifferentDetector(EnumSet<EnumAlgorithmAssumptions> assumptions){
		
	}
			

	@Override
	public boolean privatelyDifferent(SearchState s1, SearchState s2) {
		if(!s1.allSuccessorsReceived || !s2.allSuccessorsReceived) return false;
		
		if(s1.successors.equals(s2.successors)){
			return true;
		}else{
			return false;
		}
	}

}
