package lp;

import java.util.HashSet;
import java.util.Set;

import tree.Operator;
import tree.SearchTree;
import analysis.OperatorSet;

public class LPPropertiecComputation {
	
	SearchTree tree;
	LPSolver solver;
	String LP = "";
	
	public LPPropertiecComputation(SearchTree tree){
		this.tree = tree;
		
		solver = new SolverCPLEX();
	}
	
	public void prepareLP(Set<OperatorSet> operatorSets){
		
		LP = "Minimize\n  obj: ";
		
		int opCount = 0;
		for(Operator op : tree.getAllOperators()){
			LP += getLPVar(op);
			if(opCount < tree.getAllOperators().size()-1) LP += " + ";
			++opCount;
		}
		
		LP += "\nSubject To\n";
		
		for(OperatorSet opSet : operatorSets){
			opCount = 0;
			for(Operator op : opSet){
				LP += getLPVar(op);
				if(opCount < opSet.size()-1) LP += " + ";
				++opCount;
			}
			
			LP += " = 1\n";
		}
		
		LP += "\nBinary\n";
		
		for(Operator op : tree.getAllOperators()){
			LP += " " + getLPVar(op) + "\n";
			
		}
		
		LP += "\nEnd\n";
		
	}
	
	public Set<String> computeLP(){
		LPSolution solution = solver.solveLP(LP);
		System.out.println("LP solution satus: " + solution.getSolutionStatus());
		System.out.println("LP solution value: " + solution.getObjctiveValue());
		
		Set<String> result = new HashSet<>();
		for(String var : solution.getAllVariables()){
			if(solution.getVariableValue(var) > 0){
				result.add(var);
			}
		}
		
		return result;
		
	}
	
	private String getLPVar(Operator op){
//		return "op"+op.hash;
		return op.opName.replace(" ", "").replace("-", "_");
	}

}
