package model;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
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
        CommonObjective objective = new CommonObjective("6 groups of 2 cards", nPlayers, CommonObjective::sixGroupsOfTwoCards);

        Optional<Cockade> cockade = objective.isCompleted(shelf);
        assertEquals(false, cockade.isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gatto)));
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Gioco, Card.Gioco)));
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Libro, Card.Libro)));
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Trofeo, Card.Trofeo)));
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Cornice, Card.Cornice)));

        cockade = objective.isCompleted(shelf);
        assertEquals(true, cockade.isPresent());



    }

}
