package view.gui;

import network.ClientStatus;
import network.NetworkManagerInterface;

public class GameViewController {

    public static ClientStatus state;
    public static NetworkManagerInterface networkManager;
    static boolean gameStarted;




    public void inizialize(java.net.URL location, java.util.ResourceBundle resources) {
        state = ClientStatus.InGame;
        networkManager = LoginController.networkManager;
        gameStarted = true;
        handleEvents();
}


    private void handleEvents() {
    }
