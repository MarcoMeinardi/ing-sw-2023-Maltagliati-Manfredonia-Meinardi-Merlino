package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

import org.junit.Test;

public class GameTest {

	@Test
	public void testConstructor() {
		Game game = new Game(new ArrayList<String>(Arrays.asList("p1", "p2")));
		assertNotNull(game.getTabletop());
		assertEquals(game.getPlayers().size(), 2);
		assertEquals(game.getCommonObjectives().size(), 2);
	}

	@Test
	public void testIterator() throws InvalidMoveException {
		Game game = new Game(new ArrayList<String>(Arrays.asList("p1", "p2")));
		Iterator<Player> it = game.iterator();
		for (int i = 0; i < 10; i++) {
			assertTrue(it.hasNext());
			Player p = it.next();
			assertEquals(p.getName(), game.getPlayers().get(i % 2).getName());
		}

		for (int y = 0; y < Shelf.ROWS; y++) {
			for (int x = 0; x < Shelf.COLUMNS; x++) {
				game.getPlayers().get(0).getShelf().insert(x, new ArrayList<Card>(Arrays.asList(Card.Pianta)));
			}
		}

		assertFalse(it.hasNext());
	}
}
