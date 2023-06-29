package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class to represent a game.
 * It contains the tabletop, the players, the common objectives and the turn of the player.
 * Call the constructor with an `ArrayList` containing the names of the players,
 * or a save state (`SaveState`).
 */
public class Game implements Iterable<Player> {

    private final TableTop tabletop;
    private final ArrayList<Player> players;
    private final ArrayList<CommonObjective> commonObjectives;
    private PlayerIterator playerIterator;
    private Optional<Integer> iteratorIndex = Optional.empty();

    /**
     * Constructor for the `Game` class
     * @param playerNames the list of players' names from which to construct the game.
     */
    public Game(ArrayList<String> playerNames) {
        this.tabletop = new TableTop(playerNames.size());
        PersonalObjective[] personalObjective = PersonalObjective.generatePersonalObjectives(playerNames.size());
        this.players = new ArrayList<>();
        for (int i = 0; i < playerNames.size(); i++) {
            this.players.add(new Player(playerNames.get(i), personalObjective[i]));
        }
        Collections.shuffle(this.players);
        this.commonObjectives = CommonObjective.generateCommonObjectives(players.size());
    }

    /**
     * Constructor for the `Game` class
     * @param saveState a `SaveState` object representing the saved state of the game.
     */
    public Game(SaveState saveState) {
        this.tabletop = new TableTop(saveState.tabletop(), saveState.players().size());
        this.players = saveState.players().stream().map(Player::new).collect(Collectors.toCollection(ArrayList::new));
        this.commonObjectives = saveState.commonObjectives().stream().map(o -> new CommonObjective(o, saveState.players().size())).collect(Collectors.toCollection(ArrayList::new));
        this.iteratorIndex = Optional.of(saveState.playerIteratorIndex());
    }

    /**
     * Return the iterator that indicates whose turn it is
     * `iteratorIndex` should be empty if we are starting a new game,
     * and contain the saved value in the case of a loaded game.
     * @return the player iterator
     */
    @Override
    public Iterator<Player> iterator() {
        if (iteratorIndex.isEmpty()) {
            playerIterator = new PlayerIterator(this);
        } else {
            playerIterator = new PlayerIterator(this, iteratorIndex.get());
        }
        return playerIterator;
    }

    /**
     * Method that returns the current tabletop object.
     * @author Lorenzo, Ludovico, Marco, Riccardo
     *
     * @return The current TableTop object
     */
    public TableTop getTabletop() {
        return tabletop;
    }

    /**
     * Method that returns the list of players in the game.
     * @author Lorenzo, Ludovico, Marco, Riccardo
     *
     * @return An ArrayList containing all Player objects in the game
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Method that returns the array of common objectives in the game.
     * @author Lorenzo, Ludovico, Marco, Riccardo
     *
     * @return An array of all CommonObjective objects in the game
     */
    public ArrayList<CommonObjective> getCommonObjectives() {
        return commonObjectives;
    }

    /**
     * Returns the final ranks of the players.
     *
     * @return The final ranks of the players
     * @author Ludovico, Marco
     */
    public ArrayList<Player> finalRanks() {
        for (Player player : players) {
            ArrayList<Cockade> helpGroupCockades = player.getShelf().getGroupsCockades();
            for (Cockade cockade : helpGroupCockades) {
                player.addCockade(cockade);
            }
        }

        ArrayList<Player> finalResult = new ArrayList<>(players);
        finalResult.sort((player1, player2) -> {
            int diff = player2.getPoints() - player1.getPoints();
            if (diff == 0) {
                return players.indexOf(player2) - players.indexOf(player1);
            } else {
                return diff;
            }
        });
        return finalResult;
    }

    /**
     * Get a serializable object representing the actual game state
     * @return a serializable object representing the actual game state.
     */
    private SaveState getSaveState() {
        SaveTableTop tableTop = this.tabletop.getSaveTableTop();
        ArrayList<SavePlayer> savePlayers = new ArrayList<>();
        for (Player player : this.players) {
            savePlayers.add(player.getSavePlayer());
        }
        ArrayList<SaveCommonObjective> saveCommonObjectives = commonObjectives.stream().map(CommonObjective::getSavable).collect(Collectors.toCollection(ArrayList::new));
        int index = playerIterator == null ? 0 : playerIterator.getIndex();

        return new SaveState(tableTop, savePlayers, saveCommonObjectives, index);
    }

    /**
     * Save the game state to a file
     * @param file the file to save the game state to
     * @throws IOException if an I/O error occurs while writing stream header
     */
    public void saveGame(File file) throws IOException {
        SaveState saveState = getSaveState();
        FileOutputStream outputFile = new FileOutputStream(file);
        ObjectOutputStream outputStream = new ObjectOutputStream(outputFile);
        outputStream.writeObject(saveState);
        outputStream.close();
        outputFile.close();
    }

    /**
     * Load a game state from a file
     * @param file the `File` object containing the path of the save file.
     * @return the loaded game
     * @throws IOException if an I/O error occurs while reading stream header
     * @throws ClassNotFoundException if the class of the serialized loaded object cannot be found
     * (probably due to a different version of the game or a wrong file path)
     */
    public static Game loadGame(File file) throws IOException, ClassNotFoundException {
        FileInputStream inputFile = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputFile);
        SaveState saveState = (SaveState)objectInputStream.readObject();
        objectInputStream.close();
        inputFile.close();

        return new Game(saveState);
    }
}
