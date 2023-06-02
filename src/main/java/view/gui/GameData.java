package view.gui;

import java.util.ArrayList;
import java.util.Optional;

import model.*;
import network.parameters.GameInfo;
import network.parameters.Update;

public class GameData {
    private static ArrayList<Shelf> shelves;
    private static Shelf myShelf;
    private static ArrayList<String> commonObjectives;
    private static ArrayList<String> playersNames;
    private static PersonalObjective myPersonalObjective;
    private static String me;
    private static Optional[][] tableTop;
    private int nPlayers;
    private ArrayList<Integer> commonObjectivesPoints;
    private static ScoreBoard scoreBoard;
    private static String currentPlayer;

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



    public static ArrayList<String> getPlayersNames(){
        return playersNames;
    }
    public static ArrayList<String> getCommonObjectives(){
        return commonObjectives;
    }
    public static PersonalObjective getMyPersonalObjective(){
        return myPersonalObjective;
    }
    public static ArrayList<Shelf> getShelves(){
        return shelves;
    }
    public static Shelf getMyShelf(){
        return myShelf;
    }
    public static Optional[][] getTableTop(){
        return tableTop;
    }
    public static ScoreBoard getScoreBoard(){
        return scoreBoard;
    }
    public static String getMe(){
        return me;
    }
    public static  String getCurrentPlayer(){
        return currentPlayer;
    }
    public void setScoreBoard(ScoreBoard scoreBoard){
        this.scoreBoard = scoreBoard;
    }
}
