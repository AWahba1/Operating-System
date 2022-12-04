package operatingSystemProject;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            int timeSlice =2;
            Scheduler scheduler = new Scheduler(timeSlice);
            Admitter admitter = new Admitter(scheduler);

        admitter.createNewProcess("Program_3", 4); 
        admitter.createNewProcess("Program_1", 0);
        admitter.createNewProcess("Program_2", 1); 




            scheduler.run();
        } catch (FileNotFoundException error) {
            System.out.println(error);
        }
    }
}
