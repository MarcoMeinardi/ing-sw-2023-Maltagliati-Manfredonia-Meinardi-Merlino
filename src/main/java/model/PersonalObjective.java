package model;

import java.util.Optional;

public class PersonalObjective extends Objective {
	private final Cell[] cellsCheck;

	public PersonalObjective(String name, Cell[] cellscheck) {
        super(name);
        this.cellsCheck = cellscheck;
	}

    @Override
    public Optional<Trophy> isCompleted(Shelf shelf) {
		// TODO
        return Optional.empty();
    }
}
