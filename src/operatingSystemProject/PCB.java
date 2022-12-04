package operatingSystemProject;

public class PCB {
    private MemoryWord processID; // 0
    private MemoryWord state;// 1
    private MemoryWord programCounter; // 2
    private MemoryWord start;// 3
    private MemoryWord end; // 4


    public PCB(int processID, State state, int pc, int start, int end) {
        this.processID = new MemoryWord("processID", processID);
        this.state = new MemoryWord("state", state);
        this.programCounter = new MemoryWord("pc", pc);
        this.start = new MemoryWord("start", start);
        this.end = new MemoryWord("end", end);
    }

    public String toString() {
        return processID + " , " + state + " , " + programCounter + " , " +start+" , "+end;
    }

    public int getProcessID() {
        return Integer.parseInt(this.processID.getValue()+"");
    }

    public void setProcessID(int processID) {
        this.processID.setValue(processID);
    }

    public State getState() {
        return (State) (this.state.getValue());
    }

    public void setState(State state) {
        this.state.setValue(state);
    }


    public int getStart() {
        return (int) (this.start.getValue());
    }

    public void setStart(int start) {
        this.start.setValue(start);
    }

    public int getProgramCounter() {
        return (int) (this.programCounter.getValue());
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter.setValue(programCounter);
    }

    public int getEnd() {
        return (int) (this.end.getValue());
    }

    public void setEnd(int end) {
        this.end.setValue(end);
    }


}
