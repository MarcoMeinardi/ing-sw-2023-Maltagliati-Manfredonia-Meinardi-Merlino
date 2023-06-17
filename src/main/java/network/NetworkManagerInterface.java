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
    public static NetworkManagerInterface getInstance(){
        throw new RuntimeException("Not implemented");
    };
    public void connect(Server server) throws Exception ;
    public void disconnect();
    public void reconnect() throws Exception;
    public boolean isConnected();
    public Optional<ServerEvent> getEvent();
    public boolean hasEvent();
    public Function<Boolean, ArrayList<Lobby>> lobbyList() throws Exception;
    public Function<LobbyCreateInfo, Lobby> lobbyCreate(LobbyCreateInfo lobbyName) throws Exception;
    public Function<String, Lobby> lobbyJoin(String lobbyName) throws Exception;
    public Function<Boolean, Boolean> lobbyLeave() throws Exception;
    public Function<Boolean, Lobby> updateLobby() throws Exception;
    public Function<Boolean, Boolean> gameStart() throws Exception;
    public Function<Boolean, Boolean> gameLoad() throws Exception;
    public Function<CardSelect, Boolean> cardSelect(CardSelect selected) throws Exception;
    public Function<Boolean, Boolean> exitGame() throws Exception;

    /**
     * Login to the server
     * @param username the username to log in with
     * @return True if successful and no game is running or GameInfo if successful and there is a game running
     * @throws Exception if there is any kind of problem during the login process
     */
    public Function<Login, Serializable> login(Login username) throws Exception;
    public void join() throws Exception;
    public Function<Message, Boolean> chat(Message message) throws Exception;
}
