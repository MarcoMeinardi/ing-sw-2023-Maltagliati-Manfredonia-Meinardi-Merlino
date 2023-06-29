package model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ScoreBoardTest {

    @Test
    public void testConstructor() throws InvalidMoveException {
        Game game = new Game(new ArrayList<String>(Arrays.asList("p1", "p2")));
        game.getPlayers().get(1).addCockade(new Cockade("c1", 10));
        game.getPlayers().get(1).addCockade(new Cockade("c2", 10));
        game.getPlayers().get(0).addCockade(new Cockade("c3", 10));
        game.getPlayers().get(1).getShelf().insert(0, new ArrayList<>(Arrays.asList(new Card(Card.Type.Plant), new Card(Card.Type.Plant), new Card(Card.Type.Plant))));

        ScoreBoard sc = new ScoreBoard(game);

        int cnt = 0;
        ArrayList<Score> target = new ArrayList<>(Arrays.asList(
                new Score(game.getPlayers().get(1).getName(), 22, "As shrimple as that"),
                new Score(game.getPlayers().get(0).getName(), 10, "NO TITLE FOR YOU! Come back, one year!")
        ));
        for (Score s : sc) {
            assertEquals(s, target.get(cnt++));
        }
    }
}
