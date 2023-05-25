package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

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

	@Test
	public void testSaveGame() throws ClassNotFoundException, IOException {
		File file = new File(new File(System.getProperty("java.io.tmpdir")), "game.ser");
		Game game1 = new Game(new ArrayList<String>(Arrays.asList("p1", "p2")));
		game1.saveGame(file);
		Game game2 = Game.loadGame(file);
		assertEquals(
			game1.getCommonObjectives().stream().map(Objective::getName).collect(Collectors.toCollection(ArrayList::new)),
			game2.getCommonObjectives().stream().map(Objective::getName).collect(Collectors.toCollection(ArrayList::new))
		);
		for (int i = 0; i < game1.getPlayers().size(); i++) {
			assertEquals(game1.getPlayers().get(i).getName(), game2.getPlayers().get(i).getName());
			assertEquals(game1.getPlayers().get(i).getCockades(), game2.getPlayers().get(i).getCockades());
			assertEquals(game1.getPlayers().get(i).getPersonalObjective().getName(), game2.getPlayers().get(i).getPersonalObjective().getName());
		}
	}
}
