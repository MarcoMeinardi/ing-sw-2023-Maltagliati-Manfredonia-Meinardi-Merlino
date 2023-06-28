package view.gui;

import controller.IdentityTheftException;
import controller.MessageTooLongException;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import model.Card;
import network.NetworkManagerInterface;
import network.Result;
import network.Server;
import network.errors.ClientNotFoundException;
import network.errors.WrongParametersException;
import network.parameters.Message;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

public class Utils {
    public static final int CHAT_HEIGHT = 5;

    private static final Logger logger = Logger.getLogger(Utils.class.getName());

    /**
     * Method that associates the card passed as parameter to it's corresponding image.
     *
     * @param card the given card
     * @return the path of the corresponding image
     */
    public static String cardToImageName(Card card) {
        switch (card.getType()) {
            case Cat -> { return String.format("/img/item tiles/Gatti1.%d.png", card.getImageIndex()); }
            case Book -> { return String.format("/img/item tiles/Libri1.%d.png", card.getImageIndex()); }
            case Frame -> { return String.format("/img/item tiles/Cornici1.%d.png", card.getImageIndex()); }
            case Game -> { return String.format("/img/item tiles/Giochi1.%d.png", card.getImageIndex()); }
            case Plant -> { return String.format("/img/item tiles/Piante1.%d.png", card.getImageIndex()); }
            case Trophy -> { return String.format("/img/item tiles/Trofei1.%d.png", card.getImageIndex()); }
            default -> throw new RuntimeException("Invalid card type");
        }
    }

    /**
     * Method that takes a `String` and a `Label` parameters.
     * The method uses `Platform.runLater` to update the label with the new text value.
     * `Platform.runLater` is used to ensure that the update is executed on the JavaFX application thread,
     * which is necessary for updating UI components.
     *
     * @param label the `Label` JavaFX component to change
     * @param text the new text to display on the label
     * @author Ludovico
     */
    public static void changeLabel(Label label, String text) {
        Platform.runLater(() -> label.setText(text));
    }

    /**
     * Method called to add a message to the chat.
     * It automatically adds to the message the username of the sender,
     * and the hour and minute the message was sent.
     * Scroll the chat to the bottom to show the last message.
     *
     * @param username the sender name
     * @param message the message to add to the chat
     * @param chat the `ListView` JavaFX object to which the message has to be added
     * @author Ludovico
     */
    public static void addMessageToChat(String username, Message message, ListView chat) {
        Calendar calendar = GregorianCalendar.getInstance();
        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        if (minute.length() == 1) {
            minute = "0" + minute;
        }

        if (message.idSender().equals(Server.SERVER_NAME)) {
            chat.getItems().add(String.format("[%s:%s] %s ", hour, minute, "From server: " + message.message()));
            if(chat.getItems().size() != CHAT_HEIGHT) {
                chat.scrollTo(chat.getItems().size() - 1);
            }
            return;
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
        if (chat.getItems().size() != CHAT_HEIGHT) {
            chat.scrollTo(chat.getItems().size() - 1);
        }
    }

    /**
     * Method called to send a message.
     * Check if the message is valid and if it is not, return after adding the
     * error to the chat, visible only by the sender and not the others in the lobby (length check,
     * empty message check, trying to use commands).
     * Scroll the chat to the bottom.
     * If the message is valid, send it to the server and add it to the chat calling the
     * addMessageToChat() method.
     * It is called when the "send" button is clicked.
     *
     * @param username the sender username
     * @param networkManager the networkManager to use to send the message event
     * @param messageInput the `TextField` JavaFX object from which to take the message
     * @param chat the `ListView` JavaFX object to which the message has to be added
     * @param players the `ListView` JavaFX object to control if the message has to be sent to a single person
     * @param messageLabel the `Label` JavaFX object to display any "chat-fatal" error
     *
     * @author Ludovico
     */
    public static void sendMessage(String username, NetworkManagerInterface networkManager, TextField messageInput, ListView chat, ListView players, Label messageLabel) {
        String messageText = messageInput.getText();
        messageInput.clear();

        //check message integrity and return if not valid
        if (messageText.isEmpty()) {
            chat.getItems().add("[ERROR] Empty message");
            if (chat.getItems().size() != CHAT_HEIGHT) {
                chat.scrollTo(chat.getItems().size()-1);
            }
            return;
        }
        if(messageText.startsWith("/help")){
            chat.getItems().add("[-Select a name from the list above to send a private message]");
            chat.getItems().add("[-Specific commands supported:]");
            chat.getItems().add("[/help: shows this message]");
            if (chat.getItems().size() != CHAT_HEIGHT) {
                chat.scrollTo(chat.getItems().size()-1);
            }
            return;
        }

        // Try to send it to the server and add it to chat
        try {
            boolean singleReceiver = players.getSelectionModel().getSelectedItem() != null && !players.getSelectionModel().getSelectedItem().toString().equals("");
            Result result;
            if (singleReceiver) {
                result = networkManager.chat(new Message(username, messageText, players.getSelectionModel().getSelectedItem().toString())).waitResult();
            } else {
                result = networkManager.chat(new Message(username, messageText)).waitResult();
            }
            if (result.isErr()) {
                Exception e = (Exception)result.getException().get();
                if (e instanceof WrongParametersException) {
                    chat.getItems().add("[ERROR] " + e.getMessage());
                } else if (e instanceof MessageTooLongException) {
                    chat.getItems().add("[ERROR] Message is too long");
                } else if (e instanceof IdentityTheftException) {
                    chat.getItems().add("[ERROR] Identity theft is not a joke, Bob!");
                } else if (e instanceof ClientNotFoundException) {
                    chat.getItems().add("[ERROR] The player has disconnected");
                } else {
                    chat.getItems().add("[ERROR] Send message failed" + (e.getMessage() != null ? " " + e.getMessage() : ""));
                }
                logger.info(result.getException().isPresent() ? result.getException().get().toString() : "Send message failed");
                if (chat.getItems().size() != CHAT_HEIGHT) {
                    chat.scrollTo(chat.getItems().size() - 1);
                }
            } else {
                Message message = new Message(username, messageText, players.getSelectionModel().getSelectedItem().toString());
                if (singleReceiver) {
                    players.getSelectionModel().clearSelection();
                }
                addMessageToChat(username, message, chat);
            }
        } catch (Exception e) {
            logger.warning("Send message failed " + e + " " + e.getMessage());
            messageLabel.setText("Couldn't send the message");
        }
    }
}
