package model;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.util.Optional;

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

    @Test
    public void TestFullDraw() {
        CardsDeck deck = new CardsDeck();
        for (int i = 0; i < 132; i++) {
            Optional<Card> card = deck.draw();
            assertFalse(card.isEmpty());
        }
        assertEquals(0, deck.size());
        assertEquals(Optional.empty(), deck.draw());
    }
}
