package core;

import java.util.ArrayList;
import java.util.Optional;

public class Game {
    private TableTop tabletop;
    private ArrayList<Player> players;
    private CommonObjective[] commonObjectives;
    private Optional<Player> firstToFinish;
}
