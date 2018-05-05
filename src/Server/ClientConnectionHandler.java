package Server;

import java.io.*;
import java.net.*;
import java.io.File;
import java.util.*;

//Handles multiple client requests concurrently
public class ClientConnectionHandler implements Runnable{
    private BufferedReader in;
    private PrintWriter out;
    private Socket client;
    private File sharedFolders;

    private static SharedState data;

    public static void setState(SharedState state){
        data = state;
    }

    /*
    Stores the client's socket into the class and gives access to the SharedState
    @socket: The client's socket
    @state: The Shared State that contains data shared between threads
    @sharedFolders: Holds the parent directory path
    */
    public ClientConnectionHandler(Socket socket,File sharedFolders){
        this.client = socket;
        this.sharedFolders = sharedFolders;
    }

    //Reads in a line of text. Prevents programing errors (i.e read() instead of readLine())
    private String read() throws IOException{
        return in.readLine();
    }

    //Writes a line of text and flushes the stream.
    private void write(String lineOfText) throws IOException{
        out.print(lineOfText);
        out.flush();
    }

    /*
     Handles the different commands from the client
     @lineOfText: The header of the request. Will contain the command
                  and arguments for the command
     @state: The Shared State that contains data shared between threads
    */
    private void commandHandler(String lineOfText){
        StringTokenizer tokenizer = new StringTokenizer(lineOfText, " ");
        String args;

        String command = tokenizer.nextToken();

        if(command.equals("DIR")){
            directory();
        }else if(command.equals("UPLOAD")){
            args = tokenizer.nextToken();
            recieve(args);

        }else if(command.equals("DOWNLOAD")){
            String fileName = tokenizer.nextToken();
            try{
                send(fileName);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    /*
       This function will receive user "UPLOAD" from Client
       @fileName: Holds the receiving filename
     */

    private void directory(){
        Set<String> files = data.getFiles();

        Iterator<String> iterator = files.iterator();

        while(iterator.hasNext()){
            try{
                String output = iterator.next() + "\n";

                write(output);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    //Creates a file and reads in the contents from the client
    //It then saves the contents locally to the file
    //@parm fileName: The name of the file
    private void recieve(String fileName){
        if(!data.isContain(fileName)){
            try{
                InputStream inStream = client.getInputStream();
                File file = new File(sharedFolders,fileName);
                file.createNewFile();
                PrintWriter writer = new PrintWriter(file);
                String fileContents;

                while((fileContents = in.readLine()) != null){
                    writer.println(fileContents);
                    writer.flush();
                }
                writer.close();
            }catch(IOException e) {
                e.printStackTrace();
            }
            data.genMap();
        }else{
            try{

            InputStream inStream = client.getInputStream();
            File file = new File(sharedFolders,fileName);
            PrintWriter writer = new PrintWriter(file);
            String fileContents;

            while((fileContents = in.readLine()) != null){
                writer.println(fileContents);
                writer.flush();
            }
            writer.close();
            }catch(IOException e) {
                e.printStackTrace();
            }

        }
    }

    //This function will send filename along with the content to client
    //@param fileName is the filename of the sending file
    private boolean send(String fileName) throws IOException{
        if(data.isWriteable(fileName)){
            data.put(fileName, false);

            File requestedFile = new File(sharedFolders, fileName);

            Scanner fileReader = new Scanner(requestedFile);
            String tmp = "";
            while(fileReader.hasNext()){
                tmp = fileReader.nextLine();
                write(tmp + "%n");
            }
            data.put(fileName, true);
            return true;
        }else{
            return false;
        }
    }

    //initialized by the runnable to be used by the thread
    public void run(){
        try {
            this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.out = new PrintWriter(client.getOutputStream());
            commandHandler(read());
        }catch(IOException e){
            System.err.println("IOError. Closing socket with client.");
            e.printStackTrace();
        }finally {
            try {
                this.in.close();
                this.out.close();
                this.client.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
