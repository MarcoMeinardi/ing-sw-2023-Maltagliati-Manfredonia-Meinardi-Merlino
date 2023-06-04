package view.gui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.PersonalObjective;

public class PersonalObjController implements Initializable {
    @FXML
    private AnchorPane pane;
    private Stage stage;

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
        imageView.setFitWidth(300);
        imageView.setFitHeight(300);
        imageView.setX(150);
        imageView.setY(50);
        pane.getChildren().add(imageView);

    }
}

