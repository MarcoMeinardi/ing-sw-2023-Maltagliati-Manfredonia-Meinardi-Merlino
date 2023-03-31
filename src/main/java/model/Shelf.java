package model;

import java.util.ArrayList;
import java.util.Optional;

public class Shelf {
    // Size of the shelf 6(rows)x5(columns)
    private Optional<Card>[][] slots;

	private static final int COLUMNS = 5;
	private static final int ROWS = 6;

	private static final int groupPoints[] = {2, 3, 5, 8};
	private static final int[] dx = {-1, 0, 1, 0};
	private static final int[] dy = {0, -1, 0, 1};

    /**
     * Constructor that creates a new shelf with a two-dimensional array of empty cards
     * representing each slot on the shelf.
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
     * Method that inserts a specified array of cards into a specified column on the shelf.
     * If the column is invalid (out of range), throws an InvalidMoveException with an appropriate error message.
     * If the number of cards being inserted is less than 1 or greater than 3, also throws an InvalidMoveException.
     * If there is not enough space in the column to insert all the cards, throws an InvalidMoveException.
     * Otherwise, inserts the cards into the column, starting from the bottom slot up to the first empty slot.
     * @author Ludovico, Marco, Lorenzo
     *
     * @param column The index of the column to insert the cards into
     * @param cards The array of cards to insert
     * @throws InvalidMoveException if the column index is out of range, the number of cards is invalid,
     * or there is not enough space in the column.
     */
    public void insert(int column, Card[] cards) throws InvalidMoveException {
        if (column < 0 || column >= COLUMNS) {
            throw new InvalidMoveException("Invalid column");
        }
        if (cards.length == 0 || cards.length > 3) {
            throw new InvalidMoveException("Invalid number of cards");
        }
        int highest = getHighest(column);
        if(cards.length > ROWS - highest) {
            throw new InvalidMoveException("Not enough space in column");
        }
        for(Card card : cards) {
            slots[highest++][column] = Optional.of(card);
        }
    }

    /**
     * Private helper method that returns the index of the highest empty slot in a specified column on the shelf.
     * @author Ludovico, Marco
     *
     * @param column The index of the column to check
     * @return The index of the highest empty slot in the column
     */
	private int getHighest(int column) {
		for(int y = 0; y < ROWS;y++){
            if(slots[y][column].isEmpty()){
                return y;
            }
        }
		return ROWS;
	}

    /**
     * Method that returns the card in a specified slot on the shelf.
     * If the specified coordinates are out of bounds, throws an InvalidMoveException with an appropriate error message.
     * @author Ludovico
     *
     * @param y The row index of the card to retrieve
     * @param x The column index of the card to retrieve
     * @return The card in the specified slot
     * @throws InvalidMoveException if the specified coordinates are out of bounds
     */
    public Optional<Card> getCard(int y, int x) throws InvalidMoveException {
        if(y >= ROWS || y < 0 || x < 0 || x >= COLUMNS){
            throw new InvalidMoveException("Card requested is out of bound");
        }
        return slots[y][x];
    }

    /**
     * Returns an ArrayList of Cockades representing all groups of adjacent
     * non-empty cards on the board with a size of at least 3.
     * A Cockade is added to the resulting ArrayList for each group, with a name indicating the size and
     * color of the group and the associated point value.
     * @author Marco
     *
     * @return ArrayList of Cockades representing all groups of adjacent non-empty cards on the board
     *         with a size of at least 3.
     */
	public ArrayList<Cockade> getGroupsCockades() {
		ArrayList<Cockade> result = new ArrayList<Cockade>();

		boolean[][] visited = new boolean[ROWS][COLUMNS];
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				if (slots[y][x].isEmpty() || visited[y][x]) continue;

				int groupSize = getGroupSize(y, x, slots[y][x].get(), visited);
				if (groupSize >= 3) {
					String cockadeName = String.format("Area of %s of size %d", slots[y][x].get().toString(), groupSize);
					result.add(new Cockade(cockadeName, groupPoints[Math.max(groupSize - 3, 3)]));
				}
			}
		}

		return result;
	}

    /**
     * Calculates and returns the size of the group of adjacent non-empty cards on the board
     * that includes the card at the specified coordinates and has the same color as the reference card.
     * @author Marco
     *
     * @param y The vertical index of the card to start the search from.
     * @param x The horizontal index of the card to start the search from.
     * @param reference The reference card used to determine the color of the group.
     * @param visited A boolean array representing which cards have already been visited.
     *
     * @return The number of cards in the group starting from the specified coordinates and
     *         having the same color as the reference card.
     */
	private int getGroupSize(int y, int x, Card reference, boolean[][] visited) {
		if (y < 0 || x < 0 || y >= ROWS || x >= COLUMNS || visited[y][x] || slots[y][x].map(value -> value.equals(reference)).orElse(false)) {
			return 0;
		}
		visited[y][x] = true;

		int result = 1;
		for (int i = 0; i < 4; i++) {
			result += getGroupSize(y + dy[i], x + dx[i], reference, visited);
		}
		
		return result;
	}
}
