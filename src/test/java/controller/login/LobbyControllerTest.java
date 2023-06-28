package controller.login;

import controller.lobby.Lobby;
import controller.lobby.LobbyAlreadyExistsException;
import controller.lobby.LobbyController;
import controller.lobby.LobbyFullException;
import controller.lobby.LobbyNotFoundException;
import controller.lobby.PlayerAlreadyInLobbyException;
import controller.lobby.PlayerNotInLobbyException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

public class LobbyControllerTest {
    @Test
    public void testGetInstance () {
        LobbyController lobbyController = LobbyController.getInstance();
        assertNotNull(lobbyController);
    }

    @Test
    public void testCreateLobby() throws LobbyAlreadyExistsException {
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.createLobby("lobby1", "player1");
        ArrayList<Lobby> lobbies = lobbyController.getLobbies();
        assertEquals(lobbies.stream().filter(lobby -> lobby.getName().equals("lobby1")).count(), 1);
    }

    @Test
    public void testFindLobby() throws LobbyAlreadyExistsException, LobbyNotFoundException {
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.createLobby("lobby2", "player2");
        Lobby lobby = lobbyController.findPlayerLobby("player2");
        assertEquals(lobby.getName(), "lobby2");
    }

    @Test
    public void testJoinLobby() throws LobbyAlreadyExistsException, LobbyNotFoundException, PlayerAlreadyInLobbyException, LobbyFullException {
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.createLobby("lobby3", "player3_1");
        lobbyController.joinLobby("lobby3", "player3_2");
        Lobby lobby = lobbyController.findPlayerLobby("player3_1");
        assertTrue(lobby.getPlayers().contains("player3_2"));
    }

    @Test(expected = LobbyNotFoundException.class)
    public void testLeaveLobby() throws LobbyNotFoundException, PlayerAlreadyInLobbyException, LobbyFullException, LobbyAlreadyExistsException, PlayerNotInLobbyException {
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.createLobby("lobby8", "player8_1");
        lobbyController.joinLobby("lobby8", "player8_2");
        assertEquals(lobbyController.findPlayerLobby("player8_2").getName(), "lobby8");
        lobbyController.leaveLobby("player8_2");
        lobbyController.findPlayerLobby("player8_2").getName();
    }

    @Test
    public void testDestroyLobby() throws LobbyAlreadyExistsException, LobbyNotFoundException, PlayerAlreadyInLobbyException, LobbyFullException, PlayerNotInLobbyException {
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.createLobby("lobby9", "player9_1");
        lobbyController.joinLobby("lobby9", "player9_2");
        lobbyController.leaveLobby("player9_1");
        lobbyController.leaveLobby("player9_2");
        assertFalse(lobbyController.getLobbies().stream().map(lobby -> lobby.getName()).anyMatch(name -> name.equals("lobby9")));
    }

    @Test(expected = LobbyAlreadyExistsException.class)
    public void testLobbyAlreadyExists() throws LobbyAlreadyExistsException {
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.createLobby("lobby10", "player10_1");
        lobbyController.createLobby("lobby10", "player10_1");
    }

    @Test(expected = LobbyNotFoundException.class)
    public void testLobbyNotFound1() throws LobbyNotFoundException, LobbyAlreadyExistsException {
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.createLobby("lobby4", "player4");
        lobbyController.findPlayerLobby("player_foo");
    }

    @Test(expected = LobbyNotFoundException.class)
    public void testLobbyNotFound2() throws LobbyNotFoundException, PlayerAlreadyInLobbyException, LobbyFullException {
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.joinLobby("lobby_foo", "player5");
    }

    @Test(expected = PlayerAlreadyInLobbyException.class)
    public void testPlayerAlreadyInLobby() throws LobbyNotFoundException, PlayerAlreadyInLobbyException, LobbyFullException, LobbyAlreadyExistsException {
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.createLobby("lobby6", "player6");
        lobbyController.joinLobby("lobby6", "player6");
    }

    @Test(expected = LobbyFullException.class)
    public void testLobbyFullException() throws LobbyNotFoundException, PlayerAlreadyInLobbyException, LobbyFullException, LobbyAlreadyExistsException {
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.createLobby("lobby7", "player7_1");
        lobbyController.joinLobby("lobby7", "player7_2");
        lobbyController.joinLobby("lobby7", "player7_3");
        lobbyController.joinLobby("lobby7", "player7_4");
        lobbyController.joinLobby("lobby7", "player7_5");
    }
}
