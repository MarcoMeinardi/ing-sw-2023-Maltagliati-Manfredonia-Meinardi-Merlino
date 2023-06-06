package controller.game;
import controller.DataBase;
import controller.lobby.ClientNotConnectedException;
import controller.lobby.Lobby;
import controller.lobby.LobbyController;
import javafx.util.Pair;
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

/**
 * This class runs an instance of game and controls it.
 * @author Ludovico, Marco, Lorenzo
 *
 */

public class GameController {

    private final Game game;
    private Lobby lobby;

    private final Iterator<Player> playerIterator;

    private final Object timerLock = new Object();
    private Timer timer = new Timer();
    private boolean isTimerRunning = false;
    private Thread healthyThread;
    private Player currentPlayer;
    private static final Logger logger = Logger.getLogger(GameController.class.getName());

    private final ClientManagerInterface clientManager;

    private final DataBase db = DataBase.getInstance();
    private boolean someoneCompleted = false;
    private final long SOLE_SURVIVOR_TIMER = 60000;
    File saveFile;

    /**
     * Constructor that creates a new game with the specified players.
     * @author Ludovico
     *
     */
    public GameController(Lobby lobby) throws Exception {
        this.lobby = lobby;
        game = new Game(lobby.getPlayers());
        playerIterator = game.iterator();
        currentPlayer = playerIterator.next();
        clientManager = GlobalClientManager.getInstance();
        healthyThread = new Thread(this::checkPlayerHealth);
        healthyThread.start();
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
    }

    public GameController(File saveFile) throws Exception {
        game = Game.loadGame(saveFile);
        clientManager = GlobalClientManager.getInstance();
        playerIterator = game.iterator();
        currentPlayer = playerIterator.next();
        healthyThread = new Thread(this::checkPlayerHealth);
        healthyThread.start();
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
    }

    public Game getGame() {
        return game;
    }

    /**
     * Checks when table needs a refill.
     *
     * @return True if refill needed, false otherwise
     * @author Ludovico
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
     *
     * @param player The player
     * @author Ludovico
     */
    private void addPersonalCockade(Player player) {
        Optional<Cockade> helpCockadePersonal = player.getPersonalObjective().isCompleted(player.getShelf());
        helpCockadePersonal.ifPresent(player::addCockade);
    }

    /**
     * Adds the common cockade to the player's shelf if the player has completed the common objective.
     * Also save the cocades and the new objective values to be returned to the client.
     *
     * @param player The player
     * @param completedObjectives The list of completed objectives
     * @param newCommonObjectivesScores The list of new common objectives scores
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
     * If true adds the first to finish cockade to the player's shelf.
     * @param player The player to check
     */
    private void addFirstToFinish(Player player){
        Optional<Cockade> firstToFinishCockade = player.getShelf().isFirstToFinish();
        if(firstToFinishCockade.isPresent() && !someoneCompleted){
            player.addCockade(firstToFinishCockade.get());
            someoneCompleted = true;
        }
    }

    private void startTimer(){
        synchronized (timerLock){
            logger.info("Starting timer");
            Message message = new Message("Server", "If no one reconnects in 60 seconds the game will end");
            ServerEvent event = ServerEvent.NewMessage(message);
            globalUpdate(event);
            timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            endTimer();
                        }
                    },
                    SOLE_SURVIVOR_TIMER
            );
            isTimerRunning = true;
        }
    }
    /**
     * This function is called when the timer is over
     * it ends the game if there is only one player connected
     * otherwise it ends the turn of the current player and selects a new player
     */
    private void endTimer() {
        synchronized (timerLock){
            nextOrEndGame();
            timer.cancel();
            isTimerRunning = false;
        }
    }

    /**
     * Tries to execute the move requested by the player.
     * @author Marco
     *
     * @param player The player
     * @param positions The positions of the cards to pick
     * @param column The column where the cards will be placed
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

    /**
     * Sends the global update event to all the clients.
     * @param event
     * @author Ludovico, Lorenzo, Marco, Riccardo, Momo
     */
    public void globalUpdate(ServerEvent event) {
        for (Player player : game.getPlayers()) {
            try {
                Optional<ClientInterface> client = clientManager.getClient(player.getName());
                if (client.isPresent() && !client.get().isDisconnected()) {
                    client.get().sendEvent(event);
                }
            } catch(Exception e) {
                logger.warning("oopsy doopsy we got an exceptionussy");
                e.printStackTrace();
            }
        }
    }

    /**
     * Finds the next player that is not disconnected.
     * @return A pair containing a boolean that is true if the game is not finished and an optional player that is Some only if there is a next valid player
     */
    private Pair<Boolean,Optional<Player>> nextNotDisconnected() {
        Optional<Player> nextPlayer = Optional.empty();
        int count = 0;
        while (playerIterator.hasNext()) {
            Player player = playerIterator.next();
            Optional<ClientInterface> client = clientManager.getClient(player.getName());
            if (client.isPresent() && !client.get().isDisconnected()) {
                if (currentPlayer.equals(player)) {
                    return new Pair<>(true, Optional.empty());
                }else{
                    return new Pair<>(true, Optional.of(player));
                }
            }
            count++;
            if(count == game.getPlayers().size()){
                return new Pair<>(false, Optional.empty());
            }
        }
        return new Pair<>(false, Optional.empty());
    }

    /**
     * Handles the game send by the client. Executes all the methods in this class that are needed to process
     * the turns. Utilizes iterator to iterate over the players. Every turn of every player the method checks if
     * the correct player is trying a move, tries to execute the move requested by the player and checks if the
     * common objectives are completed. At the end of turn the method checks if the game is over and in that case
     * checks the personal objectives completed and sends the final ranking to the clients; otherwise it moves to the next turn.
     * Handles when a player sends a message in the chat.
     *
     * @param call The call from the client
     * @param client The client
     * @return The result of the call
     * @author Ludovico, Lorenzo
     */
    public Result handleGame(Call call, ClientInterface client) {
        Result result;
        try{
            switch (call.service()) {
                case CardSelect -> {
                    if(!(call.params() instanceof CardSelect)) {
                        throw new WrongParametersException("CardSelect", call.params().getClass().getName(), "CardSelect");
                    }
                    CardSelect cardSelect = (CardSelect) call.params();
                    String username = client.getUsername();
                    Player player = game.getPlayers().stream().filter(p -> p.getName().equals(username)).findFirst().orElseThrow();
                    synchronized (timerLock){
                        if (!currentPlayer.equals(player) || isTimerRunning) {
                            throw new NotYourTurnException();
                        }
                    }
                    doMove(player, cardSelect.selectedCards(), cardSelect.column());
                    completePlayerTurn(player);
                    result = Result.empty(call.id());
                }
                case GameChatSend -> {
                    if (!(call.params() instanceof Message)) {
                        throw new WrongParametersException("Message", call.params().getClass().getName(), "GameChatSend");
                    }
                    Message new_chat_message = (Message)call.params();
                    ServerEvent event = ServerEvent.NewMessage(new_chat_message);
                    if (new_chat_message.idReceiver().isEmpty()) {
                        globalUpdate(event);
                    } else {
                        Optional<ClientInterface> receiver = clientManager.getClient(new_chat_message.idReceiver().get());
                        if(receiver.isEmpty() || receiver.get().isDisconnected()) {
                            throw new ClientNotFoundException();
                        }
                        receiver.get().sendEvent(event);
                    }

                    result = Result.empty(call.id());
                }
                default -> throw new WrongThreadException();
            }
        } catch(Exception e) {
            result = Result.err(e, call.id());
        }
        return result;
    }

    /**
     * makes the player exit the game
     * and ends the game
     * @author Ludovico, Lorenzo, Marco
     */
    public void exitGame() {
        ArrayList<Cockade> completedObjectives = new ArrayList<>();
        ArrayList<Integer> newCommonObjectivesScores = new ArrayList<>();
        addCommonCockade(currentPlayer, completedObjectives, newCommonObjectivesScores);
        addFirstToFinish(currentPlayer);

        // If no one is connected, don't do end game stuff
        boolean isSomeoneAlive = currentlyConnectedPlayers() > 0;
        LobbyController lobbyController = LobbyController.getInstance();
        for(Player player : game.getPlayers()) {
            Optional<ClientInterface> client = clientManager.getClient(player.getName());
            client.get().setCallHandler(lobbyController::handleLobbySearch);
            if (client.get().getStatus() != ClientStatus.Disconnected) {
                client.get().setStatus(ClientStatus.InLobbySearch);
            } else {
                client.get().setLastValidStatus(ClientStatus.InLobbySearch);
            }
        }

        if (isSomeoneAlive) {
            ScoreBoard scoreBoard = ScoreBoard.create(game).build();
            for(Player player: game.getPlayers()){
                addPersonalCockade(player);
                try{
                    Optional<ClientInterface> client = clientManager.getClient(player.getName());
                    client.get().sendEvent(ServerEvent.End(scoreBoard));
                }catch (Exception e) {
                    logger.warning("Client disconnected while exiting game, they won't receive the final ranking");
                }
            }
            lobbyController.endGame(this);
        } else {
            lobbyController.exitGame(this);
        }
    }

    /**
     * return the players of the game
     *
     * @return the players of the game
     * @author marco
     */
    public ArrayList<Player> getPlayers() {
        synchronized (game.getPlayers()) {
            return game.getPlayers();
        }
    }

    /**
     * return the player with the given username
     *
     * @assumes the player is in the game
     * @param username The username of the player to search
     * @return the player with the given username
     * @throws RuntimeException if the player is not found
     * @author marco
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
     * Return the order of the players
     * @return
     * @author Lorenzo
     */
    public ArrayList<String> getPlayersOrder() {
        ArrayList<String> playersOrder = new ArrayList<>();
        synchronized (game.getPlayers()) {
            for (Player player: game.getPlayers()) {
                playersOrder.add(player.getName());
            }
        }
        return playersOrder;
    }

    private void completePlayerTurn(Player player) {
        ArrayList<Cockade> completedObjectives = new ArrayList<>();
        ArrayList<Integer> newCommonObjectivesScores = new ArrayList<>();
        addCommonCockade(player, completedObjectives, newCommonObjectivesScores);
        addFirstToFinish(player);
        refillTable();
        saveGame();
        Pair<Boolean,Optional<Player>> nextToPlay = nextNotDisconnected();
        if(nextToPlay.getKey()) {
            if(nextToPlay.getValue().isEmpty()){
                startTimer();
            }else{
                Player nextPlayer = nextToPlay.getValue().get();
                Update update = new Update(
                        player.getName(),
                        game.getTabletop().getSerializable(),
                        player.getShelf().getSerializable(),
                        nextPlayer.getName(),
                        completedObjectives,
                        newCommonObjectivesScores
                );
                ServerEvent event = ServerEvent.Update(update);
                globalUpdate(event);
                currentPlayer = nextPlayer;
            }
        }else{
            exitGame();
        }
    }

    /**
     * Prepares the game for the start
     * @param player
     * @throws Exception
     * @author Marco, Lorenzo
     */
    public GameInfo getGameInfo(Player player) throws Exception {
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

    private void saveGame() {
        try {
            game.saveGame(saveFile);
        } catch (IOException e) {
            logger.warning("Failed to save game");
        }
    }

    private void deleteSave() {
        if (!saveFile.delete()) {
            logger.warning("Failed to delete save file (" + saveFile + ")");
        }
    }

    /**
     * Try to find the next available player or end the game if there are no more players connected
     */
    private void nextOrEndGame(){
        Pair<Boolean, Optional<Player>> nextToPlay = nextNotDisconnected();
        if(!nextToPlay.getKey() || nextToPlay.getValue().isEmpty()){
            logger.info("Most of the player are disconnected, closing the game");
            exitGame();
        } else {
            ArrayList<Cockade> completedObjectives = new ArrayList<>();
            ArrayList<Integer> newCommonObjectivesScores = new ArrayList<>();
            addCommonCockade(currentPlayer, completedObjectives, newCommonObjectivesScores);
            saveGame();
            Player nextPlayer = nextToPlay.getValue().get();
            logger.info("Changing player" + nextPlayer.getName());
            Update update = new Update(
                    currentPlayer.getName(),
                    game.getTabletop().getSerializable(),
                    currentPlayer.getShelf().getSerializable(),
                    nextPlayer.getName(),
                    completedObjectives,
                    newCommonObjectivesScores
            );
            ServerEvent event = ServerEvent.Update(update);
            globalUpdate(event);
            currentPlayer = nextPlayer;
        }
    }

    private int currentlyConnectedPlayers(){
        int connectedPlayers = 0;
        for(Player player: game.getPlayers()){
            Optional<ClientInterface> client = clientManager.getClient(player.getName());
            if(client.isPresent() && !client.get().isDisconnected()){
                connectedPlayers++;
            }
        }
        return connectedPlayers;
    }
    private void checkPlayerHealth() {
        logger.info("Checking player health");
        while(true){
            Optional<ClientInterface> client = clientManager.getClient(currentPlayer.getName());
            if(client.isEmpty() || client.get().isDisconnected()){
                logger.info("Player " + currentPlayer.getName() + " disconnected while playing");
                Pair<Boolean, Optional<Player>> nextToPlay = nextNotDisconnected();
                if(!nextToPlay.getKey() || nextToPlay.getValue().isEmpty()){
                    logger.info("All players disconnected, ending game");
                    exitGame();
                    return;
                } else {
                    completePlayerTurn(currentPlayer);
                }
            }
            synchronized (timerLock){
                if(isTimerRunning && currentlyConnectedPlayers() > 1){
                    isTimerRunning = false;
                    timer.cancel();
                    logger.info("Timer stopped trying new turn");
                    completePlayerTurn(currentPlayer);
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.warning("Thread interrupted while checking player health");
                e.printStackTrace();
                return;
            }
        }
    }
}
