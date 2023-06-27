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
import javafx.scene.control.*;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.*;
import network.*;
import network.parameters.CardSelect;
import network.parameters.Message;
import network.parameters.Update;

import java.io.IOException;
import java.net.URL;

import java.util.*;

/**
 * Class that handles a new instance of game.
 *
 */

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
    private Label sureLabel;
    @FXML
    private RadioButton yesSureButton;
    @FXML
    private RadioButton noSureButton;
    @FXML
    private Button sureChoiceButton;
    @FXML
    public Button sendMessageButton;
    @FXML
    private Button printAllShelvesButton;
    @FXML
    private Button printCommonObjectivesButton;
    @FXML
    private Button printPersonalObjectivesButton;
    @FXML
    private Button endGame;
    @FXML
    private TextField messageInput;
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
    private boolean isPaused = false;

    /**
     * Method that initializes the game scene.
     * Checks if the player is reconnecting from a disconnection or if it's a new game, then retrieves the game data
     * accordingly.
     * Checks if it's the player's turn and sets the message label accordingly.
     * Updates the GUI adding the list of the players, the chat and the game board.
     * It then starts the server thread that will handle the events received from the server.
     *
     * @author Ludovico
     *
    **/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (LobbyViewController.gameInfo == null) {
            gameData = new GameData(LoginController.gameInfo, LoginController.username);
            username = LoginController.username;
            networkManager = LoginController.networkManager;
            lobby = LoginController.gameInfo.lobby();
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
        fillShelf(gameData.getMyShelf());
        Platform.runLater(() -> {
            sendMessageButton.setDefaultButton(true);
            if(lobby.isHost(username)){
                endGame.setVisible(true);
            }
            else{
                endGame.setVisible(false);
            }
            yesSureButton.setVisible(false);
            noSureButton.setVisible(false);
            sureChoiceButton.setVisible(false);
            sureLabel.setVisible(false);
        });
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
     * This method fills the game scene with images of items based on the contents of the `table` array.
     * The `putImageOnScene` method is then called to add the image to the scene at the appropriate location.
     * The method also calls `removeImages` to remove any existing images on the table before filling it with new images.
     *
     * @param table The table of optional cards representing the scene.
     * @author Ludovico
     */
    private void fillScene(Optional<Card>[][] table) {
        imageToIndices.clear();
        removeImages(false);

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (table[y][x].isPresent()) {
                    String imageName = Utils.cardToImageName(table[y][x].get());
                    putImageOnScene(imageName, y, x, cardSize, cardSize, cardOffSet, cardOffSet, cardStep, cardStep, false);
                }
            }
        }
    }

    /**
     * Method that takes a `Shelf` object as input and fills the shelf with images of items based on the cards present on the shelf.
     * The `putImageOnScene` method is then called to place the image on the scene.
     * The method also calls `removeImages` to remove any existing images on the shelf before filling it with new images.
     *
     * @param shelf to be filled with images
     * @author Ludovico
     */

    private void fillShelf(Shelf shelf) {
        Optional<Card>[][] shelfCards = shelf.getShelf();

        removeImages(true);

        for (int y = 0; y < shelfRows; y++){
            for(int x = 0; x < shelfColumns; x++){
                String imageName;
                if(shelfCards[y][x].isPresent()){
                    imageName = Utils.cardToImageName(shelfCards[y][x].get());
                    putImageOnScene(imageName, y, x,  shelfCardSize, shelfCardSize, shelfOffSetX, shelfOffSetY, shelfCardStepX, shelfCardStepY, true);
                }
            }
        }

    }

    /**
     * This method adds an image to a pane.
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
     * @author Ludovico
     */
    private void putImageOnScene(String imageName, int y, int x, int height, int width, int offsetX, int offsetY, int stepX, int stepY, boolean isShelf){
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
            imageView.setOnMouseClicked(event -> handleCardSelection(imageView));
            imageToIndices.put(imageView, new int[]{y, x});
        }
    }


    /**
     * Method that removes images from a pane.
     * The method takes a boolean parameter `isShelf`
     * which determines whether to remove only images with "Shelf and Card" in their ID or all images with "Card" in their ID.
     * The method loops through all the children of the pane and adds the appropriate images to a list of nodes to be removed.
     * Finally, it loops through the list and removes each node from the pane.
     *
     * @param isShelf specifies whether the images to be removed are on the shelf or not
     * @author Ludovico
     */
    private void removeImages(boolean isShelf){
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
     * Method that handles the selection of a card represented by an ImageView object.
     * It checks if the card is already selected, and if so,
     * it removes it from the list of selected images and removes the selection effect.
     * If the card is not already selected, it adds it to the list of selected images
     * and applies a yellow drop shadow effect to indicate selection.
     * If the number of selected cards exceeds 3,
     * it displays a message indicating that no more than 3 cards can be selected.
     *
     * @param image the image that has been selected
     * @author Ludovico
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
                Utils.changeLabel(messageLabel, "You can't select more than 3 cards");
            }
        }

    }

    /**
     * Method that is called when the player tries to move selected cards to a specified column.
     * It first checks if any cards are selected, if the column input is empty or null,
     * if the column input is a valid number between 1 and 5,
     * if it's the player's turn, and if the game is paused.
     * If all checks pass, it gets the indices of the selected cards
     * and sends a request to the network manager to move the cards to the specified column.
     * If there is an error, it displays an error message.
     *
     * @param actionEvent The action event triggered by the move button
     * @author Ludovico
     */
    @FXML
    private void tryMove(ActionEvent actionEvent){
        ArrayList<Point> selectedCards = new ArrayList<>();
        String column = columnInput.getText();
        String columnHelper = columnInput.getText();

        if(!column.matches("\\d+")){
            messageLabel.setText("Select a valid column!");
            return;
        }

        if(selectedImages.size() == 0){
            messageLabel.setText("Select cards!");
            return;
        }
        if(columnHelper.trim().isEmpty() || column == null) {
            messageLabel.setText("Left blank!");
            return;
        }
        if(
            column.length() > 1 ||
            Integer.parseInt(column) > 5 ||
            Integer.parseInt(column) < 1
        ) {
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
            Platform.runLater(() -> {
                try {
                    Result result = networkManager.cardSelect(new CardSelect(Integer.valueOf(column) - 1, selectedCards)).waitResult();
                    if (result.isErr()) {
                        System.out.println("[ERROR] " + result.getException().orElse("Cannot select cards"));
                        messageLabel.setText("[ERROR] " + result.getException().orElse("Cannot select cards"));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
            messageLabel.setText("Couldn't perform the move");
        }

    }

    /**
     * Method that initializes the lobby scene.
     * It adds the players to the list view and adds the messages to the chat.
     *
     * @author Ludovico
     */
    @FXML
    private void startLobby(){
		players.getItems().clear();
		players.getItems().addAll(lobby.getPlayers());
        //add messages to the chat
        chat.getItems().add("[Type /help to see the list of commands]");
    }

    /**
     * Method called to send a message.
     * It is called when the "send" button is clicked.
     * @param actionEvent the "send" button is clicked
     * @author Ludovico
     */
    @FXML
    private void sendMessage(ActionEvent actionEvent) {
        Utils.sendMessage(username, networkManager, messageInput, chat, players, messageLabel);
    }

    /**
     * method that is triggered when a button is clicked ("Print Personal Objectives" button).
     * It creates a new stage (window) and loads the contents of the FXML file called "PersonalObj.fxml" into it.
     * It then sets the scene of the new stage to the loaded FXML file and displays the stage to the user.
     *
     * @param actionEvent the event that triggered the method
     * @author Ludovico
     */
    @FXML
    private void printPersonalObjectivesButton(ActionEvent actionEvent) {
        try {
            Stage newStage = new Stage();
            Parent newRoot = FXMLLoader.load(getClass().getResource("/fxml/PersonalObj.fxml"));
            Scene newScene = new Scene(newRoot, POPUP_WIDTH, POPUP_HEIGHT);
            newStage.setScene(newScene);
            newStage.setResizable(false);
            newStage.show();
        } catch (IOException e) {
            messageLabel.setText("Couldn't load the personal objectives");
        }
    }

    /**
     * method that is triggered when a button is clicked ("Print All Shelves" button).
     * It creates a new stage (window) and loads the contents of the FXML file called "Shelves.fxml" into it.
     * It then sets the scene of the new stage to the loaded FXML file and displays the stage to the user.
     *
     * @param actionEvent the event that triggered the method
     * @author Ludovico
     */
    @FXML
    private void printAllShelvesObjectivesButton(ActionEvent actionEvent) {
        try {
            Stage newStage = new Stage();
            Parent newRoot = FXMLLoader.load(getClass().getResource("/fxml/Shelves.fxml"));
            Scene newScene;
            if(gameData.getPlayersNames().size() == 2){
                newScene = new Scene(newRoot, SHELVES_POPUP_WIDTH, SHELVES_POPUP_HEIGHT/2);
            }
            else {
                newScene = new Scene(newRoot, SHELVES_POPUP_WIDTH, SHELVES_POPUP_HEIGHT);
            }
            newStage.setScene(newScene);
            newStage.setResizable(false);
            newStage.show();
        } catch (IOException e) {
            messageLabel.setText("Couldn't load the shelves");
        }
    }

    /**
     * method that is triggered when a button is clicked ("Print Common Objectives" button).
     * It creates a new stage (window) and loads the contents of the FXML file called "CommonObj.fxml" into it.
     * It then sets the scene of the new stage to the loaded FXML file and displays the stage to the user.
     *
     * @param actionEvent the event that triggered the method
     * @author Ludovico
     */
    @FXML
    private void printCommonObjectivesButton(ActionEvent actionEvent) {
        try {
            Stage newStage = new Stage();
            Parent newRoot = FXMLLoader.load(getClass().getResource("/fxml/CommonObj.fxml"));
            Scene newScene = new Scene(newRoot, POPUP_WIDTH, POPUP_HEIGHT);
            newStage.setScene(newScene);
            newStage.setResizable(false);
            newStage.show();
        } catch (IOException e) {
            messageLabel.setText("Couldn't load the common objectives");
        }
    }

    /**
     * This method is responsible for printing the last view of the program.
     * It interrupts the server thread,
     * loads the End.fxml file using FXMLLoader,
     * sets the new scene to the stage, and displays the stage.
     *
     * @author Ludovico
     */
    private void printEnd(){
        try {
            serverThread.interrupt();
            Parent newRoot = FXMLLoader.load(getClass().getResource("/fxml/End.fxml"));
            stage = (Stage) (sendMessageButton.getScene().getWindow());
            scene = new Scene(newRoot, WIDTH, HEIGHT);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            Utils.changeLabel(messageLabel, "Couldn't load the end screen");
        }
    }

    /**
     * This method is responsible for printing the last view of the program if the host stops the game.
     * It interrupts the server thread,
     * loads the MessageStoppedGame.fxml file using FXMLLoader,
     * sets the new scene to the stage, and displays the stage.
     *
     */

    private void goToMessage(){
        try {
            serverThread.interrupt();
            Parent newRoot = FXMLLoader.load(getClass().getResource("/fxml/MessageStoppedGame.fxml"));
            stage = (Stage) (sendMessageButton.getScene().getWindow());
            scene = new Scene(newRoot, WIDTH, HEIGHT);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Couldn't load the final message screen");
        }
    }

    /**
     * This method is responsible for printing the last view of the program if the server stopped.
     * It interrupts the server thread,
     * loads the MessageReturnToLogin.fxml file using FXMLLoader,
     * sets the new scene to the stage, and displays the stage.
     *
     */

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
            messageLabel.setText("Couldn't load the final message screen");
        }
    }

    /**
     * This method is called when the host clicks the stop game button.
     * It modifies the visibility of the buttons and labels in the scene to ask the host if he is sure to stop the game.
     *
     * @param actionEvent
     */
    @FXML
    private void endTheGame(ActionEvent actionEvent) {
        sureLabel.setVisible(true);
        yesSureButton.setVisible(true);
        noSureButton.setVisible(true);
        sureChoiceButton.setVisible(true);
        printCommonObjectivesButton.setVisible(false);
        printPersonalObjectivesButton.setVisible(false);
        printAllShelvesButton.setVisible(false);
        endGame.setVisible(false);
    }

    /**
     * Show normal game buttons after the stop game confirms selection failed or has been aborted
     *
     * @author Ludovico
     */
    private void afterStopConfirmFail() {
        Platform.runLater(() -> {
            sureLabel.setVisible(false);
            yesSureButton.setVisible(false);
            noSureButton.setVisible(false);
            sureChoiceButton.setVisible(false);
            printCommonObjectivesButton.setVisible(true);
            printPersonalObjectivesButton.setVisible(true);
            printAllShelvesButton.setVisible(true);
            endGame.setVisible(true);
        });
    }

    /**
     * This method is called when the host submits their choice about stopping the game.
     * If the host decided to stop the game and everything works, the method just returns
     * waiting for the server to end the game.
     * If something went wrong or the host decided to continue the game,
     * the method changes the visibility of the buttons and labels
     * to the original state.
     *
     * @param actionEvent
     */
    @FXML
    private void submitChoice(ActionEvent actionEvent){
        if(yesSureButton.isSelected()){
            Platform.runLater(() -> {
                try {
                    Result result = networkManager.exitGame().waitResult();
                    if (result.isErr()) {
                        System.out.println("[ERROR] " + result.getException().orElse("Cannot stop the game"));
                        messageLabel.setText("Cannot stop the game");
                        afterStopConfirmFail();
                    }
                } catch (Exception e) {
                    messageLabel.setText("Cannot stop the game");
                    System.out.println("[ERROR] " + e.getMessage());
                }
            });
        } else {
            afterStopConfirmFail();
        }
    }

    /**
     * Method that handles events received from the server.
     * It first checks if there is an event available, and if not, it returns.
     * If there is an event, it switches on the type of the event and performs the appropriate action.
     *
     * - NewMessage: prints the message received from the server
     * - Update: updates the game data with the new information received from the server, then update
     *   the GUI where necessary and sets the yourTurn boolean to true if it's the turn of the player
     * - End: calls the printEnd method to redirect to the scoreboard view of the game
     * - Join and Leave: updates the list of players in the game and notifies the players
     * - Pause: pauses the game and notifies the players
     * - Resume: resumes the game and notifies the players
     * - ExitGame: notifies the players that the game has been stopped sending them to a scene explaining the situation
     * - ServerDisconnect: notifies the players that the server has been disconnected sending them to a scene explaining the situation
     *
     * @author Ludovico
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
                    Platform.runLater(() -> Utils.addMessageToChat(username, message, chat));
                }
            }
            case Update -> {
                Update update = (Update)event.get().getData();
                for (Cockade commonObjective : update.completedObjectives()) {
                    if (update.idPlayer().equals(username)) {
                        System.out.format("[*] You completed %s getting %d points%n", commonObjective.name(), commonObjective.points());
                        Platform.runLater(() -> Utils.addMessageToChat(username, new Message(Server.SERVER_NAME,"You completed " + commonObjective.name() + " getting " + commonObjective.points() + " points"), chat));
                    } else {
                        System.out.format("[*] %s completed %s getting %d points%n", update.idPlayer(), commonObjective.name(), commonObjective.points());
                        Platform.runLater(() -> Utils.addMessageToChat(username, new Message(Server.SERVER_NAME, update.idPlayer() + " completed " + commonObjective.name() + " getting " + commonObjective.points() + " points"), chat));
                    }
                }
                gameData.update(update);
                if (update.nextPlayer().equals(username)) {
                    yourTurn = true;
                    System.out.println("[*] It's your turn");
                    Utils.changeLabel(messageLabel, "It's your turn");
                } else {
                    yourTurn = false;
                    System.out.println("[*] It's " + update.nextPlayer() + "'s turn");
                    Utils.changeLabel(messageLabel, "It's " + update.nextPlayer() + "'s turn");
                }
                Platform.runLater(() -> {
                    fillScene(gameData.getTableTop());
                    fillShelf(gameData.getMyShelf());
                    selectedImages.clear();
                });
            }
            case End -> {
                ScoreBoard scoreboard = (ScoreBoard)event.get().getData();
                gameData.setScoreBoard(scoreboard);
                System.out.println("[*] Game ended");
                Platform.runLater(() -> printEnd());
            }
            case Join -> {
                String joinedPlayer = (String)event.get().getData();
                try {
                    lobby.addPlayer(joinedPlayer);
                    Platform.runLater(() -> {
                        Utils.addMessageToChat(username, new Message(Server.SERVER_NAME, joinedPlayer + " joined the lobby"), chat);
                        players.getItems().clear();
                        players.getItems().addAll(lobby.getPlayers());
                    });
                } catch (Exception e) {
                    throw new RuntimeException("Added already existing player to lobby");
                }
            }
            case Leave -> {
                String leftPlayer = (String)event.get().getData();
                try {
                    lobby.removePlayer(leftPlayer);
                    Platform.runLater(() -> {
                        Utils.addMessageToChat(username, new Message(Server.SERVER_NAME, leftPlayer + " left the lobby"), chat);
                        players.getItems().clear();
                        players.getItems().addAll(lobby.getPlayers());
                        if (lobby.isHost(username)) {
                            endGame.setVisible(true);
                        } else {
                            endGame.setVisible(false);
                        }
                    });

                } catch (Exception e) {
                    throw new RuntimeException("Removed non existing player from lobby");
                }
                System.out.format("[*] %s left the %s%n", leftPlayer, state == ClientStatus.InLobby ? "lobby" : "game");
            }
            case Pause -> {
                if (!isPaused) {
                    System.out.println("[WARNING] Someone has disconnected");
                    Utils.changeLabel(messageLabel, "Someone has disconnected");
                }
                isPaused = true;
            }
            case Resume -> {
                System.out.println("Game resumed");
                Utils.changeLabel(messageLabel, "Game resumed");
                isPaused = false;
            }
            case ExitGame -> {
                Platform.runLater(() -> goToMessage());
            }
            case ServerDisconnect -> {
                System.out.println("[WARNING] Server disconnected");
                Utils.changeLabel(messageLabel, "Server disconnected");
                Platform.runLater(() -> returnToLoginMessage());
            }
            default -> throw new RuntimeException("Unhandled event");
        }
    }

    /**
     *  Method that returns an instance of the `GameData` class.
     *  The `static` keyword means that the method can be called without creating an instance of the class.
     *  The `GameData` object being returned is a singleton instance that holds data related to the game being played.
     *
     *  @return GameData
     *  @author Ludovico
     */
    public static GameData getGameData(){
        return gameData;
    }

}
