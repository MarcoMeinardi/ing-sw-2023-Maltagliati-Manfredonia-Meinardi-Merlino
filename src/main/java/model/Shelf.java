package model;

import java.util.ArrayList;

public class Shelf {
    // Size of the shelf 6(rows)x5(columns)
    private Card[][] slots;

	private static final int COLUMNS = 5;
	private static final int ROWS = 6;

	private static final int groupPoints[] = {2, 3, 5, 8};
	private static final int[] dx = {-1, 0, 1, 0};
	private static final int[] dy = {0, -1, 0, 1};

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

    public Card getCard(int y, int x) throws InvalidMoveException {
        if(y >= ROWS || y < 0 || x < 0 || x >= COLUMNS){
            throw new InvalidMoveException("Card requested is out of bound");
        }
        return slots[y][x];
    }

	public ArrayList<Cockade> getGroupsCockades() {
		ArrayList<Cockade> result = new ArrayList<Cockade>();

		boolean[][] visited = new boolean[ROWS][COLUMNS];
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				if (!visited[y][x]) {
					int groupSize = getGroupSize(y, x, slots[y][x], visited);
					if (groupSize >= 3) {
						String cockadeName = String.format("Area of %s of size %d", slots[y][x].toString(), groupSize);
						result.add(new Cockade(cockadeName, groupPoints[Math.max(groupSize - 3, 3)]));
					}
				}
			}
		}

		return result;
	}

	private int getGroupSize(int y, int x, Card reference, boolean[][] visited) {
		if (y < 0 || x < 0 || y >= ROWS || x >= COLUMNS || visited[y][x] || !slots[y][x].equals(reference)) {
			return 0;
		}
		visited[y][x] = true;

		int result = 1;
		for (int i = 0; i < 4; i++) {
			result += getGroupSize(y + dy[i], x + dx[i], reference, visited);
		}
		
		return result;
	}
}
