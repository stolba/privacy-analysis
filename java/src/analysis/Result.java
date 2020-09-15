package analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumMap;

import javax.naming.directory.InvalidAttributesException;


public class Result {
	final String header;
	final String traceDir;
	final int agentID;
	final String heuristic;
	final String whichStates;
	final String whenSending;
	
	boolean valid = false;
	
	EnumMap<EnumPrivacyProperty,Integer> groundTruth = new EnumMap<>(EnumPrivacyProperty.class);
	EnumMap<EnumPrivacyProperty,Integer> resultValues = new EnumMap<>(EnumPrivacyProperty.class);
	
	
	/**
	 * 
	 * @param args:
	 * 0 - header
	 * 1 - trace directory
	 * 2 - agentID
	 * 3 - heuristic (proj/ma/uniform-cp/uniform-cp-sec)
	 * 4 - which states (states-all/states-relevant/states-macro)
	 * 5 - when sending (send-extract/send-create)
	 * @throws InvalidAttributesException 
	 */
	public Result(String[] args) throws InvalidAttributesException{
		if(args.length < 6){
			throw new InvalidAttributesException(args.toString());
		}else{
			header = args[0];
			traceDir = args[1];
			agentID = Integer.parseInt(args[2]);
			heuristic = args[3];
			whichStates = args[4];
			whenSending = args[5];
		}
	}


	public void increaseGroundTruthCount(EnumPrivacyProperty prop) {
		Integer originalValue = groundTruth.containsKey(prop) ? groundTruth.get(prop) : 0;
		groundTruth.put(prop,originalValue+1);
	}
	
	public void setAllValid(){
		valid = true;
	}
	
	public void writePropertyCount(EnumPrivacyProperty prop,int count) {
		resultValues.put(prop,count);
	}
	
	
	public String toString(){
		String result = header + "," +traceDir + "," +agentID + "," +heuristic + "," +whichStates + "," +whenSending + "," + valid + ",";
		
		int i = 0;
		for(EnumPrivacyProperty prop : EnumPrivacyProperty.values()){
			int resultValue = resultValues.containsKey(prop) ? resultValues.get(prop) : 0;
			int groundTruthValue = groundTruth.containsKey(prop) ? groundTruth.get(prop) : 0;
			
			result = result +  prop + "," + resultValue + "," + groundTruthValue;
			
			if(i<EnumPrivacyProperty.values().length){
				result+=",";
			}
			i++;
		}
		
		return result;
	}
	
	protected void writeFile(String outFileName){
		System.out.println("writing "+outFileName+"...");
	    File outFile = new File(outFileName);
	    
	    if (!outFile.exists()) {
	        try {
	            outFile.createNewFile();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    PrintWriter writer = null;

	    try {
	        writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile,true)));
	        writer.println(toString());
	        
	        writer.flush();
	        writer.close();
	    } catch (IOException ex){
	      // report
	    } finally {
	       try {writer.close();} catch (Exception ex) {}
	    }
	}
	
	

}
