package model;

import java.util.Optional;

/**
 * Class that holds information about the tabletop.
 * It contains the grid of cards and the deck of cards.
 * You can construct it from the number of players, for a new game,
 * or from a saved object representing the tabletop.
 */
public class TableTop {
	public static final int SIZE = 9;
	public static final int MAX_PLAYERS = 6;

	private final CardsDeck deck;
	private final Optional<Card>[][] table;

	private final int nPlayers;

	private static final int[] dx = {-1, 0, 1, 0};
	private static final int[] dy = {0, -1, 0, 1};

	public static final int[][] PLAYER_NUMBER_MASK = {
		{MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS, 3          , 4          , MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS},
		{MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS, 2          , 2          , 4          , MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS},
		{MAX_PLAYERS, MAX_PLAYERS, 3          , 2          , 2          , 2          , 3          , MAX_PLAYERS, MAX_PLAYERS},
		{MAX_PLAYERS, 4          , 2          , 2          , 2          , 2          , 2          , 2          , 3          },
		{4          , 2          , 2          , 2          , 2          , 2          , 2          , 2          , 4          },
		{3          , 2          , 2          , 2          , 2          , 2          , 2          , 4          , MAX_PLAYERS},
		{MAX_PLAYERS, MAX_PLAYERS, 3          , 2          , 2          , 2          , 3          , MAX_PLAYERS, MAX_PLAYERS},
		{MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS, 4          , 2          , 2          , MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS},
		{MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS, 4          , 3          , MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS}
	};

	/**
	 * Returns true if the position is used in this tabletop, false otherwise.
	 * @author Marco
	 *
	 * @param y The y coordinate of the position.
	 * @param x The x coordinate of the position.
	 * @return The card at the specified position.
	 */
	public boolean isUsed(int y, int x) {
		return PLAYER_NUMBER_MASK[y][x] <= nPlayers;
	}

	/**
	 * Creates a new TableTop object with a specified number of players.
	 * The size of the table is set to SIZE x SIZE, and it is filled with cards
	 * according to the rules of the game.
	 * @author Marco
	 *
	 * @param nPlayers The number of players in the game.
	 */
	public TableTop(int nPlayers) {
		table = new Optional[SIZE][SIZE];
		for (int y = 0; y < SIZE; y++){
			for (int x = 0; x < SIZE; x++){
				table[y][x] = Optional.empty();
			}
		}
		this.nPlayers = nPlayers;
		deck = new CardsDeck();
		fillTable();
	}

	/**
	 * Creates a new TableTop object from a saved tabletop.
	 * @param tableTop the `SaveTableTop` object to load from.
	 * @param nPlayers the number of players in the game.
	 */
	public TableTop(SaveTableTop tableTop, int nPlayers) {
		table = new Optional[SIZE][SIZE];
		for (int y = 0; y < SIZE; y++){
			for (int x = 0; x < SIZE; x++){
				table[y][x] = Optional.ofNullable(tableTop.grid()[y][x]);
			}
		}
		this.nPlayers = nPlayers;
		deck = tableTop.deck();
	}

	/**
	 * Fills any empty spaces on the table with cards drawn from the deck,
	 * as long as the card meets the requirements for the position.
	 * @author Marco, Ludovico
	 */
	public void fillTable() {
		for (int y = 0; y < SIZE; y++){
			for (int x = 0; x < SIZE; x++){
				if(isUsed(y, x) && table[y][x].isEmpty()){
					table[y][x] = deck.draw();
				}
			}
		}
	}

	/**
	 * Determines if the table needs to be refilled based on whether there are any empty spaces
	 * next to non-empty spaces. Returns true if a refill is needed, false otherwise.
	 * @author Marco, Ludovico
	 *
	 * @return True if the table needs to be refilled, false otherwise.
	 */
	public boolean needRefill() {
		for (int y = 0; y < SIZE - 1; y++) {
			for (int x = 0; x < SIZE - 1; x++) {
				if (isUsed(y, x) && table[y][x].isPresent()) {
					if ((isUsed(y + 1, x) && table[y + 1][x].isPresent()) || (isUsed(y, x + 1) && table[y][x + 1].isPresent())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Determines if a specified position on the table is pickable based on whether it is adjacent to an empty space and has not already been picked.
	 * @author Ludovico, Lorenzo, Marco, Riccardo
	 *
	 * @param y The vertical index of the specified position.
	 * @param x The horizontal index of the specified position.
	 * @return True if the specified position is pickable, false otherwise.
	 * @throws InvalidMoveException If the specified position is not valid or is already empty.
	 */
	public boolean isPickable(int y, int x) throws InvalidMoveException {
		if (y >= SIZE || x >= SIZE || y < 0 || x < 0) {
			throw new InvalidMoveException("Invalid position");
		}
		if (!isUsed(y, x)) {
			throw new InvalidMoveException("Card place not used");
		}
		if (table[y][x].isEmpty()) {
			throw new InvalidMoveException("Empty position");
		}
		for (int i = 0; i < 4; i++) {
			if(
				y + dy[i] >= SIZE || y + dy[i] < 0 ||
				x + dx[i] >= SIZE || x + dx[i] < 0 ||
				table[y + dy[i]][x + dx[i]].isEmpty()
			) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Picks a card from the table and returns it.
	 * @author Ludovico, Marco
	 *
	 * @param y The vertical index of the specified position.
	 * @param x The horizontal index of the specified position.
	 * @return The card at the specified position.
	 * @throws InvalidMoveException If the specified position is already empty or not valid.
	 */
	public Card pickCard(int y, int x) throws InvalidMoveException {
		Optional<Card> card = getCard(y, x);
		if (card.isEmpty()) {
			throw new InvalidMoveException("Empty position");
		}
		table[y][x] = Optional.empty();
		return card.get();
	}

	/**
	 * Getter for the `deck` field.
	 * @return the `deck` field.
	 */
	public CardsDeck getDeck() {
		return deck;
	}

	/**
	 * Return the card at the given coordinates.
	 * @param y the y coordinate.
	 * @param x the x coordinate.
	 * @return the card at the given coordinates (might be empty).
	 * @throws InvalidMoveException if the coordinates are outside the tabletop.
	 */
	public Optional<Card> getCard(int y, int x) throws InvalidMoveException {
		if (y < 0 || x < 0 || y >= SIZE || x >= SIZE) {
			throw new InvalidMoveException("Invalid position");
		}
		return table[y][x];
	}

	/**
	 * Returns a serializable copy of the table
	 * Note: this method must only be used to send the table over network,
	 * DON'T use it in any other case and once received, convert it immediately
	 * to it's `Optional` form
	 *
	 * @return a serializable copy of the table
	 * @author Marco
	 */
	public Card[][] getSerializable() {
		Card[][] result = new Card[SIZE][SIZE];

		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				result[y][x] = table[y][x].orElse(null);
			}
		}

		return result;
	}

	/**
	 * Return a serializable copy of the tabletop
	 * @return a serializable copy of the tabletop
	 */
	public SaveTableTop getSaveTableTop() {
		return new SaveTableTop(getSerializable(), deck);
	}
}

