package controller;

import controller.lobby.Lobby;
import controller.lobby.LobbyController;
import org.junit.Test;

import java.util.ArrayList;

public class LobbyControllerTest {
    @Test
    public void testGetIstance(){
        LobbyController lobbyController = LobbyController.getInstance();
        assert(lobbyController != null);
    }

    @Test
    public void testCreateLobby(){
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.createLobby("test", "test");
        ArrayList<Lobby> lobbies = lobbyController.getLobbies();
        assert(lobbies.stream().filter(lobby -> lobby.getName().equals("test")).count() == 1);
    }

    @Test
    public void testFindLobby(){
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.createLobby("test", "test");
        try {
            Lobby lobby = lobbyController.findLobby("test");
            assert(lobby.getName().equals("test"));
        } catch (Exception e) {
            assert(false);
        }
    }

    @Test
    public void testJoinLobby(){
        LobbyController lobbyController = LobbyController.getInstance();
        lobbyController.createLobby("test", "test");
        try{
            lobbyController.joinLobby("test", "test2");
            Lobby lobby = lobbyController.findLobby("test");
            Lobby lobby2 = lobbyController.findLobby("test2");
            assert(lobby.getPlayers().contains("test2"));
        }catch (Exception e){
            assert(false);
        }
    }


}
