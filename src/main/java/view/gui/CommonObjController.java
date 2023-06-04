package view.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import model.CommonObjective;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CommonObjController implements Initializable {
    @FXML
    private HBox imageContainer;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GameData gameData = GameViewController.getGameData();
        ArrayList<String>commonObjectives = gameData.getCommonObjectives();
        ArrayList<String> allObjectives = CommonObjective.generateAllCommonObjectives(gameData.getPlayersNames().size())
            .stream()
            .map(CommonObjective::getName)
            .collect(Collectors.toCollection(ArrayList::new));

        for (String commonObjective : commonObjectives) {
            int objectiveIndex = allObjectives.indexOf(commonObjective);
            if (objectiveIndex == -1) {
                throw new RuntimeException("Cannot find objective");
            }

            String imageName = String.format("/img/common goal cards/%d.jpg", objectiveIndex + 1);
            String imagePath = getClass().getResource(imageName).toExternalForm();
            Image image = new Image(imagePath);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageContainer.getChildren().add(imageView);
        }
    }

}
