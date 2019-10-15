package tree;

import java.util.Map;

public class Operator {
	public int agentID;
	public int ownerID;
	public String opName;
	public boolean isPrivate;
	public int opID;
	public int cost;
	
	public Map<String,Integer> pre;
	public Map<String,Integer> eff;
}
