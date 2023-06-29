package model;

import java.io.Serializable;
import java.util.*;

/**
 * Class to handle the game's deck of cards.
 * The deck gets filled when the object is constructed,
 * to draw a card, call `draw()`, if the deck is not empty, it
 * will return an optional containing the drawn card.
 */
public class CardsDeck implements Serializable {
    private final Stack<Card> cards;
    public static final int CARDS_PER_TYPE = 22;
    public static final int IMAGES_PER_TYPE = 3;
    public static final int TOTAL_CARDS = CARDS_PER_TYPE * Card.Type.values().length;

    /**
     * Constructor that creates a new deck of cards by initializing a stack of cards
     * with all card values except for empty, and adding 22 instances of each card value
     * to the stack. The stack is then shuffled.
     * @author Lorenzo, Ludovico, Marco, Riccardo
     */
    public CardsDeck() {
        cards = new Stack<>();
        for (Card.Type c : Card.Type.values()) {
            for (int i = 0; i < CARDS_PER_TYPE; i++) {
                cards.push(new Card(c, i % IMAGES_PER_TYPE + 1));
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
