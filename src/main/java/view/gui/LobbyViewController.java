package view.gui;

import cli.Utils;
import com.sun.javafx.scene.control.LabeledText;
import controller.lobby.Lobby;
import controller.lobby.LobbyController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import network.ClientStatus;
import network.NetworkManagerInterface;
import network.Result;
import network.parameters.LobbyCreateInfo;

import java.io.IOException;
import java.util.ArrayList;

import static view.gui.SceneController.networkManager;


public class LobbyViewController implements Initializable {

    private Stage stage;
    private Scene scene;
    @FXML
    private Text nameUser;

    @FXML
    private Button btnSelect;
    @FXML
    private static Text nameLobby;
    @FXML
    private Label noFound;
    @FXML
    private ListView<String> listView;



    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        nameUser.setText(SceneController.username);

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
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/createLobby.fxml"));
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

    public void createLobby(ActionEvent actionEvent) throws Exception {
        String lobbyName = LobbyViewController.nameLobby.getText();
        Result<Lobby> result = networkManager.lobbyCreate(new LobbyCreateInfo(lobbyName)).waitResult();
        if (result.isOk()) {
            Lobby lobby = ((Result<Lobby>) result).unwrap();
            System.out.println("Lobby created: " + lobby.getName());
        }

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/lobby.fxml"));
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
}
