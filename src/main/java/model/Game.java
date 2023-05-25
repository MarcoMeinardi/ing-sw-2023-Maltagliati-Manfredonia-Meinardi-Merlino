package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Collectors;

public class Game implements Iterable<Player> {

    private TableTop tabletop;
    private ArrayList<Player> players;
    private ArrayList<CommonObjective> commonObjectives;
    private PlayerIterator playerIterator;

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
        this.playerIterator = saveState.playerIterator();
    }

    @Override
    public Iterator<Player> iterator() {
        if (playerIterator == null) {
            playerIterator = new PlayerIterator(this);
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


    private SaveState getSaveState() {
        SaveTableTop tableTop = this.tabletop.getSaveTableTop();
        ArrayList<SavePlayer> savePlayers = new ArrayList<>();
        for (Player player : this.players) {
            savePlayers.add(player.getSavePlayer());
        }
        ArrayList<String> saveCommonObjectives = commonObjectives.stream().map(CommonObjective::getName).collect(Collectors.toCollection(ArrayList::new));

        return new SaveState(tableTop, savePlayers, saveCommonObjectives, playerIterator);
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
