package controller.lobby;

import controller.DataBase;
import controller.game.GameController;
import model.Player;
import network.*;
import network.parameters.LobbyCreateInfo;
import network.parameters.Message;
import network.parameters.WrongParametersException;
import network.errors.ClientNotFoundException;

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

    private static final String SAVESTATES_DIRECTORY = "save-states";
    private static final String SAVESTATES_PREFIX = "save_";

    private LobbyController() {}

    /**
     * Singleton pattern, if the instance is null, it creates a new one and starts it
     * @return the instance of the LobbyController
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
                synchronized (lobbies) {
                    for (Lobby lobby : lobbies.values()) {
                        ArrayList<String> players = lobby.getPlayers();
                        for (int i = 0; i < players.size(); i++) {
                            Optional<ClientInterface> client = GlobalClientManager.getInstance().getClient(players.get(i));
                            if (client.isEmpty()) {
                                throw new ClientNotFoundException();
                            } else {
                                if (client.get().isDisconnected()) {
                                    leaveLobby(players.get(i));
                                    client.get().setCallHandler(LobbyController.getInstance()::handleLobbySearch);
                                    i--;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  Finds the lobby that contains a specific player.
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
     * Creates a new lobby with the specified name and host player.
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

    public ArrayList<Lobby> getLobbies() {
        synchronized (lobbies){
            return new ArrayList<>(lobbies.values());
        }
    }

    /**
     * Joins a player to a specified lobby.
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
     * Handles the lobby search call based on the specified service and parameters.
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
     * Handles the in-lobby call based on the specified service and parameters.
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
                case GameChatSend -> {
                    if(!(call.params() instanceof String)){
                        throw new WrongParametersException("String", call.params().getClass().getName(), "GameChatSend");
                    }
                    Message message = new Message(client.getUsername(), (String) call.params());
                    ServerEvent event = ServerEvent.NewMessage(message);
                    globalUpdate(lobby, event);
                    result = Result.empty(call.id());
                }
                default -> result = Result.err(new WrongServiceException(), call.id());
            }
        } catch (Exception e) {
            result = Result.err(e, call.id());
        }
        return result;
    }

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

            File saveFile = File.createTempFile(SAVESTATES_PREFIX, ".srl", new File(SAVESTATES_DIRECTORY));
            db.put(lobby.getPlayers().stream().collect(Collectors.toCollection(HashSet::new)), saveFile);
            try {
                db.write();
            } catch (Exception e) {
                Logger.getLogger(LobbyController.class.getName()).warning("Cannot save db " + e.getMessage());
            }
            GameController game = new GameController(lobby);
            games.add(game);
            lobbies.remove(lobby.getName());
        }
    }

    public void endGame(GameController game) {
        synchronized (games){
            games.remove(game);
            db.remove(game.getGame().getPlayers().stream().map(Player::getName).collect(Collectors.toCollection(ArrayList::new)));
            try {
                db.write();
            } catch (Exception e) {
                Logger.getLogger(LobbyController.class.getName()).warning("Cannot save db " + e.getMessage());
            }
        }
    }

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
     * Sends a global update event to all clients in the specified lobby.
     * @param lobby the Lobby object to send the update event to
     * @param event the ServerEvent object representing the update event
     * @author Marco
     */
    private void globalUpdate(Lobby lobby, ServerEvent event) {
        synchronized (lobby) {
            for(String player : lobby.getPlayers()) {
                try {
                    Optional<ClientInterface> client = GlobalClientManager.getInstance().getClient(player);
                    if(client.isPresent()) {
                        client.get().sendEvent(event);
                    }
                } catch (Exception e) {
                    Logger.getLogger(LobbyController.class.getName()).warning("Error while sending global update event to client" + player + " " + e.getMessage());
                }
            }
        }
    }
}
