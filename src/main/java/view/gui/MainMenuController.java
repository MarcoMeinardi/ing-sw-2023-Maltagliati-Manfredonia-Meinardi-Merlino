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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import network.ClientStatus;
import network.Result;

import java.io.IOException;
import java.util.ArrayList;

import static view.gui.LoginController.networkManager;

/**
 * MainMenuController is the class that manages the main menu scene.
 **/

public class MainMenuController implements Initializable {
    private static final int WIDTH = 1140;
    private static final int HEIGHT = 760;
    private Stage stage;
    private Scene scene;
    @FXML
    private Text nameUser;
    @FXML
    private Label noFound;
    @FXML
    private ListView<String> listView;
    @FXML
    private Pane pane;

    /**
     * Dummy constructor
     *
     */

    public void MainMenuController() {
    }

    /**
     * Method initialize is used to initialize the main menu scene.
     * Sets the name of the user on the top left of the scene and sets the close request of the stage.
     * It also calls the method askNetForLobbies to get the list of the lobbies present at the moment of the connection.
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
        nameUser.setText(LoginController.username);
        askNetForLobbies();
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

    /**
     * Method refreshLobbies is used to refresh the list of the lobbies present at the
     * moment of the click of the linked button.
     * Calls the method askNetForLobbies to get the list of the lobbies.
     *
     * @param actionEvent
     * the click of the button refresh lobbies by the user
     *
     * @throws IOException
     *
     * @author Ludovico
     */

    public void refreshLobbies(javafx.event.ActionEvent actionEvent){
        listView.getItems().clear();
        askNetForLobbies();
    }

    /**
     * Method askNetForLobbies is used to get the list of the lobbies present at the moment of the connection.
     *
     * @author Ludovico
     */

    public void askNetForLobbies(){
        try {
            Result<ArrayList<Lobby>> result = networkManager.lobbyList().waitResult();
            if (result.isOk()) {
                ArrayList<Lobby> lobbies = ((Result<ArrayList<Lobby>>) result).unwrap();
                if (lobbies.isEmpty()) {
                    noFound.setText("No lobbies found");
                } else {
                    for (Lobby lobby : ((Result<ArrayList<Lobby>>) result).unwrap()) {
                        listView.getItems().add(lobby.getName());
                    }
                }
            } else {
                System.out.println("[ERROR] " + result.getException());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method switchToCreateLobby is used to switch to the create lobby scene.
     * It is called when the user clicks on the create lobby button.
     *
     * @param actionEvent
     * the click of the button create lobby by the user
     *
     * @author Ludovico
     */

    public void switchToCreateLobby(javafx.event.ActionEvent actionEvent ) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/CreateLobby.fxml"));
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
     * Method joinLobby is used to join a lobby.
     * It is called when the user clicks on the join lobby button after selecting a lobby from the list.
     * It checks if the user has selected a lobby and if it is not the case it prints an error message.
     *
     * @param actionEvent
     * the click of the button join lobby by the user
     *
     * @author Ludovico
     */

    public void joinLobby(javafx.event.ActionEvent actionEvent) {

        String lobbyName = listView.getSelectionModel().getSelectedItem();
        if(lobbyName == null) {
            System.out.println("[ERROR] No lobby selected");
            noFound.setText("No lobby selected");
            return;
        }

        try {
            Result 	result = networkManager.lobbyJoin(lobbyName).waitResult();
            if (result.isOk()) {
                LoginController.lobby = ((Result<Lobby>)result).unwrap();
                LoginController.state = ClientStatus.InLobby;
                System.out.println(LoginController.username + " joined: " + LoginController.lobby.getName());
            } else {
                noFound.setText("Login failed");
                System.out.println("[ERROR] " + result.getException().orElse("Login failed"));
            }
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Lobby.fxml"));
            stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(root, WIDTH, HEIGHT);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

    }

}
