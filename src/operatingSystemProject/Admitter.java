
package operatingSystemProject;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Admitter {
    private FileManager fileManger;
    private Scheduler scheduler;


    private HashSet<String> processIDs;


    public Admitter(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.fileManger = this.scheduler.getFileManger();
        processIDs = new HashSet<>();

    }


    //adjusts pendingProcesses hashmap 
    public void createNewProcess(String fileName, int timeCreated) throws FileNotFoundException {


        if (this.scheduler.getPendingProcesses().containsKey(timeCreated)) {
            this.scheduler.getPendingProcesses().get(timeCreated).add(fileName);
        } else {
            Queue<String> queue = new LinkedList<>();
            queue.add(fileName);
            this.scheduler.getPendingProcesses().put(timeCreated, queue);
        }

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


}
