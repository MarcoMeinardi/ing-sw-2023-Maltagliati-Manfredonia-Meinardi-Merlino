package model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;


public class CommonObjectiveTest {

    /**
     * Test the method generateCommonObjectives
     *
     * @author Ludovico
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

    @Test
    public void testGetValue() {
        CommonObjective objective = CommonObjective.generateAllCommonObjectives(2).get(0);
        assertEquals(objective.getValue(), 8);
    }

    /**
     * Test the `Objective.equals` method
     *
     * @author Marco
     */
    @Test
    public void testEquals() {
        ArrayList<CommonObjective> commonObjectives = CommonObjective.generateCommonObjectives(2);

        assertEquals(commonObjectives.get(0), commonObjectives.get(0));
        assertNotEquals(commonObjectives.get(0), commonObjectives.get(1));
        assertNotEquals("Foo", commonObjectives.get(0));
    }

    /**
     * Test the objective "FiveCardsInDiagonal" sud-west to north-east
     *
     * @throws InvalidMoveException
     * @author Ludovico, Marco
     */
    @Test
    public void testFiveCardsInDiagonal1() throws InvalidMoveException {
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = CommonObjective.generateAllCommonObjectives(nPlayers).get(10);

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));

        assertTrue(objective.isCompleted(shelf, "foo").isPresent());
    }

    /**
     * Test the objective "FiveCardsInDiagonal" nor-west to sud-east
     *
     * @throws InvalidMoveException
     * @author Marco
     */
    @Test
    public void testFiveCardsInDiagonal2() throws InvalidMoveException {
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = CommonObjective.generateAllCommonObjectives(nPlayers).get(10);

        assertFalse(objective.isCompleted(shelf, "foo").isPresent());

        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));

        assertTrue(objective.isCompleted(shelf, "foo").isPresent());
    }

    /**
     * Test the objective "isEqualsCorners"
     *
     * @throws InvalidMoveException
     * @author Ludovico
     */
    @Test
    public void isEqualCorners() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = CommonObjective.generateAllCommonObjectives(nPlayers).get(7);

        assertFalse(objective.isCompleted(shelf, "foo").isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));

        assertTrue(objective.isCompleted(shelf, "foo").isPresent());
    }

    /**
     * Test the objective "fourRowsOfAtMostThreeDifferentCards"
     *
     * @throws InvalidMoveException
     * @author Ludovico
     */
    @Test
    public void testFourRowsOfAtMostThreeDifferentCards() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = CommonObjective.generateAllCommonObjectives(nPlayers).get(6);

        assertFalse(objective.isCompleted(shelf, "foo").isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0))));

        assertTrue(objective.isCompleted(shelf, "foo").isPresent());
    }

    /**
     * Test the objective "fourGroupsOfFourCards"
     *
     * @throws InvalidMoveException
     * @author Ludovico
     */
    @Test
    public void testFourGroupsOfFourCards() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = CommonObjective.generateAllCommonObjectives(nPlayers).get(2);

        assertFalse(objective.isCompleted(shelf, "foo").isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Cat, 0), new Card(Card.Type.Cat, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Cat, 0), new Card(Card.Type.Cat, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Game, 0), new Card(Card.Type.Game, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Game, 0), new Card(Card.Type.Game, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Book, 0), new Card(Card.Type.Book, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Book, 0), new Card(Card.Type.Book, 0))));

        assertTrue(objective.isCompleted(shelf, "foo").isPresent());
    }

    /**
     * Test the objective "twoColumnsOfSixDifferentCards"
     *
     * @throws InvalidMoveException
     * @author Ludovico
     */
    @Test
    public void testTwoColumnsOfSixDifferentCards() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = CommonObjective.generateAllCommonObjectives(nPlayers).get(1);

        assertFalse(objective.isCompleted(shelf, "foo").isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Cat, 0), new Card(Card.Type.Game, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Book, 0), new Card(Card.Type.Trophy, 0), new Card(Card.Type.Frame, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Cat, 0), new Card(Card.Type.Game, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Book, 0), new Card(Card.Type.Trophy, 0), new Card(Card.Type.Frame, 0))));

        assertTrue(objective.isCompleted(shelf, "foo").isPresent());
    }

    /**
     * Test the objective "twoSquareGroups"
     *
     * @throws InvalidMoveException
     * @author Ludovico
     */
    @Test
    public void testTwoSquareGroups() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = CommonObjective.generateAllCommonObjectives(nPlayers).get(0);

        assertFalse(objective.isCompleted(shelf, "foo").isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Cat, 0), new Card(Card.Type.Cat, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Cat, 0), new Card(Card.Type.Cat, 0))));

        assertTrue(objective.isCompleted(shelf, "foo").isPresent());
    }

    /**
     * Test the objective "twoRowsWithFiveDifferentCards
     *
     * @throws InvalidMoveException
     * @author Ludovico
     */
    @Test
    public void testTwoRowsWithFIveDifferentCards() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = CommonObjective.generateAllCommonObjectives(nPlayers).get(5);

        assertFalse(objective.isCompleted(shelf, "foo").isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Cat, 0), new Card(Card.Type.Cat, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Game, 0), new Card(Card.Type.Game, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Book, 0), new Card(Card.Type.Book, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Trophy, 0), new Card(Card.Type.Trophy, 0))));

        assertTrue(objective.isCompleted(shelf, "foo").isPresent());
    }

    /**
     * Test the objective "threeColumnsOfAtMostThreeDifferentCards
     *
     * @throws InvalidMoveException
     * @author Ludovico
     */
    @Test
    public void testThreeColumnsOfAtMostThreeDifferentCards() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = CommonObjective.generateAllCommonObjectives(nPlayers).get(4);

        assertFalse(objective.isCompleted(shelf, "foo").isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Game, 0), new Card(Card.Type.Cat, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Cat, 0), new Card(Card.Type.Cat, 0), new Card(Card.Type.Cat, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0),new Card(Card.Type.Game, 0), new Card(Card.Type.Cat, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Cat, 0), new Card(Card.Type.Game, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Cat, 0), new Card(Card.Type.Cat, 0), new Card(Card.Type.Cat, 0))));

        assertTrue(objective.isCompleted(shelf, "foo").isPresent());
    }

    /**
     * Test the objective "equalsX"
     *
     * @throws InvalidMoveException
     * @author Ludovico
     */
    @Test
    public void testEqualsX() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = CommonObjective.generateAllCommonObjectives(nPlayers).get(9);

        assertFalse(objective.isCompleted(shelf, "foo").isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Cat, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Cat, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Cat, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(2, new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Cat, 0), new Card(Card.Type.Plant, 0))));;

        assertTrue(objective.isCompleted(shelf, "foo").isPresent());
    }

    /**
     * Test the objective "eightEquals"
     *
     * @throws InvalidMoveException
     * @author Ludovico
     */
    @Test
    public void testEightEquals() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = CommonObjective.generateAllCommonObjectives(nPlayers).get(8);

        assertFalse(objective.isCompleted(shelf, "foo").isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));

        assertTrue(objective.isCompleted(shelf, "foo").isPresent());
    }

    /**
     * Test the objective "stairsShape"
     *
     * @throws InvalidMoveException
     * @author Ludovico
     */
    @Test
    public void testStairsShape() throws InvalidMoveException{
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = CommonObjective.generateAllCommonObjectives(nPlayers).get(11);

        assertFalse(objective.isCompleted(shelf, "foo").isPresent());

        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Cat, 0), new Card(Card.Type.Game, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(0,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Cat, 0), new Card(Card.Type.Game, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Cat, 0), new Card(Card.Type.Game, 0), new Card(Card.Type.Book, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Game, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(2,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Game, 0), new Card(Card.Type.Book, 0), new Card(Card.Type.Trophy, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(3,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Book, 0), new Card(Card.Type.Trophy, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(4,  new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Trophy, 0))));

        assertTrue(objective.isCompleted(shelf, "foo").isPresent());
    }

    /**
     * Test the objective "6 groups of 2 cards"
     *
     * @throws InvalidMoveException
     * @author Ludovico
     */
    @Test
    public void testSixGroupOfTwoCards() throws InvalidMoveException {
        int nPlayers = 2;
        Shelf shelf = new Shelf();
        CommonObjective objective = CommonObjective.generateAllCommonObjectives(nPlayers).get(3);

        assertFalse(objective.isCompleted(shelf, "foo").isPresent());

        shelf.insert(0, new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Plant, 0), new Card(Card.Type.Plant, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(1, new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Cat, 0), new Card(Card.Type.Cat, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(2, new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Game, 0), new Card(Card.Type.Game, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(3, new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Book, 0), new Card(Card.Type.Book, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(4, new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Trophy, 0), new Card(Card.Type.Trophy, 0))));
        assertFalse(objective.isCompleted(shelf, "foo").isPresent());
        shelf.insert(4, new ArrayList<Card>(Arrays.asList(new Card(Card.Type.Frame, 0), new Card(Card.Type.Frame, 0))));

        assertTrue(objective.isCompleted(shelf, "foo").isPresent());
    }
}
