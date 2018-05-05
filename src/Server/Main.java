package Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args){

        //String mainDirectory = "/home/colinorian/TestDirectory";
        //String mainDirectory = "/home/ibrahim/Desktop/TestDirectory";

        String mainDirectory = ".";

        int port = 8080;
        Server server = new Server(port, mainDirectory);
        try{
            System.out.println(mainDirectory);
            server.listen();
        }catch(IOException e){
            System.out.print("Error in Listen() method");
            e.printStackTrace();
        }
    }

}
