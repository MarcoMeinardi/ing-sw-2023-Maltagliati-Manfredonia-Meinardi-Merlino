package controller.game;



import model.Cockade;
import model.Point;
import network.GlobalClientManager;
import model.PersonalObjective;
import model.Player;
import network.NetworkManagerInterface;
import controller.lobby.LobbyController;
import network.ClientManagerInterface;
import network.Server;
import network.parameters.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class GameControllerTest {

    @Test
    public void addPersonalCockadeTest() {
        Player player = new Player("p", PersonalObjective.generateAllPersonalObjectives().get(0));
        Optional<Cockade> helpCockadePersonal = player.getPersonalObjective().isCompleted(player.getShelf());
        assertEquals(helpCockadePersonal.isPresent(), false);
    }

    @Test
    public void addCommonCockadeTest() {
        Player player = new Player("p", PersonalObjective.generateAllPersonalObjectives().get(0));
        Optional<Cockade> helpCockadeCommon = player.getPersonalObjective().isCompleted(player.getShelf());
        assertFalse(helpCockadeCommon.isPresent());
    }

    @Test
    public void FirstToFinishTest() {
        Player player1 = new Player("p1", PersonalObjective.generateAllPersonalObjectives().get(0));
        Player player2 = new Player("p2", PersonalObjective.generateAllPersonalObjectives().get(0));
        Optional<Cockade> firstToFinishCockade = player1.getShelf().getFinishCockade();
        if (firstToFinishCockade.isPresent()) {
            Cockade expectedCockade = firstToFinishCockade.get();
            assertTrue(player1.getCockades().contains(expectedCockade));
            assertTrue(player2.getCockades().contains(expectedCockade));
        } else {
            System.out.println("No first-to-finish cockade found.");
        }
    }

    @Test
    public void gameControllerTest() throws Exception {
        ClientManagerInterface clientManager = GlobalClientManager.getInstance();
        LobbyController lobbyController = LobbyController.getInstance();
        NetworkManagerInterface nm1 = network.rpc.client.NetworkManager.getInstance();
        NetworkManagerInterface nm2 = network.rmi.client.NetworkManager.getInstance();
        nm1.connect(new Server("localhost", 8000));
        nm2.connect(new Server("localhost", 8001));
        assertFalse(nm1.login(new Login("p1")).waitResult().isErr());
        assertFalse(nm2.login(new Login("p2")).waitResult().isErr());
        assertFalse(nm1.lobbyCreate(new LobbyCreateInfo("BigTest")).waitResult().isErr());
        assertFalse(nm2.lobbyList().waitResult().isErr());
        assertTrue(nm2.lobbyJoin("asdasdasd").waitResult().isErr());
        assertFalse(nm2.lobbyJoin("BigTest").waitResult().isErr());
        assertFalse(nm1.lobbyLeave().waitResult().isErr());
        assertFalse(nm1.lobbyJoin("BigTest").waitResult().isErr());

        assertFalse(nm1.chat(new Message("p1", "Foo", null)).waitResult().isErr());
        assertTrue(nm1.chat(new Message("p2", "Foo", null)).waitResult().isErr());
        assertFalse(nm2.chat(new Message("p2", "Foo", "p1")).waitResult().isErr());
        assertFalse(nm2.gameStart().waitResult().isErr());
        assertFalse(nm1.chat(new Message("p1", "Foo", null)).waitResult().isErr());
        assertTrue(nm1.chat(new Message("p2", "Foo", null)).waitResult().isErr());
        assertFalse(nm2.chat(new Message("p2", "Foo", "p1")).waitResult().isErr());

        assertFalse(
            nm1.cardSelect(new CardSelect(0, new ArrayList<>(List.of(new Point(1, 3))))).waitResult().isErr() &&
            nm2.cardSelect(new CardSelect(0, new ArrayList<>(List.of(new Point(1, 3))))).waitResult().isErr()
        );

        assertFalse(nm2.exitGame().waitResult().isErr());
        assertFalse(nm1.lobbyCreate(new LobbyCreateInfo("BigTest")).waitResult().isErr());
        assertFalse(nm2.lobbyJoin("BigTest").waitResult().isErr());
        assertFalse(nm1.gameLoad().waitResult().isErr());
    }
}


