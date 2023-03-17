package model;

import java.util.*;

public class CardsDeck{
    private ArrayList<Card> cards;
    private int deck_top;

    //Create a new deck of cards
    public void CardsDeck(){
        ArrayList<Card> cards = new ArrayList<Card>();
        for (Card c:Card.values()) {
            if (c == Card.Empty) {
                continue;
            }
            for (int i = 0; i < 22; i++) {
                cards.add(c);
            }
        }
        Collections.shuffle(cards);
        deck_top = 0;
    }

    //Draw a card from the deck
    public Optional<Card> draw() {
        Optional<Card> to_draw;
        if (deck_top < cards.size()) {
            to_draw = Optional.of(cards.get(deck_top));
            deck_top += 1;
        } else {
            to_draw = Optional.empty();
        }
        return to_draw;
    }

    //Return the number of cards left in the deck
    public int size() {
        return cards.size();
    }

}
