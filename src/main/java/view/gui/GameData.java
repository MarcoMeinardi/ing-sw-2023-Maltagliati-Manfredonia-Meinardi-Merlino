package view.gui;

import java.util.ArrayList;
import java.util.Optional;

import model.*;
import network.parameters.GameInfo;
import network.parameters.Update;

/**
 * This class contains all the data needed to update the GUI.
 * It is used by the GUI to update the view.
 *
 */

public class GameData {
    private ArrayList<Shelf> shelves;
    private Shelf myShelf;
    private ArrayList<String> commonObjectives;
    private ArrayList<String> playersNames;
    private PersonalObjective myPersonalObjective;
    private String me;
    private Optional[][] tableTop;
    private int nPlayers;
    private ArrayList<Integer> commonObjectivesPoints;
    private ScoreBoard scoreBoard;
    private String currentPlayer;

    /**
     *
     * This constructor is used to create the GameData object when the game starts.
     * @param data contains all the data needed to create the GameData object.
     * @param me is the name of the player using the GUI.
     *
     */

    public GameData(GameInfo data, String me){
        this.me = me;
        this.playersNames = data.players();
        this.nPlayers = this.playersNames.size();
        this.commonObjectives = data.commonObjectives();
        this.myPersonalObjective = new PersonalObjective(data.personalObjective());
        this.shelves = new ArrayList<>();
        this.commonObjectivesPoints = data.commonObjectivesPoints();
        this.currentPlayer = data.currentPlayer();

        updateTableTop(data.tableTop());

        for (int i = 0; i < playersNames.size(); i++) {
            shelves.add(new Shelf(data.shelves().get(i)));
            if (playersNames.get(i).equals(me)) {
                myShelf = shelves.get(i);
            }
        }
    }

    /**
     * This method is used to update the GameData object.
     * It updates the shelves of the players, the tabletop and the common objectives.
     *
     * @param update contains all the data needed to update the GameData object.
     *
     */

    public void update(Update update) {
        updateTableTop(update.tableTop());

        for (int i = 0; i < nPlayers; i++) {
            if (playersNames.get(i).equals(update.idPlayer())) {
                shelves.set(i, new Shelf(update.shelf()));
                if (playersNames.get(i).equals(me)) {
                    myShelf = shelves.get(i);
                }
                break;
            }
        }

        for (int i = 0; i < update.completedObjectives().size(); i++) {
            for (int j = 0; j < commonObjectives.size(); j++) {
                if (update.completedObjectives().get(i).name().equals(commonObjectives.get(j))) {
                    commonObjectivesPoints.set(j, update.newCommonObjectivesScores().get(i));
                    break;
                }
            }
        }
    }

    /**
     * This method is used to update the tableTop.
     *
     * @param tableTop is the new tableTop.
     *
     */

    private void updateTableTop(Card[][] tableTop) {
        this.tableTop = new Optional[TableTop.SIZE][TableTop.SIZE];

        for (int y = 0;  y < TableTop.SIZE; y++) {
            for (int x = 0; x < TableTop.SIZE; x++) {
                this.tableTop[y][x] = tableTop[y][x] != null ?
                        Optional.of(tableTop[y][x]) :
                        Optional.empty();
            }
        }
    }



    public ArrayList<String> getPlayersNames(){
        return playersNames;
    }
    public ArrayList<String> getCommonObjectives(){
        return commonObjectives;
    }
    public PersonalObjective getMyPersonalObjective(){
        return myPersonalObjective;
    }
    public ArrayList<Shelf> getShelves(){
        return shelves;
    }
    public Shelf getMyShelf(){
        return myShelf;
    }
    public Optional[][] getTableTop(){
        return tableTop;
    }
    public ScoreBoard getScoreBoard(){
        return scoreBoard;
    }
    public String getMe(){
        return me;
    }
    public String getCurrentPlayer(){
        return currentPlayer;
    }
    public void setScoreBoard(ScoreBoard scoreBoard){
        this.scoreBoard = scoreBoard;
    }
}
