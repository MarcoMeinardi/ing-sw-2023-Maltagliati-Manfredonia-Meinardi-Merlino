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

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.*;
import network.ClientStatus;
import network.NetworkManagerInterface;
import network.Result;
import network.ServerEvent;
import network.parameters.CardSelect;
import network.parameters.Message;
import network.parameters.Update;

import java.io.IOException;
import java.net.URL;

import java.util.*;

public class GameViewController implements Initializable {

    private  static final int POPUP_WIDTH = 400;
    private static final int POPUP_HEIGHT = 500;
    private static final int WIDTH = 1140;
    private static final int HEIGHT = 760;
    private static final int SIZE = 9;
    private static final int shelfRows = 6;
    private static final int shelfColumns = 5;
    private Stage stage;
    private Scene scene;
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
    private Map<int[], ImageView> indicesToImage = new HashMap<>();
    private List<ImageView> selectedImages = new ArrayList<>();
    private boolean yourTurn = false;
    private boolean isPaused = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameData = new GameData(LobbyViewController.gameInfo, LobbyViewController.username);
        username = LobbyViewController.username;
        networkManager = LobbyViewController.networkManager;
        lobby = LobbyViewController.lobby;
        state = ClientStatus.InGame;
        if(gameData.getCurrentPlayer().equals(username)){
            yourTurn = true;
            messageLabel.setText("It's your turn!");
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
        int[] counter = new int[Card.values().length];
        for(int i = 0; i < Card.values().length; i++){
            counter[i] = 1;
        }
        imageToIndices.clear();
        List<Node> toRemove = new ArrayList<>();
        for(Node child : pane.getChildren()){
            if(child.getId() != null){
                if(child.getId().contains("Gatto") ||
                        child.getId().contains("Libro") ||
                            child.getId().contains("Cornice") ||
                                child.getId().contains("Gioco") ||
                                    child.getId().contains("Pianta") ||
                                        child.getId().contains("Trofeo")){
                    toRemove.add(child);
                }
            }
        }

        for(Node child : toRemove){
            pane.getChildren().remove(child);
        }

        for(int y = 0; y < SIZE; y++){
            for(int x = 0; x < SIZE; x++){
                String imageName;
                String imagePath;
                Image image;
                final ImageView imageView;
                if(table[y][x].isPresent()){
                    switch (table[y][x].get()){
                        case Gatto:
                            imageName = "/img/item tiles/Gatti1." + counter[Card.Gatto.ordinal()] + ".png";
                            counter[Card.Gatto.ordinal()] = (counter[Card.Gatto.ordinal()]%3) + 1;
                            putImageOnScene(imageName, y, x, Card.Gatto);
                            break;
                        case Libro:
                            imageName = "/img/item tiles/Libri1." + counter[Card.Libro.ordinal()] + ".png";
                            counter[Card.Libro.ordinal()] = (counter[Card.Libro.ordinal()]%3) + 1;
                            putImageOnScene(imageName, y, x, Card.Libro);
                            break;
                        case Cornice:
                            imageName = "/img/item tiles/Cornici1." + counter[Card.Cornice.ordinal()] + ".png";
                            counter[Card.Cornice.ordinal()] = (counter[Card.Cornice.ordinal()]%3) + 1;
                            putImageOnScene(imageName, y, x, Card.Cornice);
                            break;
                        case Gioco:
                            imageName = "/img/item tiles/Giochi1." + counter[Card.Gioco.ordinal()] + ".png";
                            counter[Card.Gioco.ordinal()] = (counter[Card.Gioco.ordinal()]%3) + 1;
                            putImageOnScene(imageName, y, x, Card.Gioco);
                            break;
                        case Pianta:
                            imageName = "/img/item tiles/Piante1." + counter[Card.Pianta.ordinal()] + ".png";
                            counter[Card.Pianta.ordinal()] = (counter[Card.Pianta.ordinal()]%3) + 1;
                            putImageOnScene(imageName, y, x, Card.Pianta);
                            break;
                        case Trofeo:
                            imageName = "/img/item tiles/Trofei1." + counter[Card.Trofeo.ordinal()] +".png";
                            counter[Card.Trofeo.ordinal()] = (counter[Card.Trofeo.ordinal()]%3) + 1;
                            putImageOnScene(imageName, y, x, Card.Trofeo);
                            break;
                    }
                }
            }
        }

    }

    private void putImageOnScene(String imageName, int y, int x, Card card){
        String imagePath = getClass().getResource(imageName).toExternalForm();
        Image image = new Image(imagePath);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(60);
        imageView.setFitWidth(60);
        imageView.setX(25+61*x);
        imageView.setY(25+61*y);
        imageView.setId(card.toString() + x + y);
        pane.getChildren().add(imageView);
        imageView.setOnMouseClicked(event ->{
            handleCardSelection(imageView);
        });
        imageToIndices.put(imageView, new int[]{y, x});
    }

    private void fillShelf(Shelf shelf) {
        Optional<Card>[][] shelfCards = shelf.getShelf();

        int[] counter = new int[Card.values().length];
        for(int i = 0; i < Card.values().length; i++){
            counter[i] = 1;
        }

        List<Node> toRemove = new ArrayList<>();
        for(Node child : pane.getChildren()){
            if(child.getId() != null){
                if((child.getId().contains("Gatto") ||
                        child.getId().contains("Libro") ||
                        child.getId().contains("Cornice") ||
                        child.getId().contains("Gioco") ||
                        child.getId().contains("Pianta") ||
                        child.getId().contains("Trofeo")) && child.getId().contains("Shelf")){
                    toRemove.add(child);
                }
            }
        }

        for(Node child : toRemove){
            pane.getChildren().remove(child);
        }

        for (int y = 0; y < shelfRows; y++){
            for(int x = 0; x < shelfColumns; x++){
                if(shelfCards[y][x].isPresent()){
                    switch (shelfCards[y][x].get()){
                        case Gatto:
                            counter[Card.Gatto.ordinal()] = (counter[Card.Gatto.ordinal()]%3) + 1;
                            putImageOnShelf("/img/item tiles/Gatti1." + counter[Card.Gatto.ordinal()] + ".png", y, x, Card.Gatto);
                            break;
                        case Libro:
                            counter[Card.Libro.ordinal()] = (counter[Card.Libro.ordinal()]%3) + 1;
                            putImageOnShelf("/img/item tiles/Libri1." + counter[Card.Libro.ordinal()] + ".png", y, x, Card.Libro);
                            break;
                        case Cornice:
                            counter[Card.Cornice.ordinal()] = (counter[Card.Cornice.ordinal()]%3) + 1;
                            putImageOnShelf("/img/item tiles/Cornici1." + counter[Card.Cornice.ordinal()] + ".png", y, x, Card.Cornice);
                            break;
                        case Gioco:
                            counter[Card.Gioco.ordinal()] = (counter[Card.Gioco.ordinal()]%3) + 1;
                            putImageOnShelf("/img/item tiles/Giochi1." + counter[Card.Gioco.ordinal()] + ".png", y, x, Card.Gioco);
                            break;
                        case Pianta:
                            counter[Card.Pianta.ordinal()] = (counter[Card.Pianta.ordinal()]%3) + 1;
                            putImageOnShelf("/img/item tiles/Piante1." + counter[Card.Pianta.ordinal()] + ".png", y, x, Card.Pianta);
                            break;
                        case Trofeo:
                            counter[Card.Trofeo.ordinal()] = (counter[Card.Trofeo.ordinal()]%3) + 1;
                            putImageOnShelf("/img/item tiles/Trofei1." + counter[Card.Trofeo.ordinal()] +".png", y, x, Card.Trofeo);
                            break;
                    }
                }
            }
        }
    }

    private void putImageOnShelf(String imageName, int y, int x, Card card){
        String imagePath = getClass().getResource(imageName).toExternalForm();
        Image image = new Image(imagePath);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(28);
        imageView.setFitWidth(28);
        imageView.setX(790+34*x);
        imageView.setY(270-30*y);
        imageView.setId(card.toString() + x + y + "shelf");
        pane.getChildren().add(imageView);
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
        String columnHelper = columnInput.getText();

        if(selectedImages.size() == 0){
            messageLabel.setText("Select cards!");
            return;
        }
        if(columnHelper.trim().isEmpty() ||
                column == null){
            messageLabel.setText("Left blank!");
            return;
        }
        if(column.length() > 1 ||
                Integer.valueOf(column) > 5 ||
                    Integer.valueOf(column) < 1){
            messageLabel.setText("Select a valid column!");
            return;
        }
        if(!yourTurn){
            messageLabel.setText("It's not your turn!");
            return;
        }
        if(isPaused){
            messageLabel.setText("Game is paused!");
            return;
        }

        for(ImageView image : selectedImages){
            int[] indices = imageToIndices.get(image);
            selectedCards.add(new Point(indices[0], indices[1]));
        }

        try {
            Result result = networkManager.cardSelect(new CardSelect(Integer.valueOf(column) - 1, selectedCards)).waitResult();
            if (result.isErr()) {
                System.out.println("[ERROR] " + result.getException().orElse("Cannot select cards"));
                messageLabel.setText("Error" + result.getException().orElse("Cannot select cards"));
            } else {
                return;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

    }

    @FXML
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

    public void printEnd(){
        try {
            serverThread.interrupt();
            Parent newRoot = FXMLLoader.load(getClass().getResource("/fxml/End.fxml"));
            stage = (Stage) (sendMessageButton.getScene().getWindow());
            scene = new Scene(newRoot, WIDTH, HEIGHT);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void changeLabel(String text){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                messageLabel.setText(text);
            }
        });
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
            case Update -> {
                Update update = (Update)event.get().getData();
                for (Cockade commonObjective : update.commonObjectives()) {
                    if (update.idPlayer().equals(username)) {
                        System.out.format("[*] You completed %s getting %d points%n", commonObjective.name(), commonObjective.points());
                        changeLabel("You completed " + commonObjective.name() + " getting " + commonObjective.points() + " points");
                    } else {
                        System.out.format("[*] %s completed %s getting %d points%n", update.idPlayer(), commonObjective.name(), commonObjective.points());
                        changeLabel(update.idPlayer() + " completed " + commonObjective.name() + " getting " + commonObjective.points() + " points");
                    }
                }
                gameData.update(update);
                if (update.nextPlayer().equals(username)) {
                    yourTurn = true;
                    System.out.println("[*] It's your turn");
                    changeLabel("It's your turn");
                } else {
                    yourTurn = false;
                    System.out.println("[*] It's " + update.nextPlayer() + "'s turn");
                    changeLabel("It's " + update.nextPlayer() + "'s turn");
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        fillScene(gameData.getTableTop());
                        fillShelf(gameData.getMyShelf());
                        selectedImages.clear();
                    }
                });
            }
            case End -> {
                ScoreBoard scoreboard = (ScoreBoard)event.get().getData();
                gameData.setScoreBoard(scoreboard);
                System.out.println("[*] Game ended");
                printEnd();
            }
            case Pause -> {
                if (!isPaused) {
                    System.out.println("[WARNING] Someone has disconnected");
                    changeLabel("Someone has disconnected");
                }
                isPaused = true;
            }
            case Resume -> {
                System.out.println("Game resumed");
                changeLabel("Game resumed");
                isPaused = false;
            }
            default -> throw new RuntimeException("Unhandled event");
        }
    }

    public static GameData getGameData(){
        return gameData;
    }

}