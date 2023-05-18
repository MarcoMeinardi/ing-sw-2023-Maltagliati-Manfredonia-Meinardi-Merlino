package view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneController {

    private Stage stage;
    private Scene scene;
    private Parent root;

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
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/lobby.fxml"));
            stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            int width = (int) Screen.getPrimary().getBounds().getWidth();
            int height = (int) Screen.getPrimary().getBounds().getHeight();
            scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
