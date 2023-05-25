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
import java.util.GregorianCalendar;
import java.util.Optional;


/**
 * Class that represents the controller of the lobby scene.
 * It contains all the methods needed to handle the events
 * that can happen in the lobby scene.
 */

public class LobbyViewController implements Initializable{
    private static final int WIDTH = 1140;
    private static final int HEIGHT = 760;
    @FXML
    public TextField messageInput;
    @FXML
    public Button startButton;
    @FXML
    public Button sendMessageButton;
    @FXML
    private ListView chat;
    @FXML
    public Button quitLobby;
    @FXML
    public ListView players;
    @FXML
    public Label descriptorLabel;
    public static NetworkManagerInterface networkManager;
    public static ClientStatus state;
    public static Lobby lobby;
    static String username;

    private Scene scene;
    private Stage stage;
    private Thread serverThread;

    /**
     * Method that initializes the lobby scene. It is called when the scene is loaded.
     * It sets the default button to the send message button and the default cancel button
     * to the quit lobby button. It also sets the username, the state, the lobby and the
     * network manager to the ones saved in the login controller. It calls the startLobby()
     * method to initialize the list view showing the names of the players and the showStart()
     * method to show the start button if the player is the owner of the lobby.
     * It also creates a thread that waits for events from the server and calls the handleEvent()
     * method to handle them.
     *
     *
     *
     * @param location
     * @param resources
     */
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        username = LoginController.username;
        state = ClientStatus.InLobby;
        lobby = LoginController.lobby;
        networkManager = LoginController.networkManager;
        sendMessageButton.setDefaultButton(true);
        quitLobby.setCancelButton(true);
        startLobby();
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

    /**
     * method called to add items to the list view showing the names of the players.
     * It is called when the lobby is created and when a player joins the lobby.
     * Calls updateLobby() method to fill the names in the right spots after they
     * are initialized.
     *
     * @autor: Ludovico
     */

    public void startLobby(){
        //initialize the list view with blank spaces
        for (int i = 0; i < 4; i++) {
            players.getItems().add("");
        }
        updateLobby();
    }

    /**
     * method called to update the names on the list view showing the names of the players.
     * It is called when a player joins or leaves the lobby.
     *
     * @autor: Ludovico
     */

    public void updateLobby() {
        //fill the list with blank spaces everytime it is updated
        for(int i = 0; i<4;i++){
            players.getItems().set(i,"");
        }
        //fill the list with the names of the players
        for(int i = 0; i < lobby.getNumberOfPlayers() ;i++){
            players.getItems().set(i, lobby.getPlayers().get(i));
        }
    }

    /**
     * method called to show the start button if the player is the owner of the lobby
     * (aka the first one in the list of players of the lobby).
     * @autor: Ludovico
     */

    public void showStart(){
        if(username.equals(lobby.getPlayers().get(0))) {
            startButton.setVisible(true);
        }
        else {
            startButton.setVisible(false);
        }
    }

    /**
     * Tries to quit the lobby after the button in the scene is clicked.
     * Checks if the server succeeded in removing player from lobby.
     * If all conditions are met the player returns to the main menu and the thread
     * handling events from server is interrupted.
     *
     * @param actionEvent quit lobby button is clicked
     * @throws Exception
     * @autor: Ludovico
     */

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
            descriptorLabel.setText("");
            descriptorLabel.setText("Leave lobby failed");
            System.out.println("[ERROR] " + result.getException().orElse("Leave lobby failed"));
        }
    }

    /**
     * Tries to start the game after the button in the scene is clicked.
     * Checks if there are enough players and if the server is ready
     * to start the game. If all conditions are met the game starts, all the players
     * are brought to the game scene and the thread handling events from server is interrupted.
     *
     * @param actionEvent the start game button is clicked
     * @throws Exception
     * @autor: Riccardo, Ludovico
     */

    public void startGame(ActionEvent actionEvent) throws Exception{
        if(lobby.getPlayers().size() < 2){
            descriptorLabel.setText("");
            descriptorLabel.setText("Not enough players");
            return;
        }
        Result result = networkManager.gameStart().waitResult();
        if (result.isOk()) {
            LoginController.state = ClientStatus.InGame;
            try {
                serverThread.interrupt();
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/Game.fxml"));
                stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
                scene = new Scene(root, WIDTH, HEIGHT);
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            descriptorLabel.setText("");
            descriptorLabel.setText("Start game failed");
            System.out.println("[ERROR] " + result.getException().orElse("Start game failed"));
        }
    }

    /**
     * method called to send a message to the server and add it to the chat.
     * It checks if the message is valid and if it is not it returns after adding the
     * error to the chat, visible only by the sender and not the others in the lobby (lenght check,
     * empty message check, trying to use commands).
     * Scrolls the chat to the bottom after adding the error message.
     * If the message is valid it sends it to the server and adds it to the chat calling the
     * addMessageToChat() method.
     * It is called when the send button is clicked.
     *
     * @param actionEvent the send button is clicked
     * @throws Exception
     * @autor: Ludovico
     */

    public void sendMessage(ActionEvent actionEvent) throws Exception{
        String messageText = messageInput.getText();
        messageInput.clear();

        //check message integrity and return if not valid
        if (messageText.isEmpty()) {
            System.out.println("[ERROR] Empty message");
            chat.getItems().add("[ERROR] Empty message");
            chat.scrollTo(chat.getItems().size()-1);
            return;
        }
        if (messageText.length() > 100) {
            System.out.println("[ERROR] Message too long");
            chat.getItems().add("[ERROR] Message too long");
            chat.scrollTo(chat.getItems().size()-1);
            return;
        }
        if (messageText.startsWith("/") || messageText.startsWith("!") || messageText.startsWith(".") || messageText.startsWith("?")) {
            System.out.println("[ERROR] Commands not supported");
            chat.getItems().add("[ERROR] Commands not supported");
            chat.scrollTo(chat.getItems().size()-1);
            return;
        }

        //try to send it to the server and add it to chat
        try{
            Result result = networkManager.chat(messageText).waitResult();
            if (result.isErr()) {
                System.out.println("[ERROR] " + result.getException().orElse("Cannot send message"));
                chat.getItems().add("[ERROR] We could not send your message, please try again later");
                chat.scrollTo(chat.getItems().size()-1);
                return;
            }
            Message message = new Message(username, messageText);
            addMessageToChat(message);
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

    }

    /**
     * method called to add a message to the chat.
     * It automatically adds to the message the username of the sender
     * and the hour and minute the message was sent.
     * Scrolls the chat to the bottom to show the last message.
     * It is called when the server sends a message to the lobby chat.
     *
     * @param message the message to add to the chat
     * @autor: Ludovico
     */

    public void addMessageToChat(Message message){
        Calendar calendar = GregorianCalendar.getInstance();
        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        chat.getItems().add("[" + hour + ":"+minute+ "] " +message.idPlayer()+ ": " + message.message());
        chat.scrollTo(chat.getItems().size()-1);
    }

    /**
     * method called to handle the events received from the server.
     * It is called every time the server sends an event.
     * The possible cases are:
     *
     * - Join: a player joined the lobby
     * - Leave: a player left the lobby
     * - Start: the game is starting
     * - Chat: a message was sent in the lobby chat
     *
     * In the join and leave cases the lobby is updated and the start button is shown if the player is the first in the lobby.
     * In the start case the game starts and the players are brought to the game scene.
     * In the chat case the message is added to the chat.
     *
     * @autor: Ludovico
     */

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
