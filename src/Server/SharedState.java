package Server;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

public class SharedState {
    private volatile Map<String, Boolean> filesStatus;
    private String fileDirectory;

    public SharedState(String fileDirectory){
        filesStatus = new ConcurrentHashMap<>();
        this.fileDirectory = fileDirectory;
        genMap();
    }

    //Populates the map with the name of all the files in the directory
    public void genMap(){
        filesStatus.clear();
        File directory = new File(this.fileDirectory);
        if(directory.isDirectory()){
            File[] contents = directory.listFiles();
            for(File current: contents){
                put(current.getName(), true);
            }
        }else{
            System.err.println("Given value does not exist or is not a directory. See line 22 of SharedState.java");
            System.exit(-1); //Terminates the program if there is an invalid directory
        }
    }

    public void put(String fileName, boolean isWriteable){
        filesStatus.put(fileName, isWriteable);
    }

    //Returns boolean variable of status of file
    public boolean isWriteable(String fileName){
        if(filesStatus.containsKey(fileName)){
            return(filesStatus.get(fileName));
        }
        return true;
    }

    //Returns if the file does exist
    public boolean isContain(String fileName){
        if(filesStatus.containsKey(fileName)){
            return true;
        }
        return false;
    }

    public Set<String> getFiles(){
        return filesStatus.keySet();
    }

}
