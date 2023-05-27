package view.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.PersonalObjective;

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
        String imageName;
        String imagePath;
        Image image;
        ImageView imageView;
        switch (personalObjective){
            case "First":
                imageName = "/img/personal goal cards/Personal_Goals.png";
                imagePath = getClass().getResource(imageName).toExternalForm();
                image = new Image(imagePath);
                imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.setImage(image);
                break;
            case "Second":
                imageName = "/img/personal goal cards/Personal_Goals2.png";
                imagePath = getClass().getResource(imageName).toExternalForm();
                image = new Image(imagePath);
                imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.setImage(image);
                break;
            case "Third":
                imageName = "/img/personal goal cards/Personal_Goals3.png";
                imagePath = getClass().getResource(imageName).toExternalForm();
                image = new Image(imagePath);
                imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.setImage(image);
                break;
        }
    }
}

