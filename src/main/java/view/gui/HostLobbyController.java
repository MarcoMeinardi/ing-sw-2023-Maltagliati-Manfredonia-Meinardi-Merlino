package view.gui;

import controller.lobby.Lobby;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import model.Player;
import network.ClientStatus;
import network.NetworkManagerInterface;



public class HostLobbyController implements Initializable{
    @FXML
    public Label player0;
    @FXML
    public Label player1;
    @FXML
    public  Label player2;
    @FXML
    public  Label player3;

    public static NetworkManagerInterface networkManager;
    public static ClientStatus state;
    public static Lobby lobby;
    static String username;
    static boolean gameStarted;

    public static BooleanProperty isLobbyChanged = new SimpleBooleanProperty(false);


    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        username = LoginController.username;
        state = ClientStatus.InLobby;
        lobby = LoginController.lobby;
        networkManager = LoginController.networkManager;
        gameStarted = false;
        player0.setText(username);
        isLobbyChanged.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                updateLobby();
            }
        });
        Runnable runnable = new LobbyThread();
        Thread lobbyThread = new Thread(runnable);
        lobbyThread.start();
    }

    public void updateLobby() {
        for(int i = 0; i < lobby.getPlayers().size(); i++){
            switch (i){
                case 0:
                    player0.setText(lobby.getPlayers().get(0));
                    break;
                case 1:
                    player1.setText(lobby.getPlayers().get(1));
                    break;
                case 2:
                    player2.setText(lobby.getPlayers().get(2));
                    break;
                case 3:
                    player3.setText(lobby.getPlayers().get(3));
                    break;
            }
        }
    }

    public static void setIsLobbyChanged(boolean value){
        isLobbyChanged.set(value);
    }


}