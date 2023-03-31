package model;

import java.util.Optional;

public class PersonalObjective extends Objective {
    private final Cell[] cellsCheck;
    private static final int[] points = {1, 2, 4, 6, 9, 12};

    /**
     * Constructor that creates a new personal objective with a specified name and
     * array of cells to check for completion.
     * @author Marco, Ludovico, Lorenzo, Riccardo
     *
     * @param name The name of the personal objective
     * @param cellscheck An array of cells to check for completion of the objective
     */
    public PersonalObjective(String name, Cell[] cellsCheck) {
        super(name);
        this.cellsCheck = cellsCheck;
    }

    // TODO doc post invalidMoveException
    @Override
    public Optional<Cockade> isCompleted(Shelf shelf) {
        int count = 0;
        try {
            for (Cell cell : cellsCheck) {
                Optional<Card> pos = shelf.getCard(cell.y(), cell.x());
                if (pos.isPresent() && pos.get() == cell.card()) {
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
