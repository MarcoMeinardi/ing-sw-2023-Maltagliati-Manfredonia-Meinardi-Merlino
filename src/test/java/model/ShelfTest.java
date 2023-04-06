package model;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ShelfTest {
    @Test
    public void testConstructor() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		for (int y = 0; y < Shelf.ROWS; y++) {
			for (int x = 0; x < Shelf.COLUMNS; x++) {
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
		for (int x = 0; x < Shelf.COLUMNS; x++) {
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
			if (x < Shelf.COLUMNS - 1) {
				for (int y = 0; y < Shelf.ROWS; y++) {
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
		shelf.insert(Shelf.COLUMNS, new Card[] {Card.Pianta});
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


	@Test
	public void testGetGroupsCockades() throws InvalidMoveException {
		/*
		 * P
		 * P PPP
		 * PGPGP
		 * PGGTP
		 * PPPPP
		 */
		Shelf shelf = new Shelf();
		assertEquals(List.of(), shelf.getGroupsCockades());

		shelf.insert(0, new Card[] {Card.Pianta, Card.Pianta, Card.Pianta});
		assertEquals(
			List.of(new Cockade("Area of Pianta of size 3", 2)),
			shelf.getGroupsCockades()
		);

		shelf.insert(1, new Card[] {Card.Pianta, Card.Gioco, Card.Gioco});
		assertEquals(
			List.of(new Cockade("Area of Pianta of size 4", 3)),
			shelf.getGroupsCockades()
		);
		
		shelf.insert(2, new Card[] {Card.Pianta, Card.Gioco, Card.Pianta});
		assertEquals(
			Arrays.asList(
				new Cockade("Area of Pianta of size 5", 5),
				new Cockade("Area of Gioco of size 3", 2)
			),
			shelf.getGroupsCockades()
		);

		shelf.insert(3, new Card[] {Card.Pianta, Card.Trofeo, Card.Gioco});
		assertEquals(
			Arrays.asList(
				new Cockade("Area of Pianta of size 6", 8),
				new Cockade("Area of Gioco of size 3", 2)
			),
			shelf.getGroupsCockades()
		);

		shelf.insert(4, new Card[] {Card.Pianta, Card.Pianta, Card.Pianta});
		assertEquals(
			Arrays.asList(
				new Cockade("Area of Pianta of size 9", 8),
				new Cockade("Area of Gioco of size 3", 2)
			),
			shelf.getGroupsCockades()
		);

		shelf.insert(0, new Card[] {Card.Pianta, Card.Pianta});
		shelf.insert(2, new Card[] {Card.Pianta});
		shelf.insert(3, new Card[] {Card.Pianta});
		shelf.insert(4, new Card[] {Card.Pianta});
		assertEquals(
			Arrays.asList(
				new Cockade("Area of Pianta of size 15", 8),
				new Cockade("Area of Gioco of size 3", 2)
			),
			shelf.getGroupsCockades()
		);
	}
}
