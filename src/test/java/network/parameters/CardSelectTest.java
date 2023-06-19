package network.parameters;

import junit.framework.TestCase;
import model.Card;
import model.Point;

import java.util.ArrayList;

public class CardSelectTest extends TestCase {

    public void testColumn() {
        CardSelect cardSelect = new CardSelect(1, null);
        assertEquals(1, cardSelect.column());
    }

    public void testSelectedCards() {
        ArrayList<Point> cards = new ArrayList<>();
        cards.add(new Point(1, 1));
        cards.add(new Point(2, 2));
        CardSelect cardSelect = new CardSelect(1, cards);
        assertEquals(cards, cardSelect.selectedCards());
    }
}