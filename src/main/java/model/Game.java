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

public class Game implements Iterable<Player> {

    private TableTop tabletop;
    private ArrayList<Player> players;
    private ArrayList<CommonObjective> commonObjectives;
    private PlayerIterator playerIterator;
    private Optional<Integer> iteratorIndex = Optional.empty();

    public Game(ArrayList<String> playersNames) {
        this.tabletop = new TableTop(playersNames.size());
        PersonalObjective[] personalObjective = PersonalObjective.generatePersonalObjectives(playersNames.size());
        this.players = new ArrayList<>();
        for (int i = 0; i < playersNames.size(); i++) {
            this.players.add(new Player(playersNames.get(i), personalObjective[i]));
        }
        Collections.shuffle(this.players);
        this.commonObjectives = CommonObjective.generateCommonObjectives(players.size());
    }

    public Game(SaveState saveState) {
        this.tabletop = new TableTop(saveState.tabletop(), saveState.players().size());
        this.players = saveState.players().stream().map(Player::new).collect(Collectors.toCollection(ArrayList::new));
        this.commonObjectives = saveState.commonObjectives().stream().map(o -> new CommonObjective(o, saveState.players().size())).collect(Collectors.toCollection(ArrayList::new));
        this.iteratorIndex = Optional.of(saveState.playerIteratorIndex());
    }

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

    public void saveGame(File file) throws IOException {
        SaveState saveState = getSaveState();
        FileOutputStream outputFile = new FileOutputStream(file);
        ObjectOutputStream outputStream = new ObjectOutputStream(outputFile);
        outputStream.writeObject(saveState);
        outputStream.close();
        outputFile.close();
    }

    public static Game loadGame(File file) throws IOException, ClassNotFoundException {
        FileInputStream inputFile = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputFile);
        SaveState saveState = (SaveState)objectInputStream.readObject();
        objectInputStream.close();
        inputFile.close();

        return new Game(saveState);
    }
}
