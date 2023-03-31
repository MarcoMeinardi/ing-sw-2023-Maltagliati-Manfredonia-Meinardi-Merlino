package model;

import java.util.*;

public class CardsDeck {
    private Stack<Card> cards;
	private static final int CARDS_PER_TYPE = 22;

    /**
     * Constructor that creates a new deck of cards by initializing a stack of cards
     * with all card values except for empty, and adding 22 instances of each card value
     * to the stack. The stack is then shuffled.
     * @author Lorenzo, Ludovico, Marco, Riccardo
     */
    public CardsDeck() {
        cards = new Stack<Card>();
        for (Card c:Card.values()) {
            if (c == Card.Empty) {
                continue;
            }
            for (int i = 0; i < CARDS_PER_TYPE; i++) {
                cards.push(c);
            }
        }
        Collections.shuffle(cards);
    }

    /**
     * Method that draws a card from the top of the deck. If the deck is empty,
     * returns an empty Optional object; otherwise, returns an Optional object
     * containing the drawn card.
     * @author Lorenzo, Ludovico, Marco, Riccardo
     *
     * @return Optional object containing a drawn card,
     * or empty Optional object if the deck is empty.
     */
    public Optional<Card> draw() {
        Optional<Card> to_draw;
        if (cards.empty()) {
            to_draw = Optional.empty();
        } else {
            to_draw = Optional.of(cards.pop());
        }
        return to_draw;
    }

    /**
     * Method that return the number of cards currently in the deck
     * @author Lorenzo, Ludovico, Marco, Riccardo
     *
     * @return The size of the deck (number of cards)
     */
    public int size() {
        return cards.size();
    }

}
