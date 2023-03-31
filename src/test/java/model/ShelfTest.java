package model;

import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

import java.util.Optional;

public class ShelfTest {
	private static final int COLUMNS = 5;
	private static final int ROWS = 6;

    @Test
    public void testConstructor() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				assertEquals(Optional.empty(), shelf.getCard(y, x));
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

			assertEquals(Optional.of(Card.Pianta), shelf.getCard(0, x));
			assertEquals(Optional.empty(), shelf.getCard(1, x));

			shelf.insert(x, new Card[] {Card.Libro, Card.Gatto});
			assertEquals(Optional.of(Card.Libro), shelf.getCard(1, x));
			assertEquals(Optional.of(Card.Gatto), shelf.getCard(2, x));
			assertEquals(Optional.empty(), shelf.getCard(3, x));

			shelf.insert(x, new Card[] {Card.Gioco, Card.Cornice, Card.Trofeo});

			assertEquals(Optional.of(Card.Gioco), shelf.getCard(3, x));
			assertEquals(Optional.of(Card.Cornice), shelf.getCard(4, x));
			assertEquals(Optional.of(Card.Trofeo), shelf.getCard(5, x));
			if (x < COLUMNS - 1) {
				for (int y = 0; y < ROWS; y++) {
					assertEquals(Optional.empty(), shelf.getCard(y, x + 1));
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
