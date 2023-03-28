package model;

public class TableTop {
	private static final int SIZE = 9;

    private CardsDeck deck;
	private Card[][] table;
	int nPlayers;

	private static final int groupPoints[] = {2, 3, 5, 8};
	private static final int[] dx = {-1, 0, 1, 0};
	private static final int[] dy = {0, -1, 0, 1};
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
		for (int y = 0; y < SIZE; y++){
			for (int x = 0; x < SIZE; x++){
				if (requiredPlayers[y][x] <= nPlayers && table[y][x] == Card.Empty) {
					table[y][x] = deck.draw().orElse(Card.Empty);
				}
			}
		}
	}

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
}
