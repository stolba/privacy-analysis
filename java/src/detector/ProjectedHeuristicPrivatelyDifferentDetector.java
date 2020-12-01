package detector;

import java.util.EnumSet;

import analysis.EnumAlgorithmAssumptions;
import tree.SearchState;

public class ProjectedHeuristicPrivatelyDifferentDetector implements
		PrivatelyDifferentStateDetectorInterface {
	
	final boolean applicable;
	
	public ProjectedHeuristicPrivatelyDifferentDetector(EnumSet<EnumAlgorithmAssumptions> assumptions){
		applicable = assumptions.contains(EnumAlgorithmAssumptions.ASSUME_PROJECTED_HEURISTIC);
	}
			

	@Override
	public boolean privatelyDifferent(SearchState s1, SearchState s2) {
		if(!applicable) return false;
		
		return s1.heuristic != s2.heuristic;
	}


	@Override
	public boolean isApplicable() {
		return applicable;
	}

}
