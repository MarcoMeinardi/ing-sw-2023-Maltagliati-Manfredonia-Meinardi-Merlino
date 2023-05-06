package network;

import controller.lobby.Lobby;
import network.parameters.CardSelect;
import network.parameters.Login;
import network.rpc.client.Function;

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
    public RemoteFunctionInterface<Boolean, ArrayList<Lobby>> lobbyList() throws Exception;
    public RemoteFunctionInterface<String, Lobby> lobbyCreate(String lobbyName) throws Exception;
    public RemoteFunctionInterface<String, Lobby> lobbyJoin(String lobbyName) throws Exception;
    public RemoteFunctionInterface<Boolean, Boolean> lobbyLeave() throws Exception;
    public RemoteFunctionInterface<Boolean, Lobby> updateLobby() throws Exception;
    public RemoteFunctionInterface<Boolean,Boolean> gameStart() throws Exception;
    public RemoteFunctionInterface<CardSelect,Boolean> cardSelect(CardSelect selected) throws Exception;
    public RemoteFunctionInterface<Login, Boolean> login(Login username) throws Exception;
}
