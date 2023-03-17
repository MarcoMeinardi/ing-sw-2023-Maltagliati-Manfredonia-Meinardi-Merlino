package model;

public class Trophy {
    private final String name;
    private final int points;

    public Trophy(String name, int points) {
        this.name = name;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }
}
