package view.gui;

import javafx.fxml.Initializable;
import model.Shelf;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ShelvesController implements Initializable {

    private ArrayList<Shelf> shelves;
    private ArrayList<String> playersNames;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GameData gameData = GameViewController.getGameData();
        shelves = gameData.getShelves();
        playersNames = gameData.getPlayersNames();
        //TODO trova un modo per stampare le shelves coi nomi dei giocatori sopra in base alle shelves che ti vengono passate
    }
}
