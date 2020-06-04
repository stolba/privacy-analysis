package lp;

import java.io.File;
import java.io.IOException;


public class SolverLPSolve extends LPSolver {
	

	@Override
	public LPSolution solveLP(String LP) {
		writeAndSolveLP(LP);
		return readSolution();
	}
	
	private void writeAndSolveLP(String LP){
		String LPfile = "problem.lp";
		writeFile(LP, LPfile);
		
		try {
            String cmd = "./lp_solve-runner";// + LPfile;// + " > " + problem + ".sol";
            System.out.println("RUN: " + cmd);
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            //builder.redirectOutput(new File("lp_solve.out"));
            Process pr = builder.start();
            

            pr.waitFor();
            System.out.println(pr.getErrorStream().read());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	private LPSolution readSolution(){
		String sol = readFileAsString(new File("./problem.sol"));
		System.out.println("SOLUTION:\n"+sol);
		
		while(sol.contains("  ")){
			sol = sol.replace("  ", " ");
		}
		
		String[] lines = sol.split("\n");
		String ov = lines[1].split(" ")[4];
		
		LPSolution lpsol = new LPSolution();
		lpsol.setObjectiveValue(Float.parseFloat(ov));
		
		for(int l=4;l<lines.length;l++){
			String var = lines[l].split(" ")[0];
			float val = Float.parseFloat(lines[l].split(" ")[1]);
			lpsol.setVariableValue(var, val);
		}
		
		return lpsol;
	}

	@Override
	public Solver isSolver() {
		return Solver.LPSOLVE;
	}

}
