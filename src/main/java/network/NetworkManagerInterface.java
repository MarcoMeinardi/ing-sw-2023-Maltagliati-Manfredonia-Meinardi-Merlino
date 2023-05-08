package network;

import controller.lobby.Lobby;
import network.parameters.CardSelect;
import network.parameters.Login;

import java.util.ArrayList;
import java.util.Optional;

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
    public Function<String, Lobby> lobbyCreate(String lobbyName) throws Exception;
    public Function<String, Lobby> lobbyJoin(String lobbyName) throws Exception;
    public Function<Boolean, Boolean> lobbyLeave() throws Exception;
    public Function<Boolean, Lobby> updateLobby() throws Exception;
    public Function<Boolean,Boolean> gameStart() throws Exception;
    public Function<CardSelect,Boolean> cardSelect(CardSelect selected) throws Exception;
    public Function<Login, Boolean> login(Login username) throws Exception;
}
