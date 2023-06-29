package model;

import java.io.Serializable;

/**
 * Record that holds information about a point: coordinates
 * It is used to check if card selection is valid
 * @param y the y coordinate
 * @param x the x coordinate
 */
public record Point(int y, int x) implements Serializable {
    public int distance(Point other) {
        return Math.abs(y - other.y) + Math.abs(x - other.x);
    }
}
