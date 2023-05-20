package view.gui;

import controller.lobby.Lobby;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import network.ClientStatus;
import network.Result;
import network.parameters.LobbyCreateInfo;

import java.io.IOException;
import java.util.ArrayList;

import static view.gui.LoginController.networkManager;


public class MainMenuController implements Initializable {

    private Stage stage;
    private Scene scene;
    @FXML
    private Text nameUser;
    @FXML
    private Label noFound;
    @FXML
    private ListView<String> listView;



    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        nameUser.setText(LoginController.username);

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


    //non dovrebbe essere qui
    public void switchToCreateLobby(javafx.event.ActionEvent actionEvent ) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/CreateLobby.fxml"));
            stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            int width = 1140;
            int height = 760;
            scene = new Scene(root, width, height);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void switchToLobby(javafx.event.ActionEvent actionEvent ) {
        //TODO prende il nome dalla lista
        String lobbyName = "prova";

        try {
            Result result = networkManager.lobbyCreate(new LobbyCreateInfo(lobbyName)).waitResult();
            if (result.isOk()) {
                LoginController.lobby = ((Result<Lobby>)result).unwrap();
                LoginController.state = ClientStatus.InLobby;
            } else {
                System.out.println("[ERROR] " + result.getException().orElse("Login failed"));
            }
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Lobby.fxml"));
            stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            int width = 1140;
            int height = 760;
            scene = new Scene(root, width, height);
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
