package controller.game;
import controller.DataBase;
import controller.IdentityTheftException;
import controller.MessageTooLongException;
import controller.NotHostException;
import controller.lobby.ClientNotConnectedException;
import controller.lobby.Lobby;
import controller.lobby.LobbyController;
import model.*;
import network.*;
import network.errors.ClientNotFoundException;
import network.parameters.CardSelect;
import network.parameters.Message;
import network.parameters.GameInfo;
import network.parameters.Update;
import network.errors.WrongParametersException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static network.Server.SERVER_NAME;

/**
 * This class runs an instance of game and controls it.
 */
public class GameController {

    private final Game game;
    private Lobby lobby;

    private final Iterator<Player> playerIterator;

    private boolean isPaused = false;
    private final Thread disconnectionChecker;
    private Player currentPlayer;
    private static final Logger logger = Logger.getLogger(GameController.class.getName());

    private final ClientManagerInterface clientManager;
    private ArrayList<Boolean> playerDisconnected;

    private final DataBase db = DataBase.getInstance();
    private final File saveFile;

    private boolean someoneCompleted = false;
    private static final long SOLE_SURVIVOR_TIMER = 60;
    private static final long DISCONNECTION_CHECK_INTERVAL = 1;
    private int pauseCounter;

    /**
     * Constructor that creates a new game with the specified players.
     * @param lobby The lobby containing the players
     * @author Ludovico
     */
    public GameController(Lobby lobby) throws Exception {
        this.lobby = lobby;
        game = new Game(lobby.getPlayers());
        playerIterator = game.iterator();
        currentPlayer = playerIterator.next();
        clientManager = GlobalClientManager.getInstance();

        for (Player player : game.getPlayers()) {
            ClientInterface client = clientManager.getClient(player.getName()).orElseThrow();
            if(client.isDisconnected()){
                throw new ClientNotConnectedException();
            }
            client.setCallHandler(this::handleGame);
            GameInfo toSend = getGameInfo(player);
            client.sendEvent(ServerEvent.Start(toSend));
        }
        saveFile = db.get(game.getPlayers().stream().map(Player::getName).collect(Collectors.toCollection(HashSet::new)));

        playerDisconnected = new ArrayList<>(game.getPlayers().size());
        for (int i = 0; i < game.getPlayers().size(); i++) {
            playerDisconnected.add(false);
        }
        disconnectionChecker = new Thread(this::checkDisconnections);
        disconnectionChecker.start();
        saveGame();
    }

    /**
     * Constructor that loads a game from a save file.
     * @param saveFile The path of the save file
     * @param lobby The lobby containing the players
     * @author Marco
     */
    public GameController(File saveFile, Lobby lobby) throws Exception {
        this.lobby = lobby;
        game = Game.loadGame(saveFile);
        clientManager = GlobalClientManager.getInstance();
        playerIterator = game.iterator();
        currentPlayer = playerIterator.next();
        for (Player player : game.getPlayers()) {
            ClientInterface client = clientManager.getClient(player.getName()).orElseThrow();
            if(client.isDisconnected()){
                throw new ClientNotConnectedException();
            }
            client.setCallHandler(this::handleGame);
            GameInfo toSend = getGameInfo(player);
            client.sendEvent(ServerEvent.Start(toSend));
        }
        this.saveFile = db.get(game.getPlayers().stream().map(Player::getName).collect(Collectors.toCollection(HashSet::new)));

        playerDisconnected = new ArrayList<>(game.getPlayers().size());
        for (int i = 0; i < game.getPlayers().size(); i++) {
            playerDisconnected.add(false);
        }
        disconnectionChecker = new Thread(this::checkDisconnections);
        disconnectionChecker.start();
    }

    /**
     * Getter for the `game` object
     * @return The current game object
     * @author Marco
     */
    public Game getGame() {
        return game;
    }

    /**
     * Checks if the tabletop needs a refill.
     * @return true if refill needed, false otherwise
     * @author Ludovico
     */
    private boolean checkRefillTable() {
        return game.getTabletop().needRefill();
    }

    /**
     * Refills the table if needed.
     * @author Ludovico
     */
    private void refillTable() {
        if (checkRefillTable()) {
            game.getTabletop().fillTable();
        }
    }

    /**
     * Check if the given player has completed the personal objective,
     * if this is the case, add the corresponding cockade to him.
     * @param player The player to consider
     * @author Ludovico
     */
    private void addPersonalCockade(Player player) {
        Optional<Cockade> helpCockadePersonal = player.getPersonalObjective().isCompleted(player.getShelf());
        helpCockadePersonal.ifPresent(player::addCockade);
    }

    /**
     * Check for completed common objectives by the player.
     * This function takes two parameters in addition to the player, references to two `ArrayList`s
     * in which the caller will find the cockades for the completed objectives and the corresponding updated scores.
     * @param player The player to consider
     * @param completedObjectives A reference to the list of completed objectives
     * @param newCommonObjectivesScores A reference to the list of updated scores
     * @author Ludovico, Marco
     */
    private void addCommonCockade(Player player, ArrayList<Cockade> completedObjectives, ArrayList<Integer> newCommonObjectivesScores) {
        for (CommonObjective objective : game.getCommonObjectives()) {
            Optional<Cockade> cockade = objective.isCompleted(player.getShelf(), player.getName());
            if (cockade.isPresent()) {
                player.addCockade(cockade.get());
                completedObjectives.add(cockade.get());
                newCommonObjectivesScores.add(objective.getValue());
            }
        }
    }

    /**
     * Checks if the player is the first to finish the game.
     * If true, add the first to finish cockade to the player's shelf.
     * @param player The player to check
     */
    private void addFirstToFinish(Player player){
        Optional<Cockade> firstToFinishCockade = player.getShelf().getFinishCockade();
        if(firstToFinishCockade.isPresent() && !someoneCompleted){
            player.addCockade(firstToFinishCockade.get());
            someoneCompleted = true;
        }
    }

    /**
     * Check if the move selected by the player is valid and make it.
     * @param player The player making the move
     * @param positions The positions of the cards to pick
     * @param column The column where the cards will be placed
     * @throws InvalidMoveException If the move is not valid
     * @author Marco, Ludovico
     */
    private void doMove(Player player, ArrayList<Point> positions, int column) throws InvalidMoveException {
        if (positions.size() < 1 || positions.size() > 3) {
            throw new InvalidMoveException("Invalid number of picked cards");
        }

        for (int i = 0; i < positions.size(); i++) {
            if (player.getShelf().getCard(Shelf.ROWS - i - 1, column).isPresent()) {
                throw new InvalidMoveException("Not enough space in column");
            }
        }

        // Check that all the cards are adjacent
        for (int i = 0; i < positions.size() - 1; i++) {
            for (int j = i + 1; j < positions.size(); j++) {
                int dist = positions.get(i).distance(positions.get(j));
                if (dist != 1 && (positions.size() != 3 || dist != 2)) {
                    throw new InvalidMoveException("Cards are not pickable (not adjacent)");
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

        ArrayList<Card> cards = new ArrayList<>();
        for (Point position : positions) {
            cards.add(game.getTabletop().pickCard(position.y(), position.x()));
        }
        player.getShelf().insert(column, cards);
    }

    /**
     * Send the global update event to all the clients.
     * @param event The event to send
     * @author Lorenzo
     */
    public void globalUpdate(ServerEvent event) {
        for (Player player : game.getPlayers()) {
            try {
                Optional<ClientInterface> client = clientManager.getClient(player.getName());
                if (client.isPresent() && !client.get().isDisconnected()) {
                    client.get().sendEvent(event);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Finds the next player that is not disconnected.
     * @return The player that will do the next turn, or empty if everyone is disconnected
     * @author Marco, Lorenzo
     */
    private Optional<Player> nextNotDisconnected() {
        int count = 0;
        while (playerIterator.hasNext()) {
            Player player = playerIterator.next();
            Optional<ClientInterface> client = clientManager.getClient(player.getName());
            if (client.isPresent() && !client.get().isDisconnected()) {
                return Optional.of(player);
            }
            count++;
            if(count == game.getPlayers().size()){
                return Optional.of(currentPlayer);
            }
        }
        return Optional.empty();
    }

    /**
     * Handles the game events sent by the client.
     * The handle events are: card selection, messages and game stop.
     * All the actions are checked to ensure no cheating.
     * - `CardSelect`: the player selects the cards to pick and the column where to place them.
     *   If the move is valid, and it is the turn of the requesting player,
     *   the move is made and the game is updated.
     *   If the move brings to the game end, the personal objectives are applied,
     *   the scoreboard is created and sent to all the clients,
     *   otherwise the standard checks are made (common objectives,
     *   first to finish) and the new game state is sent.
     * - `GameChatSend`: the player sends a message to the chat.
     * The message is checked and sent to the specified client/s.
     * - `GameStop`: the host stops the game.
     * The game is stopped and the clients are notified.
     *
     * @param call A `Call` object containing the event requested by the client
     * @param client The client object
     * @return A `Result` object containing the result of the event handling
     * @author Ludovico, Lorenzo, Marco
     */
    public Result handleGame(Call call, ClientInterface client) {
        Result result;
        try {
            switch (call.service()) {
                case CardSelect -> {
                    if(!(call.params() instanceof CardSelect)) {
                        throw new WrongParametersException("CardSelect", call.params().getClass().getName(), "CardSelect");
                    }
                    synchronized (disconnectionChecker) {
                        CardSelect cardSelect = (CardSelect)call.params();
                        String username = client.getUsername();
                        Player player = game.getPlayers().stream().filter(p -> p.getName().equals(username)).findFirst().orElseThrow();
                        if (!currentPlayer.equals(player)) {
                            throw new NotYourTurnException();
                        } else if (isPaused) {
                            throw new GamePausedException();
                        }
                        doMove(player, cardSelect.selectedCards(), cardSelect.column());
                        if (completePlayerTurn(player)) {
                            disconnectionChecker.interrupt();
                        }
                        result = Result.empty(call.id());
                    }
                }
                case GameChatSend -> {
                    if (!(call.params() instanceof Message newChatMessage)) {
                        throw new WrongParametersException("Message", call.params().getClass().getName(), "GameChatSend");
                    }
                    if (newChatMessage.message().length() > 100) {
                        throw new MessageTooLongException();
                    }
                    if (!newChatMessage.idSender().equals(client.getUsername())) {
                        throw new IdentityTheftException();
                    }
                    ServerEvent event = ServerEvent.NewMessage(newChatMessage);
                    if (newChatMessage.idReceiver().isEmpty()) {
                        globalUpdate(event);
                    } else {
                        Optional<ClientInterface> receiver = clientManager.getClient(newChatMessage.idReceiver().get());
                        if(receiver.isEmpty() || receiver.get().isDisconnected()) {
                            throw new ClientNotFoundException();
                        }
                        receiver.get().sendEvent(event);
                    }

                    result = Result.empty(call.id());
                }
                case ExitGame -> {
                    synchronized (disconnectionChecker) {
                        if (!lobby.isHost(client.getUsername())) {
                            throw new NotHostException();
                        }
                        logger.info("Game stopped by host");
                        globalUpdate(ServerEvent.ExitGame());
                        exitGame(false);
                        disconnectionChecker.interrupt();
                        result = Result.empty(call.id());
                    }
                }
                default -> throw new WrongThreadException();
            }
        } catch (Exception e) {
            result = Result.err(e, call.id());
        }
        return result;
    }

    /**
     * Method to handle the end of the game.
     * If endGame is true, end the game, send the final ranking to the clients and cancel the game from disk
     * @param endGame If set to true, completely end the game, otherwise just make the players exit the game, but keep the save
     * @author Ludovico, Lorenzo, Marco
     */
    public void exitGame(boolean endGame) {
        ArrayList<Cockade> completedObjectives = new ArrayList<>();
        ArrayList<Integer> newCommonObjectivesScores = new ArrayList<>();
        addCommonCockade(currentPlayer, completedObjectives, newCommonObjectivesScores);
        addFirstToFinish(currentPlayer);

        LobbyController lobbyController = LobbyController.getInstance();
        for(Player player : game.getPlayers()) {
            if (endGame) {
                addPersonalCockade(player);
            }
            Optional<ClientInterface> client = clientManager.getClient(player.getName());
            client.get().setCallHandler(lobbyController::handleLobbySearch);
            if (client.get().getStatus() != ClientStatus.Disconnected) {
                client.get().setStatus(ClientStatus.InLobbySearch);
            } else {
                client.get().setLastValidStatus(ClientStatus.InLobbySearch);
            }
        }

        if (endGame) {
            ScoreBoard scoreBoard = new ScoreBoard(game);
            for(Player player: game.getPlayers()){
                try {
                    Optional<ClientInterface> client = clientManager.getClient(player.getName());
                    client.get().sendEvent(ServerEvent.End(scoreBoard));
                } catch (Exception e) {
                    logger.warning("Client disconnected while exiting game, they won't receive the final ranking");
                }
            }
            deleteSave();
            lobbyController.endGame(this);
        } else {
            lobbyController.exitGame(this);
        }
    }

    /**
     * Getter for the players `ArrayList`
     * @return The players of the game
     * @author Marco
     */
    public ArrayList<Player> getPlayers() {
        synchronized (game.getPlayers()) {
            return game.getPlayers();
        }
    }

    /**
     * Return the `Player` object with the given username
     * @param username The username of the player to search
     * @return the `Player` object with the given username
     * @throws RuntimeException if the player is not found
     * @author Marco
     */
    public Player getPlayer(String username) {
        synchronized (game.getPlayers()) {
            for (Player player : game.getPlayers()) {
                if (player.getName().equals(username)) {
                    return player;
                }
            }
        }
        throw new RuntimeException("Player not found");
    }

    /**
     * Method to update the scoring status of the game. It is called at the end of each turn.
     * It checks for common objectives, first to finish, tabletop refill and game end.
     * If the game is over, call the `exitGame` method with `true` as parameter, if only one player
     * is connected, set pause to true, otherwise update all the client with the new game state.
     * @param player The player that just finished the turn
     * @return true if the game is over, false otherwise
     * @author Marco, Lorenzo
     */
    private boolean completePlayerTurn(Player player) {
        ArrayList<Cockade> completedObjectives = new ArrayList<>();
        ArrayList<Integer> newCommonObjectivesScores = new ArrayList<>();
        addCommonCockade(player, completedObjectives, newCommonObjectivesScores);
        addFirstToFinish(player);
        refillTable();
        Optional<Player> nextToPlay = nextNotDisconnected();
        saveGame();
        if (nextToPlay.isEmpty()) {  // Game is over
            exitGame(true);
            return true;
        } else if (nextToPlay.get().equals(currentPlayer)) {  // Sole survivor or all disconnected, waiting for the checker thread to do stuffs
            isPaused = true;  // Prevent cheating for sole survivor
        } else {
            Update update = new Update(
                player.getName(),
                game.getTabletop().getSerializable(),
                player.getShelf().getSerializable(),
                nextToPlay.get().getName(),
                completedObjectives,
                newCommonObjectivesScores
            );
            ServerEvent event = ServerEvent.Update(update);
            globalUpdate(event);
            currentPlayer = nextToPlay.get();
        }
        return false;
    }

    /**
     * Get a `GameInfo` object containing the state of the game after with all the visible information
     * for the given player.
     * @param player The player requesting the information
     * @author Marco
     */
    public GameInfo getGameInfo(Player player) {
        ArrayList<Card[][]> shelves = game.getPlayers().stream().map(p -> p.getShelf().getSerializable()).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> players = game.getPlayers().stream().map(Player::getName).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> commonObjectives = game.getCommonObjectives().stream().map(CommonObjective::getName).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Integer> commonObjectivesPoints = game.getCommonObjectives().stream().map(CommonObjective::getValue).collect(Collectors.toCollection(ArrayList::new));
        return new GameInfo(
            lobby,
            game.getTabletop().getSerializable(),
            players,
            shelves,
            commonObjectives,
            commonObjectivesPoints,
            player.getPersonalObjective().getName(),
            currentPlayer.getName()
        );
    }

    /**
     * Call the `Game`'s method to save to file the current state.
     * @author Marco
     */
    private void saveGame() {
        try {
            game.saveGame(saveFile);
        } catch (IOException e) {
            logger.warning("Failed to save game");
        }
    }

    /**
     * Delete the save for this game.
     * @author Marco
     */
    private void deleteSave() {
        if (!saveFile.delete()) {
            logger.warning("Failed to delete save file (" + saveFile + ")");
        }
    }

    /**
     * Method that runs in a thread to handle client disconnections.
     * It runs every 'DISCONNECTION_CHECK_INTERVAL`'s seconds.
     * For every player, if he has changed state (connected/disconnected) since the last check,
     * all the clients are notified.
     * If the disconnected player was the lobby host, the ownership of the lobby is passed to the next
     * player in the list.
     * If the disconnected player was the current player, his turn is ended.
     * If only one player is connected, the game is paused.
     * If only one player remains in the game for more than `SOLE_SURVIVOR_TIMER checks,
     * it is decreed as the winner and the game is terminated.
     * If no one is connected anymore, the game is ended keeping the save file.
     * @throws RuntimeException If the game state is broken and an illegal action happens
     * @author Marco
     */
    private void checkDisconnections() {
        boolean wasPaused = false;
        while (true) {
            try {
                Thread.sleep(DISCONNECTION_CHECK_INTERVAL * 1000);
            } catch (InterruptedException e) {
                return;
            }
            synchronized (disconnectionChecker) {
                int activePlayers = 0; 
                boolean currentPlayerActive = false;
                ArrayList<Player> players = game.getPlayers();
                for (int i = 0; i < players.size(); i++) {
                    boolean wasDisconnected = playerDisconnected.get(i);
                    Optional<ClientInterface> client = clientManager.getClient(players.get(i).getName());
                    if (client.isPresent() && !client.get().isDisconnected()) {
                        activePlayers++;
                        if (wasDisconnected) {
                            playerDisconnected.set(i, false);
                            logger.info("Player " + players.get(i).getName() + " reconnected");
                            try {
                                lobby.addPlayer(players.get(i).getName());
                            } catch (Exception e) {
                                throw new RuntimeException("Broken reconnection in game");
                            }
                            ServerEvent event = ServerEvent.Join(players.get(i).getName());
                            globalUpdate(event);
                        }
                        if (players.get(i).equals(currentPlayer)) {
                            currentPlayerActive = true;
                        }
                    } else {
                        if (!wasDisconnected) {
                            playerDisconnected.set(i, true);
                            logger.info("Player " + players.get(i).getName() + " disconnected");
                            try {
                                lobby.removePlayer(players.get(i).getName());
                            } catch (Exception e) {
                                throw new RuntimeException("Broken left in game");
                            }
                            ServerEvent event = ServerEvent.Leave(players.get(i).getName());
                            globalUpdate(event);
                        }
                        if (players.get(i).equals(currentPlayer)) {
                            currentPlayerActive = false;
                        }
                    }
                }

                if (isPaused) pauseCounter++;

                if (activePlayers == 0) {
                    logger.info("All players disconnected, closing game");
                    exitGame(false);
                    return;
                }
                if (!currentPlayerActive) {
                    logger.info("Current player disconnected, skipping turn");
                    if (completePlayerTurn(currentPlayer)) {
                        return;
                    }
                }
                if (activePlayers == 1) {
                    if (!wasPaused) {
                        isPaused = true;
                        pauseCounter = 0;
                        logger.info("Game paused");
                        ServerEvent event = ServerEvent.NewMessage(new Message(SERVER_NAME, String.format("Game paused if no one reconnects in %d seconds the game will end", SOLE_SURVIVOR_TIMER)));
                        globalUpdate(event);
                    } else if (pauseCounter >= SOLE_SURVIVOR_TIMER) {
                        logger.info("Timeout expired, ending game");
                        exitGame(true);
                        return;
                    }
                }
                if (activePlayers > 1 && wasPaused) {
                    isPaused = false;
                    logger.info("Game resumed");
                    ServerEvent event = ServerEvent.NewMessage(new Message(SERVER_NAME, "Game resumed"));
                    globalUpdate(event);
                }
                wasPaused = isPaused;
            }
        }
    }
}
