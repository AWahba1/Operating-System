package operatingSystemProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManager {


    public FileManager() {
    }

    


    public void writeDataInFile(String fileName, String data) throws IOException {
        File newFile = new File(System.getProperty("user.dir")+"/src/operatingSystemProject/files/" + fileName + ".txt");

        newFile.createNewFile();
        FileWriter myWriter = new FileWriter(System.getProperty("user.dir")+"/src/operatingSystemProject/files/" + fileName + ".txt");
        myWriter.write(data); 
        myWriter.close();
    }

    public String readDataFromFile(String fileName) throws IOException {
    	
        File file = new File(System.getProperty("user.dir")+"/src/operatingSystemProject/files/" + fileName + ".txt");
        Scanner s = new Scanner(file);
        String instruction = "";
        while (s.hasNextLine()) {
            instruction += s.nextLine()+"\n";
        }
        
       
        return instruction;
    }
    
    
    public void writeProcessToDisk(String processStack,String fileName) throws IOException {
    	
        File newFile = new File(System.getProperty("user.dir")+"/src/operatingSystemProject/disk/" + fileName + ".txt");
        newFile.createNewFile();
        FileWriter myWriter = new FileWriter(System.getProperty("user.dir")+"/src/operatingSystemProject/disk/" + fileName + ".txt");
        myWriter.write(processStack); 
        myWriter.close();
    }
    
    public ArrayList<String> readProcessFromDisk(String  fileName) throws FileNotFoundException{
        //System.out.println(fileName);
        File file = new File(System.getProperty("user.dir")+"/src/operatingSystemProject/disk/" + fileName + ".txt");
        Scanner s = new Scanner(file);
        ArrayList<String> instructions =new ArrayList<String>();
        String [] words=null;
        if (s.hasNextLine())
        {
        	words=s.nextLine().split(", ");
        	for (int i=0; i<words.length;i++)
        	{
        		String word=words[i].replace("[", "");
		       	 word=word.replace("]", "");
		        // System.out.println(word);
		       	 instructions.add(word);
        	}
        	
        }
        
        return instructions;
    	
    }
    
    
    
    
}