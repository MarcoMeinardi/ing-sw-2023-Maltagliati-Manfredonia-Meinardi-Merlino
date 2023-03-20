package model;

public class TableTop {
	private static final int SIZE = 11;

    private CardsDeck deck;
	private Card[][] table;
	static private final int[][] requiredPlayers = {};//TODO: hardcode read matrix
	int nPlayers;

	public TableTop(int nPlayers) {
		table = new Card[SIZE][SIZE];
		this.nPlayers = nPlayers;
		// TODO: load required player matrix
		fillTable();
	}

    public CardsDeck getDeck() {
        return deck;
    }

	private void fillTable() {
		// TODO
	}

	private boolean needRefill() {
		// TODO
		return false;
	}

	private Boolean isUsed(int x, int y) throws InvalidMoveException{
		return nPlayers >= requiredPlayers[x][y];
	}

	public Card getCard(int x, int y) throws InvalidMoveException {
		if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
			throw new InvalidMoveException("Invalid position");
		}
		return table[x][y];
	}

	public void setCard(int x, int y, Card card) throws InvalidMoveException {
		if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
			throw new InvalidMoveException("Invalid position");
		}
		if (!isUsed(x, y)) {
			throw new InvalidMoveException("Card place not used");
		}
		table[x][y] = card;
	}

}
