package view.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import model.Card;
import model.Shelf;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller class for the shelves scene showing the four shelves of the players.
 *
 */

public class ShelvesController implements Initializable {

    private static final int shelfCardSize = 24;
    private static final int shelfCardStepX = 30;
    private static final int shelfCardStepY = 27;
    private static final int shelfRows = 6;
    private static final int shelfColumns = 5;
    @FXML
    private AnchorPane pane;
    @FXML
    private Label player;
    @FXML
    private Label player2;
    @FXML
    private Label player3;
    @FXML
    private Label player4;
    @FXML
    private ImageView shelf;
    @FXML
    private ImageView shelf2;
    @FXML
    private ImageView shelf3;
    @FXML
    private ImageView shelf4;
    private ArrayList<Shelf> shelves;
    private ArrayList<String> playersNames;

    /**
     * Method called to load the scene with the shelves of the players in game.
     * The shelves are filled with the items of the players.
     * The scene changes depending on the number of players in game.
     *
     * @param url
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resourceBundle
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     *
     * @author Ludovico, Riccardo
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GameData gameData = GameViewController.getGameData();
        shelves = gameData.getShelves();
        playersNames = gameData.getPlayersNames();
        switch (playersNames.size()){
            case 2:
                pane.getChildren().remove(player3);
                pane.getChildren().remove(player4);
                pane.getChildren().remove(shelf3);
                pane.getChildren().remove(shelf4);
                break;
            case 3:
                pane.getChildren().remove(player4);
                pane.getChildren().remove(shelf4);
                break;
        }
        for(Shelf shelf : shelves){
            fillShelf(shelf, shelves.indexOf(shelf));
        }
    }

    /**
     * Method used to calculate the position of the image of the item on the shelf.
     * Puts the images of the shelf calling the method putImageOnScene.
     *
     * @param shelf
     * shelf of the player
     *
     * @param playerIndex
     * index of the player. Used to know which shelf to fill
     *
     * @author Ludovico
     */

    private void fillShelf(Shelf shelf, int playerIndex) {
        Optional<Card>[][] shelfCards = shelf.getShelf();
        int shelfOffSetY = 0;
        int shelfOffSetX = 0;

        switch (playerIndex){
            case 0:
                shelfOffSetY = 228;
                shelfOffSetX = 126;
                break;
            case 1:
                shelfOffSetY = 228;
                shelfOffSetX = 526;
                break;
            case 2:
                shelfOffSetY = 588;
                shelfOffSetX = 126;
                break;
            case 3:
                shelfOffSetY = 588;
                shelfOffSetX = 526;
                break;
        }

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
                    putImageOnScene(imageName, y, x,  shelfCardSize, shelfCardSize, shelfOffSetX, shelfOffSetY, shelfCardStepX, shelfCardStepY);
                }
            }
        }

        for(int i = 0; i < playersNames.size(); i++){
            if(i == 0){
                player.setText(playersNames.get(i));
            }
            if(i == 1){
                player2.setText(playersNames.get(i));
            }
            if(i == 2){
                player3.setText(playersNames.get(i));
            }
            if(i == 3){
                player4.setText(playersNames.get(i));
            }
        }

    }

    /**
     * Puts an image on the scene
     * @param imageName
     * Name of the image.
     *
     * @param y
     * Value of the y coordinate in the shelf.
     *
     * @param x
     * Value of the x coordinate in the shelf.
     *
     * @param height
     * Height of the image.
     *
     * @param width
     * Width of the image.
     *
     * @param offsetX
     * Offset of the x coordinate.
     *
     * @param offsetY
     * Offset of the y coordinate.
     *
     * @param stepX
     * Step of the x coordinate.
     *
     * @param stepY
     * Step of the y coordinate.
     *
     * @author Ludovico
     */

    public void putImageOnScene(String imageName, int y, int x, int height, int width, int offsetX, int offsetY, int stepX, int stepY){
        String imagePath = getClass().getResource(imageName).toExternalForm();
        Image image = new Image(imagePath);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        imageView.setX(offsetX + stepX*x);
        imageView.setY(offsetY - stepY*y);
        pane.getChildren().add(imageView);
    }


}