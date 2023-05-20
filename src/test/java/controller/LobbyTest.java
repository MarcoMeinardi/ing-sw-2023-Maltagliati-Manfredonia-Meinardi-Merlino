package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import controller.lobby.Lobby;
import controller.lobby.LobbyFullException;
import controller.lobby.PlayerAlreadyInLobbyException;
import controller.lobby.PlayerNotInLobbyException;

// A lot of things are already covered by LobbyControllerTest
public class LobbyTest {

    @Test
    public void testGetNumberOfPlayers() throws PlayerAlreadyInLobbyException, LobbyFullException {
        Lobby lobby = new Lobby("lobby", "player1");
        lobby.addPlayer("player2");
        assertEquals(lobby.getNumberOfPlayers(), 2);
    }

    @Test
    public void testIsHost() throws PlayerAlreadyInLobbyException, LobbyFullException {
        Lobby lobby = new Lobby("lobby", "player1");
        lobby.addPlayer("player2");
        assertTrue(lobby.isHost("player1"));
        assertFalse(lobby.isHost("player2"));
        assertFalse(lobby.isHost("player11"));
    }

    @Test(expected = PlayerNotInLobbyException.class)
    public void testPlayerNotInLobbyException() throws PlayerNotInLobbyException, PlayerAlreadyInLobbyException, LobbyFullException {
        Lobby lobby = new Lobby("lobby", "player1");
        lobby.addPlayer("player2");
        lobby.removePlayer("player2");
        lobby.removePlayer("player2");
    }
}
