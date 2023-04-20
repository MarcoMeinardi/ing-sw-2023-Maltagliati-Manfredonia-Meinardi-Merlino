package controller;
import model.Cockade;
import model.CommonObjective;
import model.Game;
import model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

public class GameController {

    Game game;
    boolean endGame;

    /** Constructor that creates a new game with the specified players.
    * @author Ludovico
    * @param playersNames The names of the players
    */
    public GameController(ArrayList<String> playersNames) {
        game = new Game(playersNames);
    }

    public boolean checkFullShelf(Player player){
        return player.getShelf().isFull();
    }

    public boolean checkRefillTable(){
        return game.getTabletop().needRefill();
    }


    private void addPersonalCockade(Player player){
        Optional<Cockade> helpCockadePersonal = player.getPersonalObjective().isCompleted(player.getShelf());
        if(helpCockadePersonal.isPresent()) {
            player.addCockade(helpCockadePersonal.get());
        }
    }

    private void addCommonCockade(Player player){
        for(CommonObjective objective : game.getCommonObjectives()) {
            Optional<Cockade> helpCockadeCommon = objective.isCompleted(player.getShelf());
            if (helpCockadeCommon.isPresent()){
                player.addCockade(helpCockadeCommon.get());
            }
        }
    }

    private ArrayList<Player> finalRanks(){
        ArrayList<Player> players = game.getPlayers();

        for(Player player : players) {

            ArrayList<Cockade> helpGroupCockades = player.getShelf().getGroupsCockades();
            for(Cockade cockade : helpGroupCockades) {
                player.addCockade(cockade);
            }

            player.calculatePoints();

        }

        Collections.sort(players, (player1, player2) -> player2.getPoints() - player1.getPoints());
        return players;
    }

}


