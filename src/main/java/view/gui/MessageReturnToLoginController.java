package view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * class controller for the scene shown in case the server is stopped
 *
 */
public class MessageReturnToLoginController {

    private static final int WIDTH = 1140;
    private static final int HEIGHT = 760;
    private Stage stage;
    private Scene scene;
    @FXML
    private Label messageLabel;

    /**
     * Method that is called when the button to return to login is clicked
     *
     * @param actionEvent event of the button to return to login clicked
     *
     * @author Ludovico
     */
    @FXML
    private void backToLogin(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(root, WIDTH, HEIGHT);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Error while loading the login page");
            e.printStackTrace();
        }
    }
}
