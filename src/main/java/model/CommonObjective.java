package model;

import java.util.Optional;
import java.util.function.Function;


public class CommonObjective extends Objective {
	String name;
	int value;
	int pointDecrement;
	Function<Shelf, Boolean> checkCompleted;

	public CommonObjective(String name, int nPlayers, Function<Shelf, Boolean> checkCompleted) {
		super(name);
		this.name = name;
		value = 8;
		pointDecrement = nPlayers > 2 ? 2 : 4;
		this.checkCompleted = checkCompleted;
	}

    @Override
    public Optional<Cockade> isCompleted(Shelf shelf) {
		Optional<Cockade> trophy = Optional.empty();
		if (checkCompleted.apply(shelf)) {
			trophy = Optional.of(new Cockade(name, value));
			value -= pointDecrement;
		}
		return trophy;
    }
}
