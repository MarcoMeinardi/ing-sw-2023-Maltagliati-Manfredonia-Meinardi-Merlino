package network;

import controller.lobby.Lobby;
import network.parameters.CardSelect;
import network.parameters.LobbyCreateInfo;
import network.parameters.Login;
import network.parameters.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Interface for the network manager object that handles all communication with the server
 */
public interface NetworkManagerInterface {
    /**
     * Get the singleton instance of the network manager
     * @return the singleton instance of the network manager
     */
    public static NetworkManagerInterface getInstance(){
        throw new RuntimeException("Not implemented");
    };

    /**
     * Connect to the server
     * @param server the server to connect to
     * @throws Exception
     */
    public void connect(Server server) throws Exception ;

    /**
     * Disconnect from the server
     */
    public void disconnect();

    /**
     * Reconnect to the server
     * @throws Exception if the client wasn't connected to the server before
     */
    public void reconnect() throws Exception;

    /**
     * Check if the client is connected to the server
     * @return true if the client is connected to the server
     */
    public boolean isConnected();

    /**
     * Get first event in the event queue if there is one
     * @return the first event in the event queue if there is one or empty if there is none
     */
    public Optional<ServerEvent> getEvent();

    /**
     * Check if there is an event in the event queue
      * @return true if there is an event in the event queue or false if there is none
     */
    public boolean hasEvent();

    /**
     * Get a list of all the lobbies on the server
     * @return the remote function handler for this call
     * @throws Exception if the network manager is not connected to the server
     */
    public Function<Boolean, ArrayList<Lobby>> lobbyList() throws Exception;

    /**
     * Create a new lobby
     * @param lobbyName the name of the lobby to create
     * @return the remote function handler for this call
     * @throws Exception if the network manager is not connected to the server
     */
    public Function<LobbyCreateInfo, Lobby> lobbyCreate(LobbyCreateInfo lobbyName) throws Exception;

    /**
     * Join a lobby with the given name
     * @param lobbyName the name of the lobby to join
     * @return the remote function handler for this call
     * @throws Exception if the network manager is not connected to the server
     */
    public Function<String, Lobby> lobbyJoin(String lobbyName) throws Exception;

    /**
     * Leave the current lobby
     * @return the remote function handler for this call
     * @throws Exception if the network manager is not connected to the server
     */
    public Function<Boolean, Boolean> lobbyLeave() throws Exception;

    /**
     * Update the current lobby info
     * @return the remote function handler for this call
     * @throws Exception if the network manager is not connected to the server
     */
    public Function<Boolean, Lobby> updateLobby() throws Exception;

    /**
     * Start a new game
     * @return the remote function handler for this call
     * @throws Exception if the network manager is not connected to the server
     */
    public Function<Boolean, Boolean> gameStart() throws Exception;

    /**
     * Load a saved game
     * @return the remote function handler for this call
     * @throws Exception if the network manager is not connected to the server
     */
    public Function<Boolean, Boolean> gameLoad() throws Exception;

    /**
     * Select cards during the game
     * @param selected the cards selected
     * @return the remote function handler for this call
     * @throws Exception if the network manager is not connected to the server
     */
    public Function<CardSelect, Boolean> cardSelect(CardSelect selected) throws Exception;
    public Function<Boolean, Boolean> exitGame() throws Exception;

    /**
     * Login to the server
     * @param username the username to log in with
     * @return True if successful and no game is running or GameInfo if successful and there is a game running
     * @throws Exception if there is any kind of problem during the login process
     */
    public Function<Login, Serializable> login(Login username) throws Exception;

    /**
     * Join all inner threads and close all connections
     * @throws Exception if there is any kind of problem during the join process
     */
    public void join() throws Exception;

    /**
     * Send a chat message
     * @param message the message to send
     * @return the remote function handler for this call
     * @throws Exception if the network manager is not connected to the server
     */
    public Function<Message, Boolean> chat(Message message) throws Exception;

}
