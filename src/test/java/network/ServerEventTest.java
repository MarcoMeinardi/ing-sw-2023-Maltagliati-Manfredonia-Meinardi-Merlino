package network;

import junit.framework.TestCase;
import model.Game;
import model.Player;
import model.ScoreBoard;
import network.parameters.GameInfo;
import network.parameters.Message;
import network.parameters.Update;

import java.security.spec.ECField;
import java.util.ArrayList;

public class ServerEventTest extends TestCase {

    public void testGetType() {
        ServerEvent event = new ServerEvent(ServerEvent.Type.Join, "test");
        assertEquals(event.getType(), ServerEvent.Type.Join);
    }

    public void testGetData() {
        ServerEvent event = new ServerEvent(ServerEvent.Type.Join, "test");
        assertEquals(event.getData(), "test");
    }

    public void testJoin() {
        ServerEvent event = ServerEvent.Join("test");
        assertEquals(event.getType(), ServerEvent.Type.Join);
        assertEquals(event.getData(), "test");
    }

    public void testLeave() {
        ServerEvent event = ServerEvent.Leave("test");
        assertEquals(event.getType(), ServerEvent.Type.Leave);
        assertEquals(event.getData(), "test");
    }

    public void testPause() {
        ServerEvent event = ServerEvent.Pause("test");
        assertEquals(event.getType(), ServerEvent.Type.Pause);
        assertEquals(event.getData(), "test");
    }

    public void testResume() {
        ServerEvent event = ServerEvent.Resume("test");
        assertEquals(event.getType(), ServerEvent.Type.Resume);
        assertEquals(event.getData(), "test");
    }

    public void testStart() {
        ServerEvent event = ServerEvent.Start();
        assertEquals(event.getType(), ServerEvent.Type.Start);
        assertEquals(event.getData(), null);
    }

    public void testTestStart() {
        GameInfo gameInfo = new GameInfo(null, null, null, null, null, null, null, null);
        ServerEvent event = ServerEvent.Start(gameInfo);
        assertEquals(event.getType(), ServerEvent.Type.Start);
        assertEquals(event.getData(), gameInfo);
    }

    public void testError() {
        Exception e = new Exception("test");
        ServerEvent event = ServerEvent.Error(e);
        assertEquals(event.getType(), ServerEvent.Type.Error);
        assertEquals(event.getData(), e);
    }

    public void testUpdate() {
        Update update = new Update(null, null, null, null, null, null);
        ServerEvent event = ServerEvent.Update(update);
        assertEquals(event.getType(), ServerEvent.Type.Update);
        assertEquals(event.getData(), update);
    }

    public void testNewMessage() {
        Message message = new Message("test", "test");
        ServerEvent event = ServerEvent.NewMessage(message);
        assertEquals(event.getType(), ServerEvent.Type.NewMessage);
        assertEquals(event.getData(), message);
    }

    public void testLobbyUpdate() {
        ArrayList<String> players = new ArrayList<>();
        players.add("test");
        ServerEvent event = ServerEvent.LobbyUpdate(players);
        assertEquals(event.getType(), ServerEvent.Type.LobbyUpdate);
        assertEquals(event.getData(), players);
    }

    public void testExitGame() {
        ServerEvent event = ServerEvent.ExitGame();
        assertEquals(event.getType(), ServerEvent.Type.ExitGame);
        assertEquals(event.getData(), null);
    }

    public void testServerDisconnect() {
        ServerEvent event = ServerEvent.ServerDisconnect();
        assertEquals(event.getType(), ServerEvent.Type.ServerDisconnect);
        assertEquals(event.getData(), null);
    }
}