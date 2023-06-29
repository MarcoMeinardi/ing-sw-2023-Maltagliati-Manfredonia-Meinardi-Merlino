package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class PlayerTest {

	@Test
	public void testConstructor() {
		PersonalObjective obj = PersonalObjective.generateAllPersonalObjectives().get(0);
		Player p = new Player("p", obj);

		assertEquals(p.getName(), "p");
		assertNotNull(p.getShelf());
		assertEquals(p.getCockades().size(), 0);
		assertEquals(p.getPoints(), 0);
		assertEquals(p.getPersonalObjective(), obj);
	}

	@Test
	public void testAddCockade() {
		PersonalObjective obj = PersonalObjective.generateAllPersonalObjectives().get(0);
		Player p = new Player("p", obj);

		Cockade c = new Cockade("test", 10);
		p.addCockade(c);
		assertEquals(p.getCockades().size(), 1);
		assertEquals(p.getCockades().get(0), c);
	}
}
