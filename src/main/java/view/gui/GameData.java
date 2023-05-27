package view.gui;

import java.util.ArrayList;
import java.util.Optional;

import model.Card;
import model.PersonalObjective;
import model.Shelf;
import model.TableTop;
import network.parameters.GameInfo;

public class GameData {
    private static ArrayList<Shelf> shelves;
    private static Shelf myShelf;
    private static ArrayList<String> commonObjectives;
    private static ArrayList<String> playersNames;
    private static PersonalObjective myPersonalObjective;
    private static String me;
    private Optional[][] tableTop;

    public GameData(GameInfo data, String me){
        this.me = me;
        this.playersNames = data.players();
        this.commonObjectives = data.commonObjectives();
        this.myPersonalObjective = new PersonalObjective(data.personalObjective());
        this.shelves = new ArrayList<>();
        updateTableTop(data.tableTop());
        for (int i = 0; i < playersNames.size(); i++) {
            shelves.add(new Shelf(data.shelves().get(i)));
            if (playersNames.get(i).equals(me)) {
                myShelf = shelves.get(i);
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
}
