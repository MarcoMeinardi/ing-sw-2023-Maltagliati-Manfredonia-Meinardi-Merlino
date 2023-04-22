package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

public class Game implements Iterable<Player> {

    private TableTop tabletop;
    private ArrayList<Player> players;
    private CommonObjective[] commonObjectives;

    public Game(ArrayList<String> playersNames) {
        this.tabletop = new TableTop(players.size());
        PersonalObjective[] personalObjective = PersonalObjective.generatePersonalObjectives(players.size());
        this.players = new ArrayList<>();
        for (int i = 0; i < playersNames.size(); i++) {
            this.players.add(new Player(playersNames.get(i), personalObjective[i]));
        }
        this.commonObjectives = CommonObjective.generateCommonObjectives(players.size());
    }

    @Override
    public Iterator<Player> iterator() {
        return new PlayerIterator(this);
    }

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
