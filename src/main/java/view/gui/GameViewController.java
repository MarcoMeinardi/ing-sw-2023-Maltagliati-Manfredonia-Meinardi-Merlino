package view.gui;

import controller.lobby.Lobby;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Card;
import network.ClientStatus;
import network.NetworkManagerInterface;
import network.Result;
import network.ServerEvent;
import network.parameters.Message;

import java.io.IOException;
import java.net.URL;

import java.util.*;

public class GameViewController implements Initializable {

    private  static final int POPUP_WIDTH = 400;
    private static final int SIZE = 9;
    private static final int POPUP_HEIGHT = 500;
    @FXML
    public Button sendMessageButton;
    @FXML
    public TextField messageInput;
    @FXML
    private ListView chat;
    @FXML
    private ListView players;
    @FXML
    private AnchorPane pane;
    public static NetworkManagerInterface networkManager;
    public static ClientStatus state;
    public static Lobby lobby;
    private Thread serverThread;
    public static GameData gameData;
    private String username;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameData = new GameData(LobbyViewController.gameInfo, LobbyViewController.username);
        username = LobbyViewController.username;
        networkManager = LobbyViewController.networkManager;
        lobby = LobbyViewController.lobby;
        state = ClientStatus.InGame;
        startLobby();
        fillScene(gameData.getTableTop());
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

    private void fillScene(Optional<Card>[][] table) {
        for(int y = 0; y < SIZE; y++){
            for(int x = 0; x < SIZE; x++){
                if(table[y][x].isPresent()){
                    ImageView imageView = null;
                    String imageName;
                    String imagePath;
                    Image image;
                    //controlla come effettivamente chiedere al table il tipo nella posizione
                    switch (table[y][x].get()){
                        case Gatto:
                            imageName = "/img/item tiles/Gatti1.2.png";
                            imagePath = getClass().getResource(imageName).toExternalForm();
                            image = new Image(imagePath);
                            imageView = new ImageView(image);
                            imageView.setFitHeight(60);
                            imageView.setFitWidth(60);
                            imageView.setX(25+60*x);
                            imageView.setY(25+60*y);
                            pane.getChildren().add(imageView);
                            break;
                        case Libro:
                            imageName = "/img/item tiles/Libri1.2.png";
                            imagePath = getClass().getResource(imageName).toExternalForm();
                            image = new Image(imagePath);
                            imageView = new ImageView(image);
                            imageView.setFitHeight(60);
                            imageView.setFitWidth(60);
                            imageView.setX(25+60*x);
                            imageView.setY(25+60*y);
                            pane.getChildren().add(imageView);
                            break;
                        case Cornice:
                            imageName = "/img/item tiles/Cornici1.2.png";
                            imagePath = getClass().getResource(imageName).toExternalForm();
                            image = new Image(imagePath);
                            imageView = new ImageView(image);
                            imageView.setFitHeight(60);
                            imageView.setFitWidth(60);
                            imageView.setX(25+60*x);
                            imageView.setY(25+60*y);
                            pane.getChildren().add(imageView);
                            break;
                        case Gioco:
                            imageName = "/img/item tiles/Giochi1.2.png";
                            imagePath = getClass().getResource(imageName).toExternalForm();
                            image = new Image(imagePath);
                            imageView = new ImageView(image);
                            imageView.setFitHeight(60);
                            imageView.setFitWidth(60);
                            imageView.setX(25+60*x);
                            imageView.setY(25+60*y);
                            pane.getChildren().add(imageView);
                            break;
                        case Pianta:
                            imageName = "/img/item tiles/Piante1.2.png";
                            imagePath = getClass().getResource(imageName).toExternalForm();
                            image = new Image(imagePath);
                            imageView = new ImageView(image);
                            imageView.setFitHeight(60);
                            imageView.setFitWidth(60);
                            imageView.setX(25+60*x);
                            imageView.setY(25+60*y);
                            pane.getChildren().add(imageView);
                            break;
                        case Trofeo:
                            imageName = "/img/item tiles/Trofei1.2.png";
                            imagePath = getClass().getResource(imageName).toExternalForm();
                            image = new Image(imagePath);
                            imageView = new ImageView(image);
                            imageView.setFitHeight(60);
                            imageView.setFitWidth(60);
                            imageView.setX(25+60*x);
                            imageView.setY(25+60*y);
                            pane.getChildren().add(imageView);
                            break;
                    }
                }
            }
        }

    }
    public void startLobby(){
        //initialize the list view with blank spaces
        for (int i = 0; i < lobby.getNumberOfPlayers(); i++) {
            players.getItems().add(lobby.getPlayers().get(i));
        }
        //add messages to the chat
        chat.getItems().add("[Type /help to see the list of commands]");
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
            if(chat.getItems().size() != 3){
                chat.scrollTo(chat.getItems().size()-1);
            }
            return;
        }
        if (messageText.length() > 100) {
            System.out.println("[ERROR] Message too long");
            chat.getItems().add("[ERROR] Message too long");
            if(chat.getItems().size() != 3){
                chat.scrollTo(chat.getItems().size()-1);
            }
            return;
        }
        if (messageText.startsWith(".") || messageText.startsWith("?")) {
            System.out.println("[ERROR] Commands not supported, use /help for more info");
            chat.getItems().add("[ERROR] Commands not supported. Use /help for more info");
            if(chat.getItems().size() != 3){
                chat.scrollTo(chat.getItems().size()-1);
            }
            return;
        }
        if(messageText.startsWith("/help")){
            chat.getItems().add("[-Select a name from the list above to send a private message]");
            chat.getItems().add("[-Specific commands supported:]");
            chat.getItems().add("[/help: shows this message]");
            if(chat.getItems().size() != 3){
                chat.scrollTo(chat.getItems().size()-1);
            }
            return;
        }

        //try to send it to the server and add it to chat
        try{
            if(players.getSelectionModel().getSelectedItem() != null && !players.getSelectionModel().getSelectedItem().toString().equals("")){
                Result result = networkManager.chat(new Message(username, messageText, players.getSelectionModel().getSelectedItem().toString())).waitResult();
                if (result.isErr()) {
                    System.out.println("[ERROR] " + result.getException().orElse("Cannot send message"));
                    chat.getItems().add("[ERROR] We could not send your message, please try again later");
                    if(chat.getItems().size() != 3){
                        chat.scrollTo(chat.getItems().size()-1);
                    }
                    return;
                }
                Message message = new Message(username, messageText, players.getSelectionModel().getSelectedItem().toString());
                players.getSelectionModel().clearSelection();
                addMessageToChat(message);
                return;
            }
            Result result = networkManager.chat(new Message(username, messageText)).waitResult();
            if (result.isErr()) {
                System.out.println("[ERROR] " + result.getException().orElse("Cannot send message"));
                chat.getItems().add("[ERROR] We could not send your message, please try again later");
                if(chat.getItems().size() != 3){
                    chat.scrollTo(chat.getItems().size()-1);
                }
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
        if(hour.length() == 1){
            hour = "0" + hour;
        }
        if(minute.length() == 1){
            minute = "0" + minute;
        }
        if (message.idReceiver().isEmpty()) {
            chat.getItems().add(String.format("[%s:%s] %s to everyone: %s", hour, minute, message.idSender(), message.message()));
        } else {
            if(!message.idSender().equals(username)) {
                chat.getItems().add(String.format("[%s:%s] %s to you: %s", hour, minute, message.idSender(), message.message()));
            }
            else{
                if(message.idReceiver().get().equals(message.idSender())){
                    chat.getItems().add(String.format("[%s:%s] %s to himself: you are a schizo", hour, minute, message.idSender()));
                }
                else {
                    chat.getItems().add(String.format("[%s:%s] you to %s: %s", hour, minute, message.idReceiver().get(), message.message()));
                }
            }
        }
        if(chat.getItems().size() != 3){
            chat.scrollTo(chat.getItems().size()-1);
        }
    }

    public void printPersonalObjectivesButton(ActionEvent actionEvent) {
        try {
            Stage newStage = new Stage();
            Parent newRoot = FXMLLoader.load(getClass().getResource("/fxml/PersonalObj.fxml"));
            Scene newScene = new Scene(newRoot, POPUP_WIDTH, POPUP_HEIGHT);
            newStage.setScene(newScene);
            newStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printAllShelvesObjectivesButton(ActionEvent actionEvent) {
        try {
            Stage newStage = new Stage();
            Parent newRoot = FXMLLoader.load(getClass().getResource("/fxml/Shelves.fxml"));
            Scene newScene = new Scene(newRoot, POPUP_WIDTH, POPUP_HEIGHT);
            newStage.setScene(newScene);
            newStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printCommonObjectivesButton(ActionEvent actionEvent) {
        try {
            Stage newStage = new Stage();
            Parent newRoot = FXMLLoader.load(getClass().getResource("/fxml/CommonObj.fxml"));
            Scene newScene = new Scene(newRoot, POPUP_WIDTH, POPUP_HEIGHT);
            newStage.setScene(newScene);
            newStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleEvent() {
        Optional<ServerEvent> event = networkManager.getEvent();
        if (event.isEmpty()) {
            return; // No event
        }
        switch (event.get().getType()) {
            case NewMessage -> {
                Message message = (Message)event.get().getData();
                if (!message.idSender().equals(username)) {
                    System.out.format("%s: %s%n", message.idSender(), message.message());
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

    public static GameData getGameData(){
        return gameData;
    }

}
