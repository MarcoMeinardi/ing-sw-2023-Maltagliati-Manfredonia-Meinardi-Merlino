package core;

public class Shelf {
    //size of the shelf 6(rows)x5(columns)
    private Card[][] slots;

    //Create a new empty shelf
    public Shelf() {
        slots = new Card[6][5];
        for(int i=0; i<6; i++) {
            for(int j=0; j<5; j++) {
                slots[i][j] = Card.Empty;
            }
        }
    }

    public void add(int column, Card[] cards) throws InvalidMoveException {
        if(column < 0 || column > 4) {
            throw new InvalidMoveException("Invalid column");
        }
        if(cards.length == 0 || cards.length > 3) {
            throw new InvalidMoveException("Invalid number of cards");
        }
        //TODO: check how many places are left in column and add cards
    }

}
