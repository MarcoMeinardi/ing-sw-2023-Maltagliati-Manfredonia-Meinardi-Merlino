package controller;

import controller.lobby.Lobby;
import controller.lobby.LobbyAlreadyExistsException;
import controller.lobby.LobbyController;
import controller.lobby.NotEnoughPlayersException;
import org.junit.Test;

import java.util.ArrayList;

public class LobbyControllerTest {
    @Test
    public void testGetIstance(){
        LobbyController lobbyController = LobbyController.getInstance();
        assert(lobbyController != null);
    }

    @Test
    public void testCreateLobby() throws LobbyAlreadyExistsException {
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.createLobby("test", "test");
        ArrayList<Lobby> lobbies = lobbyController.getLobbies();
        assert(lobbies.stream().filter(lobby -> lobby.getName().equals("test")).count() == 1);
    }

    @Test
    public void testFindLobby() throws LobbyAlreadyExistsException {
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.createLobby("test2", "test45");
        try {
            Lobby lobby = lobbyController.findPlayerLobby("test45");
            assert(lobby.getName().equals("test2"));
        } catch (Exception e) {
            assert(false);
        }
    }

    @Test
    public void testJoinLobby() throws Exception {
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.createLobby("test3", "test34");
        lobbyController.joinLobby("test3", "test35");
        Lobby lobby = lobbyController.findPlayerLobby("test34");
        assert(lobby.getPlayers().contains("test35"));
    }


}
