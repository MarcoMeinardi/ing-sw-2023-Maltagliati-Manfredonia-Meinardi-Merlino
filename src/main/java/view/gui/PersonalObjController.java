package view.gui;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class PersonalObjController implements Initializable {
    private String personalObjective;
    private GameData gameData;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GameData gameData = GameViewController.getGameData();
        personalObjective = gameData.getMyPersonalObjective().getName();
        //TODO crea un'immagine per la personal objective. Usa il nome della personal objective per caricare l'immagine
    }

}
