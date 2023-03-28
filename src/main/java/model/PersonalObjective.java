package model;

import java.util.Optional;

public class PersonalObjective extends Objective {
    private final Cell[] cellsCheck;
    static final int[] points = {1, 2, 4, 6, 9, 12};

    /**
     * @author Marco, Ludovico, Lorenzo, Riccardo
     * Constructor that creates a new personal objective with a specified name and
     * array of cells to check for completion.
     *
     * @param name The name of the personal objective
     * @param cellscheck An array of cells to check for completion of the objective
     */
    public PersonalObjective(String name, Cell[] cellscheck) {
        super(name);
        this.cellsCheck = cellscheck;
    }

   // TODO doc post invalidmoveexception
    @Override
    public Optional<Cockade> isCompleted(Shelf shelf) {
        int count = 0;
        try {
            for(int i = 0; i < cellsCheck.length; i++) {
                if (shelf.getCard(cellsCheck[i].y(), cellsCheck[i].x()) == cellsCheck[i].card()) {
                    count++;
                }
            }
        } catch (InvalidMoveException e) {
            // TODO add log
        }
        if (count > 0) {
            return Optional.of(new Cockade(this.name,points[count - 1]));
        }
        return Optional.empty();
    }
}
