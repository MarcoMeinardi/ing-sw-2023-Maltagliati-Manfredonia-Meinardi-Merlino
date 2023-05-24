package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Game implements Iterable<Player> {

    private TableTop tabletop;
    private ArrayList<Player> players;
    private ArrayList<CommonObjective> commonObjectives;
    private PlayerIterator playerIterator;

    public Game(ArrayList<String> playersNames) {
        this.tabletop = new TableTop(playersNames.size());
        PersonalObjective[] personalObjective = PersonalObjective.generatePersonalObjectives(playersNames.size());
        this.players = new ArrayList<>();
        for (int i = 0; i < playersNames.size(); i++) {
            this.players.add(new Player(playersNames.get(i), personalObjective[i]));
        }
        Collections.shuffle(this.players);
        this.commonObjectives = CommonObjective.generateCommonObjectives(players.size());
    }

    @Override
    public Iterator<Player> iterator() {
        playerIterator = new PlayerIterator(this);
        return playerIterator;
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
    public ArrayList<CommonObjective> getCommonObjectives() {
        return commonObjectives;
    }


}
