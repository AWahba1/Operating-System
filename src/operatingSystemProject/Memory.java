package operatingSystemProject;

import java.io.IOException;
import java.util.*;

public class Memory {

    private MemoryWords memoryWords;
    private Disk disk;
    private int leastRecentlyUsed = 0;

    public Memory(SystemCallHandler systemCallHandler) {
        this.memoryWords = new MemoryWords();
        this.disk = new Disk(systemCallHandler);
    }

    // newProcess came from Ready Queue
    // process ID came from pending Queue
    public PCB swap(Process newProcess, int processID, String[] instructions) throws Exception {
        int newProcessID = newProcess == null ? processID : newProcess.getId();
        int programCounter = 0;
        State state = newProcess == null ? State.Ready : newProcess.getState();
        int start = 0;
        int end = instructions.length + start + 4 + 3; // processID came from pen

        if (memoryWords.getMemoryWords()[0] == null && newProcess == null) { // beginning with 0  in ready [1]
            putPCBInMemory(processID, programCounter, state, start, end);
            putInstructionsInMemory(instructions, start, end);
            leastRecentlyUsed = 20;
        } else if (memoryWords.getMemoryWords()[20] == null && newProcess == null) { // beginning with 20  in ready [2] in run [1]
            start = 20;
            end = instructions.length + start + 4 + 3;
            putPCBInMemory(processID, programCounter, state, start, end);
            putInstructionsInMemory(instructions, start, end);
            leastRecentlyUsed = 0;
        } else if (memoryWords.getMemoryWords()[0] == null ||memoryWords.getMemoryWords()[20] == null ) {
            // this the case of nullify
            ArrayList<String> diskWords = this.getDisk().readProcessFromDisk(newProcessID); // we get the running from Disk
            start=memoryWords.getMemoryWords()[0]==null?0:20;
            int i = start;
            int instructionCount = 0;
            for (String word : diskWords) {
                if (word.equals("null"))// nulls are ignored and not included in the end here
                    break;
                String wordString = (word.split(",")[0]).replace("{", "");
                String wordOriginal = (word.split(",")[1]).replace("}", "");
                if (wordString.split(" ")[0].equalsIgnoreCase("instruction"))
                    instructionCount++;

                if (wordString.equals("start"))
                    wordOriginal = start + "";


                memoryWords.getMemoryWords()[i] = new MemoryWord(wordString, wordOriginal);
                i++;
            }
            end = 5 + 3 + instructionCount + start - 1;
            
            leastRecentlyUsed=20-start;
            memoryWords.getMemoryWords()[start + 4].setValue(end);
            programCounter = Integer.parseInt((String) memoryWords.getMemoryWords()[start + 2].getValue());

        } else {
            start = this.leastRecentlyUsed; // old process start
            this.leastRecentlyUsed = 20 - start;
            end = (int) memoryWords.getMemoryWords()[start + 4].getValue(); // old process
            putProcessInDisk(start, end); // put old process in disk
            if (newProcess == null) { // pending process enters memory
                end = instructions.length + start + 4 + 3;
                putPCBInMemory(processID, programCounter, state, start, end);
                putInstructionsInMemory(instructions, start, end);

            } else { // process wants to run
                ArrayList<String> diskWords = this.getDisk().readProcessFromDisk(newProcessID); // we get the running from Disk
                // System.out.println("disk words "+diskWords);
                int i = start;

                int instructionCount = 0;
                for (String word : diskWords) {
//                	System.out.println(word);
                    if (word.equals("null"))// nulls are ignored and not included in the end here
                        break;

                    String wordString = (word.split(",")[0]).replace("{", "");
                    String wordOriginal = (word.split(",")[1]).replace("}", "");

                    if (wordString.split(" ")[0].equalsIgnoreCase("instruction"))
                        instructionCount++;
//                    System.out.println("Word string "+wordString);
//                    System.out.println("Word Original "+wordOriginal);
                    if (wordString.equals("start"))
                        wordOriginal = start + "";


                    memoryWords.getMemoryWords()[i] = new MemoryWord(wordString, wordOriginal);
                    i++;
                }
//                System.out.println("Instruction Count "+instructionCount);
                end = 5 + 3 + instructionCount + start - 1;
                //System.out.println("end "+end);
                memoryWords.getMemoryWords()[start + 4].setValue(end);


                programCounter = Integer.parseInt((String) memoryWords.getMemoryWords()[start + 2].getValue());

            }
        }
        System.out.println("Program with id " + newProcessID + " entered memory @ location: " + start);

        System.out.println("\nMEMORY:");
        System.out.println(this);
        return new PCB(newProcessID, state, programCounter, start, end);
    }

    public void nullify(Process process) throws Exception {
        int start = getMemoryWords().findStart(process.getId());
        if (start == -1) {
            throw new Exception("@nullify process with ID " + process.getId() + " not in memory");
        }
        int end = Integer.parseInt(getMemoryWords().getMemoryWords()[start + 4].getValue() + "");
        for (; start <= end; start++) {
            memoryWords.getMemoryWords()[start] = null;
        }
    }

    public String getNextInstruction(Process process) throws Exception {
        // pass id & get start then get programCounter from PCB in mem

        int start = memoryWords.findStart(process.getId());
        if (start == -1) {
            throw new Exception("Process with ID " + process.getId() + " not in Memory !");
        }
        int programCounter = Integer.parseInt(memoryWords.getMemoryWords()[start + 2].getValue() + "") + start + 5; // [PCB:0->4,instructions]
        // System.out.println("pc "+programCounter);
        int end = Integer.parseInt(memoryWords.getMemoryWords()[start + 4].getValue() + "");
        String instruction = "exit";
        while (memoryWords.getMemoryWords()[end] == null || !memoryWords.getMemoryWords()[end].getName().contains("instruction"))
            end--;

        if (programCounter <= end) {

            instruction = (String) memoryWords.getMemoryWords()[programCounter].getValue();

            //System.out.println("inst "+instruction);
            setProgramCounter(process.getId(), Integer.parseInt((memoryWords.getMemoryWords()[start + 2].getValue() + "")) + 1);

        }
        // process.setProgramCounter(programCounter + 1);
        return instruction;
    }

    public Boolean isProcessExists(int processID) {
        return memoryWords.findStart(processID) != -1;
    }

    public boolean isFinished(int processID) {

        int start = memoryWords.findStart(processID);
        int programCounter = (int) (memoryWords.getMemoryWords()[start + 2].getValue());
        int end = (int) (memoryWords.getMemoryWords()[start + 4].getValue());
        while (memoryWords.getMemoryWords()[end] == null || !memoryWords.getMemoryWords()[end].getName().contains("instruction"))
            end--;

        return (programCounter + start + 5) == (end + 1);
    }


    private void putProcessInDisk(int start, int end) throws IOException {
        MemoryWord[] diskInfo = new MemoryWord[20];
        int j = 0;
        int oldProcessID = Integer.parseInt(memoryWords.getMemoryWords()[start].getValue() + "");
        System.out.println("Process with ID " + oldProcessID + " is swapped from Memory to Disk");
        for (int i = start; i <= end; i++) {
            diskInfo[j] = memoryWords.getMemoryWords()[i];
            memoryWords.getMemoryWords()[i] = null;
            j++;
        }
        disk.writeProcessInDisk(diskInfo, oldProcessID);
    }

    private void putInstructionsInMemory(String[] instructions, int start, int end) {
        int j = 0;
        for (int i = start + 5; i <= (end - 3) && j < instructions.length; i++) {
            memoryWords.getMemoryWords()[i] = new MemoryWord("instruction " + j, instructions[j]);
            j++;
        }
    }

    private void putPCBInMemory(int processID, int programCounter, State state, int start, int end) {
        memoryWords.getMemoryWords()[start] = new MemoryWord("processID", processID);
        memoryWords.getMemoryWords()[start + 1] = new MemoryWord("state", state);
        memoryWords.getMemoryWords()[start + 2] = new MemoryWord("pc", programCounter);
        memoryWords.getMemoryWords()[start + 3] = new MemoryWord("start", start);
        memoryWords.getMemoryWords()[start + 4] = new MemoryWord("end", end);
    }


    public void assignString(String variableName, String value, int ownerID) throws Exception {

        memoryWords.put(variableName, value, ownerID);


    }

    public void assignInteger(String variableName, int value, int ownerID) throws Exception {
        memoryWords.put(variableName, value, ownerID);


    }

    public int getIntegerVariable(String variableName, int ownerID) {
        if (memoryWords.containsKey(variableName, ownerID))
            return Integer.parseInt(memoryWords.get(variableName, ownerID) + "");
        return Integer.MIN_VALUE;
    }

    public String getStringVariable(String variableName, int ownerID) {
        if (memoryWords.containsKey(variableName, ownerID))
            return "" + memoryWords.get(variableName, ownerID);
        return null;
    }

    public boolean containsIntegerKey(String value, int ownerID) {
        return memoryWords.containsKey(value, ownerID);
    }

    public boolean containsStringKey(String value, int ownerID) {
        return memoryWords.containsKey(value, ownerID);
    }

    public String toString() {
        return memoryWords.toString();
    }

    public MemoryWords getMemoryWords() {
        return memoryWords;
    }

    public void setMemoryWords(MemoryWords memoryWords) {
        this.memoryWords = memoryWords;
    }

    public Disk getDisk() {
        return disk;
    }

    public void setDisk(Disk disk) {
        this.disk = disk;
    }

    public int getLeastRecentlyUsed() {
        return leastRecentlyUsed;
    }

    public void setLeastRecentlyUsed(int leastRecentlyUsed) {
        this.leastRecentlyUsed = leastRecentlyUsed;
    }

    public int getProgramCounterFromMemory(int processID) throws Exception {
        int start = memoryWords.findStart(processID);
        if (start == -1) {
            throw new Exception("Process with ID " + processID + " not in Memory !");
        }
        return (int) (memoryWords.getMemoryWords()[start + 2].getValue());
    }

    public void setProgramCounter(int processID, int programCounter) {
        int start = memoryWords.findStart(processID);
        memoryWords.getMemoryWords()[start + 2].setValue(programCounter);
        ;
    }

    public State getState(Process process, int processID) throws Exception {
        int start = memoryWords.findStart(processID);
        if (start == -1) {
            return process.getPcb().getState();
            //throw new Exception("@getState process with ID " + processID + " not in memory");
        }
        //System.out.println(((State) memoryWords.getMemoryWords()[start + 1].getValue()).equals(process.getState()) + "," + processID);
        memoryWords.getMemoryWords()[start + 1].setValue(process.getPcb().getState());

        return (State) (memoryWords.getMemoryWords()[start + 1].getValue());
    }

    public void setState(Process process, int processID, State state) {
        //System.out.println(processID+" "+state);
        int start = memoryWords.findStart(processID);
        //System.out.println("start "+start);
        if (start != -1)
            memoryWords.getMemoryWords()[start + 1].setValue(state);
        process.getPcb().setState(state);
        ;
    }


    public int getId(int processID) {
        int start = memoryWords.findStart(processID);
        return (int) (memoryWords.getMemoryWords()[start].getValue());
    }

    public void setId(int processID, int id) {
        int start = memoryWords.findStart(processID);
        memoryWords.getMemoryWords()[start].setValue(id);
        ;
    }


}
