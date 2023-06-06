package model;

import network.ClientInterface;
import network.ClientManagerInterface;
import network.GlobalClientManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;

public class ScoreBoard implements Serializable, Iterable<Score> {
    private enum VictoryType{
        LANDSLIDE,
        BALANCED,
        TOO_CLOSE
    }

    private final ArrayList<Score> scores;
	private final String winner;
    private final String most_cats;
    private final String most_books;
    private final String most_games;
    private final String most_frames;
    private final String most_trophies;
    private final String most_plants;
    private final String sole_survivor;

    private final VictoryType victoryType;

    public ScoreBoard(Game game) {
        scores = new ArrayList<>();
        ArrayList<Player> players = game.finalRanks();
        winner = players.get(0).getName();
        most_cats = players.stream().max(Comparator.comparingInt(p -> p.getShelf().countCard(Card.Type.Gatto))).get().getName();
        most_books = players.stream().max(Comparator.comparingInt(p -> p.getShelf().countCard(Card.Type.Libro))).get().getName();
        most_games = players.stream().max(Comparator.comparingInt(p -> p.getShelf().countCard(Card.Type.Gioco))).get().getName();
        most_frames = players.stream().max(Comparator.comparingInt(p -> p.getShelf().countCard(Card.Type.Cornice))).get().getName();
        most_trophies = players.stream().max(Comparator.comparingInt(p -> p.getShelf().countCard(Card.Type.Trofeo))).get().getName();
        most_plants = players.stream().max(Comparator.comparingInt(p -> p.getShelf().countCard(Card.Type.Pianta))).get().getName();
        sole_survivor = findSoleSurvivor(players);
        victoryType = findVictoryType(players);
        for (Player player : players) {
            Score score = createScore(player);
            scores.add(score);
        }
    }

    @Override
    public Iterator<Score> iterator() {
        return scores.iterator();
    }

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
        return null;
    }

    private VictoryType findVictoryType(ArrayList<Player> players) {
        float ratio = (float) players.get(0).getPoints() / players.get(1).getPoints();
        if(ratio >= 1.7){
            return VictoryType.LANDSLIDE;
        }else if(ratio >= 1.2){
            return VictoryType.BALANCED;
        }else{
            return VictoryType.TOO_CLOSE;
        }
    }
    private Score createScore(Player player) {
        String playerName = player.getName();
        String title = "";
        if (sole_survivor != null) {
            if (playerName.equals(sole_survivor)) {
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
            } else if (playerName.equals(most_cats)) {
                title = "Can Mew Imyagine An Imyaginyary Mewnyagerie Mewnyager Imyagining Mewnyaging An Imyaginyary Mewnyagerie.";
            } else if (playerName.equals(most_trophies)) {
                title = "That's a lot of trophies for someone who didn't win.";
            }else if (playerName.equals(most_books)) {
                title = "Shelf full of books? Well not that you had any winner trophy to put on it anyway.";
            } else if (playerName.equals(most_games)) {
                title = "Nice guys finish last. Am I right gamer?";
            } else if (playerName.equals(most_frames)) {
                title = "May your frames be many and your photos few.";
            }  else if (playerName.equals(most_plants)) {
                title = "You took the plants? Good, your brain could use some extra oxygen.";
            } else {
                title = "NO TITLE FOR YOU! Come back, one year!";
            }
        }
        return new Score(player.getName(), player.getPoints(), title);
    }
}
