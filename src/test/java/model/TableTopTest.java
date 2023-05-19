package model;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class TableTopTest {

	@Test
	public void testFillTable() throws InvalidMoveException {
		TableTop table = new TableTop(3);
		for (int y = 0; y < TableTop.SIZE; y++) {
			for (int x = 0; x < TableTop.SIZE; x++) {
				if (TableTop.PLAYER_NUMBER_MASK[y][x] <= 3) {
					assertNotEquals(table.getCard(y, x), Optional.empty());
				}
			}
		}
	}

	@Test
	public void testNeedRefill() throws InvalidMoveException {
		TableTop table = new TableTop(4);
		assertFalse(table.needRefill());
		for(int y = 0; y < 9; y++) {
			for(int x = 0; x < 9; x++) {
				if(
					table.getCard(y, x).isPresent() &&
					(y != 8 || x != 4) &&
					(y != 3 || x != 8) &&
					(y != 4 || x != 0) &&
					(y != 4 || x != 3) &&
					(y != 5 || x != 4) &&
					(y != 5 || x != 3) // Will be removed later this last
				){
					table.pickCard(y, x);
				}
			}
		}
		assertFalse(table.needRefill());
		table.pickCard(5, 3);
		assertTrue(table.needRefill());
	}

	@Test
	public void testIsPickable() throws InvalidMoveException {
		TableTop table = new TableTop(3);
		assertTrue(table.isPickable(0, 3));
		assertFalse(table.isPickable(4, 3));
	}

	@Test
	public void testPickCard() throws InvalidMoveException {
		TableTop table = new TableTop(3);
		assertEquals(table.getCard(0, 3).orElse(null), table.pickCard(0, 3));
		assertEquals(table.getCard(0, 3), Optional.empty());
	}

	@Test
	public void testGetDeck() {
		TableTop table = new TableTop(3);
		assertNotNull(table.getDeck());
		assertEquals(95, table.getDeck().size());
	}

	@Test(expected = InvalidMoveException.class)
	public void testGetCardException() throws InvalidMoveException {
		TableTop table = new TableTop(3);
		table.getCard(TableTop.SIZE, 0);
	}

	@Test(expected = InvalidMoveException.class)
	public void testIsPickableException1() throws InvalidMoveException {
		TableTop table = new TableTop(3);
		table.isPickable(TableTop.SIZE, 0);
	}

	@Test(expected = InvalidMoveException.class)
	public void testIsPickableException2() throws InvalidMoveException {
		TableTop table = new TableTop(3);
		table.isPickable(TableTop.SIZE - 1, TableTop.SIZE - 1);
	}

	@Test(expected = InvalidMoveException.class)
	public void testIsPickableException3() throws InvalidMoveException {
		TableTop table = new TableTop(3);
		table.pickCard(5, 5);
		table.isPickable(5, 5);
	}

	@Test(expected = InvalidMoveException.class)
	public void testPickCardException() throws InvalidMoveException {
		TableTop table = new TableTop(3);
		table.pickCard(5, 5);
		table.pickCard(5, 5);
	}

	@Test
	public void testGetSerializable() throws InvalidMoveException {
		TableTop table = new TableTop(3);
		Card[][] cards = table.getSerializable();
		for (int y = 0; y < TableTop.SIZE; y++) {
			for (int x = 0; x < TableTop.SIZE; x++) {
				if (TableTop.PLAYER_NUMBER_MASK[y][x] <= 3) {
					if (table.getCard(y, x).isPresent()) {
						assertEquals(cards[y][x], table.getCard(y, x).get());
					} else {
						assertNull(cards[y][x]);
					}
				} else {
					assertNull(cards[y][x]);
				}
			}
		}
	}
}
