package network.parameters;

import junit.framework.TestCase;
import model.Card;
import model.Cockade;
import model.TableTop;

import java.util.ArrayList;

public class UpdateTest extends TestCase {

    public void testIdPlayer() {
        Update update = new Update("idPlayer", null, null, null, null, null);
        assertEquals("idPlayer", update.idPlayer());
    }

    public void testTableTop() {
        TableTop tableTop = new TableTop(3);
        Update update = new Update(null, tableTop.getSerializable(), null, null, null, null);
        for (int i = 0; i < tableTop.getSerializable().length; i++) {
            for (int j = 0; j < tableTop.getSerializable()[i].length; j++) {
                assertEquals(tableTop.getSerializable()[i][j], update.tableTop()[i][j]);
            }
        }
    }

    public void testShelf() {
        ArrayList<Card[][]> shelves = new ArrayList<>();
        Card[][] shelf = new Card[4][5];
        shelves.add(shelf);
        Update update = new Update(null, null, shelf, null, null, null);
        assertEquals(shelf, update.shelf());
    }

    public void testNextPlayer() {
        Update update = new Update(null, null, null, "nextPlayer", null, null);
        assertEquals("nextPlayer", update.nextPlayer());
    }

    public void testCompletedObjectives() {
        ArrayList<Cockade> completedObjectives = new ArrayList<>();
        completedObjectives.add(new Cockade("Obiettivo 1", 1));
        Update update = new Update(null, null, null, null, null, null);
        assertEquals(completedObjectives, update.completedObjectives());
    }

    public void testNewCommonObjectivesScores() {
        ArrayList<Integer> newCommonObjectivesScores = new ArrayList<>();
        newCommonObjectivesScores.add(1);
        Update update = new Update(null, null, null, null, null, newCommonObjectivesScores);
        assertEquals(newCommonObjectivesScores, update.newCommonObjectivesScores());
    }
}