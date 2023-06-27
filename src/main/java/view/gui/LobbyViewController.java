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

import network.*;
import network.parameters.GameInfo;
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
    private TextField messageInput;
    @FXML
    private Button startButton;
    @FXML
    private Button sendMessageButton;
    @FXML
    private ListView chat;
    @FXML
    private Button quitLobby;
    @FXML
    private ListView players;
    @FXML
    private Label descriptorLabel;
    @FXML
    private Button loadButton;
    public static NetworkManagerInterface networkManager;
    public static ClientStatus state;
    public static Lobby lobby;
    static String username;

    private Scene scene;
    private Stage stage;
    private Thread serverThread;
    public static GameInfo gameInfo;
    private boolean alreadyShowedHostMessage = false;


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
     *
     * @param resources
     *
     * @author Ludovico
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
     * method called to add items to the list view showing
     * the names of the players and add the help message in chat.
     * It is called when the lobby is created and when a player joins the lobby.
     * Calls updateLobby() method to fill the names in the right spots
     *
     * @author Ludovico
     */
    private void startLobby() {
        chat.getItems().add("[Type /help to see the list of commands]");
        updateLobby();
    }

    /**
     * method called to update the names on the list view showing the names of the players.
     * It is called when a player joins or leaves the lobby.
     *
     * @author Ludovico
     */
    private void updateLobby() {
        players.getItems().clear();
        players.getItems().addAll(lobby.getPlayers());
    }

    /**
     * method called to show the start button if the player is the owner of the lobby
     * (aka the first one in the list of players of the lobby).
     *
     * @author Ludovico
     */
    private void showStart(){
        if(lobby.isHost(username)){
            startButton.setVisible(true);
            loadButton.setVisible(true);
            if(!alreadyShowedHostMessage){
                Utils.addMessageToChat(username, new Message(Server.SERVER_NAME, "You are the owner of the lobby, you can start the game when you want!"), chat);
                alreadyShowedHostMessage = true;
            }
        }
        else {
            alreadyShowedHostMessage = false;
            startButton.setVisible(false);
            loadButton.setVisible(false);
        }
    }

    /**
     * Tries to quit the lobby after the button in the scene is clicked.
     * Checks if the server succeeded in removing player from lobby.
     * If all conditions are met the player returns to the main menu and the thread
     * handling events from server is interrupted.
     *
     * @param actionEvent quit lobby button is clicked
     *
     * @throws Exception
     *
     * @author Ludovico
     */

    @FXML
    private void quitLobby(ActionEvent actionEvent) throws Exception {
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
                Utils.changeLabel(descriptorLabel, "Couldn't load the main menu");
                e.printStackTrace();
            }
        } else {
            Utils.changeLabel(descriptorLabel, "");
            Utils.changeLabel(descriptorLabel, "Leave lobby failed");
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
     * @author Riccardo, Ludovico
     */
    @FXML
    private void startGame(ActionEvent actionEvent) throws Exception {
        if(lobby.getPlayers().size() < 2){
            Utils.changeLabel(descriptorLabel, "");
            Utils.changeLabel(descriptorLabel, "Not enough players");
            return;
        }
        Result result = networkManager.gameStart().waitResult();
        if (!result.isOk()) {
            Utils.changeLabel(descriptorLabel, "");
            Utils.changeLabel(descriptorLabel, "Start game failed");
            System.out.println("[ERROR] " + result.getException().orElse("Start game failed"));
        }
    }

    /**
     * method called to switch to the game scene.
     *
     * @author Ludovico
     */

    private void switchToGame() {
        LoginController.state = ClientStatus.InGame;
        state = ClientStatus.InGame;
        try {
            serverThread.interrupt();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Game.fxml"));
            //get stage from an element in the scene. Could be anything else
            stage = (Stage) ((Node)startButton).getScene().getWindow();
            scene = new Scene(root, WIDTH, HEIGHT);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Utils.changeLabel(descriptorLabel, "Couldn't load the game");
            e.printStackTrace();
        }
    }


    /**
     * method called to send a message to the server and add it to the chat.
     * It checks if the message is valid and if it is not it returns after adding the
     * error to the chat, visible only by the sender and not the others in the lobby (length check,
     * empty message check, trying to use commands).
     * Scrolls the chat to the bottom after adding the error message.
     * If the message is valid it sends it to the server and adds it to the chat calling the
     * addMessageToChat() method.
     * It is called when the send button is clicked.
     *
     * @param actionEvent the send button is clicked
     * @author Ludovico
     */

    @FXML
    private void sendMessage(ActionEvent actionEvent) {
        Utils.sendMessage(username, networkManager, messageInput, chat, players, descriptorLabel);
    }

    /**
     * Method called when the user wants to load a game.
     * Checks if the lobby has at least 2 players.
     * It sends a request to the server to load the game.
     * If the request is successful, the game is loaded.
     *
     *
     * @param actionEvent
     * The click of the load button by the user.
     *
     * @throws Exception
     */
    @FXML
    private void loadGame(ActionEvent actionEvent) throws Exception {
        if(lobby.getNumberOfPlayers() < 2){
            Utils.changeLabel(descriptorLabel, "You need at least 2 players to start a game");
            return;
        }
        Result result = networkManager.gameLoad().waitResult();
        if(result.isOk()){
            System.out.println("[INFO] Game loaded");
        }
        else{
            Utils.changeLabel(descriptorLabel, "We could not load the game");
        }
    }

    private void returnToLoginMessage(){
        try {
            serverThread.interrupt();
            Parent newRoot = FXMLLoader.load(getClass().getResource("/fxml/MessageReturnToLogin.fxml"));
            stage = (Stage) (sendMessageButton.getScene().getWindow());
            scene = new Scene(newRoot, WIDTH, HEIGHT);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            Utils.changeLabel(descriptorLabel, "Couldn't load the error message scene");
            throw new RuntimeException(e);
        }
    }

    /**
     * method called to handle the events received from the server.
     * It is called every time the server sends an event.
     * The possible cases are:
     *
     * - Join: a player joined the lobby
     * - Leave: a player left the lobby
     * - newMessage: a message was sent in the lobby chat
     * - Start: the game is starting
     * - ServerDisconnect: the server disconnected
     *
     * In the join and leave cases the lobby is updated and the start button is shown if the player is the first in the lobby.
     * In the start case the game starts and the players are brought to the game scene.
     * In the chat case the message is added to the chat.
     *
     * @author Ludovico
     */
    private void handleEvent() {
        Optional<ServerEvent> event = networkManager.getEvent();
        if (event.isEmpty()) {
            return; // No event
        }
        switch (event.get().getType()) {
            case Join -> {
                String joinedPlayer = (String)event.get().getData();
                if (!lobby.getPlayers().contains(joinedPlayer)) {
                    try {
                        lobby.addPlayer(joinedPlayer);
                        Platform.runLater(() -> {
                            Utils.addMessageToChat(username, new Message(Server.SERVER_NAME, joinedPlayer + " joined the lobby"), chat);
                            updateLobby();
                            showStart();
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> Utils.changeLabel(descriptorLabel, "We could not add the player to the lobby"));
                        throw new RuntimeException("Couldn't add player to lobby");
                    }
                }
                if (!joinedPlayer.equals(username)) {
                    System.out.println(joinedPlayer + " joined the lobby");
                }
            }
            case Leave -> {
                String leftPlayer = (String)event.get().getData();
                try {
                    lobby.removePlayer(leftPlayer);
                    Platform.runLater(() -> {
                        Utils.addMessageToChat(username, new Message(Server.SERVER_NAME, leftPlayer + " left the lobby"), chat);
                        updateLobby();
                        showStart();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> Utils.changeLabel(descriptorLabel, "We could not remove the player from the lobby"));
                    throw new RuntimeException("Removed non existing player from lobby");
                }
                System.out.format("%s left the %s%n", leftPlayer, state == ClientStatus.InLobby ? "lobby" : "game");
            }
            case NewMessage -> {
                Message message = (Message)event.get().getData();
                if (!message.idSender().equals(username)) {
                    System.out.format("%s: %s%n", message.idSender(), message.message());
                    Platform.runLater(() -> Utils.addMessageToChat(username, message, chat));
                }
            }
            case Start -> {
                System.out.println("[*] Game has started");
                state = ClientStatus.InGame;
                gameInfo = (GameInfo)event.get().getData();
                Platform.runLater(() -> {
                    try {
                        switchToGame();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.changeLabel(descriptorLabel, "We could not switch to the game scene");
                    }
                });

            }
            case ServerDisconnect -> {
                System.out.println("[WARNING] Server disconnected");
                Platform.runLater(this::returnToLoginMessage);
            }
            default -> throw new RuntimeException("Unhandled event");
        }
    }


}
