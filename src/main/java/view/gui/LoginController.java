package view.gui;

import controller.lobby.Lobby;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import network.parameters.GameInfo;
import network.parameters.Login;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * LoginController is the class that manages the login scene.
**/

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
    private RadioButton RMIButton;
    @FXML
    private Button loginButton;
    @FXML
    private TextField selectedIp;
    public static String username;
    public static NetworkManagerInterface networkManager;
    public static ClientStatus state;
    public static String ip;
    public static int port;
    public static Lobby lobby;
    public static GameInfo gameInfo;
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    /**
     * Method initialize is used to initialize the login scene.
     * Sets the default button to the login button and sets the close request of the stage.
     *
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     *
     * @author Ludovico
     */
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        Platform.runLater(() -> loginButton.setDefaultButton(true));
    }

    /**
     * Method called when the user presses the login button. It switches to the main menu scene.
     * It checks if the input is valid and if the connection to the server is successful.
     * If the login is successful, it switches to the main menu scene.
     * If the login is not successful, it prints an error message.
     * In case of reconnection of player that previously disconnected, it switches to the game scene
     * of the game they were playing, if it is still going, with the method switchToGame.
     *
     * @param actionEvent
     * The click of the login button.
     * */

    @FXML
    private void switchToMainMenu(javafx.event.ActionEvent actionEvent) {

        try {
            stage = (Stage)(loginButton.getScene().getWindow());
            errorLabel.setText("");
            username = namePlayer.getText();

            //check if the input is valid
            if(username == null || username.equals("") || username.equals(" ")){
                errorLabel.setText("Invalid name!");
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
                    logger.warning("Connection failed" + e + " " + e.getMessage());
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
                        lobby = gameInfo.lobby();
                        switchToGame();
                        return;
                    }
                }
                else{
                    errorLabel.setText("Username unavailable");
                    logger.info(result.getException().isPresent() ? result.getException().get().toString() : "Login failed");
                    return;
                }
            } catch (Exception e) {
                errorLabel.setText("Login failed");
                logger.warning("Login failed" + e + " " + e.getMessage());
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
            errorLabel.setText("Login failed");
            logger.severe(e + " " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Method switchToGame is used to switch to the game scene.
     * It is called when a player that previously disconnected reconnects to the game.
     * It switches to the game scene of the game they were playing, if it is still going.
     *
     * @throws IOException
     * @see IOException
     * @author Ludovico
     */

    private void switchToGame() throws IOException {
        Platform.runLater(() -> {
            try {
                Parent newRoot = FXMLLoader.load(getClass().getResource("/fxml/Game.fxml"));
                scene = new Scene(newRoot, WIDTH, HEIGHT);
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
            } catch (IOException e) {
                errorLabel.setText("Couldn't log you in ongoing game");
                logger.severe(e + " " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

}
