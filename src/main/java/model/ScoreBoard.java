package model;

import network.ClientInterface;
import network.ClientManagerInterface;
import network.GlobalClientManager;

import java.io.Serializable;
import java.util.*;

/**
 * Class to construct a serializable scoreboard
 * Construct it by giving the `Game` object from which to build
 * the scoreboard, and send it over network to the clients
 */
public class ScoreBoard implements Serializable, Iterable<Score> {
    private enum VictoryType{
        LANDSLIDE,
        BALANCED,
        TOO_CLOSE
    }

    private final ArrayList<Score> scores;
    private final Map<String, ArrayList<Cockade>> cockades;
    private final String winner;
    private final String mostCats;
    private final String mostBooks;
    private final String mostGames;
    private final String mostFrames;
    private final String mostTrophies;
    private final String mostPlants;
    private final String soleSurvivor;

    private final VictoryType victoryType;

    /**
     * Constructor of the class to create the scoreboard from the final game state.
     * @param game the game object to construct the scoreboard from
     */
    public ScoreBoard(Game game) {
        scores = new ArrayList<>();
        cockades = new HashMap<>();
        ArrayList<Player> players = game.finalRanks();
        winner = players.get(0).getName();
        mostCats = players.stream().max(Comparator.comparingInt(p -> p.getShelf().countCard(Card.Type.Cat))).get().getName();
        mostBooks = players.stream().max(Comparator.comparingInt(p -> p.getShelf().countCard(Card.Type.Book))).get().getName();
        mostGames = players.stream().max(Comparator.comparingInt(p -> p.getShelf().countCard(Card.Type.Game))).get().getName();
        mostFrames = players.stream().max(Comparator.comparingInt(p -> p.getShelf().countCard(Card.Type.Frame))).get().getName();
        mostTrophies = players.stream().max(Comparator.comparingInt(p -> p.getShelf().countCard(Card.Type.Trophy))).get().getName();
        mostPlants = players.stream().max(Comparator.comparingInt(p -> p.getShelf().countCard(Card.Type.Plant))).get().getName();

        soleSurvivor = findSoleSurvivor(players);
        if (soleSurvivor != null) {
            victoryType = null;
            Player solePlayer = players.stream().filter(p -> p.getName().equals(soleSurvivor)).findFirst().get();
            players.remove(solePlayer);
            players.add(0, solePlayer);
        } else {
            victoryType = findVictoryType(players);
        }
        for (Player player : players) {
            Score score = createScore(player);
            scores.add(score);
            cockades.put(player.getName(), player.getCockades());
        }
    }

    /**
     * Iterator for the scoreboard to facilitate the iteration over the scores
     * @return the iterator for the scoreboard
     */
    @Override
    public Iterator<Score> iterator() {
        return scores.iterator();
    }

    /**
     * Check if the game ended because a player remained alone for too long
     * @param players the list of players
     * @return the name of the sole survivor or null
     */
    private String findSoleSurvivor(ArrayList<Player> players) {
        String soleSurvivor = null;
        ClientManagerInterface clientManager;
        int connected = 0;
        try {
            clientManager = GlobalClientManager.getInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot get client manager");
        }
        for(Player player : players) {
            Optional<ClientInterface> client = clientManager.getClient(player.getName());
            if(client.isPresent() && !client.get().isDisconnected()){
                connected++;
                soleSurvivor = player.getName();
            }
        }
        if(connected == 1) return soleSurvivor;
        // I know nulls are not best practice, but since we need to send this object over the network,
        // it is useless to wrap it in an Optional
        return null;
    }

    /**
     * Get the type of win from the lead of the first player
     * @param players the list of players
     * @return the type of win
     */
    private VictoryType findVictoryType(ArrayList<Player> players) {
        float ratio = (float)players.get(0).getPoints() / players.get(1).getPoints();
        if(ratio >= 1.7){
            return VictoryType.LANDSLIDE;
        }else if(ratio >= 1.2){
            return VictoryType.BALANCED;
        }else{
            return VictoryType.TOO_CLOSE;
        }
    }

    /**
     * Create the score object for the player
     * It contains the total points and the title
     * @param player the player to create the score for
     * @return the `Score` object for the given player
     */
    private Score createScore(Player player) {
        String playerName = player.getName();
        String title = "";
        if (soleSurvivor != null) {
            if (playerName.equals(soleSurvivor)) {
                title = "Many fall in the face of chaos; but not this one, not today.";
            } else {
                title = "Madness, our old friend!";
            }
        } else {
            if (playerName.equals(winner)) {
                switch (victoryType){
                    case LANDSLIDE -> {
                        title = "As shrimple as that";
                    }
                    case BALANCED -> {
                        title = "It ain't much but it's honest work.";
                    }
                    case TOO_CLOSE -> {
                        title = "The risk was calculated but, boy am I bad at math.";
                    }
                }
            } else if (playerName.equals(mostCats)) {
                title = "Can Mew Imyagine An Imyaginyary Mewnyagerie Mewnyager Imyagining Mewnyaging An Imyaginyary Mewnyagerie.";
            } else if (playerName.equals(mostTrophies)) {
                title = "That's a lot of trophies for someone who didn't win.";
            }else if (playerName.equals(mostBooks)) {
                title = "Shelf full of books? Well not that you had any winner trophy to put on it anyway.";
            } else if (playerName.equals(mostGames)) {
                title = "Nice guys finish last. Am I right gamer?";
            } else if (playerName.equals(mostFrames)) {
                title = "May your frames be many and your photos few.";
            }  else if (playerName.equals(mostPlants)) {
                title = "You took the plants? Good, your brain could use some extra oxygen.";
            } else {
                title = "NO TITLE FOR YOU! Come back, one year!";
            }
        }
        return new Score(player.getName(), player.getPoints(), title);
    }

    /**
     * Get the cockades of a player
     * @param playerName the name of the player
     * @return the cockades of the player
     */
    public ArrayList<Cockade> getCockades(String playerName) {
        return cockades.get(playerName);
    }

    /**
     * Get the number of players in the scoreboard
     * @return the number of players
     */
    public int size(){
        return scores.size();
    }
}
