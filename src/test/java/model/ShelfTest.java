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
		cards[0][0] = Optional.of(new Card(Card.Type.Plant, 0));
		cards[2][1] = Optional.of(new Card(Card.Type.Cat, 0));
		Shelf shelf = new Shelf(cards);

		assertEquals(shelf.getCard(0, 0).get(), new Card(Card.Type.Plant, 0));
		assertEquals(shelf.getCard(2, 1).get(), new Card(Card.Type.Cat, 0));
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
			shelf.insert(x, new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0))));

			assertEquals(Optional.of(new Card(Card.Type.Plant, 0)), shelf.getCard(0, x));
			assertEquals(Optional.empty(), shelf.getCard(1, x));

			shelf.insert(x, new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Book, 0), new Card(Card.Type.Cat, 0))));
			assertEquals(Optional.of(new Card(Card.Type.Book, 0)), shelf.getCard(1, x));
			assertEquals(Optional.of(new Card(Card.Type.Cat, 0)), shelf.getCard(2, x));
			assertEquals(Optional.empty(), shelf.getCard(3, x));

			shelf.insert(x, new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Game, 0), new Card(Card.Type.Frame, 0), new Card(Card.Type.Trophy, 0))));

			assertEquals(Optional.of(new Card(Card.Type.Game, 0)), shelf.getCard(3, x));
			assertEquals(Optional.of(new Card(Card.Type.Frame, 0)), shelf.getCard(4, x));
			assertEquals(Optional.of(new Card(Card.Type.Trophy, 0)), shelf.getCard(5, x));
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
		shelf.insert(-1, new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0))));
	}

	@Test(expected = InvalidMoveException.class)
	public void testInsertTooLargeColumn() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		shelf.insert(Shelf.COLUMNS, new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0))));
	}

	@Test(expected = InvalidMoveException.class)
	public void testInsertEmptyArray() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		shelf.insert(0, new ArrayList<Card>(Arrays.asList()));
	}

	@Test(expected = InvalidMoveException.class)
	public void testInsertTooBigArray() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		shelf.insert(0, new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Cat, 0), new Card(Card.Type.Trophy, 0), new Card(Card.Type.Game, 0))));
	}

	@Test(expected = InvalidMoveException.class)
	public void testInsertTooManyCards() throws InvalidMoveException {
		Shelf shelf = new Shelf();
		shelf.insert(2, new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Game, 0), new Card(Card.Type.Frame, 0), new Card(Card.Type.Trophy, 0))));
		shelf.insert(2, new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Game, 0), new Card(Card.Type.Frame, 0), new Card(Card.Type.Trophy, 0))));
		shelf.insert(2, new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0))));
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

		shelf.insert(0, new ArrayList<>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
		assertEquals(
			List.of(new Cockade("Area of Plant of size 3", 2)),
			shelf.getGroupsCockades()
		);

		shelf.insert(1, new ArrayList<>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Game, 0), new Card(Card.Type.Game, 0))));
		assertEquals(
			List.of(new Cockade("Area of Plant of size 4", 3)),
			shelf.getGroupsCockades()
		);
		
		shelf.insert(2, new ArrayList<>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Game, 0), new Card(Card.Type.Plant, 0))));
		assertEquals(
			Arrays.asList(
				new Cockade("Area of Plant of size 5", 5),
				new Cockade("Area of Game of size 3", 2)
			),
			shelf.getGroupsCockades()
		);

		shelf.insert(3, new ArrayList<>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Trophy, 0), new Card(Card.Type.Game, 0))));
		assertEquals(
			Arrays.asList(
				new Cockade("Area of Plant of size 6", 8),
				new Cockade("Area of Game of size 3", 2)
			),
			shelf.getGroupsCockades()
		);

		shelf.insert(4, new ArrayList<>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
		assertEquals(
			Arrays.asList(
				new Cockade("Area of Plant of size 9", 8),
				new Cockade("Area of Game of size 3", 2)
			),
			shelf.getGroupsCockades()
		);

		shelf.insert(0, new ArrayList<>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
		shelf.insert(2, new ArrayList<>(Arrays.asList(new Card(Card.Type.Plant, 0))));
		shelf.insert(3, new ArrayList<>(Arrays.asList(new Card(Card.Type.Plant, 0))));
		shelf.insert(4, new ArrayList<>(Arrays.asList(new Card(Card.Type.Plant, 0))));
		assertEquals(
			Arrays.asList(
				new Cockade("Area of Plant of size 15", 8),
				new Cockade("Area of Game of size 3", 2)
			),
			shelf.getGroupsCockades()
		);
	}

	@Test
	public void testIsFull() throws InvalidMoveException {
		Optional<Card>[][] cards = new Optional[Shelf.ROWS][Shelf.COLUMNS];
		for (int y = 0; y < Shelf.ROWS; y++) {
			for (int x = 0; x < Shelf.COLUMNS; x++) {
				cards[y][x] = Optional.of(new Card(Card.Type.values()[(int)Math.floor(Math.random() * Card.Type.values().length)], 0));
			}
		}
		cards[Shelf.ROWS - 1][Shelf.COLUMNS - 1] = Optional.empty();
		Shelf shelf = new Shelf(cards);

		assertFalse(shelf.isFull());
		shelf.insert(Shelf.COLUMNS - 1, new ArrayList<>(Arrays.asList(new Card(Card.Type.Plant, 0))));
		assertTrue(shelf.isFull());
	}

	@Test
	public void testGetSerializable() throws InvalidMoveException {
		Optional<Card>[][] cards = new Optional[Shelf.ROWS][Shelf.COLUMNS];
		for (int y = 0; y < Shelf.ROWS; y++) {
			for (int x = 0; x < Shelf.COLUMNS; x++) {
				if (Math.random() < 0.9) {
					cards[y][x] = Optional.of(new Card(Card.Type.values()[(int)Math.floor(Math.random() * Card.Type.values().length)], 0));
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
