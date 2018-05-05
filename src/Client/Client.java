package Client;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private BufferedReader in;
    private PrintWriter out;
    private String serverIP;
    private Socket serverSocket;

    private ArrayList<String> serverFiles;
    private ArrayList<String> localFiles;

    private File localDirectory;

    public Client(String localDirName, String serverIP){
        this.localDirectory = new File(localDirName);
        this.serverIP = serverIP;

        try {
            genLocalDirectory();
        }catch(IOException e){
            e.printStackTrace();
            System.err.println("Error with local directory.");
        }
        try {
            genServerDirectory();
        }catch(IOException e){
            e.printStackTrace();
            System.err.println("Error connecting to the server. Possible errors: \n" +
                               "1.You entered the wrong ip\n"+
                               "2.The Server isn't active.");
        }
    }

    //Writes a line of text to the socket 8080 at the ipNumber
    //@param text: The text that will be sent to the server
    //@param ipNumber: The ipNumber of the server
    public void write(String text, String ipNumber) throws IOException{
        in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        out = new PrintWriter(serverSocket.getOutputStream());

        out.println(text);
        out.flush();
    }

    //Reads in a line of text from the server
    public String read() throws IOException{
        return in.readLine();
    }

    /* Requests the server for it's shared folder directory
       and saves the name of the files locally
    */
    public void genServerDirectory() throws IOException{
        serverSocket = new Socket(serverIP, 8080);
        write("DIR", serverIP);
        String input;
        serverFiles = new ArrayList<>();
        while((input = read()) != null){
            serverFiles.add(input);
        }
        cleanUp();
    }

    /*Saves the name of the files in the local directory
     */
    public void genLocalDirectory() throws IOException{
        if(localDirectory.isDirectory()){
            File[] contents = localDirectory.listFiles();
            localFiles = new ArrayList<>();
            for(File current: contents){
                localFiles.add(current.getName());
            }
        }else{
            System.err.println("Given value does not exist or is not a directory. See line 58 of Client.java");
            System.exit(-1); //Terminates the program if there is an invalid directory
        }
    }

    /*
    Uploads a file to the server.
    @arg fileName: The name of the file.
     */
    public void send(String fileName) throws IOException{
            serverSocket = new Socket(serverIP, 8080);
            File file = new File(localDirectory.getAbsolutePath(), fileName);
            Scanner fileReader = new Scanner(file);

            //Writes the header of the request to the server
            String header = ("UPLOAD " + fileName);
            write(header, serverIP);

            //Reads a line of text from the file and sends that line to the server
            while(fileReader.hasNext()){
                String output = fileReader.nextLine();
                write(output, serverIP);
            }

            cleanUp();
            genServerDirectory();
    }

    /*
    This function will receive incoming data, such as a file and its text
    @param fileName is the filename of the incoming file
     */
    public void receive(String fileName)throws IOException{
        serverSocket = new Socket(serverIP,8080);
        File receivingFile = new File(localDirectory,fileName);
        receivingFile.createNewFile();

        write("DOWNLOAD " + fileName, serverIP);
        String tmp = "";
        tmp = in.readLine();
        if(tmp != null) {
            String lines[] = tmp.split("%n");
            BufferedWriter buffedWrite = new BufferedWriter(new FileWriter(receivingFile));
            for (int i = 0; i < lines.length; i++) {
                buffedWrite.write(lines[i] + "\n");
            }
            buffedWrite.close();
        }
        cleanUp();
        genLocalDirectory();
        genServerDirectory();

    }


    //getters
    public ArrayList<String> getLocalFiles() {
        return localFiles;
    }

    //getters
    public ArrayList<String> getServerFiles() {
        return serverFiles;
    }

    //Closes the streams and sockets
    private void cleanUp(){
        try {
            in.close();
            out.close();
            serverSocket.close();
        }catch(IOException e){
            System.err.println("You done goofed.");
            e.printStackTrace();
        }
    }
}
