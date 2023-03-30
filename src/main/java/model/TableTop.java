package model;

public class TableTop {
	private static final int SIZE = 9;

    private CardsDeck deck;
	private Card[][] table;
	int nPlayers;

	private static final int[] dx = {-1, 0, 1, 0};
	private static final int[] dy = {0, -1, 0, 1};

	private static final int[][] requiredPlayers = {
		{11537317, 11537317, 11537317, 3       , 4       , 11537317, 11537317, 11537317, 11537317},
		{11537317, 11537317, 11537317, 2       , 2       , 4       , 11537317, 11537317, 11537317},
		{11537317, 11537317, 3       , 2       , 2       , 2       , 3       , 11537317, 11537317},
		{11537317, 4       , 2       , 2       , 2       , 2       , 2       , 2       , 3       },
		{4       , 2       , 2       , 2       , 2       , 2       , 2       , 2       , 4       },
		{3       , 2       , 2       , 2       , 2       , 2       , 2       , 4       , 11537317},
		{11537317, 11537317, 3       , 2       , 2       , 2       , 3       , 11537317, 11537317},
		{11537317, 11537317, 11537317, 4       , 2       , 2       , 11537317, 11537317, 11537317},
		{11537317, 11537317, 11537317, 11537317, 4       , 3       , 11537317, 11537317, 11537317}
	};

	/**
	 * Creates a new TableTop object with a specified number of players.
	 * The size of the table is set to SIZE x SIZE, and it is filled with cards
	 * according to the rules of the game.
	 * @author Marco
	 *
	 * @param nPlayers The number of players in the game.
	 */
	public TableTop(int nPlayers) {
		table = new Card[SIZE][SIZE];
		this.nPlayers = nPlayers;
		deck = new CardsDeck();
		fillTable();
	}

    public CardsDeck getDeck() {
        return deck;
    }

	/**
	 * Fills any empty spaces on the table with cards drawn from the deck,
	 * as long as the card meets the requirements for the position.
	 * @author Marco, Ludovico
	 */
	private void fillTable() {
		for (int y = 0; y < SIZE; y++){
			for (int x = 0; x < SIZE; x++){
				if (requiredPlayers[y][x] <= nPlayers && table[y][x] == Card.Empty) {
					table[y][x] = deck.draw().orElse(Card.Empty);
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
	private boolean needRefill() {
		for (int y = 0; y < SIZE - 1; y++) {
			for (int x = 0; x < SIZE - 1; x++) {
				if (table[y][x] != Card.Empty) {
					if (table[y + 1][x] != Card.Empty || table[y][x + 1] != Card.Empty) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Determines if a specified position on the table has been used by a player based on the
	 * required number of players for that position. Throws an InvalidMoveException if the specified position is not valid.
	 * @author Lorenzo, Marco, Ludovico, Riccardo
	 *
	 * @param x The horizontal index of the specified position.
	 * @param y The vertical index of the specified position.
	 * @return True if the specified position has been used, false otherwise.
	 * @throws InvalidMoveException If the specified position is not valid.
	 */
	private Boolean isUsed(int x, int y) throws InvalidMoveException {
		return nPlayers >= requiredPlayers[x][y];
	}

	public Card getCard(int x, int y) throws InvalidMoveException {
		if (y < 0 || x < 0 || y >= SIZE || x >= SIZE) {
			throw new InvalidMoveException("Invalid position");
		}
		return table[x][y];
	}

	public void setCard(int x, int y, Card card) throws InvalidMoveException {
		if (y < 0 || x < 0 || y >= SIZE || x >= SIZE) {
			throw new InvalidMoveException("Invalid position");
		}
		if (!isUsed(x, y)) {
			throw new InvalidMoveException("Card place not used");
		}
		table[x][y] = card;
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
		if(y >= SIZE || x >= SIZE || y < 0 || x < 0){
			throw new InvalidMoveException("Invalid position");
		}
		if(nPlayers < requiredPlayers[y][x]){
			throw new InvalidMoveException("Invalid position");
		}
		if(table[y][x] == Card.Empty){
			throw new InvalidMoveException("Empty position");
		}
		for (int i = 0; i < 4; i++) {
			if(y + dy[i] >= SIZE || y + dy[i] < 0 || x + dx[i] >= SIZE || x + dx[i] < 0 || table[y + dy[i]][x + dx[i]] == Card.Empty){
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
	 * @throws InvalidMoveException If the specified position is already empty.
	 */

	public Card pickCard(int y, int x) throws InvalidMoveException {
		Card card = getCard(y, x);
		if (card == Card.Empty) {
			throw new InvalidMoveException("No card in this cell");
		}
		table[y][x] = Card.Empty;
		return card;
	}
}
