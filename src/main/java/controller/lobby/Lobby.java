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

    /**
     * Get the lobby name
     * @return the lobby name
     */

    public String getName() {
        synchronized (name) {
            return name;
        }
    }

    /**
     * Get the players in the lobby
     * @return the players in the lobby
     */

    public ArrayList<String> getPlayers() {
        synchronized (players){
            return players;
        }
    }

    /**
     * Add a player to the lobby
     * @param player the player to add
     * @throws PlayerAlreadyInLobbyException if the player is already in the lobby
     * @throws LobbyFullException if the lobby is full
     */

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

    /**
     * Remove a player from the lobby
     * @param player the player to remove
     * @throws PlayerNotInLobbyException if the player is not in the lobby
     * @author Riccardo
     */

    public void removePlayer(String player) throws PlayerNotInLobbyException {
        synchronized (players){
            if(!players.contains(player)) {
                throw new PlayerNotInLobbyException();
            }
            players.remove(player);
        }
    }

    /**
     * Check if a player is the host of the lobby
     * @param player the player to check
     * @return true if the player is the host, false otherwise
     */

    public boolean isHost(String player) {
        synchronized (players){
            return players.get(0).equals(player);
        }
    }

    /**
     * Get the number of players in the lobby
     * @return the number of players in the lobby
     */

    public int getNumberOfPlayers() {
        synchronized (players){
            return players.size();
        }
    }
}
