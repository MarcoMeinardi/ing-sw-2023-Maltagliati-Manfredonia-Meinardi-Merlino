package model;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Class that represents the shelf of a player
 * It holds the cards that the player has placed on the shelf.
 * You can construct an empty shelf by calling the constructor with no parameters,
 * or give a serializable representation of the shelf (as a matrix of `Card`) to load it.
 */
public class Shelf {
	// Size of the shelf 6(rows)x5(columns)
	private final Optional<Card>[][] slots;

	public static final int COLUMNS = 5;
	public static final int ROWS = 6;

	private static final int[] groupPoints = {2, 3, 5, 8};
	private static final int[] dx = {-1, 0, 1, 0};
	private static final int[] dy = {0, -1, 0, 1};

	/**
	 * Constructor that creates a new shelf with a two-dimensional array of empty cards
	 * representing each slot on the shelf.
	 *
	 * @author Lorenzo, Marco, Ludovico
	 */
	public Shelf() {
		slots = new Optional[ROWS][COLUMNS];
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				slots[y][x] = Optional.empty();
			}
		}
	}

	/**
	 * Constructor to initialize a shelf from a save state
	 * @param cards a matrix of `Card` representing the shelf
	 */
	public Shelf(Card[][] cards) {
		slots = new Optional[ROWS][COLUMNS];
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				if (cards[y][x] != null) {
					slots[y][x] = Optional.of(cards[y][x]);
				} else {
					slots[y][x] = Optional.empty();
				}
			}
		}
	}

	/**
	 * Constructor to initialize a shelf with a specified two-dimensional array of cards.
	 * Note: this game must not be used by the controller it has to be used only for testing or for the CLI
	 *
	 * @param slots The two-dimensional array of cards to initialize the shelf with
	 * @author Marco
	 */
	public Shelf(Optional<Card>[][] slots) {
		this.slots = slots;
	}

	/**
	 * Method that inserts a specified array of cards into a specified column on the shelf.
	 * If the column is invalid (out of range), throws an InvalidMoveException with an appropriate error message.
	 * If the number of cards being inserted is less than 1 or greater than 3, also throws an InvalidMoveException.
	 * If there is not enough space in the column to insert all the cards, throws an InvalidMoveException.
	 * Otherwise, inserts the cards into the column, starting from the bottom slot up to the first empty slot.
	 *
	 * @param column The index of the column to insert the cards into
	 * @param cards  The array of cards to insert
	 * @throws InvalidMoveException if the column index is out of range, the number of cards is invalid,
	 *                              or there is not enough space in the column.
	 * @author Ludovico, Marco, Lorenzo
	 */
	public void insert(int column, ArrayList<Card> cards) throws InvalidMoveException {
		if (column < 0 || column >= COLUMNS) {
			throw new InvalidMoveException("Invalid column");
		}
		if (cards.size() == 0 || cards.size() > 3) {
			throw new InvalidMoveException("Invalid number of cards");
		}
		int highest = getHighest(column);
		if (cards.size() > ROWS - highest) {
			throw new InvalidMoveException("Not enough space in column");
		}
		for (Card card : cards) {
			slots[highest++][column] = Optional.of(card);
		}
	}

	/**
	 * Private helper method that returns the index of the highest empty slot in a specified column on the shelf.
	 *
	 * @param column The index of the column to check
	 * @return The index of the highest empty slot in the column
	 * @author Ludovico, Marco
	 */
	private int getHighest(int column) {
		for (int y = 0; y < ROWS; y++) {
			if (slots[y][column].isEmpty()) {
				return y;
			}
		}
		return ROWS;
	}

	/**
	 * Method that returns the card in a specified slot on the shelf.
	 * If the specified coordinates are out of bounds, throws an InvalidMoveException with an appropriate error message.
	 *
	 * @param y The row index of the card to retrieve
	 * @param x The column index of the card to retrieve
	 * @return The card in the specified slot
	 * @throws InvalidMoveException if the specified coordinates are out of bounds
	 * @author Ludovico
	 */
	public Optional<Card> getCard(int y, int x) throws InvalidMoveException {
		if (y >= ROWS || y < 0 || x < 0 || x >= COLUMNS) {
			throw new InvalidMoveException("Card requested is out of bound");
		}
		return slots[y][x];
	}

	/**
	 * Returns an ArrayList of Cockades representing all groups of adjacent
	 * non-empty cards on the board with a size of at least 3.
	 * A Cockade is added to the resulting ArrayList for each group, with a name indicating the size and
	 * color of the group and the associated point value.
	 *
	 * @return ArrayList of Cockades representing all groups of adjacent non-empty cards on the board
	 * with a size of at least 3.
	 * @author Marco
	 */
	public ArrayList<Cockade> getGroupsCockades() {
		ArrayList<Cockade> result = new ArrayList<>();

		boolean[][] visited = new boolean[ROWS][COLUMNS];
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				if (slots[y][x].isEmpty()) continue;

				int groupSize = getGroupSize(y, x, slots[y][x].get().getType(), visited);
				if (groupSize >= 3) {
					String cockadeName = String.format("Area of %s of size %d", slots[y][x].get().getType(), groupSize);
					result.add(new Cockade(cockadeName, groupPoints[Math.min(groupSize - 3, 3)]));
				}
			}
		}

		return result;
	}

	/**
	 * Calculates and returns the size of the group of adjacent non-empty cards on the board
	 * that includes the card at the specified coordinates and has the same color as the reference card.
	 *
	 * @param y         The vertical index of the card to start the search from.
	 * @param x         The horizontal index of the card to start the search from.
	 * @param reference The reference card used to determine the color of the group.
	 * @param visited   A boolean array representing which cards have already been visited.
	 * @return The number of cards in the group starting from the specified coordinates and
	 * having the same color as the reference card.
	 * @author Marco
	 */
	public int getGroupSize(int y, int x, Card.Type reference, boolean[][] visited) {
		if (y < 0 || x < 0 || y >= ROWS || x >= COLUMNS || visited[y][x] || !slots[y][x].map(value -> value.getType()  == reference).orElse(false)) {
			return 0;
		}
		visited[y][x] = true;

		int result = 1;
		for (int i = 0; i < 4; i++) {
			result += getGroupSize(y + dy[i], x + dx[i], reference, visited);
		}

		return result;
	}

	/**
	 * Returns a boolean indicating whether the shelf is full or not.
	 *
	 * @return True if the shelf is full, false otherwise
	 * @author Ludovico
	 */
	public boolean isFull() {
		for (int x = 0; x < COLUMNS; x++) {
			if (slots[ROWS - 1][x].isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a serializable copy of the shelf
	 * Note: this method must only be used to send the shelf over network,
	 * DON'T use it in any other case and once received, convert it immediately
	 * to it's `Optional` form
	 *
	 * @return a serializable copy of the shelf
	 * @author Marco
	 */
	public Card[][] getSerializable() {
		Card[][] result = new Card[ROWS][COLUMNS];

		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				result[y][x] = slots[y][x].orElse(null);
			}
		}

		return result;
	}

	/**
	 * This method must NOT be used outside of testing.
	 * Inserts the card in the desired position, disregarding the rules of the game.
	 *
	 * @param column The column in which the card will be inserted
	 * @param row    The row in which the card will be inserted
	 * @param card   The card to be inserted
	 * @author Ludovico
	 */
	public void insertTest(int column, int row, Card card) {
		slots[row][column] = Optional.of(card);
	}

	/**
	 * Count the number of cards of a certain type in the shelf
	 *
	 * @param card the type of card to count
	 * @return the number of cards of a certain type in the shelf
	 * @author Lorenzo
	 */
	public int countCard(Card.Type card) {
		int count = 0;
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				if (slots[y][x].isPresent() && slots[y][x].get().getType() == card) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Getter for the `slots` field (they represent the whole shelf information)
	 * @return the `slots` field
	 */
	public Optional<Card>[][] getShelf() {
		return slots;
	}

	/**
	 * Return a cockade for the first player to finish the shelf if the shelf is full
	 * The controller must not award this cockade to multiple players.
	 * @return a cockade for the first player to finish the shelf if the shelf is full, empty otherwise
	 */
	public Optional<Cockade> getFinishCockade() {
		if (isFull()) {
			return Optional.of(new Cockade("First to finish", 1));
		} else {
			return Optional.empty();
		}
	}

}
