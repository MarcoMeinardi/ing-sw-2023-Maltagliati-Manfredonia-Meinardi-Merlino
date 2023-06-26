package view.gui;

import controller.lobby.Lobby;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import network.ClientStatus;
import network.Result;
import network.ServerEvent;
import network.parameters.LobbyCreateInfo;

import java.io.IOException;
import java.util.Optional;

import static view.gui.LoginController.networkManager;

/**
 * CreateLobbyController is the class that manages the create lobby scene.
 **/

public class CreateLobbyController implements Initializable {
    private static final int WIDTH = 1140;
    private static final int HEIGHT = 760;
    private Stage stage;
    private Scene scene;
    @FXML
    private Text nameUser;
    @FXML
    private TextField nameLobby;
    @FXML
    private Label messageDisplay;
    @FXML
    private Button btnSelect;
    private Thread serverThread;
    private ClientStatus state;

    /**
     * dummy constructor
     *
    **/

    private CreateLobbyController() {
    }


    /**
     * Method initialize is used to initialize the create lobby scene.
     * Puts the username of the player in the text field and sets the close request of the stage.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if the location is not known.
     *
     * @param resources the resources used to localize the root object, or null if the root object was not localized.
     *
     * @author Ludovico
     * */
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        btnSelect.setDefaultButton(true);
        nameUser.setText(LoginController.username);
        state = ClientStatus.InLobbySearch;
        serverThread = new Thread(() -> {
            while (state != ClientStatus.Disconnected) {
                synchronized (networkManager) {
                    try {
                        while (!networkManager.hasEvent()) {
                            networkManager.wait();
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                handleEvent();
            }
        });
        serverThread.start();
    }


    /**
     * Method createLobby is used to create a lobby.
     * It sends a request to the server to create a lobby with the name inserted by the user.
     * If the lobby is created successfully, the user is redirected to the lobby scene.
     * If the lobby name is empty or already exists, the user is notified with a message.
     * If the server is not reachable, the user is notified with a message.
     *
     * @param actionEvent
     * the click of the create lobby button by the user.
     *
     * @throws Exception
     *
     * @author Ludovico
     * */

    public void createLobby(ActionEvent actionEvent) throws Exception {

        String lobbyName = nameLobby.getText();
        if(lobbyName.isEmpty()) {
            System.out.println("[ERROR] Lobby name is empty");
            messageDisplay.setText("Lobby name is empty");
            return;
        }

        Result<Lobby> result = networkManager.lobbyCreate(new LobbyCreateInfo(lobbyName)).waitResult();
        if (result.isOk()) {
            LoginController.lobby = ((Result<Lobby>) result).unwrap();
            LoginController.state = ClientStatus.InLobby;
            System.out.println("Lobby created: " + LoginController.lobby.getName());
        } else {
            messageDisplay.setText("Lobby name already exists");
            System.out.println("[ERROR] " + result.getException());
        }

        try {
            serverThread.interrupt();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Lobby.fxml"));
            stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(root, WIDTH, HEIGHT);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method is responsible for printing the last view of the program if the server stopped.
     * It interrupts the server thread,
     * loads the MessageReturnToLogin.fxml file using FXMLLoader,
     * sets the new scene to the stage, and displays the stage.
     *
     */

    private void returnToLoginMessage(){
        try {
            serverThread.interrupt();
            Parent newRoot = FXMLLoader.load(getClass().getResource("/fxml/MessageReturnToLogin.fxml"));
            stage = (Stage) (nameUser.getScene().getWindow());
            scene = new Scene(newRoot, WIDTH, HEIGHT);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * method that handles events received from the server.
     * It first checks if there is an event available, and if not, it returns.
     * If there is an event, it switches on the type of the event and performs the appropriate action.
     * - ServerDisconnect: notifies the players that the server has been disconnected sending them to a scene explaining the situation
     *
     * @author Ludovico
     */
    private void handleEvent() {
        Optional<ServerEvent> event = networkManager.getEvent();
        if (event.isEmpty()) {
            return; // No event
        }
        switch (event.get().getType()) {
            case ServerDisconnect -> {
                System.out.println("[WARNING] Server disconnected");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        returnToLoginMessage();
                    }
                });
            }
            case Join -> {
                System.out.println("[INFO] you joined the lobby");
            }
            default -> throw new RuntimeException("Unhandled event");
        }
    }


}
