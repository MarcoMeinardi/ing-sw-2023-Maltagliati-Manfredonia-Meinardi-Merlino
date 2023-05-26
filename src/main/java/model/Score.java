package model;

import java.io.Serializable;

/**
 * A score in the final score board.
 * @param username the username of the player.
 * @param score the score of the player.
 * @param title the title of the player.
 */
public record Score(String username, int score, String title) implements Serializable, Comparable<Score> {
    @Override
    public int compareTo(Score o) {
        return o.score() - score;
    }
}
