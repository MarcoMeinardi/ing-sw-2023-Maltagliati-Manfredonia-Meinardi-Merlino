package controller.lobby;

import controller.DataBase;
import controller.IdentityTheftException;
import controller.MessageTooLongException;
import controller.NotHostException;
import controller.game.GameController;
import model.Player;
import network.*;
import network.parameters.LobbyCreateInfo;
import network.parameters.Message;
import network.errors.WrongParametersException;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LobbyController extends Thread {
    private static LobbyController instance = null;
    private HashMap<String, Lobby> lobbies = new HashMap<>();
    private ArrayList<GameController> games = new ArrayList<>();
    private DataBase db = DataBase.getInstance();
    private final ClientManagerInterface clientManager;

    private static final String SAVESTATES_DIRECTORY = "save-states";
    private static final String SAVESTATES_PREFIX = "save_";

    /**
     * Initializes the `clientManager` variable by calling the `getInstance()` method of the `GlobalClientManager` class.
     * If an exception is caught during this process, it throws a `RuntimeException` with the message "Cannot get client manager instance".
     * The constructor is marked as private, which means it can only be accessed within the `LobbyController` class itself.
     *
     * @throws RuntimeException if an exception is caught during the initialization of the `clientManager` variable
     * @author Marco
     */

    private LobbyController() {
        try {
            clientManager = GlobalClientManager.getInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot get client manager instance");
        }
    }

    /**
     * method that returns an instance of the `LobbyController` class.
     * It uses the Singleton design pattern to ensure that only one instance of the class is created.
     * If the `instance` variable is null, it creates a new instance of the `LobbyController` class and starts it.
     * If an exception occurs during the creation or starting of the instance, it prints the stack trace.
     * Finally, it returns the instance variable.
     *
     * @return the instance of the `LobbyController` class
     * @author Riccardo, Lorenzo
     */
    public static LobbyController getInstance() {
        if (instance == null) {
            try {
                instance = new LobbyController();
                instance.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    /**
     * This method continuously checks the status of players in lobbies and handles disconnections.
     * It sleeps for 1000 milliseconds between each iteration.
     * If a player is disconnected, they are removed from the lobby and their status is updated.
     * The method also sets the call handler for disconnected clients to the LobbyController's handleLobbySearch method.
     * If a client is not found in the ClientManager, a ClientNotFoundException is thrown.
     * Any exceptions that occur during execution are printed to the standard error stream
     * @author Riccardo, Lorenzo
     */
    public void run() {
        try {
            while (true) {
                Thread.sleep(1000);
                ArrayList<String> toRemove = new ArrayList<>();
                synchronized (lobbies) {
                    for (Lobby lobby : lobbies.values()) {
                        ArrayList<String> players = lobby.getPlayers();
                        for (String player : players) {
                            Optional<ClientInterface> client = clientManager.getClient(player);
                            if (client.isEmpty() || client.get().isDisconnected()) {
                                toRemove.add(player);
                                client.get().setCallHandler(LobbyController.getInstance()::handleLobbySearch);
                            }
                        }
                    }
                    for (String player : toRemove) {
                        leaveLobby(player);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  method that searches for a lobby containing a player with a given name.
     *  It takes in a String parameter `playerName` and returns a `Lobby` object.
     *  If the player is found in a lobby, the method returns that lobby.
     *  If the player is not found in any lobby, it throws a `LobbyNotFoundException`.
     *  The method is synchronized on the `lobbies` object to ensure thread safety.
     *
     *  @param playerName the name of the player to search for
     *  @return the Lobby object that contains the player
     *  @throws LobbyNotFoundException if the player is not found in any lobby
     *  @author Riccardo, Lorenzo
     */
    public Lobby findPlayerLobby(String playerName) throws LobbyNotFoundException {
        synchronized (lobbies){
            for(Lobby lobby : lobbies.values()){
                if(lobby.getPlayers().contains(playerName)){
                    return lobby;
                }
            }
        }
        throw new LobbyNotFoundException();
    }

    /**
     * method that creates a new lobby with a given name and host.
     * It first checks if a lobby with the same name already exists in the `lobbies` map, and if so,
     * it throws a `LobbyAlreadyExistsException`.
     * If not, it creates a new `Lobby` object with the given name and host,
     * adds it to the `lobbies` map, and returns the new `Lobby` object.
     * The `synchronized` block ensures that the `lobbies` map is accessed in a thread-safe manner.
     *
     * @param lobbyname the name of the lobby to create
     * @param host the name of the player who will be the host of the lobby
     * @return the created Lobby object
     * @throws LobbyAlreadyExistsException if a lobby with the same name already exists
     * @author Riccardo, Lorenzo
     */
    public Lobby createLobby(String lobbyname, String host) throws LobbyAlreadyExistsException {
        if(lobbies.containsKey(lobbyname)){
            throw new LobbyAlreadyExistsException();
        }
        Lobby lobby = new Lobby(lobbyname, host);
        synchronized (lobbies){
            lobbies.put(lobbyname, lobby);
        }
        return lobby;
    }

    /**
     * Returns the list of lobbies.
     *
     * @return the list of lobbies
     * @author Riccardo, Lorenzo
     */
    public ArrayList<Lobby> getLobbies() {
        synchronized (lobbies){
            return new ArrayList<>(lobbies.values());
        }
    }

    /**
     * method that allows a player to join a lobby.
     *
     * @param lobbyname the name of the lobby to join
     * @param player the name of the player to join the lobby
     * @return the Lobby object that the player has joined
     * @throws LobbyNotFoundException if the specified lobby does not exist
     * @throws PlayerAlreadyInLobbyException if the player is already in a lobby
     * @throws LobbyFullException if the lobby is already full and cannot accept more players
     * @author Riccardo, Lorenzo
     */
    public Lobby joinLobby(String lobbyname, String player) throws LobbyNotFoundException, PlayerAlreadyInLobbyException, LobbyFullException {
        Lobby lobby;
        synchronized (lobbies) {
            if(!lobbies.containsKey(lobbyname)) {
                throw new LobbyNotFoundException();
            }
            lobby = lobbies.get(lobbyname);
        }
        lobby.addPlayer(player);
        globalUpdate(lobby, ServerEvent.Join(player));
        return lobby;
    }

    /**
     * Removes a player from their current lobby.
     *
     * @param player the name of the player to remove from the lobby
     * @throws LobbyNotFoundException if the player is not found in any lobby
     * @throws PlayerNotInLobbyException if the player is not in a lobby
     * @author Riccardo, Lorenzo
     */
    public void leaveLobby(String player) throws LobbyNotFoundException, PlayerNotInLobbyException {
        Lobby lobby = findPlayerLobby(player);
        lobby.removePlayer(player);
        if(lobby.getNumberOfPlayers() == 0){
            synchronized (lobbies){
                lobbies.remove(lobby.getName());
            }
        } else {
            globalUpdate(lobby, ServerEvent.Leave(player));
        }
    }

    /**
     * method that handles lobby search requests from clients.
     * It takes in a `Call` object and a `ClientInterface` object as parameters and returns a `Result` object containing a `Serializable` object.
     *
     * @param call the Call object representing the lobby search request
     * @param client the ClientInterface object of the client making the request
     * @return a Result object containing the result of the lobby search operation
     * @author Riccardo, Lorenzo
     */
    public Result<Serializable> handleLobbySearch(Call<Serializable> call, ClientInterface client) {
        Result<Serializable> result;
        try {
            switch (call.service()) {
                case LobbyCreate -> {
                    if (!(call.params() instanceof LobbyCreateInfo)) {
                        throw new WrongParametersException("LobbyCreateInfo", call.params().getClass().getName(), "LobbyCreate");
                    }
                    Lobby created = createLobby(((LobbyCreateInfo)call.params()).name(), client.getUsername());
                    client.setStatus(ClientStatus.InLobby);
                    client.setCallHandler(this::handleInLobby);
                    result = Result.ok(created, call.id());
                }
                case LobbyList -> result = Result.ok(getLobbies(), call.id());
                case LobbyJoin -> {
                    if (!(call.params() instanceof String)) {
                        throw new WrongParametersException("String", call.params().getClass().getName(), "LobbyJoin");
                    }
                    String selected_lobby = (String)call.params();
                    Lobby joined = joinLobby(selected_lobby, client.getUsername());
                    client.setStatus(ClientStatus.InLobby);
                    client.setCallHandler(this::handleInLobby);
                    result = Result.ok(joined, call.id());
                }
                default -> result = Result.err(new WrongServiceException(), call.id());
            }
        } catch (Exception e) {
            result = Result.err(e, call.id());
        }
        return result;
    }

    /**
     * method that handles incoming calls from clients who are currently in a lobby.
     * It takes in a `Call` object and a `ClientInterface` object as parameters, and returns a `Result` object that contains a `Serializable` object.
     *
     * @param call the Call object representing the in-lobby request
     * @param client the ClientInterface object of the client making the request
     * @return a Result object containing the result of the in-lobby operation
     * @author Riccardo, Lorenzo
     */
    public Result<Serializable> handleInLobby(Call<Serializable> call, ClientInterface client) {
        Result<Serializable> result;
        try {
            Lobby lobby = findPlayerLobby(client.getUsername());
            switch (call.service()) {
                case LobbyLeave -> {
                    leaveLobby(client.getUsername());
                    client.setStatus(ClientStatus.InLobbySearch);
                    client.setCallHandler(this::handleLobbySearch);
                    result = Result.empty(call.id());
                }
                case LobbyUpdate -> {
                    result = Result.ok(lobby, call.id());
                }
                case GameStart -> {
                    if (lobby.getNumberOfPlayers() < 2) {
                        throw new NotEnoughPlayersException();
                    }
                    if (!lobby.isHost(client.getUsername())) {
                        throw new NotHostException();
                    }
                    startGame(lobby);
                    client.setStatus(ClientStatus.InGame);
                    result = Result.empty(call.id());
                }
                case GameLoad -> {
                    HashSet<String> dbKey = new HashSet<>(lobby.getPlayers());
                    if (!db.containsKey(dbKey)) {
                        throw new GameNotFoundException();
                    }
                    if (!lobby.isHost(client.getUsername())) {
                        throw new NotHostException();
                    }
                    loadGame(lobby);
                    client.setStatus(ClientStatus.InGame);
                    result = Result.empty(call.id());
                }
                case GameChatSend -> {
                    if (!(call.params() instanceof Message)) {
                        throw new WrongParametersException("Message", call.params().getClass().getName(), "GameChatSend");
                    }
                    Message newChatMessage = (Message)call.params();
                    if (newChatMessage.message().length() > 100) {
                        throw new MessageTooLongException();
                    }
                    if (!newChatMessage.idSender().equals(client.getUsername())) {
                        throw new IdentityTheftException();
                    }
                    ServerEvent event = ServerEvent.NewMessage(newChatMessage);
                    if (newChatMessage.idReceiver().isEmpty()) {
                        globalUpdate(lobby, event);
                    } else {
                        Optional<ClientInterface> receiver = clientManager.getClient(newChatMessage.idReceiver().get());
                        if(receiver.isEmpty()){
                            throw new ClientNotConnectedException();
                        }
                        receiver.get().sendEvent(event);
                    }

                    result = Result.empty(call.id());
                }
                default -> result = Result.err(new WrongServiceException(), call.id());
            }
        } catch (Exception e) {
            result = Result.err(e, call.id());
        }
        return result;
    }

    /**
     * method called `startGame` that takes a `Lobby` object as a parameter and throws an `Exception`.
     * It creates a `File` object representing a directory called `SAVESTATES_DIRECTORY` and checks if it exists.
     * If it doesn't exist, it creates the directory. If it exists but is not a directory, it throws an exception.
     *
     * @param lobby the lobby in which to start the game
     * @throws Exception if the game cannot be started for any reason (e.g. the save directory cannot be created)
     * @author Marco
     */
    public void startGame(Lobby lobby) throws Exception {
        synchronized (lobby) {
            File saveDirectory = new File(SAVESTATES_DIRECTORY);
            if (!saveDirectory.exists()) {
                if (!saveDirectory.mkdir()) {
                    throw new Exception("Cannot create save directory");
                }
            } else if (!saveDirectory.isDirectory()) {
                throw new Exception("Save directory (" + SAVESTATES_DIRECTORY + ") exists and is not a directory");
            }

            HashSet<String> dbKey = new HashSet<>(lobby.getPlayers());
            if (!db.containsKey(dbKey)) {
                File saveFile = File.createTempFile(SAVESTATES_PREFIX, ".srl", new File(SAVESTATES_DIRECTORY));
                db.put(dbKey, saveFile);
                try {
                    db.write();
                } catch (Exception e) {
                    Logger.getLogger(LobbyController.class.getName()).warning("Cannot save db " + e.getMessage());
                }
            }
            GameController game = new GameController(lobby);
            games.add(game);
            lobbies.remove(lobby.getName());
        }
    }

    /**
     * Loads a save game from the specified lobby.
     *
     * @param lobby the lobby from which to load the game
     * @throws Exception if the game cannot be loaded for any reason (e.g. the save file does not exist)
     * @author Marco
     */
    private void loadGame(Lobby lobby) throws Exception {
        synchronized (lobby) {
            HashSet<String> dbKey = new HashSet<>(lobby.getPlayers());
            File saveFile = db.get(dbKey);
            GameController game = new GameController(saveFile, lobby);
            games.add(game);
            lobbies.remove(lobby.getName());
        }
    }

    /**
     * Ends the specified game.
     *
     * @param game the GameController object representing the game to end
     * @author Marco
     */
    public void endGame(GameController game) {
        synchronized (games){
            games.remove(game);
            db.remove(game.getGame().getPlayers().stream().map(Player::getName).collect(Collectors.toCollection(HashSet::new)));
            try {
                db.write();
            } catch (Exception e) {
                Logger.getLogger(LobbyController.class.getName()).warning("Cannot save db " + e.getMessage());
            }
        }
    }

    /**
     * Exits the specified game.
     *
     * @param game the GameController object representing the game to exit
     * @author Marco
     */
    public void exitGame(GameController game) {
        synchronized (games){
            games.remove(game);
        }
    }

    /**
     * Searches for a game containing the specified player.
     *
     * @param username the username of the player to search for
     * @return an Optional object containing the GameController object representing the game, if found, or an empty Optional object otherwise
     * @author Marco
     */
    public Optional<GameController> searchGame(String username) {
        synchronized (games) {
            for (GameController game : games) {
                if (game.getPlayers().stream().filter(p -> p.getName().equals(username)).findAny().isPresent()) {
                    return Optional.of(game);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * method that takes in a `Lobby` object and a `ServerEvent` object as parameters.
     * It synchronizes on the `Lobby` object and then iterates through all the players in the lobby.
     * For each player, it tries to get the corresponding `ClientInterface` object from a `clientManager`
     * and checks if it is present and not disconnected.
     * If it is, it sends the `ServerEvent` object to the client using the `sendEvent` method.
     * If the client is not present or disconnected, it logs a warning message.
     * If there is an exception while sending the event, it logs an error message.
     * This method is likely used to update all clients in a lobby with a new event.
     *
     * @param lobby the Lobby object to send the update event to
     * @param event the ServerEvent object representing the update event
     * @author Marco, Lorenzo
     */
    private void globalUpdate(Lobby lobby, ServerEvent event) {
        synchronized (lobby) {
            for(String player : lobby.getPlayers()) {
                try {
                    Optional<ClientInterface> client = clientManager.getClient(player);
                    if (client.isPresent() && !client.get().isDisconnected()) {
                        client.get().sendEvent(event);
                    } else {
                        Logger.getLogger(LobbyController.class.getName()).warning("Client " + player + " not connected");
                    }
                } catch (Exception e) {
                    Logger.getLogger(LobbyController.class.getName()).warning("Error while sending global update event to client" + player + " " + e.getMessage());
                }
            }
        }
    }
}
