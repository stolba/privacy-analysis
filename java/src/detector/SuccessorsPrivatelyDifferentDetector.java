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
		
		System.out.println("SuccessorsPrivatelyDifferentDetector Passed!");
		
		if(s1.successors.equals(s2.successors)){
			System.out.println("SuccessorsPrivatelyDifferentDetector DETECTED!");
			return true;
		}else{
			return false;
		}
	}


	@Override
	public boolean isApplicable() {
		return true;
	}

}
