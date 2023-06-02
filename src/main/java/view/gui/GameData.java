package view.gui;

import java.util.ArrayList;
import java.util.Optional;

import model.*;
import network.parameters.GameInfo;
import network.parameters.Update;

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

        for (int i = 0; i < update.commonObjectives().size(); i++) {
            for (int j = 0; j < commonObjectives.size(); j++) {
                if (update.commonObjectives().get(i).name().equals(commonObjectives.get(j))) {
                    commonObjectivesPoints.set(j, update.newCommonObjectivesScores().get(i));
                    break;
                }
            }
        }
    }

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
