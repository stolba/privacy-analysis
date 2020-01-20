package input;

import tree.Operator;
import tree.SearchState;
import tree.Variable;

public interface SearchTraceInputInterface {
	
	public void addVariable(Variable var);
	
	public void addOperator(Operator op);
	
	public void addStateSequential(SearchState state);
	
	public void afterAllVariablesProcessed();
	
	public void afterAllOperatorsProcessed();
	
	public void afterAllStatesProcessed();

}
