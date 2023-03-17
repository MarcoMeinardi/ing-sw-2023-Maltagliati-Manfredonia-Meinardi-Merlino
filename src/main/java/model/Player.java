package model;

import java.util.ArrayList;

public class Player {
    private PersonalObjective personalObjective;
    private CommonObjective[] commonObjectives;
    private ArrayList<Trophy> trophies;
    private Shelf shelf;

    public Shelf getShelf() {
        return shelf;
    }

    public ArrayList<Trophy> getTrophies() {
        return trophies;
    }
}
