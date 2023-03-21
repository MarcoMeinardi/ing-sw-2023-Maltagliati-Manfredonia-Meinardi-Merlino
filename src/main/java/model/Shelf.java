package model;

public class Shelf {
    // Size of the shelf 6(rows)x5(columns)
    private Card[][] slots;

	private static final int COLUMNS = 5;
	private static final int ROWS = 6;

    // Create a new empty shelf
    public Shelf() {
        slots = new Card[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                slots[i][j] = Card.Empty;
            }
        }
    }

    public void insert(int column, Card[] cards) throws InvalidMoveException {
        if (column < 0 || column > 4) {
            throw new InvalidMoveException("Invalid column");
        }
        if (cards.length == 0 || cards.length > 3) {
            throw new InvalidMoveException("Invalid number of cards");
        }
        int highest = getHighest(column);
        if(cards.length > ROWS - highest){
            throw new InvalidMoveException("Not enough space in column");
        }
        for(Card card:cards){
            slots[highest++][column] = card;
        }
    }

	private int getHighest(int column) {
		for(int i = 0; i < ROWS;i++){
            if(slots[i][column] == Card.Empty){
                return i;
            }
        }
		return ROWS;
	}
}
