package model;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Iterator to track the current player in the game.
 */
public class PlayerIterator implements Iterator<Player>, Serializable {

    private int index;
    private final int maxIndex;
    private final Game game;

    /**
     * Constructor for the `PlayerIterator` class
     * This constructor is used for a new game and needs only the initial game as a parameter
     * @param game the `Game` object
     */
    public PlayerIterator(Game game) {
        index = 0;
        maxIndex = game.getPlayers().size() - 1;
        this.game = game;
    }

    /**
     * Constructor for the `PlayerIterator` class
     * This constructor is used for loaded games and needs the game object,
     * as well as the saved index
     * @param game the `Game` object
     * @param index the saved index of the current player
     */
    public PlayerIterator(Game game, int index) {
        maxIndex = game.getPlayers().size() - 1;
        if (index == 0) {
            this.index = maxIndex;
        }
        else {
            this.index = index - 1;
        }
        this.game = game;
    }

    /**
     * Check if the game is over
     * @return if another player gets to play or the game is over
     */
    @Override
    public boolean hasNext() {
        if (index == 0) {
            return game.getPlayers().stream().noneMatch(player -> player.getShelf().isFull());
        }
        return true;
    }

    /**
     * Get the next player in the game
     * @return the next player to play
     */
    @Override
    public Player next() {
        if(index == maxIndex) {
            index = 0;
            return game.getPlayers().get(maxIndex);
        }
        else {
            index++;
            return game.getPlayers().get(index - 1);
        }
    }

    /**
     * Getter for the `index` field
     * Used only to save the game
     * @return the `index` field
     */
    public int getIndex() {
        return index;
    }
}
