package controller.game;
import controller.lobby.Lobby;
import controller.lobby.LobbyController;
import model.*;
import network.*;
import network.parameters.CardSelect;
import network.parameters.Message;
import network.parameters.StartingInfo;
import network.parameters.Update;
import network.parameters.WrongParametersException;
import network.rpc.server.ClientManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class runs an instance of game and controls it.
 * @author Ludovico, Marco, Lorenzo
 *
 */

public class GameController {

    private final Game game;

    private static final int DISCONNECTION_TIMOUT = 10000;

    private static final int MAX_DISCONNECTION_TRIES = 18;

    private Boolean gamePaused = false;
    private final Iterator<Player> playerIterator;
    private Player currentPlayer;
	private Thread globalUpdateThread = null;
    private static final Logger logger = Logger.getLogger(GameController.class.getName());

    private final ClientManagerInterface clientManager;

    /**
     * Constructor that creates a new game with the specified players.
     * @author Ludovico
     *
     */
    public GameController(Lobby lobby) throws Exception {
        game = new Game(lobby.getPlayers());
        playerIterator = game.iterator();
        currentPlayer = playerIterator.next();
        clientManager = ClientManager.getInstance();
        for (Player player : game.getPlayers()) {
            ClientInterface client = clientManager.getClient(player.getName()).orElseThrow();
            client.setCallHandler(this::handleGame);
            sendStartInfo(player);
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
            for (int j = i + 1; j < positions.size(); j++) {
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
                (p1.x() != p2.x() || p1.x() != p3.x()) &&
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
        while (true) {
            List<String> connectedPlayers = game.getPlayers().stream().filter(p -> clientManager.isClientConnected(p.getName())).map(Player::getName).toList();
            if (connectedPlayers.size() == game.getPlayers().size()) {
                try {
                    for(Player player: game.getPlayers()){
                        Optional<ClientInterface> client = clientManager.getClient(player.getName());
                        if(client.isEmpty()){
                            throw new RuntimeException("Client not found" + player.getName());
                        }
                        client.get().send(ServerEvent.Resume(null));
                    }
                    return;
                }catch (Exception e){
                    logger.warning("Error while sending resume event to client" + e.getMessage());
                }
            }
            for (String player:  connectedPlayers) {
                Optional<ClientInterface> client = clientManager.getClient(player);
                if(client.isPresent()){
                    try{
                        client.get().send(ServerEvent.Pause("waiting for all players to connect"));
                    }catch (Exception e){
                        logger.warning("Error while sending pause event to client" + e.getMessage());
                    }
                }
            }

            try {
                Thread.sleep(DISCONNECTION_TIMOUT);
            } catch (InterruptedException e) {
                logger.warning("Error while sleeping" + e.getMessage());
            }

            count++;

            if (count == MAX_DISCONNECTION_TRIES) {
                exitGame();
            }

        }

    }

    public void globalUpdate(ServerEvent event) {
        while (true) {
            try {
                for(Player player : game.getPlayers()) {
                    Optional<ClientInterface> client = clientManager.getClient(player.getName());
                    if(client.isEmpty()) {
                        throw new RuntimeException("Client not found" + player.getName());
                    }
                    client.get().send(event);
                }
                break;
            } catch (Exception e) {
                logger.warning("Error while sending global update event to client" + e.getMessage());
                checkDisconnection();
            }
        }
    }

    public Result handleGame(Call call, ClientInterface client){
        Result result;
        try{
            if(globalUpdateOnGoing()){
                throw new WaitingForUpdateException();
            }
            switch (call.service()){
                case CardSelect -> {
                    if(!(call.params() instanceof CardSelect)){
                        throw new WrongParametersException("CardSelect", call.params().getClass().getName(), "CardSelect");
                    }
                    CardSelect cardSelect = (CardSelect) call.params();
                    String username = client.getUsername();
                    Player player = game.getPlayers().stream().filter(p -> p.getName().equals(username)).findFirst().orElseThrow();
                    if(!currentPlayer.equals(player)){
                        throw new NotYourTurnException();
                    }
                    doMove(player, cardSelect.selectedCards(), cardSelect.column());
                    addCommonCockade(player);
                    refillTable();
                    if(playerIterator.hasNext()){
						currentPlayer = playerIterator.next();
                        Update update = new Update(username, game.getTabletop().getSerializable(), player.getShelf().getSerializable(), currentPlayer.getName());
                        setGlobalUpdate(ServerEvent.Update(update));
                    }else{
                        for(Player p : game.getPlayers()){
                            addPersonalCockade(player);
                        }
                        ScoreBoard scoreBoard = new ScoreBoard(game);
						setGlobalUpdate(ServerEvent.End(scoreBoard));
                        exitGame();
                    }
                    result = Result.empty(call.id());
                }
                case GameChatSend -> {
                    if(!(call.params() instanceof String)){
                        throw new WrongParametersException("String", call.params().getClass().getName(), "GameChatSend");
                    }
                    Message message = new Message(client.getUsername(), (String) call.params());
                    ServerEvent event = ServerEvent.NewMessage(message);
                    setGlobalUpdate(event);
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

	private void setGlobalUpdate(ServerEvent event) throws Exception{
        if(globalUpdateThread != null && globalUpdateThread.isAlive()){
            throw new GlobalUpdateAlreadyOngoingException();
        }
		globalUpdateThread = new Thread(() -> globalUpdate(event));
		globalUpdateThread.start();
	}

    public void exitGame() {
        LobbyController lobbyController = LobbyController.getInstance();
        for(Player player: game.getPlayers()){
            Optional<ClientInterface> client = clientManager.getClient(player.getName());
            if(client.isPresent()){
                client.get().setCallHandler(lobbyController::handleLobbySearch);  // TODO block toxic boys
            }
        }
        lobbyController.endGame(this);
    }

    public ArrayList<String> getPlayersOrder(){
        ArrayList<String> playersOrder = new ArrayList<>();
        synchronized (game.getPlayers()){
            for(Player player: game.getPlayers()){
                playersOrder.add(player.getName());
            }
        }
        return playersOrder;
    }

    private void sendStartInfo(Player player) throws Exception {
        ArrayList<Card[][]> shelves = game.getPlayers().stream().map(p -> p.getShelf().getSerializable()).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> players = game.getPlayers().stream().map(Player::getName).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> commonObjectives = game.getCommonObjectives().stream().map(CommonObjective::getName).collect(Collectors.toCollection(ArrayList::new));
        ServerEvent toSend = ServerEvent.Start(new StartingInfo(
            game.getTabletop().getSerializable(),
            players,
            shelves,
            commonObjectives,
            player.getPersonalObjective().getName()
        ));
        ClientInterface client = clientManager.getClient(player.getName()).orElseThrow();
        client.send(toSend);
    }
}
