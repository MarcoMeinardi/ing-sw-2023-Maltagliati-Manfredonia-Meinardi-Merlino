package controller.game;
import controller.DataBase;
import controller.lobby.Lobby;
import controller.lobby.LobbyController;
import model.*;
import network.*;
import network.parameters.CardSelect;
import network.parameters.Message;
import network.parameters.GameInfo;
import network.parameters.Update;
import network.parameters.WrongParametersException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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

    private final Iterator<Player> playerIterator;
    private Player currentPlayer;
    private static final Logger logger = Logger.getLogger(GameController.class.getName());

    private final ClientManagerInterface clientManager;

    private final DataBase db = DataBase.getInstance();
    File saveFile;

    /**
     * Constructor that creates a new game with the specified players.
     * @author Ludovico
     *
     */
    public GameController(Lobby lobby) throws Exception {
        game = new Game(lobby.getPlayers());
        playerIterator = game.iterator();
        currentPlayer = playerIterator.next();
        clientManager = GlobalClientManager.getInstance();
        for (Player player : game.getPlayers()) {
            ClientInterface client = clientManager.getClient(player.getName()).orElseThrow();
            client.setCallHandler(this::handleGame);
            GameInfo toSend = getGameInfo(player);
            client.sendEvent(ServerEvent.Start(toSend));
        }

        saveFile = db.get(game.getPlayers().stream().map(Player::getName).collect(Collectors.toCollection(HashSet::new)));
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
            Optional<Cockade> cockade = objective.isCompleted(player.getShelf());
            if (cockade.isPresent()) {
                player.addCockade(cockade.get());
                completedObjectives.add(cockade.get());
                newCommonObjectivesScores.add(objective.getValue());
            }
        }
    }

    /**
     * Returns the final ranks of the players.
     * @author Ludovico, Marco
     *
     * @return The final ranks of the players
     * @author Ludovico
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
     * checks for disconnected players in the game and sends a resume event to the clients if all the players are connected
     * @author Ludovico, Lorenzo, Marco, Riccardo
     */

    /**
     * Sends the global update event to all the clients.
     * @param event
     * @author Ludovico, Lorenzo, Marco, Riccardo, Momo
     */
    public void globalUpdate(ServerEvent event) {
        for(Player player : game.getPlayers()){
            try {
                ClientInterface client = clientManager.getClient(player.getName()).get();
                client.sendEvent(event);
            } catch(Exception e) {
                logger.warning("oopsy doopsy we got an exceptionussy");
                e.printStackTrace();
            }
        }
    }

    private Optional<Player> nextNotDisconnected(){
        Optional<Player> nextPlayer = Optional.empty();
        int count = 0;
        while(playerIterator.hasNext()){
            Player player = playerIterator.next();
            if(clientManager.getClient(player.getName()).isPresent()){
                return Optional.of(player);
            }
            count++;
            if(count == game.getPlayers().size()){
                break;
            }
        }
        return nextPlayer;
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
    public Result handleGame(Call call, ClientInterface client){
        Result result;
        try{
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
                    ArrayList<Cockade> completedObjectives = new ArrayList<>();
                    ArrayList<Integer> newCommonObjectivesScores = new ArrayList<>();
                    addCommonCockade(player, completedObjectives, newCommonObjectivesScores);
                    refillTable();
                    Optional<Player> nextPlayer = nextNotDisconnected();
                    if(nextPlayer.isPresent()){
                        currentPlayer = nextPlayer.get();
                        Update update = new Update(
                            username,
                            game.getTabletop().getSerializable(),
                            player.getShelf().getSerializable(),
                            currentPlayer.getName(),
                            completedObjectives,
                            newCommonObjectivesScores
                        );
                        globalUpdate(ServerEvent.Update(update));
                        saveGame();
                    }else{
                        for(Player p : game.getPlayers()){
                            addPersonalCockade(p);
                        }
                        ScoreBoard scoreBoard = new ScoreBoard(game);
                        globalUpdate(ServerEvent.End(scoreBoard));
                        deleteSave();
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
                    globalUpdate(event);
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
    /**
     * makes the player exit the game
     * and ends the game
     * @author Ludovico, Lorenzo, Marco
     */

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

    public ArrayList<String> getPlayersOrder(){
        ArrayList<String> playersOrder = new ArrayList<>();
        synchronized (game.getPlayers()){
            for(Player player: game.getPlayers()){
                playersOrder.add(player.getName());
            }
        }
        return playersOrder;
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
            game.getTabletop().getSerializable(),
            players,
            shelves,
            commonObjectives,
            commonObjectivesPoints,
            player.getPersonalObjective().getName(),
            currentPlayer.getName()
        );
    }

    private void saveGame() throws IOException {
        game.saveGame(saveFile);
    }

    private void deleteSave() {
        if (!saveFile.delete()) {
            logger.warning("Failed to delete save file (" + saveFile + ")");
        }
    }
}
