package model;

import java.util.ArrayList;

public class Game {
    private TableTop tabletop;
    private ArrayList<Player> players;
    private CommonObjective[] commonObjectives;

    /**
     * Method that returns the current tabletop object.
     * @author Lorenzo, Ludovico, Marco, Riccardo
     *
     * @return The current TableTop object
     */
    public TableTop getTabletop() {
        return tabletop;
    }

    /**
     * Method that returns the list of players in the game.
     * @author Lorenzo, Ludovico, Marco, Riccardo
     *
     * @return An ArrayList containing all Player objects in the game
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Method that returns the array of common objectives in the game.
     * @author Lorenzo, Ludovico, Marco, Riccardo
     *
     * @return An array of all CommonObjective objects in the game
     */
    public CommonObjective[] getCommonObjectives() {
        return commonObjectives;
    }

}
