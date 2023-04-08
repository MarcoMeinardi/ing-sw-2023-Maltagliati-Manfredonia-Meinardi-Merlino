package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;


public class CommonObjective extends Objective {
	String name;
	int value;
	int pointDecrement;
	Function<Shelf, Boolean> checkCompleted;

	private static final int INITIAL_VALUE = 8;
	private static final int POINT_DECREMENT = 2;
	private static final int POINT_DECREMENT_2_PLAYERS = 4;

	/**
	 * Constructor that creates a new common objective with a specified name,
	 * value, and point decrement based on the number of players.
	 * The objective also includes a function for checking if it has been completed.
	 * @author Marco, Lorenzo, Ludovico, Riccardo
	 *
	 * @param name The name of the objective
	 * @param nPlayers The number of players in the game
	 * @param checkCompleted A function for checking if the objective has been completed
	 */
	public CommonObjective(String name, int nPlayers, Function<Shelf, Boolean> checkCompleted) {
		super(name);
		this.name = name;
		value = INITIAL_VALUE;
		pointDecrement = nPlayers > 2 ? POINT_DECREMENT_2_PLAYERS : POINT_DECREMENT;
		this.checkCompleted = checkCompleted;
	}

	public static CommonObjective[] generateCommonObjectives(int nPlayers){
		CommonObjective[] selected_objectives = new CommonObjective[2];
		ArrayList<CommonObjective> all_objectives = new ArrayList<>();
		all_objectives.add(new CommonObjective("4 groups of 4 cards", nPlayers, CommonObjective::fourGroupsOfFourCards));
		all_objectives.add(new CommonObjective("6 groups of 2 cards", nPlayers, CommonObjective::sixGroupsOfTwoCards));
		Collections.shuffle(all_objectives);
		selected_objectives[0] = all_objectives.get(0);
		selected_objectives[1] = all_objectives.get(1);
		return selected_objectives;
	}

	/**
	 * Method that checks if the common objective has been completed using a specified
	 * shelf, and returns an Optional object containing a Cockade trophy if it has.
	 * If the objective is completed, the value of the trophy is decreased by the point decrement.
	 * @author Marco, Ludovico, Lorenzo, Riccardo
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


	// Common objectives functions

	private static Boolean fourGroupsOfFourCards(Shelf shelf) {
		boolean[][] visited = new boolean[Shelf.ROWS][Shelf.COLUMNS];

		int groups = 0;
		try {
			for (int y = 0; y < Shelf.ROWS; y++) {
				for (int x = 0; x < Shelf.COLUMNS; x++) {
					if (shelf.getCard(y, x).isPresent()) {
						groups += Math.floor(shelf.getGroupSize(y, x, shelf.getCard(y, x).get(), visited) / 4);
					}
				}
			}
		} catch (InvalidMoveException e) {}

		return groups >= 4;
	}

	private static Boolean sixGroupsOfTwoCards(Shelf shelf) {
		boolean[][] visited = new boolean[Shelf.ROWS][Shelf.COLUMNS];

		int groups = 0;
		try {
			for (int y = 0; y < Shelf.ROWS; y++) {
				for (int x = 0; x < Shelf.COLUMNS; x++) {
					if (shelf.getCard(y, x).isPresent()) {
						groups += Math.floor(shelf.getGroupSize(y, x, shelf.getCard(y, x).get(), visited) / 2);
					}
				}
			}
		} catch (InvalidMoveException e) {}

		return groups >= 6;
	}
}
