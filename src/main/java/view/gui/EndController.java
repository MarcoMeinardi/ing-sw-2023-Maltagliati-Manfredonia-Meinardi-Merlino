package view.gui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
     * Method that shows the game results and the final grade of the player with all the objectives completed.
     *
     * @author Ludovico
     */

    private void showScoreBoard() {
        int position = 1;
        String your_title = "Why is my life like this";
        ArrayList<Cockade> playerCockades = new ArrayList<>();

        if(scoreBoard.size() == 2){
            messageLabel3.setVisible(false);
            messageLabel4.setVisible(false);
            cockadesList3.setVisible(false);
            cockadesList4.setVisible(false);
            cockadeImage3.setVisible(false);
            cockadeImage4.setVisible(false);
        }
        else if(scoreBoard.size() == 3){
            messageLabel4.setVisible(false);
            cockadesList4.setVisible(false);
            cockadeImage4.setVisible(false);
        }

        for (Score score : scoreBoard) {
            if(position == 1){
                Utils.changeLabel(messageLabel1, " [" + position + "] " + score.username() + ": " + score.score() +" points");
                playerCockades = scoreBoard.getCockades(score.username());
                for(Cockade cockade : playerCockades){
                    cockadesList1.getItems().add(cockade.name() + " giving points: "+ cockade.points());
                }
            }
            else if(position == 2){
                Utils.changeLabel(messageLabel2, " [" + position + "] " + score.username() + ": " + score.score() +" points");
                playerCockades = scoreBoard.getCockades(score.username());
                for(Cockade cockade : playerCockades){
                    cockadesList2.getItems().add(cockade.name() + " giving points: "+ cockade.points());
                }
            }
            else if(position == 3){
                Utils.changeLabel(messageLabel3, " [" + position + "] " + score.username() + ": " + score.score() +" points");
                playerCockades = scoreBoard.getCockades(score.username());
                for(Cockade cockade : playerCockades){
                    cockadesList3.getItems().add(cockade.name() + " giving points: "+ cockade.points());
                }
            }
            else if(position == 4){
                Utils.changeLabel(messageLabel4, " [" + position + "] " + score.username() + ": " + score.score() +" points");
                playerCockades = scoreBoard.getCockades(score.username());
                for(Cockade cockade : playerCockades){
                    cockadesList4.getItems().add(cockade.name() + " giving points: "+ cockade.points());
                }
            }
            if (score.username().equals(username)) {
                your_title = score.title();
            }
            System.out.println("Your final grade: "+your_title);
            Utils.changeLabel(titleLabel, "Your final grade: "+your_title);
            position++;
        }

        addChangeOfImage(cockadesList1, 1);
        addChangeOfImage(cockadesList2, 2);
        addChangeOfImage(cockadesList3, 3);
        addChangeOfImage(cockadesList4, 4);

    }

    private void addChangeOfImage(ListView cockadesList, int player){
        ObservableList<String> items = cockadesList.getItems();

        cockadesList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleCockadeSelection(player, newValue.toString())
        );

    }

    /**
     * Method that handles the selection of a cockade in the list.
     * It shows the correct token next to the list of cockades of the player.
     *
     * @param player
     * The player that completed the objective selected.
     *
     * @param selectedCockade
     * The cockade selected by the user.
     *
     * @author Ludovico
     */

    private void handleCockadeSelection(int player, String selectedCockade){
        Pattern pattern = Pattern.compile("\\d+");

        Matcher matcher = pattern.matcher(selectedCockade);
        String lastNumber = null;
        while (matcher.find()) {
            lastNumber = matcher.group();
        }

        if (selectedCockade == null || lastNumber == null) {
            return;
        }

        if((lastNumber.contains("2") ||
                lastNumber.contains("4") ||
                    lastNumber.contains("6") ||
                        lastNumber.contains("8") ||
                            (lastNumber.contains("1") && selectedCockade.contains("finish"))) &&
                                !lastNumber.contains("12")){

            if (lastNumber.equals("1")){
                if(player == 1){
                    cockadeImage1.setImage(new Image("/img/scoring tokens/end game.jpg"));
                }
                else if(player == 2){
                    cockadeImage2.setImage(new Image("/img/scoring tokens/end game.jpg"));
                }
                else if(player == 3){
                    cockadeImage3.setImage(new Image("/img/scoring tokens/end game.jpg"));
                }
                else if(player == 4){
                    cockadeImage4.setImage(new Image("/img/scoring tokens/end game.jpg"));
                }
            }
            else{
                if(player == 1){
                    cockadeImage1.setImage(new Image("/img/scoring tokens/scoring_" + lastNumber + ".jpg"));
                }
                else if(player == 2){
                    cockadeImage2.setImage(new Image("/img/scoring tokens/scoring_" +  lastNumber + ".jpg"));
                }
                else if(player == 3){
                    cockadeImage3.setImage(new Image("/img/scoring tokens/scoring_" + lastNumber + ".jpg"));
                }
                else if(player == 4){
                    cockadeImage4.setImage(new Image("/img/scoring tokens/scoring_" +  lastNumber + ".jpg"));
                }
            }
        }
        else{
            if(player == 1){
                cockadeImage1.setImage(new Image("/img/scoring tokens/transparent.png"));
            }
            else if(player == 2){
                cockadeImage2.setImage(new Image("/img/scoring tokens/transparent.png"));
            }
            else if(player == 3){
                cockadeImage3.setImage(new Image("/img/scoring tokens/transparent.png"));
            }
            else if(player == 4){
                cockadeImage4.setImage(new Image("/img/scoring tokens/transparent.png"));
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
     * @author
     *
     * @throws IOException
     */

    @FXML
    private void goToLobbies(ActionEvent actionEvent) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(root, WIDTH, HEIGHT);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
            Utils.changeLabel(titleLabel, "Error loading the main menu");
        }
    }


}
