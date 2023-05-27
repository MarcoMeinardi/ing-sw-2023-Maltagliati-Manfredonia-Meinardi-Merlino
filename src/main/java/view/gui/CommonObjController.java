package view.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CommonObjController implements Initializable {
    private ArrayList<String> commonObjectives;
    private GameData gameData;
    @FXML
    private VBox imageContainer;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GameData gameData = GameViewController.getGameData();
        commonObjectives = gameData.getCommonObjectives();
        for (int i = 0; i < commonObjectives.size(); i++) {
            if (commonObjectives.get(i).equals("2 square-shaped groups")) {
                String imageName = "/img/common goal cards/1.jpg";
                String imagePath = getClass().getResource(imageName).toExternalForm();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.getChildren().add(imageView);
            }
            if (commonObjectives.get(i).equals("2 columns of 6 different cards")) {
                String imageName = "/img/common goal cards/2.jpg";
                String imagePath = getClass().getResource(imageName).toExternalForm();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.getChildren().add(imageView);
            }
            if (commonObjectives.get(i).equals("4 groups of 4 cards")) {
                String imageName = "/img/common goal cards/3.jpg";
                String imagePath = getClass().getResource(imageName).toExternalForm();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.getChildren().add(imageView);
            }
            if (commonObjectives.get(i).equals("6 groups of 2 cards")) {
                String imageName = "/img/common goal cards/4.jpg";
                String imagePath = getClass().getResource(imageName).toExternalForm();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.getChildren().add(imageView);
            }
            if (commonObjectives.get(i).equals("3 columns of at most 3 different cards")) {
                String imageName = "/img/common goal cards/5.jpg";
                String imagePath = getClass().getResource(imageName).toExternalForm();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.getChildren().add(imageView);
            }
            if (commonObjectives.get(i).equals("2 rows with 5 different cards")) {
                String imageName = "/img/common goal cards/6.jpg";
                String imagePath = getClass().getResource(imageName).toExternalForm();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.getChildren().add(imageView);
            }
            if (commonObjectives.get(i).equals("4 row of at most 3 different cards")) {
                String imageName = "/img/common goal cards/7.jpg";
                String imagePath = getClass().getResource(imageName).toExternalForm();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.getChildren().add(imageView);
            }
            if (commonObjectives.get(i).equals("all equal corner")) {
                String imageName = "/img/common goal cards/8.jpg";
                String imagePath = getClass().getResource(imageName).toExternalForm();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.getChildren().add(imageView);
            }
            if (commonObjectives.get(i).equals("eight equal cards")) {
                String imageName = "/img/common goal cards/9.jpg";
                String imagePath = getClass().getResource(imageName).toExternalForm();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.getChildren().add(imageView);
            }
            if (commonObjectives.get(i).equals("X shapes group")) {
                String imageName = "/img/common goal cards/10.jpg";
                String imagePath = getClass().getResource(imageName).toExternalForm();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.getChildren().add(imageView);
            }
            if (commonObjectives.get(i).equals("5 cards in diagonal")) {
                String imageName = "/img/common goal cards/11.jpg";
                String imagePath = getClass().getResource(imageName).toExternalForm();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.getChildren().add(imageView);
            }
            if (commonObjectives.get(i).equals("stair-shaped cards")) {
                String imageName = "/img/common goal cards/12.jpg";
                String imagePath = getClass().getResource(imageName).toExternalForm();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageContainer.getChildren().add(imageView);
            }
        }
    }

}
