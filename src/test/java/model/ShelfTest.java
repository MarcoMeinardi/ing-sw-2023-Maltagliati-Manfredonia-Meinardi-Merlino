package model;

import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

public class ShelfTest {
	private static final int COLUMNS = 5;
	private static final int ROWS = 6;

    @Test
    public void testConstructor() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				assertEquals(Card.Empty, shelf.getCard(y, x));
			}
		}
    }

	@Test(expected = InvalidMoveException.class)
	public void testGetter() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		shelf.getCard(-1, 0);
	}

	@Test
	public void testInsert() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		for (int x = 0; x < COLUMNS; x++) {
			shelf.insert(x, new Card[] {Card.Pianta});

			assertEquals(Card.Pianta, shelf.getCard(0, x));
			assertEquals(Card.Empty, shelf.getCard(1, x));

			shelf.insert(x, new Card[] {Card.Libro, Card.Gatto});
			assertEquals(Card.Libro, shelf.getCard(1, x));
			assertEquals(Card.Gatto, shelf.getCard(2, x));
			assertEquals(Card.Empty, shelf.getCard(3, x));

			shelf.insert(x, new Card[] {Card.Gioco, Card.Cornice, Card.Trofeo});

			assertEquals(Card.Gioco, shelf.getCard(3, x));
			assertEquals(Card.Cornice, shelf.getCard(4, x));
			assertEquals(Card.Trofeo, shelf.getCard(5, x));
			if (x < COLUMNS - 1) {
				for (int y = 0; y < ROWS; y++) {
					assertEquals(Card.Empty, shelf.getCard(y, x + 1));
				}
			}
		}
	}

	@Test(expected = InvalidMoveException.class)
	public void testInsertNegativeColumn() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		shelf.insert(-1, new Card[] {Card.Pianta});
	}

	@Test(expected = InvalidMoveException.class)
	public void testInsertTooLargeColumn() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		shelf.insert(COLUMNS, new Card[] {Card.Pianta});
	}

	@Test(expected = InvalidMoveException.class)
	public void testInsertEmptyCard() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		shelf.insert(0, new Card[] {Card.Empty});
	}

	@Test(expected = InvalidMoveException.class)
	public void testInsertEmptyArray() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		shelf.insert(0, new Card[] {});
	}

	@Test(expected = InvalidMoveException.class)
	public void testInsertTooBigArray() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		shelf.insert(0, new Card[] {Card.Pianta, Card.Gatto, Card.Trofeo, Card.Gioco});
	}

	@Test(expected = InvalidMoveException.class)
	public void testInsertTooManyCards() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		shelf.insert(2, new Card[] {Card.Gioco, Card.Cornice, Card.Trofeo});
		shelf.insert(2, new Card[] {Card.Gioco, Card.Cornice, Card.Trofeo});
		shelf.insert(2, new Card[] {Card.Pianta});
	}
}
