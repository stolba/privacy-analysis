package detector;

import tree.SearchState;

public class ProjectedHeuristicPrivatelyDifferentDetector implements
		PrivatelyDifferentStateDetectorInterface {

	@Override
	public boolean privatelyDifferent(SearchState s1, SearchState s2) {
		return s1.heuristic != s2.heuristic;
	}

}
