package view.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import model.PersonalObjective;

public class PersonalObjController implements Initializable {
    @FXML
    private VBox imageContainer;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GameData gameData = GameViewController.getGameData();
        PersonalObjective personalObjective = gameData.getMyPersonalObjective();
        int objectiveIndex = PersonalObjective.generateAllPersonalObjectives().indexOf(personalObjective);

        if (objectiveIndex == -1) {
            throw new RuntimeException("Cannot find objective");
        }

        String imageName;
        if (objectiveIndex == 0) {
            imageName = "/img/personal goal cards/Personal_Goals.png";
        } else {
            imageName = String.format("/img/personal goal cards/Personal_Goals%d.png", objectiveIndex + 1);
        }
        String imagePath = getClass().getResource(imageName).toExternalForm();
        Image image = new Image(imagePath);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageContainer.getChildren().add(imageView);
    }
}

