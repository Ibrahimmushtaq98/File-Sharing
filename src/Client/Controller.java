package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.fxml.Initializable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML private Button downloadBtn;
    @FXML private Button uploadBtn;
    @FXML private Button chooseDir;
    @FXML private Button startConnect;
    @FXML private Button refresh;
    @FXML private TextField direOut;
    @FXML private TextField ipAddIN;
    @FXML private SplitPane splt;
    @FXML private TextArea txtDebug;

    @FXML private ListView<String> clientList;
    @FXML private ListView<String> serverList;

    private Stage stage;
    private Client client;
    private String host;
    private String path;


    public void setStage(Stage stage){
        this.stage = stage;
    }

    //Runs the code on start-up TODO initalize the Client class
    public Controller(){
    }

    //Start when fxml is done loading
    public void initialize(URL location, ResourceBundle resources){
        downloadBtn.disableProperty().setValue(true);
        uploadBtn.disableProperty().setValue(true);
        refresh.disableProperty().setValue(true);
    }

    //This function will download server data from the server list to client
    @FXML private void downloadAction(ActionEvent event){

        if(serverList.getSelectionModel().getSelectedItems().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setContentText("Please select a file from Server List");
            return;

        }

        String serverFileName = serverList.getSelectionModel().getSelectedItem();
        if(null != serverFileName){
            try{
                client.receive(serverFileName);
            }catch(IOException e){

            }

            clientList.getItems().clear();
            clientList.getItems().addAll(client.getLocalFiles());

            serverList.getSelectionModel().clearSelection();

        }


    }

    //This function will upload client data from the client list to server
    @FXML private void uploadAction(ActionEvent event){

        if(clientList.getSelectionModel().getSelectedItems().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setContentText("Please select a file from Client List");
            return;

        }

        String localFileName = clientList.getSelectionModel().getSelectedItem();
        if(null != localFileName){
            try {
                client.send(localFileName);
            }catch(IOException e){
                e.printStackTrace();
            }

            serverList.getItems().clear();
            serverList.getItems().addAll(client.getServerFiles());

            clientList.getSelectionModel().clearSelection();
        }
    }

    //This will choose the directory
    @FXML private void setChooseDir(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        path = directoryChooser.showDialog(stage).toString();

        direOut.setText(path);

    }

    //This function will start the Client Connection
    @FXML private void setStartConnect(){



        if(ipAddIN.getText().trim().equals("")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setContentText("Please enter a valid IP Address");
            alert.showAndWait();
            return;
        }

        client = new Client(path,host);
        client.getLocalFiles();
        client.getServerFiles();

        clientList.getItems().setAll(client.getLocalFiles());
        serverList.getItems().setAll(client.getServerFiles());

        downloadBtn.disableProperty().setValue(false);
        uploadBtn.disableProperty().setValue(false);
        refresh.disableProperty().setValue(false);


    }

    //This function will refresh the server and client list
    @FXML private void refreshList(){
        try {
            client.genServerDirectory();
            client.genLocalDirectory();

            clientList.getItems().clear();
            serverList.getItems().clear();

            clientList.getItems().setAll(client.getLocalFiles());
            serverList.getItems().setAll(client.getServerFiles());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}