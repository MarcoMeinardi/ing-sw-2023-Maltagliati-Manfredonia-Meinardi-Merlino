package view.gui;

import controller.lobby.Lobby;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import javafx.stage.Stage;
import model.Player;
import network.ClientStatus;
import network.NetworkManagerInterface;
import network.Result;
import network.ServerEvent;

import java.io.IOException;
import java.util.Optional;


public class LobbyViewController implements Initializable{
    private static final int WIDTH = 1140;
    private static final int HEIGHT = 760;
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
    private Scene scene;
    private Stage stage;


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
                while (state != ClientStatus.Disconnected || gameStarted) {
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

    public void quitLobby(ActionEvent actionEvent) throws Exception {
        Result result = networkManager.lobbyLeave().waitResult();
        if (result.isOk()) {
            LoginController.state = ClientStatus.InLobbySearch;
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
                stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
                scene = new Scene(root, WIDTH, HEIGHT);
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("[ERROR] " + result.getException().orElse("Leave lobby failed"));
        }
    }



    private void handleEvent() {
        Optional<ServerEvent> event = networkManager.getEvent();
        if (event.isEmpty()) {
            return; // No event
        }
        switch (event.get().getType()) {
            case Join -> {
                String joinedPlayer = (String)event.get().getData();
                try {
                    lobby.addPlayer(joinedPlayer);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            updateLobby();
                        }
                    });
                } catch (Exception e) {}  // Cannot happen
                if (!joinedPlayer.equals(username)) {
                    System.out.println(joinedPlayer + " joined the lobby");
                }
            }
            case Leave -> {
                String leftPlayer = (String)event.get().getData();
                try {
                    lobby.removePlayer(leftPlayer);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            updateLobby();
                        }
                    });
                } catch (Exception e) {}  // Cannot happen
                System.out.format("%s left the %s%n", leftPlayer, state == ClientStatus.InLobby ? "lobby" : "game");
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