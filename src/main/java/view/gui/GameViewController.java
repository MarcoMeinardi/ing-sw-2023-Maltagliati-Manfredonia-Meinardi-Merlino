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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Card;
import model.Point;
import network.ClientStatus;
import network.NetworkManagerInterface;
import network.Result;
import network.ServerEvent;
import network.parameters.CardSelect;
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
    @FXML
    private Label messageLabel;
    @FXML
    private TextField columnInput;
    public static NetworkManagerInterface networkManager;
    public static ClientStatus state;
    public static Lobby lobby;
    private Thread serverThread;
    public static GameData gameData;
    private String username;
    private Map<ImageView, int[]> imageToIndices = new HashMap<>();
    private List<ImageView> selectedImages = new ArrayList<>();
    private boolean yourTurn = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameData = new GameData(LobbyViewController.gameInfo, LobbyViewController.username);
        username = LobbyViewController.username;
        networkManager = LobbyViewController.networkManager;
        lobby = LobbyViewController.lobby;
        state = ClientStatus.InGame;
        if(lobby.getPlayers().get(0).equals(username)){
            yourTurn = true;
        }
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
        int catNumber = 1;
        int bookNumber = 1;
        int frameNumber = 1;
        int toyNumber = 1;
        int plantNumber = 1;
        int trophyNumber = 1;
        imageToIndices.clear();

        for(int y = 0; y < SIZE; y++){
            for(int x = 0; x < SIZE; x++){
                String imageName;
                String imagePath;
                Image image;
                final ImageView imageView;
                if(table[y][x].isPresent()){
                    switch (table[y][x].get()){
                        case Gatto:
                            imageName = "/img/item tiles/Gatti1." + catNumber + ".png";
                            if(catNumber == 3) {
                                catNumber = 1;
                            } else {
                                catNumber++;
                            }
                            imagePath = getClass().getResource(imageName).toExternalForm();
                            image = new Image(imagePath);
                            imageView = new ImageView(image);
                            imageView.setFitHeight(60);
                            imageView.setFitWidth(60);
                            imageView.setX(25+61*x);
                            imageView.setY(25+61*y);
                            imageToIndices.put(imageView, new int[]{y, x});
                            imageView.setOnMouseClicked(event ->{
                                handleCardSelection(imageView);
                            });
                            pane.getChildren().add(imageView);
                            break;
                        case Libro:
                            imageName = "/img/item tiles/Libri1." + bookNumber + ".png";
                            if(bookNumber == 3) {
                                bookNumber = 1;
                            } else {
                                bookNumber++;
                            }
                            imagePath = getClass().getResource(imageName).toExternalForm();
                            image = new Image(imagePath);
                            imageView = new ImageView(image);
                            imageView.setFitHeight(60);
                            imageView.setFitWidth(60);
                            imageView.setX(25+61*x);
                            imageView.setY(25+61*y);
                            imageToIndices.put(imageView, new int[]{y, x});
                            imageView.setOnMouseClicked(event ->{
                                handleCardSelection(imageView);
                            });
                            pane.getChildren().add(imageView);
                            break;
                        case Cornice:
                            imageName = "/img/item tiles/Cornici1." + frameNumber + ".png";
                            if(frameNumber == 3) {
                                frameNumber = 1;
                            } else {
                                frameNumber++;
                            }
                            imagePath = getClass().getResource(imageName).toExternalForm();
                            image = new Image(imagePath);
                            imageView = new ImageView(image);
                            imageView.setFitHeight(60);
                            imageView.setFitWidth(60);
                            imageView.setX(25+61*x);
                            imageView.setY(25+61*y);
                            imageToIndices.put(imageView, new int[]{y, x});
                            imageView.setOnMouseClicked(event ->{
                                handleCardSelection(imageView);
                            });
                            pane.getChildren().add(imageView);
                            break;
                        case Gioco:
                            imageName = "/img/item tiles/Giochi1." + toyNumber + ".png";
                            if(toyNumber == 3) {
                                toyNumber = 1;
                            } else {
                                toyNumber++;
                            }
                            imagePath = getClass().getResource(imageName).toExternalForm();
                            image = new Image(imagePath);
                            imageView = new ImageView(image);
                            imageView.setFitHeight(60);
                            imageView.setFitWidth(60);
                            imageView.setX(25+61*x);
                            imageView.setY(25+61*y);
                            imageToIndices.put(imageView, new int[]{y, x});
                            imageView.setOnMouseClicked(event ->{
                                handleCardSelection(imageView);
                            });
                            pane.getChildren().add(imageView);
                            break;
                        case Pianta:
                            imageName = "/img/item tiles/Piante1." + plantNumber + ".png";
                            if(plantNumber == 3) {
                                plantNumber = 1;
                            } else {
                                plantNumber++;
                            }
                            imagePath = getClass().getResource(imageName).toExternalForm();
                            image = new Image(imagePath);
                            imageView = new ImageView(image);
                            imageView.setFitHeight(60);
                            imageView.setFitWidth(60);
                            imageView.setX(25+61*x);
                            imageView.setY(25+61*y);
                            imageToIndices.put(imageView, new int[]{y, x});
                            imageView.setOnMouseClicked(event ->{
                                handleCardSelection(imageView);
                            });
                            pane.getChildren().add(imageView);
                            break;
                        case Trofeo:
                            imageName = "/img/item tiles/Trofei1." + trophyNumber +".png";
                            if(trophyNumber == 3) {
                                trophyNumber = 1;
                            } else {
                                trophyNumber++;
                            }
                            imagePath = getClass().getResource(imageName).toExternalForm();
                            image = new Image(imagePath);
                            imageView = new ImageView(image);
                            imageView.setFitHeight(60);
                            imageView.setFitWidth(60);
                            imageView.setX(25+61*x);
                            imageView.setY(25+61*y);
                            imageToIndices.put(imageView, new int[]{x, y});
                            imageView.setOnMouseClicked(event ->{
                                handleCardSelection(imageView);
                            });
                            pane.getChildren().add(imageView);
                            break;
                    }
                }
            }
        }

    }

    private void handleCardSelection(ImageView image){

        if(selectedImages.contains(image)){
            selectedImages.remove(image);
            image.setEffect(null);
        }else{
            if(selectedImages.size() < 3){
                selectedImages.add(image);
                DropShadow selectionEffect = new DropShadow();
                selectionEffect.setColor(Color.YELLOW);
                selectionEffect.setRadius(10);
                selectionEffect.setSpread(0.5);
                image.setEffect(selectionEffect);
            }
            else{
                messageLabel.setText("You can't select more than 3 cards");
            }
        }

    }

    @FXML
    private void tryMove(ActionEvent actionEvent){
        ArrayList<Point> selectedCards = new ArrayList<>();
        String column = columnInput.getText();

        if(selectedImages.size() == 0){
            messageLabel.setText("Select cards!");
        }
        if(column == null ||
                column == "" ||
                    column.length() > 1 ||
                        Integer.valueOf(column) > 5 ||
                            Integer.valueOf(column) < 1){
            messageLabel.setText("Select a valid column!");
        }

        for(ImageView image : selectedImages){
            int[] indices = imageToIndices.get(image);
            selectedCards.add(new Point(indices[0], indices[1]));
        }

        try {
            Result result = networkManager.cardSelect(new CardSelect(Integer.valueOf(column), selectedCards)).waitResult();
            if (result.isErr()) {
                System.out.println("[ERROR] " + result.getException().orElse("Cannot select cards"));
                messageLabel.setText("Can't select cards");
            } else {
                return;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
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
            newStage.setResizable(false);
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
            newStage.setResizable(false);
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
            newStage.setResizable(false);
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
