package operatingSystemProject;

import java.util.*;

public class Process {
    private PCB pcb;
    private String currentInstruction;


    public Process(int id, int start, int end) {
        this.pcb = new PCB(id, State.New, 0, start, end);
    }

    public Process(PCB pcb) {
        this.pcb = pcb;
    }

    

   
   
    public String getCurrentInstruction() {
		return currentInstruction;
	}

	public void setCurrentInstruction(String currentInstruction) {
		this.currentInstruction = currentInstruction;
	}

//	public int getProgramCounter() {
//        return this.pcb.getProgramCounter();
//    }
//
//    public void setProgramCounter(int programCounter) {
//        this.pcb.setProgramCounter(programCounter);
//    }
//
    public State getState() {
        return this.pcb.getState();
    }
//
//    public void setState(State state) {
//        this.pcb.setState(state);
//    }
//
//


    public PCB getPcb() {
        return pcb;
    }

    public void setPcb(PCB pcb) {
        this.pcb = pcb;
    }

    public int getId() {
        return this.pcb.getProcessID();
    }
//
//    public void setId(int id) {
//        this.pcb.setProcessID(id);
//    }


}
