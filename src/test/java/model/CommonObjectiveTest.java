package model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;


public class CommonObjectiveTest {

    /**
     * Test the constructor of CommonObjective
     *
     * @author Ludovico
     */
    @Test
    public void testConstructor() {

        CommonObjective objective = new CommonObjective("test", 2, CommonObjective::fiveCardsInDiagonalTest);
        assertEquals("test", objective.getName());
        assertEquals(8, objective.getValue());

    }

    /**
     * Test the method generateCommonObjectives
     *
     * @autor: Ludovico
     */
    @Test
    public void testGenerateCommonObjectives() {

        ArrayList<CommonObjective> commonObjectives = CommonObjective.generateCommonObjectives(2);
        assertEquals(2, commonObjectives.size());

        commonObjectives = CommonObjective.generateCommonObjectives(3);
        assertEquals(2, commonObjectives.size());

        commonObjectives = CommonObjective.generateCommonObjectives(4);
        assertEquals(2, commonObjectives.size());

    }

    /**
     * Test the Objective.equals method
     *
     * @autor: Marco
     */
    @Test
    public void testEquals() {
        ArrayList<CommonObjective> commonObjectives = CommonObjective.generateCommonObjectives(2);

        assertTrue(commonObjectives.get(0).equals(commonObjectives.get(0)));
        assertFalse(commonObjectives.get(0).equals(commonObjectives.get(1)));
        assertFalse(commonObjectives.get(0).equals("Foo"));
    }

    /**
     * Test the objective "FiveCardsInDiagonal" sud-west to nord-east
     *
     * @throws InvalidMoveException
     * @autor: Ludovico, Marco
     */

    @Test
    public void testFiveCardsInDiagonal1() throws InvalidMoveException {
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = new CommonObjective("5 cards in diagonal", nPlayers, CommonObjective::fiveCardsInDiagonalTest);

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));

        assertTrue(objective.isCompleted(shelf).isPresent());
    }

    /**
     * Test the objective "FiveCardsInDiagonal" nor-west to sud-east
     *
     * @throws InvalidMoveException
     * @autor: Marco
     */

    @Test
    public void testFiveCardsInDiagonal2() throws InvalidMoveException {
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = new CommonObjective("5 cards in diagonal", nPlayers, CommonObjective::fiveCardsInDiagonalTest);

        assertFalse(objective.isCompleted(shelf).isPresent());

        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));

        assertTrue(objective.isCompleted(shelf).isPresent());
    }

    /**
     * Test the objective "isEqualsCorners"
     *
     * @throws InvalidMoveException
     * @autor: Ludovico
     */

    @Test
    public void isEqualsCorners() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = new CommonObjective("all equal corners", nPlayers, CommonObjective::equalCornersTest);

        assertFalse(objective.isCompleted(shelf).isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));

        assertTrue(objective.isCompleted(shelf).isPresent());
    }

    /**
     * Test the objective "fourRowsOfAtMostThreeDifferentCards"
     *
     * @throws InvalidMoveException
     * @autor: Ludovico
     */

    @Test
    public void testFourRowsOfAtMostThreeDifferentCards() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = new CommonObjective("4 rows of at most 3 different cards", nPlayers, CommonObjective::fourRowsOfAtMostThreeDifferentCardsTest);

        assertFalse(objective.isCompleted(shelf).isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Pianta)));

        assertTrue(objective.isCompleted(shelf).isPresent());
    }

    /**
     * Test the objective "fourGroupsOfFourCards"
     *
     * @throws InvalidMoveException
     * @autor: Ludovico
     */

    @Test
    public void testFourGroupsOfFourCards() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = new CommonObjective("4 groups of 4 cards", nPlayers, CommonObjective::fourGroupsOfFourCardsTest);

        assertFalse(objective.isCompleted(shelf).isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gatto)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gatto)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Gioco, Card.Gioco)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Gioco, Card.Gioco)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Libro, Card.Libro)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Libro, Card.Libro)));

        assertTrue(objective.isCompleted(shelf).isPresent());
    }

    /**
     * Test the objective "twoColumnsOfSixDifferentCards"
     *
     * @throws InvalidMoveException
     * @autor: Ludovico
     */

    @Test
    public void testTwoColumnsOfSixDifferentCards() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = new CommonObjective("2 columns of 6 different cards", nPlayers, CommonObjective::twoColumnsOfSixDifferentCardsTest);

        assertFalse(objective.isCompleted(shelf).isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gatto, Card.Gioco)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Libro, Card.Trofeo, Card.Cornice)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gatto, Card.Gioco)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Libro, Card.Trofeo, Card.Cornice)));

        assertTrue(objective.isCompleted(shelf).isPresent());
    }

    /**
     * Test the objective "twoSquareGroups"
     *
     * @throws InvalidMoveException
     * @autor: Ludovico
     */

    @Test
    public void testTwoSquareGroups() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = new CommonObjective("2 square-shaped groups", nPlayers, CommonObjective::twoSquareGroupsTest);

        assertFalse(objective.isCompleted(shelf).isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gatto)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gatto)));

        assertTrue(objective.isCompleted(shelf).isPresent());
    }

    /**
     * Test the objective "twoRowsWithFiveDifferentCards
     *
     * @throws InvalidMoveException
     * @autor: Ludovico
     */

    @Test
    public void testTwoRowsWithFIveDifferentCards() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = new CommonObjective("2 rows with 5 different cards", nPlayers, CommonObjective::twoRowsWithFiveDifferentCardsTest);

        assertFalse(objective.isCompleted(shelf).isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gatto)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Gioco, Card.Gioco)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Libro, Card.Libro)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Trofeo, Card.Trofeo)));

        assertTrue(objective.isCompleted(shelf).isPresent());
    }

    /**
     * Test the objective "threeColumnsOfAtMostThreeDifferentCards
     *
     * @throws InvalidMoveException
     * @autor: Ludovico
     */

    @Test
    public void testThreeColumnsOfAtMostThreeDifferentCards() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = new CommonObjective("3 columns of at most 3 different cards", nPlayers, CommonObjective::threeColumnsOfAtMostThreeDifferentCardsTest);

        assertFalse(objective.isCompleted(shelf).isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gioco, Card.Gatto)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gatto, Card.Gatto)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta,Card.Gioco, Card.Gatto)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gatto, Card.Gioco)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gatto, Card.Gatto)));

        assertTrue(objective.isCompleted(shelf).isPresent());
    }

    /**
     * Test the objective "equalsX"
     *
     * @throws InvalidMoveException
     * @autor: Ludovico
     */

    @Test
    public void testEqualsX() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = new CommonObjective("X shape group", nPlayers, CommonObjective::equalsXTest);

        assertFalse(objective.isCompleted(shelf).isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gatto, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Pianta, Card.Gatto)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(2, new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gatto, Card.Pianta)));;

        assertTrue(objective.isCompleted(shelf).isPresent());
    }

    /**
     * Test the objective "eightEquals"
     *
     * @throws InvalidMoveException
     * @autor: Ludovico
     */

    @Test
    public void testEightEquals() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = new CommonObjective("eight equal cards", nPlayers, CommonObjective::eightEqualsTest);

        assertFalse(objective.isCompleted(shelf).isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta, Card.Pianta)));

        assertTrue(objective.isCompleted(shelf).isPresent());
    }

    /**
     * Test the objective "stairsShape"
     *
     * @throws InvalidMoveException
     * @autor: Ludovico
     */

    @Test
    public void testStairsShape() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = new CommonObjective("stair-shaped cards", nPlayers, CommonObjective::stairsShapeTest);

        assertFalse(objective.isCompleted(shelf).isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Gatto, Card.Gioco)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gioco)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gioco, Card.Libro)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(Card.Gioco)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(Card.Gioco, Card.Libro, Card.Trofeo)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(Card.Libro, Card.Trofeo)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(Card.Trofeo)));

        assertTrue(objective.isCompleted(shelf).isPresent());
    }

    /**
     * Test the objective "6 groups of 2 cards"
     *
     * @throws InvalidMoveException
     * @autor: Ludovico
     */

    @Test
    public void testSixGroupOfTwoCards() throws InvalidMoveException {
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = new CommonObjective("6 groups of 2 cards", nPlayers, CommonObjective::sixGroupsOfTwoCardsTest);

        assertFalse(objective.isCompleted(shelf).isPresent());

        shelf.insert(0, new ArrayList<Card>(Arrays.asList(Card.Pianta, Card.Pianta)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(1, new ArrayList<Card>(Arrays.asList(Card.Gatto, Card.Gatto)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(2, new ArrayList<Card>(Arrays.asList(Card.Gioco, Card.Gioco)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(3, new ArrayList<Card>(Arrays.asList(Card.Libro, Card.Libro)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(4, new ArrayList<Card>(Arrays.asList(Card.Trofeo, Card.Trofeo)));
        assertFalse(objective.isCompleted(shelf).isPresent());
        shelf.insert(4, new ArrayList<Card>(Arrays.asList(Card.Cornice, Card.Cornice)));

        assertTrue(objective.isCompleted(shelf).isPresent());
    }
}
