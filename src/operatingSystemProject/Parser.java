package operatingSystemProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {

	
	public String[] readFile(String fileName) throws FileNotFoundException {

        File file = new File(System.getProperty("user.dir")+"/src/operatingSystemProject/programs/" + fileName + ".txt");
        Scanner scanner = new Scanner(file);
        String instruction = "";
        while (scanner.hasNextLine()) {	
        	String st=scanner.nextLine();
        	String[] str=st.split(" ");
        	if (str[0].equals("assign"))
        	{
        		if (str[2].equals("readFile"))
        		{
        			instruction+=str[2]+" "+str[3]+" (belonging to assign) ,";
        			
        		}
        		else if (str[2].equals("input"))
        		{
        			instruction+="input"+" (belonging to assign) ,";
        			
        		}
        		
        	}
        	
        		instruction += st + ",";
        	
            
        }
        String[] instructions = instruction.split(",");
        return instructions;
    }
}
