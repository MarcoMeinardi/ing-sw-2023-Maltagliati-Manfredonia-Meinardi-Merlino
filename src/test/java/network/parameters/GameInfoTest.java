package network.parameters;

import controller.lobby.Lobby;
import junit.framework.TestCase;
import model.Card;
import model.TableTop;

import java.util.ArrayList;

public class GameInfoTest extends TestCase {

    public void testLobby() {
        Lobby lobby = new Lobby("Lobby", "Marco");
        GameInfo gameInfo = new GameInfo(lobby, null, null, null, null, null, null, null);
        assertEquals(lobby, gameInfo.lobby());
    }

    public void testTableTop() {
        TableTop tableTop = new TableTop(3);
        GameInfo gameInfo = new GameInfo(null, tableTop.getSerializable(), null, null, null, null, null, null);
        for (int i = 0; i < tableTop.getSerializable().length; i++) {
            for (int j = 0; j < tableTop.getSerializable()[i].length; j++) {
                assertEquals(tableTop.getSerializable()[i][j], gameInfo.tableTop()[i][j]);
            }
        }
    }
    public void testPlayers() {
        ArrayList<String> players = new ArrayList<>();
        players.add("Marco");
        players.add("Lorenzo");
        GameInfo gameInfo = new GameInfo(null, null, players, null, null, null, null, null);
        assertEquals(players, gameInfo.players());
    }

    public void testShelves() {
        ArrayList<Card[][]> shelves = new ArrayList<>();
        Card[][] shelf = new Card[4][5];
        shelves.add(shelf);
        GameInfo gameInfo = new GameInfo(null, null, null, shelves, null, null, null, null);
        assertEquals(shelves, gameInfo.shelves());
    }

    public void testCommonObjectives() {
        ArrayList<String> commonObjectives = new ArrayList<>();
        commonObjectives.add("Obiettivo 1");
        commonObjectives.add("Obiettivo 2");
        GameInfo gameInfo = new GameInfo(null, null, null, null, commonObjectives, null, null, null);
        assertEquals(commonObjectives, gameInfo.commonObjectives());
    }

    public void testCommonObjectivesPoints() {
        ArrayList<Integer> commonObjectivesPoints = new ArrayList<>();
        commonObjectivesPoints.add(1);
        commonObjectivesPoints.add(2);
        GameInfo gameInfo = new GameInfo(null, null, null, null, null, commonObjectivesPoints, null, null);
        assertEquals(commonObjectivesPoints, gameInfo.commonObjectivesPoints());
    }

    public void testPersonalObjective() {
        GameInfo gameInfo = new GameInfo(null, null, null, null, null, null, "Obiettivo personale", null);
        assertEquals("Obiettivo personale", gameInfo.personalObjective());
    }

    public void testCurrentPlayer() {
        GameInfo gameInfo = new GameInfo(null, null, null, null, null, null, null, "Marco");
        assertEquals("Marco", gameInfo.currentPlayer());
    }
}