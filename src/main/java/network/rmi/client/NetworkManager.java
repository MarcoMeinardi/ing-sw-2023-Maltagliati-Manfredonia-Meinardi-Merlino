package network.rmi.client;

import controller.lobby.Lobby;
import network.Function;
import network.NetworkManagerInterface;
import network.Server;
import network.ServerEvent;
import network.parameters.CardSelect;
import network.parameters.Login;

import java.util.ArrayList;
import java.util.Optional;

public class NetworkManager implements NetworkManagerInterface {
    @Override
    public void connect(Server server) throws Exception {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void reconnect() throws Exception {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public Optional<ServerEvent> getEvent() {
        return Optional.empty();
    }

    @Override
    public boolean hasEvent() {
        return false;
    }

    @Override
    public Function<Boolean, ArrayList<Lobby>> lobbyList() throws Exception {
        return null;
    }

    @Override
    public Function<String, Lobby> lobbyCreate(String lobbyName) throws Exception {
        return null;
    }

    @Override
    public Function<String, Lobby> lobbyJoin(String lobbyName) throws Exception {
        return null;
    }

    @Override
    public Function<Boolean, Boolean> lobbyLeave() throws Exception {
        return null;
    }

    @Override
    public Function<Boolean, Lobby> updateLobby() throws Exception {
        return null;
    }

    @Override
    public Function<Boolean, Boolean> gameStart() throws Exception {
        return null;
    }

    @Override
    public Function<CardSelect, Boolean> cardSelect(CardSelect selected) throws Exception {
        return null;
    }

    @Override
    public Function<Login, Boolean> login(Login username) throws Exception {
        return null;
    }
}
