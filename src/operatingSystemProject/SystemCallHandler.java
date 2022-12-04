package operatingSystemProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SystemCallHandler {

    private final Scanner sc = new Scanner(System.in);
    private final FileManager fileManager;
    private final Parser parser;
    private  Memory memory;

    public SystemCallHandler(Scheduler scheduler) {
        this.memory = scheduler.getMemory();
        this.fileManager = new FileManager();
        this.parser = new Parser();

    }

    public void print(String toBePrinted) {
        System.out.println(toBePrinted);
    }

    public String takeInput() {
        return sc.nextLine();
    }

    public void assignString(String variableName, String value, int ownerID) throws Exception {
        memory.assignString(variableName, value, ownerID);
    }

    public void assignInteger(String variableName, int value, int ownerID) throws Exception {
        memory.assignInteger(variableName, value, ownerID);
    }

    public int getIntegerVariable(String variableName, int ownerID) {
        return memory.getIntegerVariable(variableName, ownerID);
    }

    public String getStringVariable(String variableName, int ownerID) {
        return memory.getStringVariable(variableName, ownerID);
    }

    public boolean containsIntegerKey(String value, int ownerID) {
        return memory.containsIntegerKey(value, ownerID);
    }

    public boolean containsStringKey(String value, int ownerID) {
        return memory.containsStringKey(value, ownerID);
    }

    public void writeDataInFile(String fileName, String data) throws IOException {
        this.fileManager.writeDataInFile(fileName, data);
    }

    public String readDataFromFile(String fileName) throws IOException {
        return this.fileManager.readDataFromFile(fileName);
    }

    public String[] readFile(String fileName) throws FileNotFoundException {
        return this.parser.readFile(fileName);

    }

    public void writeProcessToDisk(String processStack, int ownerID) throws IOException {

        this.fileManager.writeProcessToDisk(processStack, "Program_" + ownerID + "_Stack");
    }

    public ArrayList<String> readProcessFromDisk(int ownerID) throws FileNotFoundException {

        return this.fileManager.readProcessFromDisk("Program_" + ownerID + "_Stack");
    }

    public Scanner getSc() {
        return sc;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public Parser getParser() {
        return parser;
    }

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }
}
