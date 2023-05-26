package view.gui;

import controller.lobby.Lobby;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import model.Cockade;
import model.CommonObjective;
import model.Shelf;
import network.ClientStatus;
import network.NetworkManagerInterface;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CommonObjController implements Initializable {
    private ArrayList<Shelf> shelves;
    public CommonObjController() {
        this.shelves = GameViewController.getShelves();
        this.commonObjectives = GameViewController.getCommonObjectives();
    }

    public static NetworkManagerInterface networkManager;
    public static ClientStatus state;
    public static Lobby lobby;
    private Thread serverThread;
    static String username;
    private ArrayList<String> playersNames;
    private ArrayList<String> commonObjectives;
    @FXML
    private String myCommonObjectives;

    private String me;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        state = ClientStatus.InGame;
        networkManager = LobbyViewController.networkManager;
        lobby = LobbyViewController.lobby;
        username = LobbyViewController.username;
        playersNames = LobbyViewController.gameInfo.players();
        me = username;
        for (int i = 0; i < lobby.getNumberOfPlayers(); i++) {
            commonObjectives.add(this.commonObjectives.get(i));
            if (playersNames.get(i).equals(me)) {
                myCommonObjectives = commonObjectives.get(i);
            }
        }
        loadCommonObjectives();
        serverThread = new Thread(() -> {
            while (state != ClientStatus.Disconnected) {
                synchronized (networkManager) {
                    try {
                        while (!networkManager.hasEvent()) {
                            networkManager.wait();
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        serverThread.start();
    }

    public void loadCommonObjectives() {
        for (int i = 0; i < CommonObjective.N_COMMON_OBJECTIVES; i++) {
            myCommonObjectives = commonObjectives.get(i);
            }
    }
}
