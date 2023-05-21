package view.gui;

import controller.lobby.Lobby;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import network.ClientStatus;
import network.Result;
import network.parameters.LobbyCreateInfo;

import java.io.IOException;

import static view.gui.LoginController.networkManager;

public class CreateLobbyController implements Initializable {
    private static final int WIDTH = 1140;
    private static final int HEIGHT = 760;
    private Stage stage;
    private Scene scene;
    @FXML
    private Text nameUser;
    @FXML
    private TextField nameLobby;

    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        nameUser.setText(LoginController.username);
    }


    public void createLobby(ActionEvent actionEvent) throws Exception {

        String lobbyName = nameLobby.getText();
        Result<Lobby> result = networkManager.lobbyCreate(new LobbyCreateInfo(lobbyName)).waitResult();

        if (result.isOk()) {
            LoginController.lobby = ((Result<Lobby>) result).unwrap();
            LoginController.state = ClientStatus.InLobby;
            System.out.println("Lobby created: " + LoginController.lobby.getName());
        } else {
            System.out.println("[ERROR] " + result.getException());
        }

        try {
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

}