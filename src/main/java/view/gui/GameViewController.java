package view.gui;

import com.sun.media.jfxmedia.events.AudioSpectrumEvent;
import controller.lobby.Lobby;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.*;
import network.ClientStatus;
import network.NetworkManagerInterface;
import network.Result;
import network.ServerEvent;
import network.parameters.GameInfo;
import network.parameters.Message;
import network.parameters.Update;
import view.cli.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GameViewController implements Initializable {

    private static final int WIDTH = 1140;
    private static final int HEIGHT = 760;
    @FXML
    public TextField messageInput;
    @FXML
    public Button sendMessageButton;
    @FXML
    private ListView chat;
  /*  @FXML
    public ListView players;*/

    public static NetworkManagerInterface networkManager;
    public static ClientStatus state;
    public static Lobby lobby;
    static String username;
    private Scene scene;
    private Stage stage;
    private Thread serverThread;
    static boolean gameStarted;
    private boolean isHost;
    private boolean doPrint;
    private boolean yourTurn;
    private boolean isPaused;
    private GameViewController game;
    String me;
    int nPlayers;
    Optional<Card>[][] tableTop;
    ArrayList<String> players;
    ArrayList<Shelf> shelves;
    Shelf myShelf;
    ArrayList<String> commonObjectives;
    ArrayList<Integer> commonObjectivesPoints;
    PersonalObjective personalObjective;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        state = ClientStatus.InGame;
        networkManager = LoginController.networkManager;
        sendMessageButton.setDefaultButton(true);
        gameStarted = true;
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

    public ClientStatus handleEvent () {
        Optional<ServerEvent> event = networkManager.getEvent();
        if (event.isEmpty()) {
            throw new RuntimeException("Empty event queue");
        }
        switch (event.get().getType()) {
            case Join -> {
                String joinedPlayer = (String)event.get().getData();
                if (!joinedPlayer.equals(username)) {
                    try {
                        lobby.addPlayer(joinedPlayer);
                    } catch (Exception e) {  // Cannot happen
                        throw new RuntimeException("Added already existing player to lobby");
                    }
                    System.out.println("[*] " + joinedPlayer + " joined the lobby");
                }
            }
            case Leave -> {
                String leftPlayer = (String)event.get().getData();
                boolean wasHost;
                try {
                    wasHost = isHost;
                    lobby.removePlayer(leftPlayer);
                    isHost = lobby.isHost(username);
                } catch (Exception e) {  // Cannot happen
                    throw new RuntimeException("Removed non existing player from lobby");
                }
                System.out.format("[*] %s left the %s%n", leftPlayer, state == ClientStatus.InLobby ? "lobby" : "game");
                if (!wasHost && isHost) {
                    System.out.println("[*] You are now the host");
                    doPrint = true;
                }
            }
            case Start -> {
                gameStarted = true;
                doPrint = true;
                game = new GameViewController((GameInfo)event.get().getData(), username);
                yourTurn = game.players.get(0).equals(username);
                System.out.println("[*] Game has started");
                if (yourTurn) {
                    System.out.println("[*] It's your turn");
                } else {
                    System.out.println("[*] It's " + game.players.get(0) + "'s turn");
                }
                return ClientStatus.InGame;
            }
            case Update -> {
                Update update = (Update)event.get().getData();
                for (Cockade commonObjective : update.commonObjectives()) {
                    if (update.idPlayer().equals(username)) {
                        System.out.format("[*] You completed %s getting %d points%n", commonObjective.name(), commonObjective.points());
                    } else {
                        System.out.format("[*] %s completed %s getting %d points%n", update.idPlayer(), commonObjective.name(), commonObjective.points());
                    }
                }
                game.update(update);
                doPrint = true;
                if (update.nextPlayer().equals(username)) {
                    yourTurn = true;
                    System.out.println("[*] It's your turn");
                } else {
                    yourTurn = false;
                    System.out.println("[*] It's " + update.nextPlayer() + "'s turn");
                }
            }
            case End -> {
                ScoreBoard scoreboard = (ScoreBoard)event.get().getData();
                System.out.println("[*] Game over!");
                System.out.println();
                System.out.println("Leaderboard:");
                int position = 1;
                for (Score score : scoreboard) {
                    System.out.format(" [%d] %s: %d points %n", position++, score.username(), score.score());
                }
                System.out.println();
                Utils.askString("[+] Press enter to continue");
                doPrint = true;
                return ClientStatus.InLobbySearch;
            }
            case NewMessage -> {
                Message message = (Message)event.get().getData();
                if (!message.idPlayer().equals(username)) {
                    System.out.format("[*] %s: %s%n", message.idPlayer(), message.message());
                }
            } case Pause -> {
                if (!isPaused) {
                    System.out.println("[WARNING] Someone has disconnected");
                    doPrint = true;
                }
                isPaused = true;
            } case Resume -> {
                System.out.println("Game resumed");
                isPaused = false;
                doPrint = true;
            }
            default -> throw new RuntimeException("Unhandled event");
        }
        return state;
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

        try{
            Result result = networkManager.chat(messageText).waitResult();
            if (result.isErr()) {
                System.out.println("[ERROR] " + result.getException().orElse("Cannot send message"));
                chat.getItems().add("We could not send your message, please try again later");
                chat.scrollTo(chat.getItems().size()-1);
                return;
            }
            Message message = new Message(username, messageText);
            addMessageToChat(message);
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

    }

    public void addMessageToChat(Message message){
        Calendar calendar = GregorianCalendar.getInstance();
        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        chat.getItems().add("[" + hour + ":"+minute+ "] " +message.idPlayer()+ ": " + message.message());
        chat.scrollTo(chat.getItems().size()-1);
    }

    public GameViewController(GameInfo data, String me) {
        this.me = me;
        this.players = data.players();
        this.commonObjectives = data.commonObjectives();
        this.commonObjectivesPoints = data.commonObjectivesPoints();
        this.nPlayers = this.players.size();

        String personalObjectiveName = data.personalObjective();
        ArrayList<PersonalObjective> allObjectives = PersonalObjective.generateAllPersonalObjectives();
        for (PersonalObjective objective : allObjectives) {
            if (objective.getName().equals(personalObjectiveName)) {
                this.personalObjective = objective;
                break;
            }
        }
        if (this.personalObjective == null) {
            throw new RuntimeException("Unknown personal objective found");
        }

        updateTableTop(data.tableTop());

        shelves = new ArrayList<>();
        for (int i = 0; i < nPlayers; i++) {
            shelves.add(convertShelf(data.shelves().get(i)));
            if (players.get(i).equals(me)) {
                myShelf = shelves.get(i);
            }
        }
    }

    public void update(Update update) {
        updateTableTop(update.tableTop());

        for (int i = 0; i < nPlayers; i++) {
            if (players.get(i).equals(update.idPlayer())) {
                shelves.set(i, convertShelf(update.shelf()));
                if (players.get(i).equals(me)) {
                    myShelf = shelves.get(i);
                }
                break;
            }
        }

        for (int i = 0; i < update.commonObjectives().size(); i++) {
            for (int j = 0; j < commonObjectives.size(); j++) {
                if (update.commonObjectives().get(i).name().equals(commonObjectives.get(j))) {
                    commonObjectivesPoints.set(j, update.newCommonObjectivesScores().get(i));
                    break;
                }
            }
        }
    }

    private void updateTableTop(Card[][] tableTop) {
        this.tableTop = new Optional[TableTop.SIZE][TableTop.SIZE];

        for (int y = 0;  y < TableTop.SIZE; y++) {
            for (int x = 0; x < TableTop.SIZE; x++) {
                this.tableTop[y][x] = tableTop[y][x] != null ?
                        Optional.of(tableTop[y][x]) :
                        Optional.empty();
            }
        }
    }

    private Shelf convertShelf(Card[][] shelf) {
        Optional<Card>[][] optionalShelf = new Optional[Shelf.ROWS][Shelf.COLUMNS];

        for (int y = 0;  y < Shelf.ROWS; y++) {
            for (int x = 0; x < Shelf.COLUMNS; x++) {
                optionalShelf[y][x] = shelf[y][x] != null ?
                        Optional.of(shelf[y][x]) :
                        Optional.empty();
            }
        }

        return new Shelf(optionalShelf);
    }


    private String cardToChar(Card card) {
        //TODO return the correct character for the card;
        return null;
    }

    public void printShelf(Shelf shelf) throws InvalidMoveException {
        for (int y = 0; y < Shelf.ROWS; y++) {
            for (int x = 0; x < Shelf.COLUMNS; x++) {
                if (shelf.getCard(y, x).isPresent()) {
                    cardToChar(shelf.getCard(y, x).get());
                } else {
                    System.out.print("Error");
                }
            }
        }

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Shelf.fxml"));
            Stage stageShelf = new Stage();
            scene = new Scene(root, WIDTH, HEIGHT);
            stageShelf.setResizable(false);
            stageShelf.setScene(scene);
            stageShelf.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void printAllShelves() throws InvalidMoveException {
        for (int i = 0; i < nPlayers; i++) {
            //TODO print the shelf of the player i
        }
    }

    public void printTableTop() {
       //TODO print the table top
    }

    public void printPersonalObjective() throws InvalidMoveException {
        Optional<Card>[][] shelfLikePersonalObjective = new Optional[Shelf.ROWS][Shelf.COLUMNS];

        for (int y = 0; y < Shelf.ROWS; y++) {
            for (int x = 0; x < Shelf.COLUMNS; x++) {
                shelfLikePersonalObjective[y][x] = Optional.empty();
            }
        }
        for (Cell cell : personalObjective.getCellsCheck()) {
            shelfLikePersonalObjective[cell.y()][cell.x()] = Optional.of(cell.card());
        }

       printShelf(new Shelf(shelfLikePersonalObjective));

        Optional<Cockade> cockade = personalObjective.isCompleted(myShelf);
        if (cockade.isEmpty()) {
            System.out.println("[*] You haven't completed any part of your personal objective yet");
        } else {
            System.out.format(
                    "[*] You will get %d %s for your personal objective%n",
                    cockade.get().points(),
                    cockade.get().points() == 1 ? "point" : "points"
            );
        }
    }

    public void printCommonObjectives() {
        for (int i = 0; i < CommonObjective.N_COMMON_OBJECTIVES; i++) {
            //TODO print the common objective i
        }
    }

}
