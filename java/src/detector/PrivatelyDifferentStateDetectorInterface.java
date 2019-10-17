package detector;

import tree.SearchState;

public interface PrivatelyDifferentStateDetectorInterface {
	
	public boolean privatelyDifferent(SearchState s1, SearchState s2);

}
