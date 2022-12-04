package operatingSystemProject;

import java.util.Arrays;

public class MemoryWords {

    private final MemoryWord[] memoryWords;

    public MemoryWords() {
        memoryWords = new MemoryWord[40];
    }


    public int findStart(int ownerID) {
        int start = -1;
        //System.out.println(Arrays.toString(memoryWords)+"find");
        //System.out.println(ownerID + " owner idddd");
//        System.out.println(memoryWords[0].getValue() + " value");
//        System.out.println(memoryWords[3].getValue()  + " start");
        if (memoryWords[0] != null && Integer.parseInt(memoryWords[0].getValue() + "") == ownerID) {
            start = Integer.parseInt(memoryWords[3].getValue() + "");
        } else if (memoryWords[20] != null && Integer.parseInt((memoryWords[20].getValue()) + "") == ownerID) {
            start = Integer.parseInt(memoryWords[23].getValue() + "");

        }

        // System.out.println("Start before return "+start);
        return start;

    }


    public void put(String variableName, Object value, int ownerID) throws Exception {
//    	System.out.println("variable Name "+variableName);
//    	System.out.println("value "+value);
//    	System.out.println("ownerId "+ownerID);
//    	
//    	System.out.println(Arrays.toString(memoryWords));
        int start = findStart(ownerID);
        int end = Integer.parseInt(memoryWords[start + 4].getValue() + "");

        //System.out.println("start "+start);
        //System.out.println("end "+end);
        boolean flag = false;
        for (int i = end - 2; i <= end; i++) {
            if (memoryWords[i] != null && memoryWords[i].getName().equals(variableName)) {
                memoryWords[i].setValue(value);
                flag = true;
                break;
            } else if (memoryWords[i] == null) {
                memoryWords[i] = new MemoryWord(variableName, value);
                flag = true;
                break;
            }
        }

        if (!flag) {
            throw new Exception("Only 3 Variables are allowed");
        }
    }


    public boolean containsKey(String variableName, int ownerID) {
        int start = findStart(ownerID);
        int end = Integer.parseInt(memoryWords[start + 4].getValue() + "");
        for (int i = end - 2; i <= end; i++) {
            if (memoryWords[i] != null && memoryWords[i].getName().equals(variableName))
                return true;

        }
        return false;
    }

    public void remove(String variableName, int ownerID) {
        int start = findStart(ownerID);
        int end = Integer.parseInt(memoryWords[start + 4].getValue() + "");

        for (int i = end - 2; i <= end; i++) {
            if (memoryWords[i] != null && memoryWords[i].getName().equals(variableName))
                memoryWords[i] = null;
        }

    }

    public Object get(String variableName, int ownerID) {
        int start = findStart(ownerID);
        int end = Integer.parseInt(memoryWords[start + 4].getValue() + "");

        for (int i = end - 2; i <= end; i++) {
            if (memoryWords[i] != null && memoryWords[i].getName().equals(variableName))
                return memoryWords[i].getValue();

        }
        return null;
    }

    public String toString() {
        String res = "[";
        for (int i = 0; i < 20; i++) {
            MemoryWord memWord = memoryWords[i];
            res += memWord == null ? "null, " : memWord.toString() + ",";
        }
        res += "\n";
        for (int i = 20; i < memoryWords.length; i++) {
            MemoryWord memWord = memoryWords[i];
            res += memWord == null ? "null, " : memWord.toString() + ",";
        }


        res += "]";
        return res;
    }

    public MemoryWord[] getMemoryWords() {
        return memoryWords;
    }

}
