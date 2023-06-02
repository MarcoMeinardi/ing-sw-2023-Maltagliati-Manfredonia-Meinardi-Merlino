package model;

import network.ClientManagerInterface;
import network.GlobalClientManager;

import java.io.Serializable;
import java.util.*;

/**
 * Final score board of the game.
 */
public class ScoreBoard implements Serializable, Iterable<Score>  {
    ArrayList<Score> scores;
    /**
     * Creates a new ScoreBoard.
     * @param game the game to get the scores from.
     */
    public static ScoreBoardBuilder create(Game game){
        return new ScoreBoardBuilder(game);
    }

    protected ScoreBoard(ArrayList<Score> scores){
        this.scores = scores;
    }

    @Override
    public Iterator<Score> iterator() {
        return scores.iterator();
    }

}
