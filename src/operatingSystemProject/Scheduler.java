package operatingSystemProject;

import operatingSystemProject.exception.SyntaxError;
import operatingSystemProject.exception.TypeMisMatch;
import operatingSystemProject.exception.VariableNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.Queue;


public class Scheduler {
    private final ProcessesQueue readyQueue;
    private final int timeSlice;
    private final Mutex userInputResource;
    private final Mutex userOutputResource;
    private final Mutex fileResource;
    private int currClockCycle;
    private final HashMap<Integer, Queue<String>> pendingProcesses;//<time,list of fileName>
    private final ProcessesQueue generalBlockedQueue;
    private final Interpreter interpreter;
    private final FileManager fileManger;
    private Process currentProcess;
    private int currentTimeCount = 0;
    private SystemCallHandler systemCallHandler;
    private final Memory memory;
    private final HashSet<String> processIDs;

    public Scheduler(int timeSlice) throws Exception {
        this.systemCallHandler = new SystemCallHandler(this);
        this.memory = new Memory(systemCallHandler);
        systemCallHandler.setMemory(memory);
        processIDs = new HashSet<>();
        this.readyQueue = new ProcessesQueue();
        this.fileManger = new FileManager();
        this.timeSlice = timeSlice;
        this.pendingProcesses = new HashMap<Integer, Queue<String>>();
        this.userInputResource = new Mutex(this, "userInputResource");
        this.userOutputResource = new Mutex(this, "userOutputResource");
        this.fileResource = new Mutex(this, "fileResource");

        generalBlockedQueue = new ProcessesQueue();
        this.interpreter = new Interpreter(this);
        this.currClockCycle = -1;
    }

    public Memory getMemory() {
        return memory;
    }

    public int createProcessID(int processID) {
        String x = "" + processID;
        while (processIDs.contains(x)) {
            x += processID;
        }
        int a = Integer.parseInt(x);
        processIDs.add(x);
        return a;
    }

    public Process createNewProcess(String fileName) throws Exception {
        String[] list = fileName.split("_");
        String[] instructions = this.getSystemCallHandler().readFile(fileName);// this is the parsed version
        //swap() unloads if no free space + return start
        //inside swap, print id of process swapped in disk
        // LRU by removing from 0 then 20 alternatively ... @0 process is added 1st and hence should be removed 1st

        //nullify 20 words starting from start returned by swap
        // fill in order PCB ...  Code (Unparsed) ... 3 null variables initially
        //end=start+instructions.length+4+3 with respect lel 0
        // process created with id and no PCB
        PCB pcb = this.memory.swap(null, createProcessID(Integer.parseInt(list[1])), instructions);


        return new Process(pcb);
    }

    public void removePendingProcesses() throws Exception {

        if (this.pendingProcesses.containsKey(this.currClockCycle)) {

            System.out.println("@ Clock Cycle: " + this.currClockCycle);
            Queue<String> list = pendingProcesses.get(this.currClockCycle); // list of filenames
            for (String fileName : list) {
                Process process = createNewProcess(fileName);
//                process.setState(State.Ready); // PCB is already created, change it in memory
                memory.setState(process, process.getId(), State.Ready);
                this.readyQueue.getProcesses().add(process);
                System.out.println("Process with id " + process.getId() + " admitted to ready queue");
            }
            System.out.println("Ready Queue: " + this.readyQueue.displayQueue());
            this.pendingProcesses.remove(this.currClockCycle);
            System.out.println("---------------------");
        }
    }

    public void run() throws Exception {

        while (true) { // OS loop
            this.currClockCycle++;
            this.removePendingProcesses();
            boolean enterInnerLoop = false;

            while (!readyQueue.getProcesses().isEmpty()) { // Ready loop


                enterInnerLoop = true;
                this.currentProcess = readyQueue.getProcesses().remove();


//                this.currentProcess.setState(State.Running); // change in PCB inside mem
                memory.setState(currentProcess, currentProcess.getId(), State.Running);
                for (int timeCounter = 0; timeCounter < this.timeSlice; timeCounter++) { // Round Robin loop

                    if (!memory.isProcessExists(currentProcess.getId())) {
                        this.currentProcess = new Process(memory.swap(currentProcess, currentProcess.getId(), new String[1]));
                    }
                    memory.setState(currentProcess, currentProcess.getId(), State.Running);
                    String currentInstruction = memory.getNextInstruction(currentProcess);
                    currentProcess.setCurrentInstruction(currentInstruction);
                    this.currentTimeCount = timeCounter;

                    if (currentInstruction.equals("exit") || memory.getState(currentProcess, currentProcess.getId()).equals(State.Completed)) {
//                        currentProcess.setState(State.Completed); // change in PCB inside mem
                        memory.setState(currentProcess, currentProcess.getId(), State.Completed);
                        memory.nullify(currentProcess);
                        printProgress(null); // process completed
                        break;
                    } else {

                        try {
                            int flag = printProgress(currentInstruction);
                            // A Process  breaks whenever it gets Blocked immediately
                            if (flag == 1) {// means got blocked
                                break;
                            } else if (flag == 2) {  // means process finished last Instruction
                                break;
                            }

                        } catch (FileNotFoundException error) {
                            System.out.println("File not Found Exception Process with id " + currentProcess.getId() + " will terminate !");
//                            currentProcess.setState(State.Completed);// change in PCB inside mem
                            memory.setState(currentProcess, currentProcess.getId(), State.Completed);
                            fileResource.semSignal(currentProcess);
                            userOutputResource.semSignal(currentProcess);
                            userInputResource.semSignal(currentProcess);
                            printProgress(null);
                            this.currClockCycle++;
                            this.removePendingProcesses();
                            break;

                        } catch (VariableNotFoundException error) {
                            System.out.println("Variable not Found Exception Process with id " + currentProcess.getId() + " will terminate !");
//                            currentProcess.setState(State.Completed);// change in PCB inside mem
                            memory.setState(currentProcess, currentProcess.getId(), State.Completed);
                            fileResource.semSignal(currentProcess);
                            userOutputResource.semSignal(currentProcess);
                            userInputResource.semSignal(currentProcess);
                            printProgress(null);
                            break;
                        } catch (SyntaxError error) {
                            System.out.println("Syntax Error in Process with id " + currentProcess.getId() + " will terminate !");
//                            currentProcess.setState(State.Completed);// change in PCB inside mem
                            memory.setState(currentProcess, currentProcess.getId(), State.Completed);
                            fileResource.semSignal(currentProcess);
                            userOutputResource.semSignal(currentProcess);
                            userInputResource.semSignal(currentProcess);
                            printProgress(null);
                            break;
                        } catch (TypeMisMatch error) {
                            System.out.println("Type mismatch in Process with id " + currentProcess.getId() + " will terminate !");
//                            currentProcess.setState(State.Completed);// change in PCB inside mem
                            memory.setState(currentProcess, currentProcess.getId(), State.Completed);
                            fileResource.semSignal(currentProcess);
                            userOutputResource.semSignal(currentProcess);
                            userInputResource.semSignal(currentProcess);
                            printProgress(null);

                            break;
                        }

                    }
                }

                //check state using PCB
                if (!memory.getState(currentProcess, currentProcess.getId()).equals(State.Completed) && !memory.getState(currentProcess, currentProcess.getId()).equals(State.Blocked)) {
//                    currentProcess.setState(State.Ready);// change in PCB inside mem
                    memory.setState(currentProcess, currentProcess.getId(), State.Ready);
                    readyQueue.getProcesses().add(currentProcess);

                    System.out.println("@ Clock Cycle: " + this.currClockCycle);
                    System.out.println("Process with id " + currentProcess.getId() + " Preempted to ready queue");
                    System.out.println("Ready Queue: " + this.readyQueue.displayQueue());
                    System.out.println("---------------------");
                }


            }

            if (!readyQueue.getProcesses().isEmpty() && enterInnerLoop)
                System.out.println("All programs finished executing.");

        }


    }

    public int printProgress(String currentInstruction) throws Exception {
        // USE PCB HEREE


        int flag = 0;
        System.out.println("@ Clock Cycle: " + this.currClockCycle);
        System.out.println("Ready Queue: " + this.readyQueue.displayQueue());
        System.out.println("General Blocked Queue : " + this.generalBlockedQueue.displayQueue()
                + " - " + userInputResource.getName() + " : " + userInputResource.getBlockedQueue().displayQueue() + " Owner ID: " + (this.userInputResource.getOwnerID() == 0 ? "N/A" : this.userInputResource.getOwnerID())
                + " - " + userOutputResource.getName() + " : " + userOutputResource.getBlockedQueue().displayQueue() + " Owner ID: " + (this.userOutputResource.getOwnerID() == 0 ? "N/A" : this.userOutputResource.getOwnerID())
                + " - " + fileResource.getName() + " : " + fileResource.getBlockedQueue().displayQueue() + " Owner ID: " + (this.fileResource.getOwnerID() == 0 ? "N/A" : this.fileResource.getOwnerID()));
        System.out.println("Process with id " + this.currentProcess.getId() + " is now " + memory.getState(currentProcess, this.currentProcess.getId()) + " @ time count : " + this.currentTimeCount);
        if (memory.getState(currentProcess, currentProcess.getId()).equals(State.Running)) {
            System.out.println("Executing: " + this.currentProcess.getCurrentInstruction());
            interpreter.executeInstruction(currentInstruction, currentProcess);
            //System.out.println(this.memory);
            //System.out.println(memory.isFinished(currentProcess.getId()));
            if (memory.isFinished(currentProcess.getId())) { // process finished
//                currentProcess.setState(State.Completed);
                //System.out.println("Entered hereeeeeeeeeee");
                memory.setState(currentProcess, currentProcess.getId(), State.Completed);
                System.out.println("Process with id " + this.currentProcess.getId() + " is now " + memory.getState(currentProcess, currentProcess.getId()));
                memory.nullify(currentProcess);

                flag = 2;
            } else if (memory.getState(currentProcess, currentProcess.getId()).equals(State.Blocked)) {
                System.out.println("Process with id " + this.currentProcess.getId() + " is now " + memory.getState(currentProcess, currentProcess.getId()) + " @ time count : " + this.currentTimeCount);
                flag = 1;
            }

        }
        //print memory here
        System.out.println("\nMemory:");
        System.out.println(this.memory);
        System.out.println("-----------------------------------------------------------------");
        if (currentInstruction != null) {
            this.currClockCycle++;
            this.removePendingProcesses();

        }
        return flag;

    }

    public Queue<Process> getReadyQueue() {
        return readyQueue.getProcesses();
    }

    public int getTimeSlice() {
        return timeSlice;
    }

    public FileManager getFileManger() {
        return fileManger;
    }

    public Mutex getUserInputResource() {
        return userInputResource;
    }

    public Mutex getUserOutputResource() {
        return userOutputResource;
    }

    public Mutex getFileResource() {
        return fileResource;
    }

    public Interpreter getParser() {
        return interpreter;
    }

    public Queue<Process> getGeneralBlockedQueue() {
        return generalBlockedQueue.getProcesses();
    }

    public int getCurrClockCycle() {
        return currClockCycle;
    }


    public HashMap<Integer, Queue<String>> getPendingProcesses() {
        return pendingProcesses;
    }

    public SystemCallHandler getSystemCallHandler() {
        return systemCallHandler;
    }

    public void setSystemCallHandler(SystemCallHandler systemCallHandler) {
        this.systemCallHandler = systemCallHandler;
    }
}