package network;

import controller.lobby.Lobby;
import network.parameters.CardSelect;
import network.parameters.LobbyCreateInfo;
import network.parameters.Login;
import network.parameters.Message;

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
    public Function<LobbyCreateInfo, Lobby> lobbyCreate(LobbyCreateInfo lobbyName) throws Exception;
    public Function<String, Lobby> lobbyJoin(String lobbyName) throws Exception;
    public Function<Boolean, Boolean> lobbyLeave() throws Exception;
    public Function<Boolean, Lobby> updateLobby() throws Exception;
    public Function<Boolean, Boolean> gameStart() throws Exception;
    public Function<Boolean, Boolean> gameLoad() throws Exception;
    public Function<CardSelect,Boolean> cardSelect(CardSelect selected) throws Exception;
    public Function<Login, Boolean> login(Login username) throws Exception;
    public void join() throws Exception;
    public Function<Message, Boolean> chat(Message message) throws Exception;
}
