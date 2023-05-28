package view.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.PersonalObjective;

import java.net.URL;
import java.util.ResourceBundle;

public class PersonalObjController implements Initializable {
    private String personalObjective;
    private GameData gameData;
    @FXML
    private ImageView imageContainer;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameData = GameViewController.getGameData();
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
            case "Fourth":
                imageName = "/img/personal goal cards/Personal_Goals4.png";
                imagePath = getClass().getResource(imageName).toExternalForm();
                image = new Image(imagePath);
                imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.setImage(image);
                break;
            case "Fifth":
                imageName = "/img/personal goal cards/Personal_Goals5.png";
                imagePath = getClass().getResource(imageName).toExternalForm();
                image = new Image(imagePath);
                imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.setImage(image);
                break;
            case "Sixth":
                imageName = "/img/personal goal cards/Personal_Goals6.png";
                imagePath = getClass().getResource(imageName).toExternalForm();
                image = new Image(imagePath);
                imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.setImage(image);
                break;
            case "Seventh":
                imageName = "/img/personal goal cards/Personal_Goals7.png";
                imagePath = getClass().getResource(imageName).toExternalForm();
                image = new Image(imagePath);
                imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.setImage(image);
                break;
            case "Eighth":
                imageName = "/img/personal goal cards/Personal_Goals8.png";
                imagePath = getClass().getResource(imageName).toExternalForm();
                image = new Image(imagePath);
                imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.setImage(image);
                break;
            case "Ninth":
                imageName = "/img/personal goal cards/Personal_Goals9.png";
                imagePath = getClass().getResource(imageName).toExternalForm();
                image = new Image(imagePath);
                imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.setImage(image);
                break;
            case "Tenth":
                imageName = "/img/personal goal cards/Personal_Goals10.png";
                imagePath = getClass().getResource(imageName).toExternalForm();
                image = new Image(imagePath);
                imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.setImage(image);
                break;
            case "Eleventh":
                imageName = "/img/personal goal cards/Personal_Goals11.png";
                imagePath = getClass().getResource(imageName).toExternalForm();
                image = new Image(imagePath);
                imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.setImage(image);
                break;
            case "Twelfth":
                imageName = "/img/personal goal cards/Personal_Goals12.png";
                imagePath = getClass().getResource(imageName).toExternalForm();
                image = new Image(imagePath);
                imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.setImage(image);
                break;
        }
    }
}

