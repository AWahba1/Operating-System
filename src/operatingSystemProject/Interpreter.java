package operatingSystemProject;

import operatingSystemProject.exception.SyntaxError;

import java.io.PrintWriter;
import java.util.Scanner;


public class Interpreter {
    private Scheduler scheduler;
    private Memory memory;
    private Scanner scanner = new Scanner(System.in);
    private InterpreterFunction interpreterFunction;
    public Interpreter(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.interpreterFunction=new InterpreterFunction(this.scheduler);
        this.memory=this.scheduler.getMemory();
    }

    public void executeInstruction(String nextInstruction, Process process) throws Exception {
        String[] instruction = nextInstruction.split(" ");


        switch (instruction[0]) {
            case "semWait":
                interpreterFunction.semWait(instruction[1], process);
                break;
            case "semSignal":
                interpreterFunction.semSignal(instruction[1], process);
                break;
            case "print":
                if (instruction.length == 3) {
                    instruction[1] = interpreterFunction.readFromFile(instruction[2], process);
                }
                interpreterFunction.print(instruction[1], process);
                break;
            case "printFromTo":
                interpreterFunction.printFromTo(instruction[1], instruction[2], process);
                break;
            case "assign":

                    interpreterFunction.assign(instruction[1], instruction[2], process);
              
                break;
            case "writeFile":
                interpreterFunction.writeToFile(instruction[1], instruction[2], process);
                break;
            case "readFile":
            {
            	String fileName = memory.getStringVariable(instruction[1], process.getId());
                interpreterFunction.readFromFile(fileName, process);
                break;
            }
               
            case "input": interpreterFunction.takeInput();
            	break;
            default: throw new SyntaxError("Syntax Error !");


        }
    }
}
