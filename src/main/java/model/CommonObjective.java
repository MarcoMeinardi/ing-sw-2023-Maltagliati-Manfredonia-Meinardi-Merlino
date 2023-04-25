package model;

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

	/**
	 * Method that generates a list of common objectives based on the number of players.
	 *
	 * @param nPlayers The number of players in the game
	 * @return An array of two randomly selected common objectives
	 * @author Marco, Lorenzo, Ludovico, Riccardo
	 */
	public static CommonObjective[] generateCommonObjectives(int nPlayers) {
		CommonObjective[] selected_objectives = new CommonObjective[2];
		ArrayList<CommonObjective> all_objectives = new ArrayList<>();

		all_objectives.add(new CommonObjective("6 groups of 2 cards", nPlayers, CommonObjective::sixGroupsOfTwoCards));
		all_objectives.add(new CommonObjective("5 cards in diagonal", nPlayers, CommonObjective::fiveCardsInDiagonal));
		all_objectives.add(new CommonObjective("all equal corners", nPlayers, CommonObjective::equalCorners));
		all_objectives.add(new CommonObjective("4 rows of at most 3 different cards", nPlayers, CommonObjective::fourRowsOfAtMostThreeDifferentCards));
		all_objectives.add(new CommonObjective("4 groups of 4 cards", nPlayers, CommonObjective::fourGroupsOfFourCards));
		all_objectives.add(new CommonObjective("2 columns of 6 different cards", nPlayers, CommonObjective::twoColumnsOfSixDifferentCards));
		all_objectives.add(new CommonObjective("2 square-shaped groups", nPlayers, CommonObjective::twoSquareGroups));
		all_objectives.add(new CommonObjective("2 rows with 5 different cards", nPlayers, CommonObjective::twoRowsWithFiveDifferentCards));
		all_objectives.add(new CommonObjective("3 columns of at most 3 different cards", nPlayers, CommonObjective::threeColumnsOfAtMostThreeDifferentCards));
		all_objectives.add(new CommonObjective("X shape group", nPlayers, CommonObjective::equalsX));
		all_objectives.add(new CommonObjective("eight equal cards", nPlayers, CommonObjective::eightEquals));
		all_objectives.add(new CommonObjective("stair-shaped cards", nPlayers, CommonObjective::stairsShape));

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
	 * or an empty Optional object if the objective has not been completed
	 * @author Marco, Ludovico, Lorenzo, Riccardo
	 */
	@Override
	public Optional<Cockade> isCompleted(Shelf shelf) {
		Optional<Cockade> cockade = Optional.empty();
		if (checkCompleted.apply(shelf)) {
			cockade = Optional.of(new Cockade(name, value));
			value -= pointDecrement;
		}
		return cockade;
	}


	// Common objectives functions

	/**
	 * Method computes the number of groups of n cards present in the given Shelf object.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @param n The size of the group to check for
	 * @return The number of groups of n cards present in the given Shelf object
	 * @author Marco
	 *
	 */
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

	/**
	 * Method that checks if the objective "4 groups of 4 cards" is done.
	 *
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean fourGroupsOfFourCards(Shelf shelf) {
		return groupsOfNCards(shelf, 4) >= 4;
	}

	/**
	 * Method that checks if the objective "6 groups of 2 cards" is done.
	 *
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean sixGroupsOfTwoCards(Shelf shelf) {
		return groupsOfNCards(shelf, 2) >= 6;
	}

	/**
	 * Method that checks if the objective "two columns with 6 different cards" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Lorenzo
	 */
	public static Boolean twoColumnsOfSixDifferentCards(Shelf shelf) {
		boolean firstCol = false;
		HashSet<Card> cards = new HashSet<>();

		try {
			for (int x = 0; x < Shelf.COLUMNS; x++) {
				for (int y = 0; y < Shelf.ROWS; y++) {
					if (shelf.getCard(y, x).isEmpty()) {
						break;
					}
					cards.add(shelf.getCard(y, x).get());
				}
				if (cards.size() == 6) {
					if (firstCol) {
						return true;
					}
					firstCol = true;
				}
				cards.clear();
			}
		} catch (InvalidMoveException e) {
			throw new RuntimeException("error while checking two columns of six different cards common objective");
		}

		return false;
	}

	/**
	 * Method that checks if the objective "five cards in diagonal" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco, Lorenzo
	 */
	public static Boolean fiveCardsInDiagonal(Shelf shelf) {
		boolean fullDiagonal;
		Card reference;

		try {
			for (int offset = 0; offset <= Shelf.ROWS - Shelf.COLUMNS; offset++) {
				// sud-west to north-est
				if (shelf.getCard(offset, 0).isPresent()) {
					reference = shelf.getCard(offset, 0).get();
					fullDiagonal = true;
					for (int x = 1; x < Shelf.COLUMNS; x++) {
						Optional<Card> card = shelf.getCard(x + offset, x);
						if (card.isEmpty() || !card.get().equals(reference)) {
							fullDiagonal = false;
							break;
						}
					}
					if (fullDiagonal) return true;
				}

				// sud-est to north-west
				if (shelf.getCard(offset, Shelf.COLUMNS - 1).isPresent()) {
					reference = shelf.getCard(offset, Shelf.COLUMNS - 1).get();
					fullDiagonal = true;
					for (int y = 1; y < Shelf.COLUMNS; y++) {
						Optional<Card> card = shelf.getCard(y + offset, Shelf.COLUMNS - y - 1);
						if (card.isEmpty() || !card.get().equals(reference)) {
							fullDiagonal = false;
							break;
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

	/**
	 * Method that check if the objective "four rows such that each row has at most three different cards" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Ludovico, Marco
	 */
	public static Boolean fourRowsOfAtMostThreeDifferentCards(Shelf shelf) {
		int count = 0;
		HashSet<Card> cards = new HashSet<>();

		try {
			for (int y = 0; y < Shelf.ROWS; y++) {
				boolean isFull = true;
				for (int x = 0; x < Shelf.COLUMNS; x++) {
					if (shelf.getCard(y, x).isEmpty()) {
						isFull = false;
						break;
					}
					cards.add(shelf.getCard(y, x).get());
				}
				if (isFull && cards.size() <= 3) {
					count++;
				}
				cards.clear();
			}
		} catch (InvalidMoveException e) {
			throw new RuntimeException("error while checking five in a row common objective");
		}

		return count == 4;
	}

	private static final int[] cornersX = {0, 0, Shelf.COLUMNS - 1, Shelf.COLUMNS - 1};
	private static final int[] cornersY = {0, Shelf.ROWS - 1, 0, Shelf.ROWS - 1};
	/**
	 * Method that checks if the objective "all equal cards in the four corners of the shelf" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Ludovico, Marco
	 */
	public static Boolean equalCorners(Shelf shelf) {
		try {
			Optional<Card> reference = shelf.getCard(cornersY[0], cornersX[0]);
			if (reference.isEmpty()) {
				return false;
			}
			for (int i = 1; i < 4; i++) {
				if (!shelf.getCard(cornersY[i], cornersX[i]).equals(reference)) {
					return false;
				}
			}

		} catch (InvalidMoveException e) {
			throw new RuntimeException("error while checking corners common objective");
		}

		return true;
	}

	/**
	 * Method that checks if the objective "two rows contains 5 different cards" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Riccardo, Marco
	 */
	public static Boolean twoRowsWithFiveDifferentCards(Shelf shelf) {
		boolean firstRow = false;
		HashSet<Card> cards = new HashSet<>();

		try {
			for (int y = 0; y < Shelf.ROWS; y++) {
				for (int x = 0; x < Shelf.COLUMNS; x++) {
					if (shelf.getCard(y, x).isEmpty()) {
						break;
					}
					cards.add(shelf.getCard(y, x).get());
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

	private static final int[] squareDx = {0, 1, 1};
	private static final int[] squareDy = {1, 0, 1};
	/**
	 * Method that checks if the objective "two non-overlapping 2x2 squares" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean twoSquareGroups(Shelf shelf) {
		boolean firstSquare = false;
		boolean[][] alreadyUsed = new boolean[Shelf.ROWS][Shelf.COLUMNS];

		try {
			for (int y = 0; y < Shelf.ROWS - 1; y++) {
				for (int x = 0; x < Shelf.COLUMNS - 1; x++) {
					Optional<Card> reference = shelf.getCard(y, x);
					if (alreadyUsed[y][x] || alreadyUsed[y][x + 1] || reference.isEmpty()) {
						continue;
					}
					boolean isValidSquare = true;
					for (int i = 0; i < 3; i++) {
						Optional<Card> card = shelf.getCard(y + squareDy[i], x + squareDx[i]);
						if (card.isEmpty() || !card.equals(reference)) {
							isValidSquare = false;
							break;
						}
					}

					if (isValidSquare) {
						if (firstSquare) {
							return true;
						}
						firstSquare = true;
						alreadyUsed[y + 1][x] = true;
						alreadyUsed[y + 1][x + 1] = true;
						x++;  // skip next cell because it would overlap
					}
				}
			}
		} catch (InvalidMoveException e) {
			throw new RuntimeException("error while checking two square groups common objective");
		}

		return false;
	}

	/**
	 * Method that checks if the objective "X-shaped equal cards" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Riccardo,Marco
	 */
	public static Boolean equalsX(Shelf shelf) {
		try {
			for (int y = 1; y < Shelf.ROWS - 1; y++) {
				for (int x = 1; x < Shelf.COLUMNS - 1; x++) {
					if (
						shelf.getCard(y, x).isPresent() &&
						shelf.getCard(y, x).equals(shelf.getCard(y - 1, x - 1)) &&
						shelf.getCard(y, x).equals(shelf.getCard(y + 1, x + 1)) &&
						shelf.getCard(y, x).equals(shelf.getCard(y - 1, x + 1)) &&
						shelf.getCard(y, x).equals(shelf.getCard(y + 1, x - 1))
					) {
						return true;
					}
				}
			}
		} catch (InvalidMoveException e) {
			throw new RuntimeException("error while checking equals X common objective");
		}

		return false;
	}

	/**
	 * Method that checks if the objective "stair shaped cards" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Riccardo,Marco
	 */
	public static Boolean stairsShape(Shelf shelf) {
		int h = 0;

		try {
			while (h < Shelf.ROWS && shelf.getCard(h, 0).isPresent()) {
				h++;
			}

			if (h < 1 || (h > 2 && h < 5)) {
				return false;
			}
			int direction = h <= 2 ? 1 : -1;
			h += direction - 1;

			for (int x = 1; x < Shelf.COLUMNS; x++) {
				if (shelf.getCard(h, x).isEmpty() || (h != Shelf.ROWS - 1 && shelf.getCard(h + 1, x).isPresent())) {
					return false;
				}
				h += direction;
			}
		} catch (InvalidMoveException e) {
			throw new RuntimeException("error while checking stairs shape common objective");
		}

		return true;
	}

	/**
	 * Method that checks if the objective "8 equal cards" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean eightEquals(Shelf shelf) {
		HashMap<Card, Integer> cardCount = new HashMap<>();

		try {
			for (int y = 0; y < Shelf.ROWS; y++) {
				for (int x = 0; x < Shelf.COLUMNS; x++) {
					if (shelf.getCard(y, x).isPresent()) {
						Card card = shelf.getCard(y, x).get();
						int count = cardCount.getOrDefault(card, 0) + 1;
						if (count == 8) {
							return true;
						}
						cardCount.put(card, count);
					}
				}
			}
		} catch (InvalidMoveException e) {
			throw new RuntimeException("error while checking eight equals common objective");
		}

		return false;
	}

	/**
	 * Method that checks if the objective "three columns where each column has at most three different cards" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean threeColumnsOfAtMostThreeDifferentCards(Shelf shelf) {
		int count = 0;
		HashSet<Card> cards = new HashSet<>();

		try {
			for (int x = 0; x < Shelf.COLUMNS; x++) {
				boolean isFull = true;
				for (int y = 0; y < Shelf.ROWS; y++) {
					if (shelf.getCard(y, x).isEmpty()) {
						isFull = false;
						break;
					}
					cards.add(shelf.getCard(y, x).get());
				}
				if (isFull && cards.size() <= 3) {
					count++;
				}
				cards.clear();
			}
		} catch (InvalidMoveException e) {
			throw new RuntimeException("error while checking five in a row common objective");
		}

		return count == 3;
	}

}
