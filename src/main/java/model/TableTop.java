package model;

public class TableTop {
	private static final int SIZE = 11;

    private CardsDeck deck;
	private Card[][] table;
	private int[][] requiredPlayers;
	int nPlayers;

	public TableTop(int nPlayers) {
		table = new Card[SIZE][SIZE];
		requiredPlayers = new int[SIZE][SIZE];
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
}
