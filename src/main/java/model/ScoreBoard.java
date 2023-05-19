package model;

import java.io.Serializable;
import java.util.*;

public class ScoreBoard implements Serializable, Iterable<Score>  {
    private final ArrayList<Score> scores;

    public ScoreBoard(Game game) {
        scores = new ArrayList<>();
        for(Player player : game.getPlayers()){
            Score score = new Score(player.getName(),player.getPoints());
            scores.add(score);
        }
        Collections.sort(scores);
    }

    @Override
    public Iterator<Score> iterator() {
        return scores.iterator();
    }

}
