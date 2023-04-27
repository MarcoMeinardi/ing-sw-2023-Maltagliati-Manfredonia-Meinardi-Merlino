package controller.game;
import controller.lobby.Lobby;
import controller.lobby.LobbyController;
import model.*;
import network.rpc.Call;
import network.rpc.Result;
import network.rpc.ServerEvent;
import network.rpc.parameters.CardSelect;
import network.rpc.parameters.Update;
import network.rpc.parameters.WrongParametersException;
import network.rpc.server.Client;
import network.rpc.server.ClientManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * This class runs an instance of game and controls it.
 * @author Ludovico, Marco, Lorenzo
 *
 */

public class GameController {

    private Game game;

    private static final int disconectionTimeOut = 10000;

    private static final int maxDisconnectionTries = 18;

    private Boolean gamePaused = false;
    private Iterator<Player> playerIterator;
    private Player currentPlayer;
    private String gameName;
	private Thread globalUpdateThread = null;
    private static final Logger logger = Logger.getLogger(GameController.class.getName());

    /**
     * Constructor that creates a new game with the specified players.
     * @author Ludovico
     *
     */
    public GameController(Lobby lobby) throws Exception{
        game = new Game(lobby.getPlayers());
        playerIterator = game.iterator();
        gameName = lobby.getName();
        currentPlayer = playerIterator.next();
        for(Player player : game.getPlayers()){
            Client client = ClientManager.getInstance().getClientByUsername(player.getName()).orElseThrow();
            client.setCallHandler(this::handleGame);
        }
    }

    /**
     * Checks when table needs a refill.
     * @author Ludovico
     *
     * @return True if refill needed, false otherwise
     */

    private boolean checkRefillTable() {
        return game.getTabletop().needRefill();
    }

    /**
     * Refills the table if needed.
     * @author Ludovico
     *
     */

    private void refillTable() {
        if (checkRefillTable()) {
            game.getTabletop().fillTable();
        }
    }

    /**
     * Adds the personal cockade to the player's shelf if the player has completed the personal objective.
     * @author Ludovico
     *
     * @param player The player
     */

    private void addPersonalCockade(Player player) {
        Optional<Cockade> helpCockadePersonal = player.getPersonalObjective().isCompleted(player.getShelf());
        helpCockadePersonal.ifPresent(player::addCockade);
    }

    /**
     * Adds the common cockade to the player's shelf if the player has completed the common objective.
     * @author Ludovico
     *
     * @param player The player
     */

    private void addCommonCockade(Player player) {
        for (CommonObjective objective : game.getCommonObjectives()) {
            Optional<Cockade> helpCockadeCommon = objective.isCompleted(player.getShelf());
            helpCockadeCommon.ifPresent(player::addCockade);
        }
    }

    /**
     * Returns the final ranks of the players.
     * @author Ludovico, Marco
     *
     * @return The final ranks of the players
     */

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

    /**
     * Tries to execute the move requested by the player.
     * @author Marco
     *
     * @param player The player
     * @param positions The positions of the cards to pick
     * @param column The column where the cards will be placed
     */
    private void doMove(Player player, ArrayList<Point> positions, int column) throws InvalidMoveException {
        if (positions.size() < 1 || positions.size() > 3) {
            throw new InvalidMoveException("Invalid number of picked cards");
        }

        // Check that all the cards are adjacent
        for (int i = 0; i < positions.size() - 1; i++) {
            for (int j = 1; j < positions.size(); j++) {
                int dist = positions.get(i).distance(positions.get(j));
                if (dist != 1 && (positions.size() != 3 || dist != 2)) {
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
            // We just have to check if they have all the same x or y, since that's the only valid disposition
            Point p1 = positions.get(0);
            Point p2 = positions.get(1);
            Point p3 = positions.get(2);

            if (
                (p1.x() != p2.x() || p1.x() != p3.x()) ||
                (p1.y() != p2.y() || p1.y() != p3.y())
            ) {
                throw new InvalidMoveException("Cards are not pickable (not colinear)");
            }
        }

        ArrayList<Card> cards = new ArrayList<Card>();
        for (Point position : positions) {
            cards.add(game.getTabletop().pickCard(position.y(), position.x()));
        }
        player.getShelf().insert(column, cards);
    }

    public void checkDisconnection() {
        int count = 0;
        ClientManager clientManager = ClientManager.getInstance();

        while (true) {
            List<String> connectedPlayers = game.getPlayers().stream().filter(p -> clientManager.isClientConnected(p.getName())).map(Player::getName).toList();
            if (connectedPlayers.size() == game.getPlayers().size()) {
                try {
                    for(Player player: game.getPlayers()){
                        Optional<Client> client = clientManager.getClientByUsername(player.getName());
                        if(client.isEmpty()){
                            throw new RuntimeException("Client not found" + player.getName());
                        }
                        client.get().send(Result.ok(ServerEvent.Resume(null), null));
                    }
                    return;
                }catch (Exception e){
                    logger.warning("Error while sending resume event to client" + e.getMessage());
                }
            }

            for (String player:  connectedPlayers) {
                Optional<Client> client = clientManager.getClientByUsername(player);
                if(client.isPresent()){
                    try{
                        client.get().send(Result.ok(ServerEvent.Pause("waiting for all players to connect"), null));
                    }catch (Exception e){
                        logger.warning("Error while sending pause event to client" + e.getMessage());
                    }
                }
            }

            try {
                Thread.sleep(disconectionTimeOut);
            } catch (InterruptedException e) {
                logger.warning("Error while sleeping" + e.getMessage());
            }

            count++;

            if (count == maxDisconnectionTries) {
				exitGame();
            }

        }

    }

    public void globalUpdate(ServerEvent event) {
        ClientManager clientManager = ClientManager.getInstance();

		while (true) {
			try {
				for(Player player : game.getPlayers()) {
					Optional<Client> client = clientManager.getClientByUsername(player.getName());
					if(client.isEmpty()) {
						throw new RuntimeException("Client not found" + player.getName());
					}
					client.get().send(Result.ok(event, null));
				}
				break;
			} catch (Exception e) {
				logger.warning("Error while sending global update event to client" + e.getMessage());
				checkDisconnection();
			}
		}
    }

    public Result handleGame(Call call, Client client){
        Result result;
        try{
            switch (call.service()){
                case CardSelect -> {
                    if(!(call.params() instanceof CardSelect)){
                        throw new WrongParametersException("CardSelect", call.params().getClass().getName(), "CardSelect");
                    }
                    if(globalUpdateOnGoing()){
                        throw new WaitingForUpdateException();
                    }
                    CardSelect cardSelect = (CardSelect) call.params();
                    String username = client.getUsername();
                    Player player = game.getPlayers().stream().filter(p -> p.getName().equals(username)).findFirst().orElseThrow();
                    if(!currentPlayer.equals(player)){
                        throw new NotYourTurnException();
                    }
                    doMove(player, cardSelect.selectedCards(), cardSelect.column());
                    addPersonalCockade(player);
                    addCommonCockade(player);
                    refillTable();
                    if(playerIterator.hasNext()){
                        Update update = new Update(username, game.getTabletop(), player.getShelf());
						setGlobalUpdate(ServerEvent.Update(update));
                        currentPlayer = playerIterator.next();
                    }else{
                        ScoreBoard scoreBoard = new ScoreBoard(game);
						setGlobalUpdate(ServerEvent.End(scoreBoard));
                        exitGame();
                    }
                    result = Result.empty(call.id());
                }
                default -> {
                    throw new WrongThreadException();
                }
            }
        }catch(Exception e){
            result = Result.err(e, call.id());
        }
        return result;
    }

	private boolean globalUpdateOnGoing() {
		return globalUpdateThread != null && globalUpdateThread.isAlive();
	}

	private void setGlobalUpdate(ServerEvent event) {
		globalUpdateThread = new Thread(() -> globalUpdate(event));
	}

    public void exitGame(){
        ClientManager clientManager = ClientManager.getInstance();
        LobbyController lobbyController = LobbyController.getInstance();
        for(Player player: game.getPlayers()){
            Optional<Client> client = clientManager.getClientByUsername(player.getName());
            if(client.isPresent()){
                client.get().setCallHandler(lobbyController::handleLobby);
            }
        }
    }

}


