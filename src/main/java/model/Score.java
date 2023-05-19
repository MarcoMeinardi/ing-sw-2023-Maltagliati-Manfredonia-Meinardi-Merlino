package model;

import java.io.Serializable;

public record Score(String username, int score) implements Serializable, Comparable<Score> {
    @Override
    public int compareTo(Score o) {
        return o.score() - score;
    }
}
