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
import model.Card;
import model.Shelf;
import model.Cockade;
import model.Point;
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

//DO NOT GIVE ID WITH "cat", "book", "frame", "toy", "plant", "trophy" IN IT TO ANY NODES IN THE FXML FILE
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

    //TODO implement label to say whose turn it is
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

    private void fillShelf(List<ImageView> selectedImages, Shelf shelf) {
        final ImageView imageView = null; //null perchè mi dava errore
        if (!selectedImages.isEmpty()) {
            imageView.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    int col = (int) (event.getX() / 60); // Calcola la colonna in base alla posizione X del click
                    int row = (int) (event.getY() / 60); // Calcola la riga in base alla posizione Y del click
/// aggiunta di una griglia?
                    if (isValidPosition(col, row)) {

                    }
                }
            });
        }
        /*int shelfSizeX = 5;
        int shelfSizeY = 6;
        int startX = 25;
        int startY = SIZE * 61 - 35;
        int catNumber = 1;
        int bookNumber = 1;
        int frameNumber = 1;
        int toyNumber = 1;
        int plantNumber = 1;
        int trophyNumber = 1;
        imageToIndices.clear();

        for (int y = startY; y < startY + shelfSizeY * 61; y += 61) {
            for (int x = startX; x < startX + shelfSizeX * 61; x += 61) {
                String imageName = null;
                String imagePath;
                Image image;
                final ImageView imageView;

                for (int i = 0; i < selectedImages.size(); i++) {
                    if (selectedImages.get(i).getId().equals("Gatto")) {
                        imageName = "/img/item tiles/Gatti1." + catNumber + ".png";
                        if (catNumber == 3) {
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
                        imageToIndices.put(imageView, new int[]{x, y});
                        pane.getChildren().add(imageView);
                        break;
                    }
                    if (selectedImages.get(i).getId().equals("Libro")) {
                        imageName = "/img/item tiles/Libri1." + bookNumber + ".png";
                        if (bookNumber == 3) {
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
                        imageToIndices.put(imageView, new int[]{x, y});
                        pane.getChildren().add(imageView);
                        break;
                    }
                    if (selectedImages.get(i).getId().equals("Cornice")) {
                        imageName = "/img/item tiles/Cornici1." + frameNumber + ".png";
                        if (frameNumber == 3) {
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
                        imageToIndices.put(imageView, new int[]{x, y});
                        pane.getChildren().add(imageView);
                        break;
                    }
                    if (selectedImages.get(i).getId().equals("Gioco")) {
                        imageName = "/img/item tiles/Giochi1." + toyNumber + ".png";
                        if (toyNumber == 3) {
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
                        imageToIndices.put(imageView, new int[]{x, y});
                        pane.getChildren().add(imageView);
                        break;
                    }
                    if (selectedImages.get(i).getId().equals("Pianta")) {
                        imageName = "/img/item tiles/Piante1." + plantNumber + ".png";
                        if (plantNumber == 3) {
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
                        imageToIndices.put(imageView, new int[]{x, y});
                        pane.getChildren().add(imageView);
                        break;
                    }
                    if (selectedImages.get(i).getId().equals("Trofeo")) {
                        imageName = "/img/item tiles/Trofei1." + trophyNumber + ".png";
                        if (trophyNumber == 3) {
                            trophyNumber = 1;
                        } else {
                            trophyNumber++;
                        }
                        imagePath = getClass().getResource(imageName).toExternalForm();
                        image = new Image(imagePath);
                        imageView = new ImageView(image);
                        imageView.setFitHeight(60);
                        imageView.setFitWidth(60);
                        imageView.setX(x);
                        imageView.setY(y);
                        imageToIndices.put(imageView, new int[]{x, y});
                        pane.getChildren().add(imageView);
                        break;
                    } else {
                            imageName = "";
                            break;
                    }
                }
            }
        }*/
    }

    private boolean isValidPosition(int col, int row) {
        int shelfSizeX = 5;
        int shelfSizeY = 6;
        return col >= 0 && col < shelfSizeX && row >= 0 && row < shelfSizeY;
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
        }
        if(columnHelper.trim().isEmpty() ||
                column == null){
            messageLabel.setText("Left blank!");
        }
        if(column.length() > 1 ||
                Integer.valueOf(column) > 5 ||
                Integer.valueOf(column) < 1){
            messageLabel.setText("Select a valid column!");
        }
        if(!yourTurn){
            messageLabel.setText("It's not your turn!");
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

    private void handleCardInsert(ImageView image) {
        if(selectedImages.contains(image)) {
            selectedImages.add(image);
        }
        else{
            messageLabel.setText("You can't insert card here");
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
            case Update -> {
                Update update = (Update)event.get().getData();
                for (Cockade commonObjective : update.commonObjectives()) {
                    if (update.idPlayer().equals(username)) {
                        System.out.format("[*] You completed %s getting %d points%n", commonObjective.name(), commonObjective.points());
                        //TODO: aggiungere un popup che dice che hai completato un obiettivo comune
                    } else {
                        System.out.format("[*] %s completed %s getting %d points%n", update.idPlayer(), commonObjective.name(), commonObjective.points());
                        //TODO: aggiungere un popup che dice che un altro giocatore ha completato un obiettivo comune
                    }
                }
                gameData.update(update);
                if (update.nextPlayer().equals(username)) {
                    yourTurn = true;
                    System.out.println("[*] It's your turn");
                    //TODO: aggiungere un popup che dice che è il tuo turno
                } else {
                    yourTurn = false;
                    System.out.println("[*] It's " + update.nextPlayer() + "'s turn");
                    //TODO: aggiungere un popup che dice che è il turno di un altro giocatore
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        fillScene(gameData.getTableTop());
                        selectedImages.clear();
                    }
                });
            }
            default -> throw new RuntimeException("Unhandled event");

        }
    }

    public static GameData getGameData(){
        return gameData;
    }

}