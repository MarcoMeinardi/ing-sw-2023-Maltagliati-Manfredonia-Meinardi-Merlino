package view.gui;

import controller.lobby.Lobby;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import network.ClientStatus;
import network.NetworkManagerInterface;
import network.Result;
import network.Server;
import network.parameters.Login;

import java.io.IOException;

public class LoginController {
    private static final int WIDTH = 1140;
    private static final int HEIGHT = 760;
    private Stage stage;
    private Scene scene;
    @FXML
    private TextField namePort;
    @FXML
    private TextField nameIP;
    @FXML
    private TextField namePlayer;
    @FXML
    private Label errorLabel;
    @FXML
    private Label errorLabel2;
    @FXML
    private RadioButton RMIButton, serverButton;
    @FXML
    private Button btnPlay1;

    public static String username;
    public static NetworkManagerInterface networkManager;
    public static ClientStatus state;
    public static String ip;
    public static int port;
    public static Lobby lobby;

    public void switchToMainMenu(javafx.event.ActionEvent actionEvent) {

        try {
            errorLabel.setText("");
            errorLabel2.setText("");
            username = namePlayer.getText();
            String port = namePort.getText();
            String ip = nameIP.getText();

            //check if the input is valid
            if(username == null || username.equals("") ){
                errorLabel.setText("Invalid name!");
                return;
            } else if (username.length() > 8) {
                errorLabel2.setText("max 8 letters in name!");
                return;
            }
            if(port == null || port.equals("") ){
                errorLabel.setText("Invalid port!");
                return;
            }
            if(ip == null || ip.equals("") ){
                errorLabel.setText("Invalid ip!");
                return;
            }

            //connection to server
            this.ip = ip;
            this.port = Integer.parseInt(port);
            if(RMIButton.isSelected()){
                networkManager = network.rmi.client.NetworkManager.getInstance();
            }
            else{
                networkManager = network.rpc.client.NetworkManager.getInstance();
            }
            try{
                networkManager.connect(new Server(this.ip, this.port));
                state = ClientStatus.Idle;
            }catch (Exception e) {
                //TODO mettere messaggio frontend
                System.out.println("[ERROR] " + e.getMessage());
                 state = ClientStatus.Disconnected;
            }

            //login
            try {
                Result result = networkManager.login(new Login(username)).waitResult();
                if (result.isOk()) {
                    if (result.unwrap().equals(Boolean.TRUE)) {
                        System.out.println("Login successful");
                        state = ClientStatus.InLobbySearch;
                    }
                    else{
                        //TODO mettere messaggio frontend e gestire caso di una continuazione di partita
                        System.out.println("[ERROR] " + result.getException().orElse("Login failed"));
                    }
                }
            } catch (Exception e) {
                //TODO mettere messaggio frontend
                System.out.println("[ERROR] " + e.getMessage());
            }

            //Creation of scene
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
            stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(root, WIDTH, HEIGHT);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
