package model;

import javax.swing.text.StyledEditorKit;
import java.util.*;
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
	 *
	 * @param name           The name of the objective
	 * @param nPlayers       The number of players in the game
	 * @param checkCompleted A function for checking if the objective has been completed
	 * @author Marco, Lorenzo, Ludovico, Riccardo
	 */
	public CommonObjective(String name, int nPlayers, Function<Shelf, Boolean> checkCompleted) {
		super(name);
		this.name = name;
		value = INITIAL_VALUE;
		pointDecrement = nPlayers > 2 ? POINT_DECREMENT_2_PLAYERS : POINT_DECREMENT;
		this.checkCompleted = checkCompleted;
	}

	public static CommonObjective[] generateCommonObjectives(int nPlayers) {
		CommonObjective[] selected_objectives = new CommonObjective[2];
		ArrayList<CommonObjective> all_objectives = new ArrayList<>();
		all_objectives.add(new CommonObjective("4 groups of 4 cards", nPlayers, CommonObjective::fourGroupsOfFourCards));
		all_objectives.add(new CommonObjective("6 groups of 2 cards", nPlayers, CommonObjective::sixGroupsOfTwoCards));
		all_objectives.add(new CommonObjective("2 columns of 6 different cards", nPlayers, CommonObjective::twoColumnsOfSixDifferentCards));
		all_objectives.add(new CommonObjective("5 cards in diagonal", nPlayers, CommonObjective::fiveCardsInDiagonal));
		Collections.shuffle(all_objectives);
		selected_objectives[0] = all_objectives.get(0);
		selected_objectives[1] = all_objectives.get(1);
		return selected_objectives;
	}

	/**
	 * Method that checks if the common objective has been completed using a specified
	 * shelf, and returns an Optional object containing a Cockade trophy if it has.
	 * If the objective is completed, the value of the trophy is decreased by the point decrement.
	 *
	 * @param shelf The shelf to check for completion of the objective
	 * @return Optional object containing a Cockade trophy,
	 * or an empty Optional object if the objective has not been completed.
	 * @author Marco, Ludovico, Lorenzo, Riccardo
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

	private static int groupsOfNCards(Shelf shelf, int n) {
		boolean[][] visited = new boolean[Shelf.ROWS][Shelf.COLUMNS];

		int groups = 0;
		try {
			for (int y = 0; y < Shelf.ROWS; y++) {
				for (int x = 0; x < Shelf.COLUMNS; x++) {
					if (shelf.getCard(y, x).isPresent()) {
						groups += shelf.getGroupSize(y, x, shelf.getCard(y, x).get(), visited) / n;
					}
				}
			}
		} catch (InvalidMoveException e) {
			throw new RuntimeException("error while checking groups of n cards common objective");
		}

		return groups;
	}

	private static Boolean fourGroupsOfFourCards(Shelf shelf) {
		return groupsOfNCards(shelf, 4) >= 4;
	}

	private static Boolean sixGroupsOfTwoCards(Shelf shelf) {
		return groupsOfNCards(shelf, 6) >= 2;
	}

	private static Boolean twoColumnsOfSixDifferentCards(Shelf shelf) {
		boolean second = false;
		HashSet<Card> cards = new HashSet();
		Card tmp;
		try {
			for (int x = 0; x < Shelf.COLUMNS; x++) {
				for (int y = 0; y < Shelf.ROWS; y++) {
					if (shelf.getCard(y, x).isEmpty()) {
						break;
					}
					tmp = shelf.getCard(y, x).get();
					if (cards.contains(tmp)) {
						break;
					}
					cards.add(tmp);
				}
				if (cards.size() == 6) {
					if (second) {
						return true;
					}
					second = true;
				}
				cards.clear();
			}
		} catch (InvalidMoveException e) {
			throw new RuntimeException("error while checking two columns of six different cards common objective");
		}
		return false;
	}

	private static Boolean fiveCardsInDiagonal(Shelf shelf) {
		boolean fullDiagonal;
		Card referece;
		try {
			for (int offset = 0; offset <= Shelf.ROWS - Shelf.COLUMNS; offset++) {
				// sud-west to north-est
				if (!shelf.getCard(offset, 0).isEmpty()) {
					referece = shelf.getCard(offset, 0).get();
					fullDiagonal = true;
					for (int y = 1; fullDiagonal && y < Shelf.COLUMNS; y++) {
						Optional<Card> card = shelf.getCard(y + offset, y);
						if (card.isEmpty() || !card.get().equals(referece)) {
							fullDiagonal = false;
						}
					}
					if (fullDiagonal) return true;
				}

				// sud-est to north-west
				if (!shelf.getCard(offset, Shelf.COLUMNS - 1).isEmpty()) {
					referece = shelf.getCard(offset, Shelf.COLUMNS - 1).get();
					fullDiagonal = true;
					for (int y = 1; fullDiagonal && y < Shelf.COLUMNS; y++) {
						Optional<Card> card = shelf.getCard(y + offset, Shelf.COLUMNS - y - 1);
						if (card.isEmpty() || !card.get().equals(referece)) {
							fullDiagonal = false;
						}
					}
					if (fullDiagonal) return true;
				}
			}
		} catch (InvalidMoveException e) {
			throw new RuntimeException("error while checking five cards in diagonal common objective");
		}
		return false;
	}

	private static Boolean fourRowsOfAtMostThreeDifferentCards(Shelf shelf) {
		int count = 0;
		HashSet<Card> cards = new HashSet();

		try {
			for (int y = 0; y < Shelf.ROWS; y++) {
				for (int x = 0; x < Shelf.COLUMNS; x++) {
					if (shelf.getCard(y, x).isEmpty()) {
						break;
					}
					cards.add(shelf.getCard(y, x).get());
				}
				if (cards.size() <= 3) {
					count++;
				}
				cards.clear();
			}
		} catch (InvalidMoveException e) {
			throw new RuntimeException("error while checking five in a row common objective");
		}

		return count == 4;
	}

	private static Boolean equalsCorners(Shelf shelf) {
		try {
			if (shelf.getCard(0, 0).isEmpty() || shelf.getCard(0, Shelf.COLUMNS-1).isEmpty() ||
					shelf.getCard(Shelf.ROWS-1, 0).isEmpty() || shelf.getCard(Shelf.ROWS-1, Shelf.COLUMNS-1).isEmpty()) {

				return false;

			} else {
				if (shelf.getCard(0, 0).equals(shelf.getCard(0, Shelf.COLUMNS-1)) &&
						shelf.getCard(0, 0).equals(shelf.getCard(Shelf.ROWS-1, 0)) &&
						shelf.getCard(0, 0).equals(shelf.getCard(Shelf.ROWS-1, Shelf.COLUMNS-1))) {
					return true;
				}

				return false;
			}

		} catch (InvalidMoveException e) {
			throw new RuntimeException("error while checking corners common objective");
		}
	}

	private static Boolean twoRowsWithFiveDifferentCards(Shelf shelf) {
		boolean firstRow = false;
		HashSet<Card> cards = new HashSet();
		Card tmp;
		try {
			for (int y = 0; y < Shelf.ROWS; y++) {
				for (int x = 0; x < Shelf.COLUMNS; x++) {
					if (shelf.getCard(y, x).isEmpty()) {
						break;
					}
					tmp = shelf.getCard(y, x).get();
					if (cards.contains(tmp)) {
						break;
					}
					cards.add(tmp);
				}
				if (cards.size() == 5) {
					if (firstRow) {
						return true;
					}
					firstRow = true;
				}
				cards.clear();
			}
		} catch (InvalidMoveException e) {
			throw new RuntimeException("error while checking two rows with five different cards common objective");
		}
		return false;
	}

}
