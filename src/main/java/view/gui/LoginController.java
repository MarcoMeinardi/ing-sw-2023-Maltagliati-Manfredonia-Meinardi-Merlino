package view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class LoginController {

    @FXML
    private ToggleGroup Server;

    @FXML
    private Button btnPlay1;

    @FXML
    private TextField nameIP;

    @FXML
    private TextField namePlayer;

    @FXML
    private TextField namePort;

    @FXML
    public void btnPlayOnClick1(ActionEvent event) {
        btnPlay1.setDisable(true);
        String namePlayer = this.namePlayer.getText();
        String nameIP = this.nameIP.getText();
        String namePort = this.namePort.getText();



        /*new Thread(() -> {
            try {
                NetworkManager.getInstance().connect(new Server(nameIP, Integer.parseInt(namePort)));
                NetworkManager.getInstance().login(namePlayer);
                NetworkManager.getInstance().createLobby("Lobby");
                NetworkManager.getInstance().joinLobby("Lobby");
                NetworkManager.getInstance().startGame();
            } catch (Exception e) {
                e.printStackTrace();
            }*/

        SceneController.changeScene(btnPlay1.getScene(), "lobby.fxml");
    }

}