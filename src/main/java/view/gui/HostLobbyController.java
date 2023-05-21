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
import network.Result;
import network.ServerEvent;

import java.util.Optional;


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
        updateLobby();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (HostLobbyController.state != ClientStatus.Disconnected || HostLobbyController.gameStarted) {
                    handleEvent();
                }
            }
        }).start();
    }

    public static void setIsLobbyChanged(boolean value){
        isLobbyChanged.set(value);
    }

    public void updateLobby() {
        int j = 0;
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
            j++;
        }
        for(int i = j; i < 4; i++){
            switch (i){
                case 0:
                    player0.setText("");
                    break;
                case 1:
                    player1.setText("");
                    break;
                case 2:
                    player2.setText("");
                    break;
                case 3:
                    player3.setText("");
                    break;
            }
        }
    }

    public void quitLobby() throws Exception {
        Result result = networkManager.lobbyLeave().waitResult();
        if (result.isOk()) {
            LoginController.state = ClientStatus.InLobbySearch;
        } else {
            System.out.println("[ERROR] " + result.getException().orElse("Leave lobby failed"));
        }
    }



    private void handleEvent() {
        Optional<ServerEvent> event = HostLobbyController.networkManager.getEvent();
        if (event.isEmpty()) {
            return; // No event
        }
        switch (event.get().getType()) {
            case Join -> {
                String joinedPlayer = (String)event.get().getData();
                try {
                    HostLobbyController.lobby.addPlayer(joinedPlayer);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            updateLobby();
                        }
                    });
                } catch (Exception e) {}  // Cannot happen
                if (!joinedPlayer.equals(HostLobbyController.username)) {
                    System.out.println(joinedPlayer + " joined the lobby");
                }
            }
            case Leave -> {
                String leftPlayer = (String)event.get().getData();
                try {
                    HostLobbyController.lobby.removePlayer(leftPlayer);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            updateLobby();
                        }
                    });
                } catch (Exception e) {}  // Cannot happen
                System.out.format("%s left the %s%n", leftPlayer, HostLobbyController.state == ClientStatus.InLobby ? "lobby" : "game");
            }
            case Start -> {
                //TODO Fare il game che inizia
            }
            case Update -> {
                //TODO update
            }
            case End -> {
                //TODO end
            }
            case NewMessage -> {
                //TODO new message
            } case Pause -> {
                //TODO pause
            } case Resume -> {
                //TODO resume
            }
            default -> throw new RuntimeException("Unhandled event");
        }
    }


}