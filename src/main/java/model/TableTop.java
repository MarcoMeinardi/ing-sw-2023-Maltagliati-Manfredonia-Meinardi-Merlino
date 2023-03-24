package model;

public class TableTop {
	private static final int SIZE = 9;

    private CardsDeck deck;
	private Card[][] table;
	int nPlayers;
	static private final int[][] requiredPlayers = {
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

	public TableTop(int nPlayers) {
		table = new Card[SIZE][SIZE];
		this.nPlayers = nPlayers;
		fillTable();
	}

    public CardsDeck getDeck() {
        return deck;
    }

	private void fillTable() {
		for (int i = 0; i < SIZE; i++){
			for (int j = 0; j < SIZE; j++){
				if (requiredPlayers[i][j] <= nPlayers && table[i][j] == Card.Empty) {
					table[i][j] = deck.draw().orElse(Card.Empty);
				}
			}
		}
	}

	private boolean needRefill() {
		for (int i = 0; i < SIZE - 1; i++) {
			for (int j = 0; j < SIZE - 1; j++) {
				if (table[i][j] != Card.Empty) {
					if (table[i + 1][j] != Card.Empty || table[i][j + 1] != Card.Empty) {
						return false;
					}
				}
			}
		}
		return true;
	}

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
}
