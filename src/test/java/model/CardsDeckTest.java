package model;

import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

public class CardsDeckTest {

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