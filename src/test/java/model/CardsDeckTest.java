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

    @Test
    public void TestCard() {
        Card card1 = new Card(Card.Type.Plant, 0);
        Card card2 = new Card(Card.Type.Plant);
        Card card3 = new Card(Card.Type.Plant, 1);
        assertEquals(card1, card2);
        assertEquals(card2, card3);
        assertEquals(card1, card3);
        assertEquals(card1, card2.getType());
        assertEquals(card1.getImageIndex(), 0);
    }
}
