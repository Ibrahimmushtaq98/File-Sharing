package Server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.io.File;

public class Server{
    private ServerSocket localSocket;
    private SharedState state;
    private File directory;

    /*
    Initalizes a Server
    @param port: The port that the server will listen at
    @param fileDirectory: The folder that will be shared across computers
    */
    public Server(int port, String fileDirectory)  {
        try {
            localSocket = new ServerSocket(port);
        }catch(IOException e){
            System.err.println("Error with socket!");
            e.printStackTrace();
        }

        try{
            directory = new File(fileDirectory);
            popMap(fileDirectory);
        }catch(IOException e){
            System.err.println("Error with Files!");
            e.printStackTrace();
        }
    }

    /*
     Populates the map with the name of all the files in the directory
     @param fileDirectory: the directory of the shared folder
     */
    private void popMap(String fileDirectory) throws IOException{
        state = new SharedState(fileDirectory);
        ClientConnectionHandler.setState(state);
    }

    //Listens to any incoming requests and creates a thread to handle the request
    public void listen() throws IOException{
        while(true){
            Socket client = localSocket.accept();
            ClientConnectionHandler handler = new ClientConnectionHandler(client, directory);
            Thread handlerThread = new Thread(handler);
            handlerThread.start();
        }
    }
}
