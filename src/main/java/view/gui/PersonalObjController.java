package view.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.net.URL;
import java.util.ResourceBundle;

import model.PersonalObjective;

/**
 * PersonalObjController is the class that manages the personal objective scene.
 **/

public class PersonalObjController implements Initializable {

    @FXML
    private ImageView objImage;

    /**
     * Method called when the scene is loaded.
     * It sets the image of the personal objective card of the game.
     *
     * @param url
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resourceBundle
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     *
     * @author Ludovico, Riccardo, Marco
     */
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
        objImage.setImage(image);
    }
}

