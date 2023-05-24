package model;

import java.io.Serializable;
import java.util.Iterator;

public class PlayerIterator implements Iterator<Player>, Serializable {

    private int index;
    private int maxIndex;

    private Game game;
    public PlayerIterator(Game game) {
        index = 0;
        maxIndex = game.getPlayers().size()-1;
        this.game = game;
    }

    @Override
    public boolean hasNext() {
        if (index == 0) {
            if (game.getPlayers().stream().anyMatch((player) -> player.getShelf().isFull())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Player next() {
        if(index == maxIndex) {
            index = 0;
            return game.getPlayers().get(maxIndex);
        }
        else {
            index++;
            return game.getPlayers().get(index-1);
        }
    }

}
