package operatingSystemProject;

import java.util.LinkedList;
import java.util.Queue;

public class Mutex {
    private BinaryValue value;
    private String name;
    private ProcessesQueue blockedQueue;
    private int ownerID;
    private Scheduler scheduler;
    private Memory memory;

    public Mutex(Scheduler scheduler,String name) {
        blockedQueue = new ProcessesQueue();
        value = BinaryValue.One;
        ownerID=0;
        this.scheduler=scheduler;
        this.name=name;
        this.memory=this.scheduler.getMemory();
    }

    public void semWait(Process process) {
        if (this.value == BinaryValue.One) {
            this.ownerID = process.getId();
//            process.setState(State.Running);
            memory.setState(process,process.getId(), State.Running);
           
            this.setValue(BinaryValue.Zero);
        } else {
            /* block this process */
//            process.setState(State.Blocked);
        	memory.setState(process,process.getId(), State.Blocked);
            blockedQueue.getProcesses().add(process);
            this.scheduler.getGeneralBlockedQueue().add(process);

        }
    }

    void semSignal(Process process) throws Exception {
       // System.out.println("semsignal method"+this.ownerID);
        if (this.ownerID == process.getId()) {
           // System.out.println("semsignal accept"+this.ownerID);
            if (this.blockedQueue.getProcesses().isEmpty()){
               this.setValue(BinaryValue.One);
               this.setOwnerID(0);
            }
            else {

                Process unBlockedProcess = blockedQueue.getProcesses().poll();
                this.scheduler.getGeneralBlockedQueue().remove(unBlockedProcess);
                System.out.println("Process with id "+unBlockedProcess.getId()+" is now unblocked and entered ready queue");	
                this.ownerID = Integer.parseInt(unBlockedProcess.getId()+"");
                //System.out.println("ownerID "+ownerID);
//                unBlockedProcess.setState(State.Ready);
                //if (memory.isProcessExists(this.ownerID))
              //  System.out.println(memory);
                	memory.setState(unBlockedProcess,this.ownerID, State.Ready);
                	// System.out.println(memory);
                this.scheduler.getReadyQueue().add(unBlockedProcess);
                

            }
        }
        
    }

    public String getName() {
        return name;
    }

    public BinaryValue getValue() {
        return value;
    }

    public void setValue(BinaryValue value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcessesQueue getBlockedQueue() {
        return blockedQueue;
    }

    public void setBlockedQueue(ProcessesQueue blockedQueue) {
        this.blockedQueue = blockedQueue;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}