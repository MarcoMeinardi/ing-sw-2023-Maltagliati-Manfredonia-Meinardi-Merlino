package view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SceneController {

    private Stage stage;
    private Scene scene;
    @FXML
    private TextField namePort;
    @FXML
    private TextField nameIP;
    @FXML
    private TextField namePlayer;

    @FXML
    private Label errorLabel;

    @FXML
    private Label errorLabel2;

    public static String username;

    public void switchToLogin(ActionEvent event) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void switchToLobby(javafx.event.ActionEvent actionEvent) {

        try {
            errorLabel.setText("");
            errorLabel2.setText("");
            username = namePlayer.getText();
            String port = namePort.getText();
            String ip = nameIP.getText();
            if(username == null || username.equals("") ){
                errorLabel.setText("Invalid name!");
                return;
            } else if (username.length() > 8) {
                errorLabel2.setText("max 8 letters in name!");
                return;
            }
            if(port == null || port.equals("") ){
                errorLabel.setText("Invalid port!");
                return;
            }
            if(ip == null || ip.equals("") ){
                errorLabel.setText("Invalid ip!");
                return;
            }
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
