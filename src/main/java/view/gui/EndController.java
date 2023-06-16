package view.gui;

import javafx.collections.ObservableList;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Cockade;
import model.Score;
import model.ScoreBoard;


import java.io.IOException;
import java.net.URL;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private ListView cockadesList1;
    @FXML
    private ListView cockadesList2;
    @FXML
    private ListView cockadesList3;
    @FXML
    private ListView cockadesList4;
    @FXML
    private Button exitButton;
    @FXML
    private Pane shelfPane;
    @FXML
    private ImageView cockadeImage1;
    @FXML
    private ImageView cockadeImage2;
    @FXML
    private ImageView cockadeImage3;
    @FXML
    private ImageView cockadeImage4;
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
    }

    /**
     * Method that shows the game results and the final grade of the player.
     *
     * @author Ludovico
     */

    private void showScoreBoard() {
        int position = 1;
        String your_title = "Why is my life like this";
        ArrayList<Cockade> playerCockades = new ArrayList<>();
        for (Score score : scoreBoard) {
            if(position == 1){
                messageLabel1.setText(" [" + position + "] " + score.username() + ": " + score.score() +" points");
                playerCockades = scoreBoard.getCockades(score.username());
                for(Cockade cockade : playerCockades){
                    cockadesList1.getItems().add(cockade.name() + " giving points: "+ cockade.points());
                }
            }
            else if(position == 2){
                messageLabel2.setText(" [" + position + "] " + score.username() + ": " + score.score() +" points");
                playerCockades = scoreBoard.getCockades(score.username());
                for(Cockade cockade : playerCockades){
                    cockadesList2.getItems().add(cockade.name() + " giving points: "+ cockade.points());
                }
            }
            else if(position == 3){
                messageLabel3.setText(" [" + position + "] " + score.username() + ": " + score.score() +" points");
                playerCockades = scoreBoard.getCockades(score.username());
                for(Cockade cockade : playerCockades){
                    cockadesList3.getItems().add(cockade.name() + " giving points: "+ cockade.points());
                }
            }
            else if(position == 4){
                messageLabel4.setText(" [" + position + "] " + score.username() + ": " + score.score() +" points");
                playerCockades = scoreBoard.getCockades(score.username());
                for(Cockade cockade : playerCockades){
                    cockadesList4.getItems().add(cockade.name() + " giving points: "+ cockade.points());
                }
            }
            if (score.username().equals(username)) {
                your_title = score.title();
            }
            System.out.println("Your final grade: "+your_title);
            titleLabel.setText("Your final grade: "+your_title);
            position++;
        }

        addChangeOfImage(cockadesList1, 1);
        addChangeOfImage(cockadesList2, 2);
        addChangeOfImage(cockadesList3, 3);
        addChangeOfImage(cockadesList4, 4);

    }

    private void addChangeOfImage(ListView cockadesList, int player){
        ObservableList<String> items = cockadesList.getItems();

        // Set the event handler for the ListView
        cockadesList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleCockadeSelection(player, newValue.toString())
        );

    }

    private void handleCockadeSelection(int player, String selectedCockade){
        Pattern pattern = Pattern.compile("\\d+");

        // Find the last number in the string
        Matcher matcher = pattern.matcher(selectedCockade);
        String lastNumber = null;
        while (matcher.find()) {
            lastNumber = matcher.group();
        }

        if (selectedCockade == null || lastNumber == null) {
            return;
        }

        if(!selectedCockade.contains("2") ||
                !selectedCockade.contains("4") ||
                    !selectedCockade.contains("6") ||
                        !selectedCockade.contains("8") ||
                            !selectedCockade.contains("1")){
            if(player == 1){
                cockadeImage1.setImage(null);
            }
            else if(player == 2){
                cockadeImage2.setImage(null);
            }
            else if(player == 3){
                cockadeImage3.setImage(null);
            }
            else if(player == 4){
                cockadeImage4.setImage(null);
            }
            return;
        }

        if (selectedCockade == "1"){
            if(player == 1){
                cockadeImage1.setImage(null);
                cockadeImage1.setImage(new Image("/img/scoring tokens/end game.jpg"));
            }
            else if(player == 2){
                cockadeImage2.setImage(null);
                cockadeImage2.setImage(new Image("/img/scoring tokens/end game.jpg"));
            }
            else if(player == 3){
                cockadeImage3.setImage(null);
                cockadeImage3.setImage(new Image("/img/scoring tokens/end game.jpg"));
            }
            else if(player == 4){
                cockadeImage4.setImage(null);
                cockadeImage4.setImage(new Image("/img/scoring tokens/end game.jpg"));
            }
        }
        else{
            if(player == 1){
                cockadeImage1.setImage(null);
                cockadeImage1.setImage(new Image("/img/scoring tokens/scoring_" + selectedCockade + ".jpg"));
            }
            else if(player == 2){
                cockadeImage2.setImage(null);
                cockadeImage2.setImage(new Image("/img/scoring tokens/scoring_" +  selectedCockade + ".jpg"));
            }
            else if(player == 3){
                cockadeImage3.setImage(null);
                cockadeImage3.setImage(new Image("/img/scoring tokens/scoring_" + selectedCockade + ".jpg"));
            }
            else if(player == 4){
                cockadeImage4.setImage(null);
                cockadeImage4.setImage(new Image("/img/scoring tokens/scoring_" +  selectedCockade + ".jpg"));
            }
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
