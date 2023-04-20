package controller;
import model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

public class GameController {

    private Game game;
    private boolean endGame;

    /** Constructor that creates a new game with the specified players.
    * @author Ludovico
    * @param playersNames The names of the players
    */
    private GameController(ArrayList<String> playersNames) {
        game = new Game(playersNames);
    }

    private boolean checkFullShelf(Player player){
        return player.getShelf().isFull();
    }

    private void setEndGame(Player player){
        if(checkFullShelf(player)){
            endGame = true;
        }
    }

    private boolean checkRefillTable(){
        return game.getTabletop().needRefill();
    }

    private void refillTable(){
        if (checkRefillTable()) {
            game.getTabletop().fillTable();
        }
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

        }

        Collections.sort(players, (player1, player2) -> player2.getPoints() - player1.getPoints());
        return players;
    }

    private boolean pickCards(ArrayList<Integer> y, ArrayList<Integer> x, Player player, int column){
        Card[] cards = new Card[y.size()];

        if(y.size() != x.size() || y.size() > 3 || y.size() < 1 || x.size() > 3 || x.size() < 1) {
            return false;
        }

        try{
            for (int i = 0; i < y.size(); i++) {
                game.getTabletop().isPickable(y.get(i), x.get(i));
                cards[i] = game.getTabletop().pickCard(y.get(i), x.get(i));
            }

            player.getShelf().insert(column, cards);

        }catch (Exception e) {
            return false;
        }

        return true;
    }



}


