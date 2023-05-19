package view.gui;

import com.sun.javafx.scene.control.LabeledText;
import controller.lobby.Lobby;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import network.NetworkManagerInterface;
import network.Result;

import java.util.ArrayList;



public class LobbyViewController implements Initializable {

    @FXML
    private Text nameUser;
    @FXML
    private Label noFound;
    @FXML
    private ListView<String> listView;


    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        nameUser.setText(SceneController.username);

        try {
            Result<ArrayList<Lobby>> result = SceneController.networkManager.lobbyList().waitResult();
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
}
