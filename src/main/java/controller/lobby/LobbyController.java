package controller.lobby;

import network.rpc.Call;
import network.rpc.Result;
import network.rpc.server.Client;

import java.io.Serializable;
import java.util.LinkedList;

public class LobbyController {
    private static LobbyController instance = null;
    private LinkedList<Lobby> lobbies = new LinkedList<Lobby>();

    private LobbyController() {}

    public static LobbyController getInstance() {
        if (instance == null) {
            instance = new LobbyController();
        }
        return instance;
    }

    public Lobby getLobby(String lobbyname) throws LobbyNotFoundException {
        synchronized (lobbies){
            for(Lobby lobby : lobbies){
                if(lobby.getName().equals(lobbyname)){
                    return lobby;
                }
            }
        }
        throw new LobbyNotFoundException();
    }

    public void createLobby(String lobbyname, String host) {
        Lobby lobby = new Lobby(lobbyname, host);
        synchronized (lobbies){
            lobbies.add(lobby);
        }
    }

    public void joinLobby(String lobbyname, String player) throws LobbyNotFoundException, PlayerAlreadyInLobbyException, LobbyFullException {
        Lobby lobby = getLobby(lobbyname);
        lobby.addPlayer(player);
    }

    public void leaveLobby(String lobbyname, String player) throws LobbyNotFoundException, PlayerNotInLobbyException {
        Lobby lobby = getLobby(lobbyname);
        lobby.removePlayer(player);
        if(lobby.getNumberOfPlayers() == 0){
            synchronized (lobbies){
                lobbies.remove(lobby);
            }
        }
    }
    public Result<Serializable> handleLobby(Call<Serializable> call, Client client) {
        Result<Serializable> result;
        ///cosdisds
        return result;
    }

}
