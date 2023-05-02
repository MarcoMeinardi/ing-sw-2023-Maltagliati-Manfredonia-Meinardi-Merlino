package controller.lobby;

import controller.game.GameController;
import network.Call;
import network.Result;
import network.WrongServiceException;
import network.parameters.WrongParametersException;
import network.rpc.server.Client;
import network.rpc.server.ClientManager;
import network.rpc.server.ClientNotFoundException;
import network.rpc.server.ClientStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class LobbyController extends Thread {
    private static LobbyController instance = null;
    private HashMap<String, Lobby> lobbies = new HashMap<>();
    private ArrayList<GameController> games = new ArrayList<>();

    private LobbyController() {}

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

    public void run() {
        try {
            while (true) {
                Thread.sleep(1000);
                synchronized (lobbies) {
                    for (Lobby lobby : lobbies.values()) {
                        ArrayList<String> players = lobby.getPlayers();
                        for (int i = 0; i < players.size(); i++) {
                            Optional<Client> client = ClientManager.getInstance().getClientByUsername(players.get(i));
                            if (client.isEmpty()) {
                                throw new ClientNotFoundException();
                            } else {
                                if (client.get().isDisconnected()) {
                                    leaveLobby(players.get(i));
                                    client.get().setLastValidStatus(ClientStatus.Disconnected);
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
                lobbies.remove(lobby.getName());
            }
        }
    }

    public Result<Serializable> handleLobbySearch(Call<Serializable> call, Client client) {
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

    public Result<Serializable> handleInLobby(Call<Serializable> call, Client client) {
        Result<Serializable> result;
        try {
            switch (call.service()) {
                case LobbyLeave -> {
                    leaveLobby(client.getUsername());
                    client.setStatus(ClientStatus.InLobbySearch);
                    client.setCallHandler(this::handleLobbySearch);
                    result = Result.empty(call.id());
                }
                case LobbyUpdate -> {
                    Lobby updatedLobby = findPlayerLobby(client.getUsername());
                    result = Result.ok(updatedLobby, call.id());
                }
                case GameStart -> {
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
            games.add(game);
        }
    }

    public void endGame(GameController game) {
        synchronized (games){
            games.remove(game);
        }
    }
}