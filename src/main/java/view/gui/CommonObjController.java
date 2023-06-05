package view.gui;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import model.CommonObjective;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller class for the common objectives scene showing the two common objectives.
 *
 */

public class CommonObjController implements Initializable {
    @FXML
    private AnchorPane pane;
    @FXML
    private Label firstObj;
    @FXML
    private Label secObj;

    /**
     * Method called to load the scene with the two common objectives of the game.
     * Sets the two images of the common objectives on the scene, just above their printed names
     * in labels.
     *
     * @param url
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resourceBundle
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     *
     * @author Ludovico
     */

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
            imageView.setFitWidth(240);
            imageView.setFitHeight(155);
            if(commonObjectives.indexOf(commonObjective) == 0) {
                firstObj.setText(commonObjective);
                imageView.setX(30);
            }
            else {
                secObj.setText(commonObjective);
                imageView.setX(330);
            }
                imageView.setY(60);
            pane.getChildren().add(imageView);
        }

    }

}
