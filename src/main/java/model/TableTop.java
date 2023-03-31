package model;

import java.util.Optional;

public class TableTop {
	private static final int SIZE = 9;
	private static final int MAX_PLAYERS = 6;

    private CardsDeck deck;
	private Optional<Card>[][] table;

	private final int player_count;

	private static final int[] dx = {-1, 0, 1, 0};
	private static final int[] dy = {0, -1, 0, 1};

	private static final int[][] player_number_mask = {
		{MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS, 3       , 4       , MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS},
		{MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS, 2       , 2       , 4       , MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS},
		{MAX_PLAYERS, MAX_PLAYERS, 3       , 2       , 2       , 2       , 3       , MAX_PLAYERS, MAX_PLAYERS},
		{MAX_PLAYERS, 4       , 2       , 2       , 2       , 2       , 2       , 2       , 3       },
		{4       , 2       , 2       , 2       , 2       , 2       , 2       , 2       , 4       },
		{3       , 2       , 2       , 2       , 2       , 2       , 2       , 4       , MAX_PLAYERS},
		{MAX_PLAYERS, MAX_PLAYERS, 3       , 2       , 2       , 2       , 3       , MAX_PLAYERS, MAX_PLAYERS},
		{MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS, 4       , 2       , 2       , MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS},
		{MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS, 4       , 3       , MAX_PLAYERS, MAX_PLAYERS, MAX_PLAYERS}
	};

	/**
	 * Returns true if the position is used in this tabletop, false otherwise.
	 * @author Marco
	 *
	 * @param y The y coordinate of the position.
	 * @param x The x coordinate of the position.
	 * @return The card at the specified position.
	 */
	private boolean isUsed(int y, int x) {
		return player_number_mask[y][x] <= player_count;
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
		this.player_count = nPlayers;
		deck = new CardsDeck();
		fillTable();
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
	 * Places the given card at the specified position on the table.
	 * @author Lorenzo
	 *
	 * @param y The vertical index of the specified position.
	 * @param x The horizontal index of the specified position.
	 * @param card The card to be placed at the specified position.
	 * @throws InvalidMoveException If the specified position is not valid.
	 */
	public void setCard(int x, int y, Card card) throws InvalidMoveException {
		if (y < 0 || x < 0 || y >= SIZE || x >= SIZE) {
			throw new InvalidMoveException("Invalid position");
		}
		if (!isUsed(y, x)) {
			throw new InvalidMoveException("Card place not used");
		}
		table[x][y] = Optional.of(card);
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
		if(isUsed(y, x)){
			throw new InvalidMoveException("Card place not used");
		}
		if(table[y][x].isEmpty()){
			throw new InvalidMoveException("Empty position");
		}
		for (int i = 0; i < 4; i++) {
			if(y + dy[i] >= SIZE || y + dy[i] < 0 ||
					x + dx[i] >= SIZE || x + dx[i] < 0 ||
						table[y + dy[i]][x + dx[i]].isEmpty()){
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

	public CardsDeck getDeck() {
		return deck;
	}

	public Optional<Card> getCard(int x, int y) throws InvalidMoveException {
		if (y < 0 || x < 0 || y >= SIZE || x >= SIZE) {
			throw new InvalidMoveException("Invalid position");
		}
		return table[x][y];
	}

}

