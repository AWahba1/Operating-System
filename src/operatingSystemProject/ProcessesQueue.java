package operatingSystemProject;

import java.util.LinkedList;
import java.util.Queue;

public class ProcessesQueue {
    private Queue<Process> processes;

    public ProcessesQueue() {
        this.processes = new LinkedList<Process>();
    }
    public StringBuilder displayQueue() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Process p : this.processes) {
            sb.append(p.getId() + ",");
        }
        sb.append("]");
        return sb;
    }


    public Queue<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(Queue<Process> processes) {
        this.processes = processes;
    }
}
