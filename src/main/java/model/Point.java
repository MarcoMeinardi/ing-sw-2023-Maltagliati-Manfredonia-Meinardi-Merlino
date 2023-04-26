package model;

import java.io.Serializable;

public record Point(int y, int x) implements Serializable {
    public int distance(Point other) {
        return Math.abs(y - other.y) + Math.abs(x - other.x);
    }
}
