package input;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import tree.Operator;
import tree.SearchState;
import tree.Variable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InputJSONReader {
	
	private ObjectMapper mapper = new ObjectMapper();
	
	private enum ReadingState {
			NONE, VARIABLES, OPERATORS, STATES, PLAN;
			
	}
	
	private ReadingState readingState = ReadingState.NONE;
	private SearchTraceInputInterface searchTraceProcessor;
	
	
	public Map<Integer,Variable> varMap = new HashMap<>();
	public Map<Integer,Operator> opMap = new HashMap<>();
	public Map<Integer,SearchState> stateMap = new HashMap<>();
	
	
	
	
	
	public void readJSONFileOffline(String filename, SearchTraceInputInterface searchTraceProcessor){
		this.searchTraceProcessor = searchTraceProcessor;
		BufferedReader reader;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = reader.readLine();
			while (line != null) {
//				System.out.println(line);
				Object readObj = readNextLine(line);
				
				if(readObj != null){
					if(readObj instanceof Variable){
						searchTraceProcessor.addVariable((Variable)readObj);
						
					}
					if(readObj instanceof Operator){
						searchTraceProcessor.addOperator((Operator)readObj);
						
					}
					if(readObj instanceof SearchState){
						searchTraceProcessor.addStateSequential((SearchState)readObj);
						
					}
				}
				// read next line
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		searchTraceProcessor.afterAllStatesProcessed();
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
					searchTraceProcessor.afterAllVariablesProcessed();
					readingState = ReadingState.NONE;
				}else{
					return var;
				}
			}
			
			if(readingState == ReadingState.OPERATORS){
				System.out.println("read op: "+line);
				
				Operator op = readOperator(line);
				
				if(op == null){
					searchTraceProcessor.afterAllOperatorsProcessed();
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
			System.out.println("error reading op: "+input+" - "+e.getMessage());
			
		} catch (JsonProcessingException e) {
			System.out.println("error reading op: "+input+" - "+e.getMessage());
		}
		
		return null;
	}

}
