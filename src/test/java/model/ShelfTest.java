package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

import java.util.ArrayList;
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

	@Test
	public void testCLIConstructor() throws InvalidMoveException {
		Optional<Card>[][] cards = new Optional[Shelf.ROWS][Shelf.COLUMNS];
		cards[0][0] = Optional.of(Card.Pianta);
		cards[2][1] = Optional.of(Card.Gatto);
		Shelf shelf = new Shelf(cards);

		assertEquals(shelf.getCard(0, 0).get(), Card.Pianta);
		assertEquals(shelf.getCard(2, 1).get(), Card.Gatto);
		assertEquals(shelf.getCard(0, 1), null);
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
			shelf.insert(x, new ArrayList<Card>(Arrays.asList(Card.Pianta)));

			assertEquals(Optional.of(Card.Pianta), shelf.getCard(0, x));
			assertEquals(Optional.empty(), shelf.getCard(1, x));

			shelf.insert(x, new ArrayList<Card>(Arrays.asList(Card.Libro, Card.Gatto)));
			assertEquals(Optional.of(Card.Libro), shelf.getCard(1, x));
			assertEquals(Optional.of(Card.Gatto), shelf.getCard(2, x));
			assertEquals(Optional.empty(), shelf.getCard(3, x));

			shelf.insert(x, new ArrayList<Card>(Arrays.asList(Card.Gioco, Card.Cornice, Card.Trofeo)));

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
		shelf.insert(-1, new ArrayList<Card>(Arrays.asList(Card.Pianta)));
	}

	@Test(expected = InvalidMoveException.class)
	public void testInsertTooLargeColumn() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		shelf.insert(Shelf.COLUMNS, new ArrayList<Card>(Arrays.asList(Card.Pianta)));
	}

	@Test(expected = InvalidMoveException.class)
	public void testInsertEmptyArray() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		shelf.insert(0, new ArrayList<Card>(Arrays.asList()));
	}

	@Test(expected = InvalidMoveException.class)
	public void testInsertTooBigArray() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		shelf.insert(0, new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gatto, Card.Trofeo, Card.Gioco)));
	}

	@Test(expected = InvalidMoveException.class)
	public void testInsertTooManyCards() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		shelf.insert(2, new ArrayList<Card>(Arrays.asList(Card.Gioco, Card.Cornice, Card.Trofeo)));
		shelf.insert(2, new ArrayList<Card>(Arrays.asList(Card.Gioco, Card.Cornice, Card.Trofeo)));
		shelf.insert(2, new ArrayList<Card>(Arrays.asList(Card.Pianta)));
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

		shelf.insert(0, new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
		assertEquals(
			List.of(new Cockade("Area of Pianta of size 3", 2)),
			shelf.getGroupsCockades()
		);

		shelf.insert(1, new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gioco, Card.Gioco)));
		assertEquals(
			List.of(new Cockade("Area of Pianta of size 4", 3)),
			shelf.getGroupsCockades()
		);
		
		shelf.insert(2, new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gioco, Card.Pianta)));
		assertEquals(
			Arrays.asList(
				new Cockade("Area of Pianta of size 5", 5),
				new Cockade("Area of Gioco of size 3", 2)
			),
			shelf.getGroupsCockades()
		);

		shelf.insert(3, new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Trofeo, Card.Gioco)));
		assertEquals(
			Arrays.asList(
				new Cockade("Area of Pianta of size 6", 8),
				new Cockade("Area of Gioco of size 3", 2)
			),
			shelf.getGroupsCockades()
		);

		shelf.insert(4, new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
		assertEquals(
			Arrays.asList(
				new Cockade("Area of Pianta of size 9", 8),
				new Cockade("Area of Gioco of size 3", 2)
			),
			shelf.getGroupsCockades()
		);

		shelf.insert(0, new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));
		shelf.insert(2, new ArrayList<Card>(Arrays.asList(Card.Pianta)));
		shelf.insert(3, new ArrayList<Card>(Arrays.asList(Card.Pianta)));
		shelf.insert(4, new ArrayList<Card>(Arrays.asList(Card.Pianta)));
		assertEquals(
			Arrays.asList(
				new Cockade("Area of Pianta of size 15", 8),
				new Cockade("Area of Gioco of size 3", 2)
			),
			shelf.getGroupsCockades()
		);
	}

	@Test
	public void testIsFull() throws InvalidMoveException {
		Optional<Card>[][] cards = new Optional[Shelf.ROWS][Shelf.COLUMNS];
		for (int y = 0; y < Shelf.ROWS; y++) {
			for (int x = 0; x < Shelf.COLUMNS; x++) {
				cards[y][x] = Optional.of(Card.values()[(int)Math.floor(Math.random() * Card.values().length)]);
			}
		}
		cards[Shelf.ROWS - 1][Shelf.COLUMNS - 1] = Optional.empty();
		Shelf shelf = new Shelf(cards);

		assertFalse(shelf.isFull());
		shelf.insert(Shelf.COLUMNS - 1, new ArrayList<Card>(Arrays.asList(Card.Pianta)));
		assertTrue(shelf.isFull());
	}

	@Test
	public void testGetSerializable() throws InvalidMoveException {
		Optional<Card>[][] cards = new Optional[Shelf.ROWS][Shelf.COLUMNS];
		for (int y = 0; y < Shelf.ROWS; y++) {
			for (int x = 0; x < Shelf.COLUMNS; x++) {
				if (Math.random() < 0.9) {
					cards[y][x] = Optional.of(Card.values()[(int)Math.floor(Math.random() * Card.values().length)]);
				} else {
					cards[y][x] = Optional.empty();
				}
			}
		}
		Shelf shelf = new Shelf(cards);
		Card[][] serializableCards = shelf.getSerializable();

		for (int y = 0; y < Shelf.ROWS; y++) {
			for (int x = 0; x < Shelf.COLUMNS; x++) {
				if (cards[y][x].isPresent()) {
					assertEquals(serializableCards[y][x], cards[y][x].get());
				} else {
					assertEquals(serializableCards[y][x], null);
				}
			}
		}
	}
}
