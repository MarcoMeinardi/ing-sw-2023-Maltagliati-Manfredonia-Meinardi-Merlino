package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.util.Optional;

public class CardsDeckTest {

    @Test
    public void testDraw() {
        CardsDeck deck = new CardsDeck();
        assertEquals(CardsDeck.TOTAL_CARDS, deck.size());
        deck.draw();
        assertEquals(CardsDeck.TOTAL_CARDS - 1, deck.size());
    }

    @Test
    public void TestFullDraw() {
        CardsDeck deck = new CardsDeck();
        for (int i = 0; i < CardsDeck.TOTAL_CARDS; i++) {
            Optional<Card> card = deck.draw();
            assertFalse(card.isEmpty());
        }
        assertEquals(0, deck.size());
        assertEquals(Optional.empty(), deck.draw());
    }
}
