package controller.lobby;

import controller.game.GameController;
import network.rpc.Call;
import network.rpc.Result;
import network.rpc.WrongServiceException;
import network.rpc.parameters.WrongParametersException;
import network.rpc.server.Client;
import network.rpc.server.ClientStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class LobbyController {
    private static LobbyController instance = null;
    private HashMap<String,Lobby> lobbies = new HashMap<>();

    private LobbyController() {}

    public static LobbyController getInstance() {
        if (instance == null) {
            instance = new LobbyController();
        }
        return instance;
    }

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

    public Lobby joinLobby(String lobbyname, String player) throws LobbyNotFoundException, PlayerAlreadyInLobbyException, LobbyFullException {
        Lobby lobby;
        synchronized (lobbies){
            if(!lobbies.containsKey(lobbyname)){
                throw new LobbyNotFoundException();
            }
            lobby = lobbies.get(lobbyname);
        }
        lobby.addPlayer(player);
        return lobby;
    }

    public void leaveLobby(String player) throws LobbyNotFoundException, PlayerNotInLobbyException {
        Lobby lobby = findPlayerLobby(player);
        lobby.removePlayer(player);
        if(lobby.getNumberOfPlayers() == 0){
            synchronized (lobbies){
                lobbies.remove(lobby);
            }
        }
    }

    public Result<Serializable> handleLobby(Call<Serializable> call, Client client) {
        Result<Serializable> result;
        try {
            switch (call.service()) {
                case LobbyCreate -> {
                    if (!(call.params() instanceof String)) {
                        throw new WrongParametersException("NewLobby", call.params().getClass().getName(), "LobbyCreate");
                    }
                    if (client.getStatus() != ClientStatus.Idle) {
                        throw new PlayerAlreadyInLobbyException();
                    }
                    Lobby created = createLobby(((String) call.params()), client.getUsername());
                    client.setStatus(ClientStatus.InLobby);
                    result = Result.ok(created, call.id());
                }
                case LobbyList -> result = Result.ok(getLobbies(), call.id());
                case LobbyJoin -> {
                    if (!(call.params() instanceof String)) {
                        throw new WrongParametersException("String", call.params().getClass().getName(), "LobbyJoin");
                    }
                    if (client.getStatus() != ClientStatus.Idle) {
                        throw new PlayerAlreadyInLobbyException();
                    }
                    String selected_lobby = (String) call.params();
                    Lobby joined = joinLobby(selected_lobby, client.getUsername());
                    client.setStatus(ClientStatus.InLobby);
                    result = Result.ok(joined, call.id());
                }
                case LobbyLeave -> {
                    if (client.getStatus() != ClientStatus.InLobby) {
                        throw new PlayerNotInLobbyException();
                    }
                    leaveLobby(client.getUsername());
                    client.setStatus(ClientStatus.Idle);
                    result = Result.empty(call.id());
                }
                case LobbyUpdate -> {
                    if (client.getStatus() != ClientStatus.InLobby) {
                        throw new PlayerNotInLobbyException();
                    }
                    Lobby updatedLobby = findPlayerLobby(client.getUsername());
                    result = Result.ok(updatedLobby, call.id());
                }
                case GameStart -> {
                    if (client.getStatus() != ClientStatus.InLobby) {
                        throw new PlayerNotInLobbyException();
                    }
                    Lobby lobby = findPlayerLobby(client.getUsername());
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
                default -> result = Result.err(new WrongServiceException(), call.id());
            }
        } catch (Exception e) {
            result = Result.err(e, call.id());
        }
        return result;
    }

    public void startGame(Lobby lobby) throws Exception{
        synchronized (lobby){
            GameController game = new GameController(lobby);
        }
    }
}
