package view.gui;

import controller.lobby.Lobby;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;
import network.ClientStatus;
import network.NetworkManagerInterface;
import network.Result;
import network.Server;
import network.parameters.GameInfo;
import network.parameters.Login;

import java.io.IOException;
import java.util.Optional;

public class LoginController implements Initializable {
    private static final int WIDTH = 1140;
    private static final int HEIGHT = 760;
    private Stage stage;
    private Scene scene;
    @FXML
    private TextField namePlayer;
    @FXML
    private Label errorLabel;
    @FXML
    private RadioButton RMIButton, serverButton;
    @FXML
    private Button loginButton;
    @FXML
    private TextField selectedIp;
    @FXML
    private Pane pane;
    public static String username;
    public static NetworkManagerInterface networkManager;
    public static ClientStatus state;
    public static String ip;
    public static int port;
    public static Lobby lobby;
    public static GameInfo gameInfo;

    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        loginButton.setDefaultButton(true);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage = (Stage) pane.getScene().getWindow();
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent t) {
                        Platform.exit();
                        System.exit(0);
                    }
                });
            }
        });
    }
    public void switchToMainMenu(javafx.event.ActionEvent actionEvent) {

        try {
            stage = (Stage)(loginButton.getScene().getWindow());
            errorLabel.setText("");
            username = namePlayer.getText();

            //check if the input is valid
            if(username == null || username.equals("") || username.equals(" ")){
                errorLabel.setText("Invalid name!");
                return;
            } else if (username.length() > 8) {
                errorLabel.setText("max 8 letters in name!");
                return;
            }


            if (selectedIp.getText().equals("")) {
                errorLabel.setText("Insert an IP address!");
                return;
            }

            //connection to server
            if(RMIButton.isSelected()){
                networkManager = network.rmi.client.NetworkManager.getInstance();
                this.ip = selectedIp.getText();
                this.port = 8001;
            }
            else{
                networkManager = network.rpc.client.NetworkManager.getInstance();
                this.ip = selectedIp.getText();
                this.port = 8000;
            }
            if (!networkManager.isConnected()) {
                try{
                    networkManager.connect(new Server(this.ip, this.port));
                    state = ClientStatus.Idle;
                }catch (Exception e) {
                    errorLabel.setText("Connection failed");
                    System.out.println("[ERROR] " + e.getMessage());
                    state = ClientStatus.Disconnected;
                    return;
                }
            }

            //login
            try {
                Result result = networkManager.login(new Login(username)).waitResult();
                if (result.isOk()) {
                    if (result.unwrap().equals(Boolean.TRUE)) {
                        state = ClientStatus.InLobbySearch;
                    }
                    else{
                        gameInfo = (GameInfo)result.unwrap();
                        state = ClientStatus.InGame;
                        lobby = new Lobby(username, gameInfo.players());
                        switchToGame();
                        return;
                    }
                }
                else{
                    errorLabel.setText("Login failed");
                    System.out.println(result.getException());
                    return;
                }
            } catch (Exception e) {
                errorLabel.setText("Login failed");
                System.out.println("[ERROR] " + e.getMessage());
                return;
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

    public void switchToGame() throws IOException {
        Platform.runLater(() -> {
            try {
                Parent newRoot = FXMLLoader.load(getClass().getResource("/fxml/Game.fxml"));
                scene = new Scene(newRoot, WIDTH, HEIGHT);
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
