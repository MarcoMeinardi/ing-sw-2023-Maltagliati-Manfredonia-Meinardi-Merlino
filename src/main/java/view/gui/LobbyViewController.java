package view.gui;

import controller.lobby.Lobby;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import network.ClientStatus;
import network.NetworkManagerInterface;
import network.Result;
import network.ServerEvent;
import network.parameters.Message;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
    @FXML
    public Button startButton;
    @FXML
    public Button sendMessageButton;
    @FXML
    private ListView chat;
    @FXML
    public TextField messageInput;
    private Label[] players;

    public static NetworkManagerInterface networkManager;
    public static ClientStatus state;
    public static Lobby lobby;
    static String username;

    private Scene scene;
    private Stage stage;
    private Thread serverThread;


    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        username = LoginController.username;
        state = ClientStatus.InLobby;
        lobby = LoginController.lobby;
        networkManager = LoginController.networkManager;
        players = new Label[]{player0, player1, player2, player3};
        updateLobby();
        showStart();
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
                handleEvent();
            }
        });
        serverThread.start();
    }

    public void updateLobby() {
        for(int i = 0; i<4;i++){
            players[i].setText("");
        }
        for(int i = 0; i < lobby.getNumberOfPlayers();i++){
            players[i].setText(lobby.getPlayers().get(i));
        }
    }

    public void showStart(){
        if(username.equals(lobby.getPlayers().get(0))) {
            startButton.setVisible(true);
        }
        else {
            startButton.setVisible(false);
        }
    }

    public void quitLobby(ActionEvent actionEvent) throws Exception {
        Result result = networkManager.lobbyLeave().waitResult();
        if (result.isOk()) {
            LoginController.state = ClientStatus.InLobbySearch;
            try {
                serverThread.interrupt();
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

    public void startGame(ActionEvent actionEvent) throws Exception{
        //TODO create to start a game
    }

    public void sendMessage(ActionEvent actionEvent) throws Exception{
        String messageText = messageInput.getText();
        messageInput.clear();

        //check message integrity and return if not valid
        if (messageText.isEmpty()) {
            return;
        }
        if (messageText.length() > 100) {
            System.out.println("[ERROR] Message too long");
            return;
        }
        if (messageText.startsWith("/") || messageText.startsWith("!") || messageText.startsWith(".") || messageText.startsWith("?")) {
            System.out.println("[ERROR] Commands not supported");
            return;
        }

        //try to send it to the server and add it to chat
        try{
            Result result = networkManager.chat(messageText).waitResult();
            if (result.isErr()) {
                System.out.println("[ERROR] " + result.getException().orElse("Cannot send message"));
                chat.getItems().add("We could not send your message, please try again later");
                return;
            }
            Message message = new Message(username, messageText);
            addMessageToChat(message);
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

    }

    public void addMessageToChat(Message message){
        if(message.message().contains("Cannot send message")){
            chat.getItems().add("We could not send your message, please try again later");
            return;
        }
        Calendar calendar = GregorianCalendar.getInstance();
        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        chat.getItems().add("[" + hour + ":"+minute+ "] " +message.idPlayer()+ ": " + message.message());
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
                            showStart();
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
                            showStart();
                        }
                    });
                } catch (Exception e) {}  // Cannot happen
                System.out.format("%s left the %s%n", leftPlayer, state == ClientStatus.InLobby ? "lobby" : "game");
            }
            case NewMessage -> {
                Message message = (Message)event.get().getData();
                if (!message.idPlayer().equals(username)) {
                    System.out.format("%s: %s%n", message.idPlayer(), message.message());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            addMessageToChat(message);
                        }
                    });
                }
            }
            default -> throw new RuntimeException("Unhandled event");
        }
    }


}
