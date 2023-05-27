package view.gui;

import controller.lobby.Lobby;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import model.Cockade;
import model.CommonObjective;
import model.Game;
import model.Shelf;
import network.ClientStatus;
import network.NetworkManagerInterface;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CommonObjController implements Initializable {
    private ArrayList<String> commonObjectives;
    private GameData gameData;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
         GameData gameData = GameViewController.getGameData();
         commonObjectives = gameData.getCommonObjectives();
         //TODO scorri le common objectives e crea un'immagine per ognuna. Usa i nomi delle common objectives per caricare le immagini, ES: "all equal corners"--> 8.jpg. Usa chatgpt per capire come mettere una immagine nel fxml
    }

}
