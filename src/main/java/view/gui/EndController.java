package view.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import model.Score;
import model.ScoreBoard;

import java.net.URL;
import java.util.ResourceBundle;

public class EndController implements Initializable {
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.scoreBoard = GameViewController.gameData.getScoreBoard();
        this.username = GameViewController.gameData.getMe();
        showScoreBoard();
    }

    private void showScoreBoard() {
        int position = 1;
        String your_title = "Why is my life like this";
        for (Score score : scoreBoard) {
            System.out.format(" [%d] %s: %d points %n", position++, score.username(), score.score());
            if(position == 1){
                messageLabel1.setText(" [" + position++ + "] " + score.username() + ":" + score.score() +" points");
            }
            else if(position == 2){
                messageLabel2.setText(" [" + position++ + "] " + score.username() + ":" + score.score() +" points");
            }
            else if(position == 3){
                messageLabel3.setText(" [" + position++ + "] " + score.username() + ":" + score.score() +" points");
            }
            else if(position == 4){
                messageLabel4.setText(" [" + position++ + "] " + score.username() + ":" + score.score() +" points");
            }
            if (score.username().equals(username)) {
                your_title = score.title();
            }
            System.out.println("Your final grade: "+your_title);
            titleLabel.setText("Your final grade: "+your_title);
        }
    }
}
