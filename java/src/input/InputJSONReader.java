package input;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import tree.Operator;
import tree.SearchState;
import tree.SearchTree;
import tree.Variable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InputJSONReader {
	
	private ObjectMapper mapper = new ObjectMapper();
	
	private enum ReadingState {
			NONE, VARIABLES, OPERATORS, STATES, PLAN;
			
	}
	
	ReadingState readingState = ReadingState.NONE;
	
	
	public Map<Integer,Variable> varMap = new HashMap<>();
	public Map<Integer,Operator> opMap = new HashMap<>();
	public Map<Integer,SearchState> stateMap = new HashMap<>();
	
	
	
	public void readJSONFileOffline(String filename, SearchTree tree){
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = reader.readLine();
			while (line != null) {
//				System.out.println(line);
				Object readObj = readNextLine(line);
				
				if(readObj != null){
					if(readObj instanceof Variable){
						tree.addVariable((Variable)readObj);
						
					}
					if(readObj instanceof Operator){
						tree.addOperator((Operator)readObj);
						
					}
					if(readObj instanceof SearchState){
						tree.addStateSequential((SearchState)readObj);
						
					}
				}
				// read next line
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tree.afterAllStatesProcessed();
	}
	
	
	public Object readNextLine(String line){
		
		if(readingState == ReadingState.NONE){
			if(line.contains("variables")){
				readingState = ReadingState.VARIABLES;
				System.out.println("read variables...");
			}
			
			if(line.contains("operators")){
				readingState = ReadingState.OPERATORS;
				System.out.println("read operators...");
			}
			
			if(line.contains("states")){
				readingState = ReadingState.STATES;
				System.out.println("read states...");
			}
			
			if(line.contains("plan")){
				readingState = ReadingState.PLAN;
				System.out.println("read plan...");
			}
		}else{
		
		
			if(readingState == ReadingState.VARIABLES){
				System.out.println("read var: "+line);
				
				Variable var = readVariable(line);
				
				if(var == null){
					readingState = ReadingState.NONE;
				}else{
					return var;
				}
			}
			
			if(readingState == ReadingState.OPERATORS){
				System.out.println("read op: "+line);
				
				Operator op = readOperator(line);
				
				if(op == null){
					readingState = ReadingState.NONE;
				}else{
					return op;
				}
			}
			
			if(readingState == ReadingState.STATES){
				System.out.println("read state: "+line);
				
				SearchState searchState = readSearchState(line);
				
				if(searchState == null){
					readingState = ReadingState.NONE;
				}else{
					return searchState;
				}
			}
			
			if(readingState == ReadingState.PLAN){
				//TODO: read plan
			}
		
		}
		
		return null;
		
	}
	
	public SearchState readSearchState(String input){
		try {
			return mapper.readValue(input, SearchState.class);
		} catch (JsonMappingException e) {
			System.out.println("error reading state: "+input);
		} catch (JsonProcessingException e) {
			System.out.println("error reading state: "+input);
		}
		
		return null;
	}
	
	public Variable readVariable(String input){
		try {
			return mapper.readValue(input, Variable.class);
		} catch (JsonMappingException e) {
			System.out.println("error reading var: "+input);
		} catch (JsonProcessingException e) {
			System.out.println("error reading var: "+input);
		}
		
		return null;
	}
	
	public Operator readOperator(String input){
		try {
			return mapper.readValue(input, Operator.class);
		} catch (JsonMappingException e) {
			System.out.println("error reading op: "+input);
		} catch (JsonProcessingException e) {
			System.out.println("error reading op: "+input);
		}
		
		return null;
	}

}
