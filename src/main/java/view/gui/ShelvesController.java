package view.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.Card;
import model.Shelf;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ShelvesController implements Initializable {

    private static final int shelfCardSize = 28;
    private static final int shelfCardStepX = 34;
    private static final int shelfCardStepY = 30;
    private static final int shelfOffSetX = 790;
    private static final int shelfOffSetY = 270;
    private static final int shelfRows = 6;
    private static final int shelfColumns = 5;
    @FXML
    private AnchorPane pane;
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
    }

    private void fillShelf(Shelf shelf) {
        Optional<Card>[][] shelfCards = shelf.getShelf();

        int[] counter = new int[Card.values().length];
        for(int i = 0; i < Card.values().length; i++){
            counter[i] = 1;
        }

        for (int y = 0; y < shelfRows; y++){
            for(int x = 0; x < shelfColumns; x++){
                String imageName = null;
                if(shelfCards[y][x].isPresent()){
                    if(shelfCards[y][x].get() == Card.Gatto){
                        counter[Card.Gatto.ordinal()] = (counter[Card.Gatto.ordinal()]%3) + 1;
                        imageName = "/img/item tiles/Gatti1." + counter[Card.Gatto.ordinal()] + ".png";
                    }
                    if(shelfCards[y][x].get() == Card.Libro){
                        counter[Card.Libro.ordinal()] = (counter[Card.Libro.ordinal()]%3) + 1;
                        imageName = "/img/item tiles/Libri1." + counter[Card.Libro.ordinal()] + ".png";
                    }
                    if(shelfCards[y][x].get() == Card.Cornice){
                        counter[Card.Cornice.ordinal()] = (counter[Card.Cornice.ordinal()]%3) + 1;
                        imageName = "/img/item tiles/Cornici1." + counter[Card.Cornice.ordinal()] + ".png";
                    }
                    if(shelfCards[y][x].get() == Card.Gioco){
                        counter[Card.Gioco.ordinal()] = (counter[Card.Gioco.ordinal()]%3) + 1;
                        imageName = "/img/item tiles/Giochi1." + counter[Card.Gioco.ordinal()] + ".png";
                    }
                    if(shelfCards[y][x].get() == Card.Pianta){
                        counter[Card.Pianta.ordinal()] = (counter[Card.Pianta.ordinal()]%3) + 1;
                        imageName = "/img/item tiles/Piante1." + counter[Card.Pianta.ordinal()] + ".png";
                    }
                    if(shelfCards[y][x].get() == Card.Trofeo){
                        counter[Card.Trofeo.ordinal()] = (counter[Card.Trofeo.ordinal()]%3) + 1;
                        imageName = "/img/item tiles/Trofei1." + counter[Card.Trofeo.ordinal()] + ".png";
                    }
                    putImageOnScene(imageName, y, x,  shelfCardSize, shelfCardSize, shelfOffSetX, shelfOffSetY, shelfCardStepX, shelfCardStepY, true);
                }
            }
        }

    }

    public void putImageOnScene(String imageName, int y, int x, int height, int width, int offsetX, int offsetY, int stepX, int stepY, boolean isShelf){
        String imagePath = getClass().getResource(imageName).toExternalForm();
        Image image = new Image(imagePath);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        imageView.setX(offsetX + stepX*x);
        imageView.setY(offsetY - stepY*y);
        String id = "Card" + x + y;
        if(isShelf){
            id += "Shelf";
        }
        imageView.setId(id);
        pane.getChildren().add(imageView);
    }


}