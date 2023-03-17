package model;

import java.util.ArrayList;

public class Game {
    private TableTop tabletop;
    private ArrayList<Player> players;
    private CommonObjective[] commonObjectives;

    public TableTop getTabletop() {
        return tabletop;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public CommonObjective[] getCommonObjectives() {
        return commonObjectives;
    }

}
