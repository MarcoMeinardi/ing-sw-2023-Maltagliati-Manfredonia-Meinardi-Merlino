package model;

public record Point(int y, int x) {
    public int distance(Point other) {
        return Math.abs(y - other.y) + Math.abs(x - other.x);
    }
}
