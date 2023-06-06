package view.gui;

import controller.lobby.Lobby;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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

    private  static final int POPUP_WIDTH = 600;
    private static final int POPUP_HEIGHT = 400;

    private  static final int SHELVES_POPUP_WIDTH = 800;
    private static final int SHELVES_POPUP_HEIGHT = 800;
    private static final int WIDTH = 1140;
    private static final int HEIGHT = 760;
    private static final int SIZE = 9;
    private static final int cardSize = 60;
    private static final int cardStep = 61;
    private static final int cardOffSet = 25;
    private static final int shelfCardSize = 28;
    private static final int shelfCardStepX = 34;
    private static final int shelfCardStepY = 30;
    private static final int shelfOffSetX = 790;
    private static final int shelfOffSetY = 270;
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
        if (LobbyViewController.gameInfo == null) {
            gameData = new GameData(LoginController.gameInfo, LoginController.username);
            username = LoginController.username;
            networkManager = LoginController.networkManager;
            lobby = LoginController.lobby;
        } else {
            gameData = new GameData(LobbyViewController.gameInfo, LobbyViewController.username);
            username = LobbyViewController.username;
            networkManager = LobbyViewController.networkManager;
            lobby = LobbyViewController.lobby;
        }
        state = ClientStatus.InGame;
        if(gameData.getCurrentPlayer().equals(username)){
            yourTurn = true;
            messageLabel.setText("It's your turn!");
        }
        else{
            messageLabel.setText("It's " + gameData.getCurrentPlayer() + "'s turn!");
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
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        stage = (Stage) pane.getScene().getWindow();
                        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                            @Override
                            public void handle(WindowEvent t) {
                                Platform.exit();
                                System.exit(0);
                            }
                        });
                    }
                });
    }

    /**
     * This method fills the game scene with images of items based on the contents of the `table` array.
     * It uses the `counter` array to keep track of the number of times each item has been added to the scene,
     * and selects the appropriate image file based on the item type and the current count.
     * The `putImageOnScene` method is then called to add the image to the scene at the appropriate location.
     *
     * @param table The table of optional cards representing the scene.
     * @author Ludovico
     */
    private void fillScene(Optional<Card>[][] table) {
        int[] counter = new int[Card.values().length];
        for (int i = 0; i < Card.values().length; i++) {
            counter[i] = 1;
        }

        imageToIndices.clear();
        removeImages(false);

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                String imageName = null;
                if (table[y][x].isPresent()) {
                    if (table[y][x].get() == Card.Gatto) {
                        imageName = "/img/item tiles/Gatti1." + counter[Card.Gatto.ordinal()] + ".png";
                        counter[Card.Gatto.ordinal()] = (counter[Card.Gatto.ordinal()] % 3) + 1;
                    }
                    if (table[y][x].get() == Card.Libro) {
                        imageName = "/img/item tiles/Libri1." + counter[Card.Libro.ordinal()] + ".png";
                        counter[Card.Libro.ordinal()] = (counter[Card.Libro.ordinal()] % 3) + 1;
                    }
                    if (table[y][x].get() == Card.Cornice) {
                        imageName = "/img/item tiles/Cornici1." + counter[Card.Cornice.ordinal()] + ".png";
                        counter[Card.Cornice.ordinal()] = (counter[Card.Cornice.ordinal()] % 3) + 1;
                    }
                    if (table[y][x].get() == Card.Gioco) {
                        imageName = "/img/item tiles/Giochi1." + counter[Card.Gioco.ordinal()] + ".png";
                        counter[Card.Gioco.ordinal()] = (counter[Card.Gioco.ordinal()] % 3) + 1;
                    }
                    if (table[y][x].get() == Card.Pianta) {
                        imageName = "/img/item tiles/Piante1." + counter[Card.Pianta.ordinal()] + ".png";
                        counter[Card.Pianta.ordinal()] = (counter[Card.Pianta.ordinal()] % 3) + 1;
                    }
                    if(table[y][x].get() == Card.Trofeo){
                        counter[Card.Trofeo.ordinal()] = (counter[Card.Trofeo.ordinal()]%3) + 1;
                        imageName = "/img/item tiles/Trofei1." + counter[Card.Trofeo.ordinal()] + ".png";
                    }
                    putImageOnScene(imageName, y, x, cardSize, cardSize, cardOffSet, cardOffSet, cardStep, cardStep, false);
                }
            }
        }
    }

    /**
     * method that takes a `Shelf` object as input and fills the shelf with images of items based on the cards present on the shelf.
     * It uses an array `counter` to keep track of the number of times each item has been placed on the shelf and selects the appropriate image file
     * based on the item type and the counter value.
     * The `putImageOnScene` method is then called to place the image on the scene.
     * The method also calls `removeImages` to remove any existing images on the shelf before filling it with new images.
     *
     * @param shelf to be filled with images
     * @author Ludovico
     */
    private void fillShelf(Shelf shelf) {
        Optional<Card>[][] shelfCards = shelf.getShelf();

        int[] counter = new int[Card.values().length];
        for(int i = 0; i < Card.values().length; i++){
            counter[i] = 1;
        }

        removeImages(true);

        for (int y = 0; y < shelfRows; y++){
            for(int x = 0; x < shelfColumns; x++){
                String imageName = null;
                if(shelfCards[y][x].isPresent()){

                    if(shelfCards[y][x].get() == Card.Gatto){
                        counter[Card.Gatto.ordinal()] = (counter[Card.Gatto.ordinal()]%3) + 1;
                        imageName = "/img/item tiles/Gatti1." + counter[Card.Gatto.ordinal()] + ".png";
                    }
                    if(shelfCards[y][x].get() == Card.Libro){
                        counter[Card.Libro.ordinal()] = (counter[Card.Libro.ordinal()]%3) + 1;
                        imageName = "/img/item tiles/Libri1." + counter[Card.Libro.ordinal()] + ".png";
                    }
                    if(shelfCards[y][x].get() == Card.Cornice){
                        counter[Card.Cornice.ordinal()] = (counter[Card.Cornice.ordinal()]%3) + 1;
                        imageName = "/img/item tiles/Cornici1." + counter[Card.Cornice.ordinal()] + ".png";
                    }
                    if(shelfCards[y][x].get() == Card.Gioco){
                        counter[Card.Gioco.ordinal()] = (counter[Card.Gioco.ordinal()]%3) + 1;
                        imageName = "/img/item tiles/Giochi1." + counter[Card.Gioco.ordinal()] + ".png";
                    }
                    if(shelfCards[y][x].get() == Card.Pianta){
                        counter[Card.Pianta.ordinal()] = (counter[Card.Pianta.ordinal()]%3) + 1;
                        imageName = "/img/item tiles/Piante1." + counter[Card.Pianta.ordinal()] + ".png";
                    }
                    if(shelfCards[y][x].get() == Card.Trofeo){
                        counter[Card.Trofeo.ordinal()] = (counter[Card.Trofeo.ordinal()]%3) + 1;
                        imageName = "/img/item tiles/Trofei1." + counter[Card.Trofeo.ordinal()] + ".png";
                    }
                    putImageOnScene(imageName, y, x,  shelfCardSize, shelfCardSize, shelfOffSetX, shelfOffSetY, shelfCardStepX, shelfCardStepY, true);

                }
            }
        }

    }

    /**
     * This method is adding an image to a pane.
     * It takes in parameters such as the image name, position, size, and whether it is on a shelf or not.
     * It creates an ImageView object with the image and sets its properties such as fit height and width, position, and ID.
     * It then adds the ImageView to the pane and sets an event handler for mouse clicks if it is not on a shelf.
     * Finally, it adds the ImageView and its position to a HashMap for later reference.
     *
     * @param imageName The name or path of the image file to be displayed.
     * @param y The row position (y-coordinate) of the image on the scene.
     * @param x The column position (x-coordinate) of the image on the scene.
     * @param height The desired height of the image.
     * @param width The desired width of the image.
     * @param offsetX The X-offset from the left side of the scene where the image should be positioned.
     * @param offsetY The Y-offset from the top side of the scene where the image should be positioned.
     * @param stepX The horizontal step size between image positions on the scene.
     * @param stepY The vertical step size between image positions on the scene.
     * @param isShelf Specifies whether the image is being placed on a shelf or not
     * @autor Ludovico
     */
    public void putImageOnScene(String imageName, int y, int x, int height, int width, int offsetX, int offsetY, int stepX, int stepY, boolean isShelf){
        String imagePath = getClass().getResource(imageName).toExternalForm();
        Image image = new Image(imagePath);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        imageView.setX(offsetX + stepX*x);
        String id = "Card" + x + y;
        if(isShelf){
            id += "Shelf";
            imageView.setY(offsetY - stepY*y);
        }
        else{
            imageView.setY(offsetY + stepY*y);
        }
        imageView.setId(id);
        pane.getChildren().add(imageView);
        imageView.toFront();
        if(!isShelf){
            imageView.setOnMouseClicked(event ->{
                handleCardSelection(imageView);
            });
            imageToIndices.put(imageView, new int[]{y, x});
        }
    }


    /**
     * method that removes images from a pane.
     * The method takes a boolean parameter `isShelf`
     * which determines whether to remove only images with "Shelf" in their ID or all images with "Card" in their ID.
     * The method loops through all the children of the pane and adds the appropriate images to a list of nodes to be removed.
     * Finally, it loops through the list and removes each node from the pane.
     *
     * @param isShelf specifies whether the images to be removed are on the shelf or not
     * @autor Ludovico
     */
    public void removeImages(boolean isShelf){
        List<Node> toRemove = new ArrayList<>();
        for(Node child : pane.getChildren()){
            if(child.getId() != null){
                if(child.getId().contains("Card")){
                    if(isShelf){
                        if(child.getId().contains("Shelf")){
                            toRemove.add(child);
                        }
                    }else{
                        toRemove.add(child);
                    }
                }
            }
        }

        for(Node child : toRemove){
            pane.getChildren().remove(child);
        }
    }

    /**
     * method that handles the selection of a card represented by an ImageView object.
     * It checks if the card is already selected, and if so,
     * it removes it from the list of selected images and removes the selection effect.
     * If the card is not already selected, it adds it to the list of selected images
     * and applies a yellow drop shadow effect to indicate selection.
     * If the number of selected cards exceeds 3,
     * it displays a message indicating that no more than 3 cards can be selected.
     *
     * @param image the image that has been selected
     * @autor Ludovico
     */
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

    /**
     * method that is called when the player tries to move selected cards to a specified column.
     * It first checks if any cards are selected, if the column input is empty or null,
     * if the column input is a valid number between 1 and 5,
     * if it's the player's turn, and if the game is paused.
     * If all checks pass, it gets the indices of the selected cards
     * and sends a request to the network manager to move the cards to the specified column.
     * If there is an error, it displays an error message.     *
     * @param actionEvent The action event triggered by the move button
     * @autor Ludovico
     */
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
                messageLabel.setText("Select valid cards!");
            } else {
                return;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

    }

    /**
     * method that initializes the lobby scene.
     * It adds the players to the list view and adds the messages to the chat.
     *
     * @autor Ludovico
     */
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

    /**
     * method that is triggered when a button is clicked ("Print Personal Objectives" button).
     * It creates a new stage (window) and loads the contents of a FXML file called "PersonalObj.fxml" into it.
     * It then sets the scene of the new stage to the loaded FXML file and displays the stage to the user.
     * If there is an error loading the FXML file, it throws a runtime exception.
     *
     * @param actionEvent the event that triggered the method
     * @autor: Ludovico
     */
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

    /**
     * method that is triggered when a button is clicked ("Print All Shelves" button).
     * It creates a new stage (window) and loads the contents of a FXML file called "Shelves.fxml" into it.
     * It then sets the scene of the new stage to the loaded FXML file and displays the stage to the user.
     * If there is an error loading the FXML file, it throws a runtime exception.
     *
     * @param actionEvent the event that triggered the method
     * @autor: Ludovico
     */
    public void printAllShelvesObjectivesButton(ActionEvent actionEvent) {
        try {
            Stage newStage = new Stage();
            Parent newRoot = FXMLLoader.load(getClass().getResource("/fxml/Shelves.fxml"));
            Scene newScene = new Scene(newRoot, SHELVES_POPUP_WIDTH, SHELVES_POPUP_HEIGHT);
            newStage.setScene(newScene);
            newStage.setResizable(false);
            newStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * method that is triggered when a button is clicked ("Print Common Objectives" button).
     * It creates a new stage (window) and loads the contents of a FXML file called "CommonObj.fxml" into it.
     * It then sets the scene of the new stage to the loaded FXML file and displays the stage to the user.
     * If there is an error loading the FXML file, it throws a runtime exception.
     *
     * @param actionEvent the event that triggered the method
     * @autor: Ludovico
     */
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

    /**
     * This method is responsible for printing the end of the program.
     * It interrupts the server thread,
     * loads the End.fxml file using FXMLLoader,
     * sets the new scene to the stage, and displays the stage.
     * If an IOException occurs, it throws a RuntimeException.
     *
     * @autor: Ludovico
     */
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

    /**
     * method that takes a `String` parameter.
     * The method uses `Platform.runLater` to update the `messageLabel` with the new text value.
     * `Platform.runLater` is used to ensure that the update is executed on the JavaFX application thread,
     * which is necessary for updating UI components.
     *
     * @param text
     * @autor: Ludovico
     */
    private void changeLabel(String text){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                messageLabel.setText(text);
            }
        });
    }


    /**
     * method that handles events received from the server.
     * It first checks if there is an event available, and if not, it returns.
     * If there is an event, it switches on the type of the event and performs the appropriate action.
     *
     * @autor: Ludovico
     */
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
                for (Cockade commonObjective : update.completedObjectives()) {
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
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        printEnd();
                    }
                });
            }
            case Join -> {
                String joinedPlayer = (String)event.get().getData();
                try {
                    lobby.addPlayer(joinedPlayer);
                } catch (Exception e) {  // Cannot happen
                    throw new RuntimeException("Added already existing player to lobby");
                }
                System.out.println("[*] " + joinedPlayer + " joined the lobby");
                changeLabel(joinedPlayer + " joined the lobby");
            }
            case Leave -> {
                String leftPlayer = (String)event.get().getData();
                try {
                    lobby.removePlayer(leftPlayer);
                    changeLabel(leftPlayer + " left the lobby");

                } catch (Exception e) {  // Cannot happen
                    throw new RuntimeException("Removed non existing player from lobby");
                }
                System.out.format("[*] %s left the %s%n", leftPlayer, state == ClientStatus.InLobby ? "lobby" : "game");
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

    /**
     *  method that returns an instance of the `GameData` class.
     *  The `static` keyword means that the method can be called without creating an instance of the class.
     *  The `GameData` object being returned is likely a singleton instance that holds data related to the game being played.
     *
     *  @return GameData
     *  @autor: Ludovico
     */
    public static GameData getGameData(){
        return gameData;
    }

}