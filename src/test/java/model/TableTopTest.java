package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Optional;

public class TableTopTest {

	@Test
	public void testFillTable() throws InvalidMoveException {
		TableTop table = new TableTop(3);
		for (int y = 0; y < TableTop.SIZE; y++) {
			for (int x = 0; x < TableTop.SIZE; x++) {
				if (TableTop.player_number_mask[y][x] <= 3) {
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
}
