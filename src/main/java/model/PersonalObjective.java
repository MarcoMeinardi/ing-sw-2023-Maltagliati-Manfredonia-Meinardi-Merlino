package model;

import java.util.Optional;

public class PersonalObjective extends Objective {
    private final Cell[] cellsCheck;
    static final int[] points = {1, 2, 4, 6, 9, 12};

    public PersonalObjective(String name, Cell[] cellscheck) {
        super(name);
        this.cellsCheck = cellscheck;
    }

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
