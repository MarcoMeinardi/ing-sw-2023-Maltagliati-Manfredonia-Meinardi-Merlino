package network;

import junit.framework.TestCase;

public class ClientStatusHandlerTest extends TestCase {

    public void testGetStatus() {
        ClientStatusHandler handler = new ClientStatusHandler();
        assertEquals(ClientStatus.Idle, handler.getStatus());
    }

    public void testSetStatus() {
        ClientStatusHandler handler = new ClientStatusHandler();
        assertEquals(ClientStatus.Idle, handler.getStatus());
        handler.setStatus(ClientStatus.InGame);
        assertEquals(ClientStatus.InGame, handler.getStatus());
    }

    public void testGetLastValidStatus() {
        ClientStatusHandler handler = new ClientStatusHandler();
        assertEquals(ClientStatus.Idle, handler.getStatus());
        handler.setStatus(ClientStatus.InGame);
        assertEquals(ClientStatus.InGame, handler.getStatus());
        handler.setStatus(ClientStatus.Disconnected);
        assertEquals(ClientStatus.Disconnected, handler.getStatus());
        assertEquals(ClientStatus.InGame, handler.getLastValidStatus());
    }

    public void testSetLastValidStatus() {
        ClientStatusHandler handler = new ClientStatusHandler();
        assertEquals(ClientStatus.Idle, handler.getStatus());
        handler.setLastValidStatus(ClientStatus.InLobby);
        assertEquals(ClientStatus.InLobby, handler.getLastValidStatus());
    }
}