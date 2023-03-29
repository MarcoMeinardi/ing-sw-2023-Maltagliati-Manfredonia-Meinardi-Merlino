package model;

import junit.framework.TestCase;
import org.junit.Test;

public class CardsDeckTest extends TestCase {

    @Test
    public void testDraw() {
        CardsDeck deck = new CardsDeck();
        assertEquals(132, deck.size());
        deck.draw();
        assertEquals(131, deck.size());
    }

    @Test
    public void testSize() {
        CardsDeck deck = new CardsDeck();
        assertEquals(132, deck.size());
    }
}