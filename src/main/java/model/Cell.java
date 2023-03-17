package model;

public class Cell {
    private final int y, x;
    private final Card card;

	public Cell(int y, int x, Card card) {
		this.y = y;
		this.x = x;
		this.card = card;
	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}

	public Card getCard() {
		return card;
	}
}
