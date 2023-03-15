package core;

import java.util.ArrayList;

public class Player {
    private Objective[] objectives;
    private ArrayList<Trophy> trophies;
    private Shelf shelf;

    public Shelf getShelf() {
        return shelf;
    }

    public ArrayList<Trophy> getTrophies() {
        return trophies;
    }
}
