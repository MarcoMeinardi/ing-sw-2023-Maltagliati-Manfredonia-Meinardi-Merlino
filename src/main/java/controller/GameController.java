package controller;
import model.Game;

import java.util.ArrayList;

public class GameController {

    public GameController(ArrayList<String> playersNames) {
        Game game = new Game(playersNames);
    }


}
