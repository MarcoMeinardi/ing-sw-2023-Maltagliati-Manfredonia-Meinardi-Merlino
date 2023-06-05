package view.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Score;
import model.ScoreBoard;


import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;

/**
 * This class is the controller for the End.fxml file.
 * It is responsible for the end screen of the game.
 *
 */

public class EndController implements Initializable {
    private static final int WIDTH = 1140;
    private static final int HEIGHT = 760;
    private static ScoreBoard scoreBoard;
    private static String username;
    @FXML
    private Label messageLabel1;
    @FXML
    private Label messageLabel2;
    @FXML
    private Label messageLabel3;
    @FXML
    private Label messageLabel4;
    @FXML
    private Label titleLabel;
    @FXML
    private Button exitButton;
    @FXML
    private Pane shelfPane;
    private Stage stage;
    private Scene scene;

    /**
     * This method is called when the End.fxml file is loaded.
     * It is responsible for setting the scoreBoard and username by calling
     * the showScoreBoard method.
     *
     * @param url
     *
     * @param resourceBundle
     *
     * @author Ludovico
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.scoreBoard = GameViewController.gameData.getScoreBoard();
        this.username = GameViewController.gameData.getMe();
        showScoreBoard();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage = (Stage) shelfPane.getScene().getWindow();
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
     * Method that shows the game results and the final grade of the player.
     *
     * @autor Ludovico
     */

    private void showScoreBoard() {
        int position = 1;
        String your_title = "Why is my life like this";
        for (Score score : scoreBoard) {
            if(position == 1){
                messageLabel1.setText(" [" + position + "] " + score.username() + ":" + score.score() +" points");
            }
            else if(position == 2){
                messageLabel2.setText(" [" + position + "] " + score.username() + ":" + score.score() +" points");
            }
            else if(position == 3){
                messageLabel3.setText(" [" + position + "] " + score.username() + ":" + score.score() +" points");
            }
            else if(position == 4){
                messageLabel4.setText(" [" + position + "] " + score.username() + ":" + score.score() +" points");
            }
            if (score.username().equals(username)) {
                your_title = score.title();
            }
            System.out.println("Your final grade: "+your_title);
            titleLabel.setText("Your final grade: "+your_title);
            position++;
        }
    }

    /**
     * Method that is called when the exit button is clicked.
     * It is responsible for loading the MainMenu.fxml file.
     *
     * @param actionEvent
     * Click of the exit button by the player.
     *
     * @throws IOException
     */

    @FXML
    private void goToLobbies(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
        stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root, WIDTH, HEIGHT);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }


}
