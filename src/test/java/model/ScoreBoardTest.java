package model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class ScoreBoardTest {

    @Test
    public void testConstructor() {
        Game game = new Game(new ArrayList<String>(Arrays.asList("p1", "p2")));
        game.getPlayers().get(1).addCockade(new Cockade("c1", 10));
        game.getPlayers().get(1).addCockade(new Cockade("c2", 10));
        game.getPlayers().get(0).addCockade(new Cockade("c3", 10));

        ScoreBoard sc = ScoreBoard.create(game).build();

        int cnt = 0;
        ArrayList<Score> target = new ArrayList<>(Arrays.asList(
                new Score(game.getPlayers().get(1).getName(), 20, "Stinky"),
                new Score(game.getPlayers().get(0).getName(), 10, "Poopy")
        ));
        for (Score s : sc) {
            assertEquals(s, target.get(cnt++));
        }
    }
}
