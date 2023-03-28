package model;

import java.util.Optional;
import java.util.function.Function;


public class CommonObjective extends Objective {
	String name;
	int value;
	int pointDecrement;
	Function<Shelf, Boolean> checkCompleted;

	/**
	 * @author Marco, Lorenzo, Ludovico, Riccardo
	 * Constructor that creates a new common objective with a specified name,
	 * value, and point decrement based on the number of players.
	 * The objective also includes a function for checking if it has been completed.
	 *
	 * @param name The name of the objective
	 * @param nPlayers The number of players in the game
	 * @param checkCompleted A function for checking if the objective has been completed
	 */
	public CommonObjective(String name, int nPlayers, Function<Shelf, Boolean> checkCompleted) {
		super(name);
		this.name = name;
		value = 8;
		pointDecrement = nPlayers > 2 ? 2 : 4;
		this.checkCompleted = checkCompleted;
	}

	/**
	 * @author Marco, Ludovico, Lorenzo, Riccardo
	 * Method that checks if the common objective has been completed using a specified
	 * shelf, and returns an Optional object containing a Cockade trophy if it has.
	 * If the objective is completed, the value of the trophy is decreased by the point decrement.
	 *
	 * @param shelf The shelf to check for completion of the objective
	 * @return Optional object containing a Cockade trophy,
	 * or an empty Optional object if the objective has not been completed.
	 */
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
