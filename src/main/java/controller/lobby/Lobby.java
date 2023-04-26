package controller.lobby;

import java.io.Serializable;
import java.util.ArrayList;

public class Lobby implements Serializable {
    private String name;
    private ArrayList<String> players;

    public Lobby(String name, String host) {
        this.name = name;
        this.players = new ArrayList<>();
        this.players.add(host);
    }

    public String getName() {
        synchronized (name) {
            return name;
        }
    }

    public ArrayList<String> getPlayers() {
        synchronized (players){
            return players;
        }
    }

    public void addPlayer(String player) throws PlayerAlreadyInLobbyException, LobbyFullException {
        synchronized (players){
            if(players.contains(player)) {
                throw new PlayerAlreadyInLobbyException();
            }
            if(players.size() == 4) {
                throw new LobbyFullException();
            }
            players.add(player);
        }
    }

    public void removePlayer(String player) throws PlayerNotInLobbyException {
        synchronized (players){
            if(!players.contains(player)) {
                throw new PlayerNotInLobbyException();
            }
            players.remove(player);
        }
    }

    protected boolean isHost(String player) {
        synchronized (players){
            return players.get(0).equals(player);
        }
    }

    public int getNumberOfPlayers() {
        synchronized (players){
            return players.size();
        }
    }
}
