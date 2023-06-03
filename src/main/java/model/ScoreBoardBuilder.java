package model;

import network.ClientManagerInterface;
import network.GlobalClientManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class ScoreBoardBuilder {
    private enum VictoryType{
        LANDSLIDE,
        BALANCED,
        TOO_CLOSE
    };
    private final ArrayList<Score> scores;
    private final Optional<Player> winner;
    private final Optional<Player> most_cats;
    private final Optional<Player> most_books;
    private final Optional<Player> most_games;
    private final Optional<Player> most_frames;
    private final Optional<Player> most_trophies;
    private final Optional<Player> most_plants;
    private final Optional<Player> sole_survivor;

    private final VictoryType victoryType;

    public ScoreBoardBuilder(Game game) {
        scores = new ArrayList<>();
        ArrayList<Player> players = game.finalRanks();
        winner = Optional.of(players.get(0));
        most_cats = players.stream().max((p1,p2) -> p1.getShelf().countCard(Card.Gatto) - p2.getShelf().countCard(Card.Gatto));
        most_books = players.stream().max((p1,p2) -> p1.getShelf().countCard(Card.Libro) - p2.getShelf().countCard(Card.Libro));
        most_games = players.stream().max((p1,p2) -> p1.getShelf().countCard(Card.Gioco) - p2.getShelf().countCard(Card.Gioco));
        most_frames = players.stream().max((p1,p2) -> p1.getShelf().countCard(Card.Cornice) - p2.getShelf().countCard(Card.Cornice));
        most_trophies = players.stream().max((p1,p2) -> p1.getShelf().countCard(Card.Trofeo) - p2.getShelf().countCard(Card.Trofeo));
        most_plants = players.stream().max((p1,p2) -> p1.getShelf().countCard(Card.Pianta) - p2.getShelf().countCard(Card.Pianta));
        sole_survivor = findSoleSurvivor(players);
        victoryType = findVictoryType(players);
        for (Player player : players) {
            Score score = createScore(player);
            scores.add(score);
        }
    }

    private Optional<Player> findSoleSurvivor(ArrayList<model.Player> players) {
        Optional<model.Player> soleSurvivor = Optional.empty();
        ClientManagerInterface clientManager;
        int connected = 0;
        try{
            clientManager = GlobalClientManager.getInstance();

        }catch (Exception e){
            return soleSurvivor;
        }
        for(model.Player player : players) {
            if(clientManager.getClient(player.getName()).isPresent()){
                connected++;
                soleSurvivor = Optional.of(player);
            }
        }
        if(connected == 1) return soleSurvivor;
        return Optional.empty();
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
        String title = "";
        if(sole_survivor.isPresent()){
            if(sole_survivor.get().equals(player)) {
                title = "Many fall in the face of chaos; but not this one, not today.";
            }else{
                title = "Madness, our old friend!";
            }
        }else{
            if (winner.isPresent() && winner.get().equals(player)) {
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
            } else if (most_cats.isPresent() && most_cats.get().equals(player)) {
                title = "Can Mew Imyagine An Imyaginyary Mewnyagerie Mewnyager Imyagining Mewnyaging An Imyaginyary Mewnyagerie.";
            } else if (most_trophies.isPresent() && most_trophies.get().equals(player)) {
                title = "That's a lot of trophies for someone who didn't win.";
            }else if (most_books.isPresent() && most_books.get().equals(player)) {
                title = "Shelf full of books? Well not that you had any winner trophy to put on it anyway.";
            } else if (most_games.isPresent() && most_games.get().equals(player)) {
                title = "Nice guys finish last. Am I right gamer?";
            } else if (most_frames.isPresent() && most_frames.get().equals(player)) {
                title = "May your frames be many and your photos few.";
            }  else if (most_plants.isPresent() && most_plants.get().equals(player)) {
                title = "You took the plants? Good, your brain could use some extra oxygen.";
            } else {
                title = "NO TITLE FOR YOU! Come back, one year!";
            }
        }
        return new Score(player.getName(), player.getPoints(), title);
    }

    public ScoreBoard build(){
        return new ScoreBoard(scores);
    }
}
