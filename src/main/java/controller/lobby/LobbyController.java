package controller.lobby;

import controller.game.GameController;
import network.*;
import network.parameters.Message;
import network.parameters.WrongParametersException;
import network.rpc.server.Client;
import network.rpc.server.ClientManager;
import network.errors.ClientNotFoundException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

public class LobbyController extends Thread {
    private static LobbyController instance = null;
    private HashMap<String, Lobby> lobbies = new HashMap<>();
    private ArrayList<GameController> games = new ArrayList<>();

    private LobbyController() {}

    /**
     * @author Riccardo, Lorenzo
     * Singleton pattern, if the instance is null, it creates a new one and starts it
     * @return the instance of the LobbyController
     *
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
     * @author Riccardo, Lorenzo
     * This method continuously checks the status of players in lobbies and handles disconnections.
     * It sleeps for 1000 milliseconds between each iteration.
     * If a player is disconnected, they are removed from the lobby and their status is updated.
     * The method also sets the call handler for disconnected clients to the LobbyController's handleLobbySearch method.
     * If a client is not found in the ClientManager, a ClientNotFoundException is thrown.
     * Any exceptions that occur during execution are printed to the standard error stream
     *
     */
    public void run() {
        try {
            while (true) {
                Thread.sleep(1000);
                synchronized (lobbies) {
                    for (Lobby lobby : lobbies.values()) {
                        ArrayList<String> players = lobby.getPlayers();
                        for (int i = 0; i < players.size(); i++) {
                            Optional<ClientInterface> client = ClientManager.getInstance().getClient(players.get(i));
                            if (client.isEmpty()) {
                                throw new ClientNotFoundException();
                            } else {
                                if (client.get().isDisconnected()) {
                                    leaveLobby(players.get(i));
                                    client.get().setStatus(ClientStatus.Disconnected);
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
     *  @author Riccardo, Lorenzo
     *  Finds the lobby that contains a specific player.
     *  @param playerName the name of the player to search for
     *  @return the Lobby object that contains the player
     *  @throws LobbyNotFoundException if the player is not found in any lobby
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
     * @author Riccardo, Lorenzo
     * Creates a new lobby with the specified name and host player.
     * @param lobbyname the name of the lobby to create
     * @param host the name of the player who will be the host of the lobby
     * @return the created Lobby object
     * @throws LobbyAlreadyExistsException if a lobby with the same name already exists
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
     * @author Riccardo, Lorenzo
     * Joins a player to a specified lobby.
     * @param lobbyname the name of the lobby to join
     * @param player the name of the player to join the lobby
     * @return the Lobby object that the player has joined
     * @throws LobbyNotFoundException if the specified lobby does not exist
     * @throws PlayerAlreadyInLobbyException if the player is already in a lobby
     * @throws LobbyFullException if the lobby is already full and cannot accept more players
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
     * @author Riccardo, Lorenzo
     * Removes a player from their current lobby.
     * @param player the name of the player to remove from the lobby
     * @throws LobbyNotFoundException if the player is not found in any lobby
     * @throws PlayerNotInLobbyException if the player is not in a lobby
     */
    public void leaveLobby(String player) throws LobbyNotFoundException, PlayerNotInLobbyException {
        Lobby lobby = findPlayerLobby(player);
        lobby.removePlayer(player);
        if(lobby.getNumberOfPlayers() == 0){
            synchronized (lobbies){
                lobbies.remove(lobby.getName());
            }
        }
    }

    /**
     * @author Riccardo, Lorenzo
     * Handles the lobby search call based on the specified service and parameters.
     * @param call the Call object representing the lobby search request
     * @param client the ClientInterface object of the client making the request
     * @return a Result object containing the result of the lobby search operation
     */
    public Result<Serializable> handleLobbySearch(Call<Serializable> call, ClientInterface client) {
        Result<Serializable> result;
        try {
            switch (call.service()) {
                case LobbyCreate -> {
                    if (!(call.params() instanceof String)) {
                        throw new WrongParametersException("NewLobby", call.params().getClass().getName(), "LobbyCreate");
                    }
                    Lobby created = createLobby(((String)call.params()), client.getUsername());
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
     * @author Riccardo, Lorenzo
     * Handles the in-lobby call based on the specified service and parameters.
     * @param call the Call object representing the in-lobby request
     * @param client the ClientInterface object of the client making the request
     * @return a Result object containing the result of the in-lobby operation
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
                    globalUpdate(lobby, ServerEvent.Leave(client.getUsername()));
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

    public boolean startGame(Lobby lobby) throws Exception {
        synchronized (lobby) {
            GameController game = new GameController(lobby);
            games.add(game);
        }
        return true;
    }

    public void endGame(GameController game) {
        synchronized (games){
            games.remove(game);
        }
    }

    /**
     * @author Marco
     * Sends a global update event to all clients in the specified lobby.
     * @param lobby the Lobby object to send the update event to
     * @param event the ServerEvent object representing the update event
     */
    private void globalUpdate(Lobby lobby, ServerEvent event) {
        synchronized (lobby) {
            for(String player : lobby.getPlayers()) {
                try {
                    Optional<ClientInterface> client = ClientManager.getInstance().getClient(player);
                    if(client.isPresent()) {
                        client.get().send(event);
                    }
                } catch (Exception e) {
                    Logger.getLogger(LobbyController.class.getName()).warning("Error while sending global update event to client" + player + " " + e.getMessage());
                }
            }
        }
    }
}
