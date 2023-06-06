package model;

import java.io.Serializable;
import java.util.Iterator;

public class PlayerIterator implements Iterator<Player>, Serializable {

    private int index;
    private int maxIndex;

    private Game game;
    public PlayerIterator(Game game) {
        index = 0;
        maxIndex = game.getPlayers().size() - 1;
        this.game = game;
    }

    public PlayerIterator(Game game, int index) {
        this.index = index;
        maxIndex = game.getPlayers().size() - 1;
        this.game = game;
    }

    @Override
    public boolean hasNext() {
        if (index == 0) {
            return game.getPlayers().stream().noneMatch(player -> player.getShelf().isFull());
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
            return game.getPlayers().get(index - 1);
        }
    }

    public int getIndex() {
        return index == 0 ? maxIndex : index - 1;
    }
}
