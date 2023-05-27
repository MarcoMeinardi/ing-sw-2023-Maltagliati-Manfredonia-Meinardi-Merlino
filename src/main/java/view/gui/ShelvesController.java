package view.gui;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Shelf;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ShelvesController implements Initializable {

    private ArrayList<Shelf> shelves;
    private ArrayList<String> playersNames;

    private static String me;
    private static Shelf myShelf;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GameData gameData = GameViewController.getGameData();
        shelves = gameData.getShelves();
        playersNames = gameData.getPlayersNames();
        for (int i = 0; i < shelves.size(); i++) {
            if (playersNames.get(i).equals(me)) {
                myShelf = shelves.get(i);
                String imageName = "/img/board/shelf.jpg";
                String imagePath = getClass().getResource(imageName).toExternalForm();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(200);
                imageView.setFitHeight(200);
            }
        }
    }
}
