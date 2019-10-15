package tree;

public class SearchState {
	public int agentID;
	public int senderID;
	public int stateID;
	public int parentID;
	public int iparentID;
	
	public int cost;
	public int heuristic;
	public int[] privateIDs;
	public int[] values;
	
	public String context;

}
