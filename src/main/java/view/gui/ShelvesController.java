package view.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import model.Shelf;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ShelvesController implements Initializable {

    private ArrayList<Shelf> shelves;
    private ArrayList<String> playersNames;

    private static String me;
    private static Shelf Shelf;

    @FXML
    private HBox imageContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GameData gameData = GameViewController.getGameData();
        shelves = gameData.getShelves();
        playersNames = gameData.getPlayersNames();
        for (int i = 0; i < playersNames.size(); i++) {
            String imageName = "/img/board/bookshelf.jpg";
            Label label = new Label(playersNames.get(i));
            label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold");
            String imagePath = getClass().getResource(imageName).toExternalForm();
            Image image = new Image(imagePath);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageContainer.getChildren().add(imageView);
            imageContainer.getChildren().add(label);
        }
    }
}