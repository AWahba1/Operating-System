package operatingSystemProject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Disk {
    private ArrayList<String> fileNames;
    private final SystemCallHandler systemCallHandler;

    public Disk(SystemCallHandler systemCallHandler) {
        this.systemCallHandler = systemCallHandler;
        this.fileNames = new ArrayList<String>();
    }

    public void writeProcessInDisk(MemoryWord[] arr, int ownerID) throws IOException {
        fileNames.add("Program_" + ownerID + "_Stack");
        this.systemCallHandler.writeProcessToDisk(Arrays.toString(arr), ownerID);
    }

    public ArrayList<String> readProcessFromDisk(int ownerID) throws FileNotFoundException {
        return this.systemCallHandler.readProcessFromDisk(ownerID);
    }


    public ArrayList<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(ArrayList<String> fileNames) {
        this.fileNames = fileNames;
    }

    public SystemCallHandler getSystemCallHandler() {
        return systemCallHandler;
    }
}
