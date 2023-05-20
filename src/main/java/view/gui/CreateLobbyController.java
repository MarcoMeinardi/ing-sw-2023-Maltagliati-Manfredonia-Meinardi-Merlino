package view.gui;

import controller.lobby.Lobby;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import network.Result;
import network.parameters.LobbyCreateInfo;

import java.io.IOException;

import static view.gui.LoginController.networkManager;

public class CreateLobbyController implements Initializable {

    private Stage stage;
    private Scene scene;
    @FXML
    private Text nameUser;
    @FXML
    private static Text nameLobby;

    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        nameUser.setText(LoginController.username);
    }


    public void createLobby(ActionEvent actionEvent) throws Exception {

        String lobbyName = nameLobby.getText();
        Result<Lobby> result = networkManager.lobbyCreate(new LobbyCreateInfo(lobbyName)).waitResult();
        if (result.isOk()) {
            Lobby lobby = ((Result<Lobby>) result).unwrap();
            System.out.println("Lobby created: " + lobby.getName());
        }

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/something.fxml"));
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
