package model;

import java.util.ArrayList;

public class Game {
    private TableTop tabletop;
    private ArrayList<Player> players;
    private CommonObjective[] commonObjectives;

    /**
     * @author Lorenzo, Ludovico, Marco, Riccardo
     * Method that returns the current tabletop object.
     *
     * @return The current TableTop object
     */
    public TableTop getTabletop() {
        return tabletop;
    }

    /**
     * @author Lorenzo, Ludovico, Marco, Riccardo
     * Method that returns the list of players in the game.
     *
     * @return An ArrayList containing all Player objects in the game
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * @author Lorenzo, Ludovico, Marco, Riccardo
     * Method that returns the array of common objectives in the game.
     *
     * @return An array of all CommonObjective objects in the game
     */
    public CommonObjective[] getCommonObjectives() {
        return commonObjectives;
    }

}
