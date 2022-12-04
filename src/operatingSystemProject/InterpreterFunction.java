package operatingSystemProject;

import operatingSystemProject.exception.TypeMisMatch;
import operatingSystemProject.exception.VariableNotFoundException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class InterpreterFunction {

    private Scheduler scheduler;
    private String inputBuffer; 
    private String fileBuffer;
    private SystemCallHandler systemCallHandler;


    public InterpreterFunction(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.systemCallHandler=this.scheduler.getSystemCallHandler();
    }

    public String readFromFile(String fileName, Process process) throws IOException {
        return fileBuffer=this.systemCallHandler.readDataFromFile(fileName);

    }

    public void writeToFile(String fileNameVariable, String variableName, Process process) throws IOException, VariableNotFoundException {
        String stringValue = systemCallHandler.getStringVariable(variableName,process.getId());;
        
        String fileName = systemCallHandler.getStringVariable(fileNameVariable,process.getId());

        
        if (stringValue == null) {
            int intValue = systemCallHandler.getIntegerVariable(variableName,process.getId());
            if (intValue == Integer.MIN_VALUE)
                throw new VariableNotFoundException("Variable Doesn't Exists");
            else {
                if (fileName == null) {
                    fileName = systemCallHandler.getIntegerVariable(fileNameVariable,process.getId()) + "";
                }
                this.systemCallHandler.writeDataInFile(fileName, intValue + "");
            }
        } else {
            this.systemCallHandler.writeDataInFile(fileName, stringValue);
        }
    }

    public void assign(String variableName, String value, Process process) throws Exception {

    		
    	if (value.equals("readFile"))
    		systemCallHandler.assignString(variableName,fileBuffer,process.getId());
    		
    	else if (value.equals("input"))
    	{
    		try {
                int intValue = Integer.parseInt(inputBuffer);
                systemCallHandler.assignInteger(variableName,intValue,process.getId());
                
            } catch (NumberFormatException e) {
            	 systemCallHandler.assignString(variableName,inputBuffer,process.getId());
               
            }
    	}
    	//assign x y
    	else 
    	{	
    		
    		
    		if (systemCallHandler.containsIntegerKey(value, process.getId()))
    		{	
    			
    			int yTemp=systemCallHandler.getIntegerVariable(value, process.getId());
    			systemCallHandler.assignInteger(variableName,yTemp, process.getId());
    			
    		}
    		
    		else if (systemCallHandler.containsStringKey(value, process.getId()))
    		{	
    			
    			String yTemp=systemCallHandler.getStringVariable(value, process.getId());
    			
    			systemCallHandler.assignString(variableName,yTemp, process.getId());
    		}
    		else
    		{	
    			systemCallHandler.assignString(variableName, value, process.getId());
    			
    			
    		}
    		
    	}
        


    }


    public void printFromTo(String startVariableName, String endVariableName, Process process) throws Exception {
    	
        int start = systemCallHandler.getIntegerVariable(startVariableName, process.getId());
        
        int end = systemCallHandler.getIntegerVariable(endVariableName, process.getId());
        
        System.out.println("print from to "+start+" "+end);
//        if (systemCallHandler.getStringVariable(startVariableName, process.getId()) != null || systemCallHandler.getStringVariable(endVariableName, process.getId()) != null) {
//            throw new TypeMisMatch("");
//        }
        if (start == Integer.MIN_VALUE) {
            throw new VariableNotFoundException("Start Variable Doesn't Exist");
        } else if (end == Integer.MIN_VALUE) {
            throw new VariableNotFoundException("End Variable Doesn't Exist");

        } else {
            for (; start <= end; start++)
            	systemCallHandler.print(start + " ");
            systemCallHandler.print("");
        }


    }

    public void print(String variableName, Process process) throws Exception {
    	
        String stringValue = systemCallHandler.getStringVariable(variableName, process.getId());
        if (stringValue == null) {
        	
            int intValue = systemCallHandler.getIntegerVariable(variableName, process.getId());
            if (intValue == Integer.MIN_VALUE)
                throw new VariableNotFoundException("Variable Doesn't Exists");
            else {
            	systemCallHandler.print(""+intValue);
            }
        } else {
        	systemCallHandler.print(stringValue);
        }


    }

    public void semSignal(String instructionResource, Process process) throws Exception {
        switch (instructionResource) {
            case "userInput":
                scheduler.getUserInputResource().semSignal(process);
                break;
            case "userOutput":
                scheduler.getUserOutputResource().semSignal(process);
                break;
            case "file":
                scheduler.getFileResource().semSignal(process);
                break;
        }
    }

    public void semWait(String instructionResource, Process process) {
        switch (instructionResource) {
            case "userInput":
                scheduler.getUserInputResource().semWait(process);
                break;
            case "userOutput":
                scheduler.getUserOutputResource().semWait(process);
                break;
            case "file":
                scheduler.getFileResource().semWait(process);
                break;
        }
    }
    
    public void takeInput()
    {
    	systemCallHandler.print("Please enter a value : "); 
        inputBuffer = systemCallHandler.takeInput(); 
    }
    

}
