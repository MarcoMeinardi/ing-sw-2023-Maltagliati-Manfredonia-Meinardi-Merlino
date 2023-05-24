package model;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class CommonObjective extends Objective {
	String name;
	int value;
	int pointDecrement;
	Function<Shelf, Boolean> checkCompleted;

	private static final int INITIAL_VALUE = 8;
	private static final int POINT_DECREMENT = 2;
	private static final int POINT_DECREMENT_2_PLAYERS = 4;
	public static final int N_COMMON_OBJECTIVES = 2;

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
		pointDecrement = nPlayers == 2 ? POINT_DECREMENT_2_PLAYERS : POINT_DECREMENT;
		this.checkCompleted = checkCompleted;
	}

	public CommonObjective(String name, int nPlayers) {
		// super constructor must be the first statement
		super(generateAllCommonObjectives(nPlayers).stream().filter(o -> o.name.equals(name)).findFirst().get().name);
		value = INITIAL_VALUE;
		pointDecrement = nPlayers == 2 ? POINT_DECREMENT_2_PLAYERS : POINT_DECREMENT;

		for (CommonObjective objective : generateAllCommonObjectives(nPlayers)) {
			if (objective.getName().equals(name)) {
				this.checkCompleted = objective.checkCompleted;
				return;
			}
		}
		throw new RuntimeException("Objective not found");
	}

	/**
	 * Generate the list of all the common objectives
	 *
	 * @param nPlayers The number of players in the game
	 * @return An array list containing all the common objectives
	 * @author Marco, Lorenzo, Ludovico, Riccardo
	 */
	private static ArrayList<CommonObjective> generateAllCommonObjectives(int nPlayers) {
		ArrayList<CommonObjective> allObjectives = new ArrayList<>();

		allObjectives.add(new CommonObjective("6 groups of 2 cards", nPlayers, CommonObjective::sixGroupsOfTwoCards));
		allObjectives.add(new CommonObjective("5 cards in diagonal", nPlayers, CommonObjective::fiveCardsInDiagonal));
		allObjectives.add(new CommonObjective("all equal corners", nPlayers, CommonObjective::equalCorners));
		allObjectives.add(new CommonObjective("4 rows of at most 3 different cards", nPlayers, CommonObjective::fourRowsOfAtMostThreeDifferentCards));
		allObjectives.add(new CommonObjective("4 groups of 4 cards", nPlayers, CommonObjective::fourGroupsOfFourCards));
		allObjectives.add(new CommonObjective("2 columns of 6 different cards", nPlayers, CommonObjective::twoColumnsOfSixDifferentCards));
		allObjectives.add(new CommonObjective("2 square-shaped groups", nPlayers, CommonObjective::twoSquareGroups));
		allObjectives.add(new CommonObjective("2 rows with 5 different cards", nPlayers, CommonObjective::twoRowsWithFiveDifferentCards));
		allObjectives.add(new CommonObjective("3 columns of at most 3 different cards", nPlayers, CommonObjective::threeColumnsOfAtMostThreeDifferentCards));
		allObjectives.add(new CommonObjective("X shapes group", nPlayers, CommonObjective::equalsX));
		allObjectives.add(new CommonObjective("eight equal cards", nPlayers, CommonObjective::eightEquals));
		allObjectives.add(new CommonObjective("stair-shaped cards", nPlayers, CommonObjective::stairsShape));

		return allObjectives;
	}

	/**
	 * Method that generates a list of common objectives based on the number of players.
	 *
	 * @param nPlayers The number of players in the game
	 * @return An array list of two randomly selected common objectives
	 * @author Marco, Lorenzo, Ludovico, Riccardo
	 */
	public static ArrayList<CommonObjective> generateCommonObjectives(int nPlayers) {
		ArrayList<CommonObjective> allObjectives = generateAllCommonObjectives(nPlayers);
		Collections.shuffle(allObjectives);
		return allObjectives.stream().limit(N_COMMON_OBJECTIVES).collect(Collectors.toCollection(ArrayList::new));
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

	/**
	 * Getter for the value attribute
	 *
	 * @return The current value of the objective
	 * @author Marco
	 */
	public int getValue() {
		return value;
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
	private static int groupsOfAtLeastNCards(Shelf shelf, int n) {
		boolean[][] visited = new boolean[Shelf.ROWS][Shelf.COLUMNS];
		int groups = 0;

		try {
			for (int y = 0; y < Shelf.ROWS; y++) {
				for (int x = 0; x < Shelf.COLUMNS; x++) {
					if (shelf.getCard(y, x).isPresent()) {
						groups += shelf.getGroupSize(y, x, shelf.getCard(y, x).get(), visited) >= n ? 1 : 0;
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
	private static Boolean fourGroupsOfFourCards(Shelf shelf) {
		return groupsOfAtLeastNCards(shelf, 4) >= 4;
	}
	/**
	 * public wrapper for fourGroupsOfFourCards method, only used for testing
	 *
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean fourGroupsOfFourCardsTest(Shelf shelf) {
		return fourGroupsOfFourCards(shelf);
	}

	/**
	 * Method that checks if the objective "6 groups of 2 cards" is done.
	 *
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	private static Boolean sixGroupsOfTwoCards(Shelf shelf) {
		return groupsOfAtLeastNCards(shelf, 2) >= 6;
	}
	/**
	 * public wrapper for sixGroupsOfTwoCards method, only used for testing
	 *
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean sixGroupsOfTwoCardsTest(Shelf shelf) {
		return sixGroupsOfTwoCards(shelf);
	}

	/**
	 * Method that checks if the objective "two columns with 6 different cards" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Lorenzo
	 */
	private static Boolean twoColumnsOfSixDifferentCards(Shelf shelf) {
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
	 * public wrapper for twoColumnsOfSixDifferentCards method, only used for testing
	 *
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean twoColumnsOfSixDifferentCardsTest(Shelf shelf) {
		return twoColumnsOfSixDifferentCards(shelf);
	}

	/**
	 * Method that checks if the objective "five cards in diagonal" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco, Lorenzo
	 */
	private static Boolean fiveCardsInDiagonal(Shelf shelf) {
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
	 * public wrapper for fiveCardsInDiagonal method, only used for testing
	 *
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean fiveCardsInDiagonalTest(Shelf shelf) {
		return fiveCardsInDiagonal(shelf);
	}

	/**
	 * Method that check if the objective "four rows such that each row has at most three different cards" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Ludovico, Marco
	 */
	private static Boolean fourRowsOfAtMostThreeDifferentCards(Shelf shelf) {
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
	/**
	 * public wrapper for fourRowsOfAtMostThreeDifferentCards method, only used for testing
	 *
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean fourRowsOfAtMostThreeDifferentCardsTest(Shelf shelf) {
		return fourRowsOfAtMostThreeDifferentCards(shelf);
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
	private static Boolean equalCorners(Shelf shelf) {
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
	 * public wrapper for equalCorners method, only used for testing
	 *
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean equalCornersTest(Shelf shelf) {
		return equalCorners(shelf);
	}

	/**
	 * Method that checks if the objective "two rows contains 5 different cards" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Riccardo, Marco
	 */
	private static Boolean twoRowsWithFiveDifferentCards(Shelf shelf) {
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
	/**
	 * public wrapper for twoRowsWithFiveDifferentCards method, only used for testing
	 *
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean twoRowsWithFiveDifferentCardsTest(Shelf shelf) {
		return twoRowsWithFiveDifferentCards(shelf);
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
	 * public wrapper for twoSquareGroups method, only used for testing
	 *
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean twoSquareGroupsTest(Shelf shelf) {
		return twoSquareGroups(shelf);
	}

	/**
	 * Method that checks if the objective "X-shaped equal cards" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Riccardo,Marco
	 */
	private static Boolean equalsX(Shelf shelf) {
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
	 * public wrapper for equalsX method, only used for testing
	 *
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean equalsXTest(Shelf shelf) {
		return equalsX(shelf);
	}

	/**
	 * Method that checks if the objective "stair shaped cards" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Riccardo,Marco
	 */
	private static Boolean stairsShape(Shelf shelf) {
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
	 * public wrapper for stairsShape method, only used for testing
	 *
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean stairsShapeTest(Shelf shelf) {
		return stairsShape(shelf);
	}

	/**
	 * Method that checks if the objective "8 equal cards" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	private static Boolean eightEquals(Shelf shelf) {
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
	 * public wrapper for eightEquals method, only used for testing
	 *
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean eightEqualsTest(Shelf shelf) {
		return eightEquals(shelf);
	}

	/**
	 * Method that checks if the objective "three columns where each column has at most three different cards" is done.
	 *
	 * @throws RuntimeException if there is any error while checking
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	private static Boolean threeColumnsOfAtMostThreeDifferentCards(Shelf shelf) {
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
	/**
	 * public wrapper for threeColumnsOfAtMostThreeDifferentCards method, only used for testing
	 *
	 * @param shelf The shelf object
	 * @return true if the objective is done, false otherwise
	 * @author Marco
	 */
	public static Boolean threeColumnsOfAtMostThreeDifferentCardsTest(Shelf shelf) {
		return threeColumnsOfAtMostThreeDifferentCards(shelf);
	}

}
