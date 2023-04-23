package controller;
import model.*;
import network.rpc.Call;
import network.rpc.Result;
import network.rpc.ServerEvent;
import network.rpc.server.Client;
import network.rpc.server.ClientManager;
import network.rpc.server.ClientStatus;
import network.rpc.server.DisconnectedClientException;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class GameController extends Thread {

    private Game game;
    private ClientManager clientManager;

    private static final int disconectionTimeOut = 10000;

    private static final int maxDisconnectionTries = 18;

    private static final Logger logger = Logger.getLogger(GameController.class.getName());

    private Result<Serializable> gameHandler(Call<Serializable> call, Client client) {
        Result result;
        //TODO: implement
        return result;
    }

    /**
     * Constructor that creates a new game with the specified players.
     *
     * @param playersNames The names of the players
     * @author Ludovico
     */
    private GameController(ArrayList<String> playersNames, ClientManager clientManager) throws DisconnectedClientException { /** to be changed in lobby*/
        game = new Game(playersNames);
        this.clientManager = clientManager;
        for(String name : playersNames){
            Optional<Client> client = clientManager.getClientByUsername(name);
            if(client.isPresent()){
                client.get().setCallHandler(this::gameHandler);
            }else{
                throw new DisconnectedClientException();
            }
        }
        if(trySendToAll(Result.serverPush(ServerEvent.Start()))){
            logger.info("Game started");
        }else{
            Exception e = new DisconnectedClientException();
            trySendToAll(Result.serverPush(ServerEvent.Error(e)));
            throw new DisconnectedClientException();
        }
    }

    @Override
    public void run() {
        for (Player player : game) {
            try{
                addCommonCockade(player);
            }catch(Exception e){
                checkDisconnection();
            }
        }
    }

    private boolean checkRefillTable() {
        return game.getTabletop().needRefill();
    }

    private void refillTable() {
        if (checkRefillTable()) {
            game.getTabletop().fillTable();
        }
    }

    private void addPersonalCockade(Player player) {
        Optional<Cockade> helpCockadePersonal = player.getPersonalObjective().isCompleted(player.getShelf());
        helpCockadePersonal.ifPresent(player::addCockade);
    }

    private void addCommonCockade(Player player) {
        for (CommonObjective objective : game.getCommonObjectives()) {
            Optional<Cockade> helpCockadeCommon = objective.isCompleted(player.getShelf());
            helpCockadeCommon.ifPresent(player::addCockade);
        }
    }

    private ArrayList<Player> finalRanks() {
        ArrayList<Player> players = game.getPlayers();

        for (Player player : players) {

            ArrayList<Cockade> helpGroupCockades = player.getShelf().getGroupsCockades();
            for (Cockade cockade : helpGroupCockades) {
                player.addCockade(cockade);
            }

        }

        players.sort((player1, player2) -> player2.getPoints() - player1.getPoints());
        return players;
    }

    private void doMove(Player player, ArrayList<Point> positions, int column) throws InvalidMoveException {
        if (positions.size() < 1 || positions.size() > 3) {
            throw new InvalidMoveException("Invalid number of picked cards");
        }

        // Check that all the cards are adjacent
        for (int i = 0; i < positions.size() - 1; i++) {
            for (int j = 1; j < positions.size(); j++) {
                int dist = positions.get(i).distance(positions.get(j));
                if (dist != 1 && dist != 2) {
                    throw new InvalidMoveException("Cards are not pickable (not adjacient)");
                }
            }
        }

        // Check that all the cards are singularly pickable
        for (Point position : positions) {
            if (!game.getTabletop().isPickable(position.y(), position.x())) {
                throw new InvalidMoveException("One of the selected cards is not pickable");
            }
        }

        if (positions.size() == 3) {
            // The three points have to be colinear
            // To check that, we can calculate the area of the triangle they form
            // If the area is 0, the points are colinear
            Point p1 = positions.get(0);
            Point p2 = positions.get(1);
            Point p3 = positions.get(2);

            // Double triangle area derived from Gauss's area formula
            int area =
                    p1.x() * (p2.y() - p3.y()) +
                            p2.x() * (p3.y() - p1.y()) +
                            p3.x() * (p1.y() - p2.y());
            if (area != 0) {
                throw new InvalidMoveException("Cards are not pickable (not colinear)");
            }
        }

        ArrayList<Card> cards = new ArrayList<Card>();
        for (Point position : positions) {
            cards.add(game.getTabletop().pickCard(position.y(), position.x()));
        }
        player.getShelf().insert(column, cards);
    }

    private void checkDisconnection() {
        int connectedClients = game.getPlayers().stream().map(Player::getName).filter(clientManager::isClientConnected).toArray().length;
        int tryNumber = 0;
        boolean allReceiving = true;
        while(connectedClients != game.getPlayers().size() && allReceiving){
            allReceiving = trySendToAll(Result.serverPush(ServerEvent.Pause("Someone has disconnected, pausing game")));
            try {
                Thread.sleep(disconectionTimeOut);
            } catch (InterruptedException e) {
                logger.warning(e.getMessage());
            }
            connectedClients = game.getPlayers().stream().map(Player::getName).filter(clientManager::isClientConnected).toArray().length;
        }
        for (Player player : game.getPlayers()) {
            clientManager.trySendToClient(player.getName(), Result.serverPush(ServerEvent.Resume("Someone has reconnected, resuming game")));
        }
        tryNumber++;
        if(tryNumber == maxDisconnectionTries){
            throw new RuntimeException("Too many disconnections");//TODO change exception
        }
    }

    private boolean trySendToAll(Result result){
        boolean val = true;
        for (Player player : game.getPlayers()) {
            val = val && clientManager.trySendToClient(player.getName(), result);
        }
        return val;
    }

}


