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

        shelf = new Shelf();
        objective = new CommonObjective("5 cards in diagonal", nPlayers, CommonObjective::fiveCardsInDiagonal);

        cockade = objective.isCompleted(shelf);
        assertEquals(false, cockade.isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta)));
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Pianta)));
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));

        cockade = objective.isCompleted(shelf);
        assertEquals(true, cockade.isPresent());

        shelf = new Shelf();
        objective = new CommonObjective("all equal corners", nPlayers, CommonObjective::equalCorners);

        cockade = objective.isCompleted(shelf);
        assertEquals(false, cockade.isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));

        cockade = objective.isCompleted(shelf);
        assertEquals(true, cockade.isPresent());

        shelf = new Shelf();
        objective = (new CommonObjective("4 rows of at most 3 different cards", nPlayers, CommonObjective::fourRowsOfAtMostThreeDifferentCards));

        cockade = objective.isCompleted(shelf);
        assertEquals(false, cockade.isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta)));
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta)));
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Pianta)));
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Pianta)));
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Pianta)));

        cockade = objective.isCompleted(shelf);
        assertEquals(true, cockade.isPresent());

        shelf = new Shelf();
        objective = (new CommonObjective("4 groups of 4 cards", nPlayers, CommonObjective::fourGroupsOfFourCards));

        cockade = objective.isCompleted(shelf);
        assertEquals(false, cockade.isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gatto)));
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gatto)));
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Gioco, Card.Gioco)));
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Gioco, Card.Gioco)));
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Libro, Card.Libro)));
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Libro, Card.Libro)));

        cockade = objective.isCompleted(shelf);
        assertEquals(true, cockade.isPresent());

        shelf = new Shelf();
        objective = (new CommonObjective("2 columns of 6 different cards", nPlayers, CommonObjective::twoColumnsOfSixDifferentCards));

        cockade = objective.isCompleted(shelf);
        assertEquals(false, cockade.isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gatto, Card.Gioco)));
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Libro, Card.Trofeo, Card.Cornice)));
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gatto, Card.Gioco)));
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Libro, Card.Trofeo, Card.Cornice)));

        cockade = objective.isCompleted(shelf);
        assertEquals(true, cockade.isPresent());

        shelf = new Shelf();
        objective = (new CommonObjective("2 square-shaped groups", nPlayers, CommonObjective::twoSquareGroups));

        cockade = objective.isCompleted(shelf);
        assertEquals(false, cockade.isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gatto)));
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gatto)));

        cockade = objective.isCompleted(shelf);
        assertEquals(true, cockade.isPresent());

        shelf = new Shelf();
        objective = (new CommonObjective("2 rows with 5 different cards", nPlayers, CommonObjective::twoRowsWithFiveDifferentCards));

        cockade = objective.isCompleted(shelf);
        assertEquals(false, cockade.isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gatto)));
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Gioco, Card.Gioco)));
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Libro, Card.Libro)));
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Trofeo, Card.Trofeo)));

        cockade = objective.isCompleted(shelf);
        assertEquals(true, cockade.isPresent());

        shelf = new Shelf();
        objective = (new CommonObjective("3 columns of at most 3 different cards", nPlayers, CommonObjective::threeColumnsOfAtMostThreeDifferentCards));

        cockade = objective.isCompleted(shelf);
        assertEquals(false, cockade.isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gioco, Card.Gatto)));
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gatto, Card.Gatto)));
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta,Card.Gioco, Card.Gatto)));
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gatto, Card.Gioco)));
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gatto, Card.Gatto)));

        cockade = objective.isCompleted(shelf);
        assertEquals(true, cockade.isPresent());

        shelf = new Shelf();
        objective = (new CommonObjective("X shape group", nPlayers, CommonObjective::equalsX));

        cockade = objective.isCompleted(shelf);
        assertEquals(false, cockade.isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gatto, Card.Pianta)));
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Pianta, Card.Gatto)));
        shelf.insert(2, new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gatto, Card.Pianta)));;

        cockade = objective.isCompleted(shelf);
        assertEquals(true, cockade.isPresent());

        shelf = new Shelf();
        objective = (new CommonObjective("eight equal cards", nPlayers, CommonObjective::eightEquals));

        cockade = objective.isCompleted(shelf);
        assertEquals(false, cockade.isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));

        cockade = objective.isCompleted(shelf);
        assertEquals(true, cockade.isPresent());

        shelf = new Shelf();
        objective = (new CommonObjective("stair-shaped cards", nPlayers, CommonObjective::stairsShape));

        cockade = objective.isCompleted(shelf);
        assertEquals(false, cockade.isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gatto, Card.Gioco)));
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gioco)));
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gioco, Card.Libro)));
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Gioco)));
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Gioco, Card.Libro, Card.Trofeo)));
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Libro, Card.Trofeo)));
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Trofeo)));

        cockade = objective.isCompleted(shelf);
        assertEquals(true, cockade.isPresent());
    }

}
