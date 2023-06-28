package controller.game;


import controller.lobby.Lobby;
import controller.lobby.LobbyFullException;
import controller.lobby.PlayerAlreadyInLobbyException;
import model.Cockade;
import model.PersonalObjective;
import model.Player;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals(helpCockadeCommon.isPresent(), false);
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
}


