package model;

public class Shelf {
    // Size of the shelf 6(rows)x5(columns)
    private Card[][] slots;

	private static final int COLUMNS = 5;
	private static final int ROWS = 6;

    // Create a new empty shelf
    public Shelf() {
        slots = new Card[ROWS][COLUMNS];
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                slots[y][x] = Card.Empty;
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
		for(int y = 0; y < ROWS;y++){
            if(slots[y][column] == Card.Empty){
                return y;
            }
        }
		return ROWS;
	}

    public Card getCard(int y, int x) throws InvalidMoveException{
        if(y >= ROWS || y < 0 || x < 0 || x >= COLUMNS){
            throw new InvalidMoveException("Card requested is out of bound");
        }
        return slots[y][x];
    }
}
