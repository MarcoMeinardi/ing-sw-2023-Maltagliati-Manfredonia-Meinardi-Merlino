package model;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Optional;

public class CommonObjectiveTest {


    @Test
    public void testGenerateCommonObjectives() {

        CommonObjective[] commonObjectives = CommonObjective.generateCommonObjectives(2);
        assertEquals(2, commonObjectives.length);

        commonObjectives = CommonObjective.generateCommonObjectives(3);
        assertEquals(2, commonObjectives.length);

        commonObjectives = CommonObjective.generateCommonObjectives(4);
        assertEquals(2, commonObjectives.length);

    }

    @Test
    public void testIsCompleted() throws InvalidMoveException {
        int nPlayers = 2;
        final int NROWS = 6;
        final int NCOLS = 5;
        Shelf shelf = new Shelf();
        ArrayList<Card> cards = new ArrayList<Card>();
        CommonObjective objective = new CommonObjective("6 groups of 2 cards", nPlayers, CommonObjective::sixGroupsOfTwoCards);

        Optional<Cockade> cockade = objective.isCompleted(shelf);
        assertEquals(false, cockade.isPresent());

        for(int i = 0; i < 2; i++){
            cards.add(Card.Pianta);
        }
        shelf.insert(0, cards);
        cards.clear();

        for(int i = 0; i < 2; i++){
            cards.add(Card.Gatto);
        }
        shelf.insert(1, cards);
        cards.clear();

        for(int i = 0; i < 2; i++){
            cards.add(Card.Gioco);
        }
        shelf.insert(2, cards);
        cards.clear();

        for(int i = 0; i < 2; i++){
            cards.add(Card.Libro);
        }
        shelf.insert(3, cards);
        cards.clear();

        for(int i = 0; i < 2; i++){
            cards.add(Card.Trofeo);
        }
        shelf.insert(4, cards);
        cards.clear();

        for (int i = 0; i < 2; i++) {
            cards.add(Card.Cornice);
        }
        shelf.insert(4, cards);
        cards.clear();

        cockade = objective.isCompleted(shelf);
        assertEquals(true, cockade.isPresent());


    }

}
