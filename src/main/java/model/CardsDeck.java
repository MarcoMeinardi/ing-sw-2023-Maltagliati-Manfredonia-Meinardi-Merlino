package model;

import java.util.*;

public class CardsDeck {
    private Stack<Card> cards;

    // Create a new deck of cards
    public CardsDeck() {
        cards = new Stack<Card>();
        for (Card c:Card.values()) {
            if (c == Card.Empty) {
                continue;
            }
            for (int i = 0; i < 22; i++) {
                cards.push(c);
            }
        }
        Collections.shuffle(cards);
    }

    // Draw a card from the deck
    public Optional<Card> draw() {
        Optional<Card> to_draw;
        if (cards.empty()) {
            to_draw = Optional.empty();
        } else {
            to_draw = Optional.of(cards.pop());
        }
        return to_draw;
    }

    // Return the number of cards left in the deck
    public int size() {
        return cards.size();
    }

}
