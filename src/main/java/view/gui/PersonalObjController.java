package view.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.PersonalObjective;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PersonalObjController implements Initializable {
    private ArrayList<String> players;
    private String personalObjective;
    private GameData gameData;
    private static PersonalObjective myPersonalObjective;
    private static String me;

    @FXML
    private ImageView imageContainer;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GameData gameData = GameViewController.getGameData();
        personalObjective = gameData.getMyPersonalObjective().getName();
        //per ogni player nella lista players se il nome del player è uguale al nome del player che ha il personal objective stampa il personal objective
        for (int i = 0; i < players.size(); i++) {
            players.add(String.valueOf(new PersonalObjective(gameData.getMyPersonalObjective().getName())));
            if (players.get(i).equals(me)) {
                myPersonalObjective = gameData.getMyPersonalObjective();
                String imageName = "/img/personal goal cards/Personal_Goal" + i + ".jpg";
                String imagePath = getClass().getResource(imageName).toExternalForm();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
            }
        }
    }
}
