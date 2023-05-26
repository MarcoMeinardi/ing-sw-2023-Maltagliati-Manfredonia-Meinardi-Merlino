package network.parameters;
import java.io.Serializable;
import java.util.Optional;

/**
 * Message class is used to send messages between clients.
 * It contains the id of the sender, the message and if the message is for a specific client, the id of the receiver.
 * @author Lorenzo
 */
public class Message implements Serializable {
    private String idSender;
    private String message;
    private String idReceiver;

    /**
     * Constructor of the class when the message is for a specific client.
     * @param idSender id of the sender
     * @param message message to send
     * @param idReceiver id of the receiver
     * @author Lorenzo
     */
    public Message(String idSender, String message, String idReceiver) {
        this.idSender = idSender;
        this.message = message;
        this.idReceiver = idReceiver;
    }

    /**
     * Constructor of the class when the message is for all the clients.
     * @param idSender id of the sender
     * @param message message to send
     * @author Lorenzo
     */
    public Message(String idSender, String message) {
        this.idSender = idSender;
        this.message = message;
        this.idReceiver = null;
    }

    /**
     * Getter of the id of the sender.
     * @return id of the sender
     * @author Lorenzo
     */
    public String idSender() {
        return idSender;
    }

    /**
     * Getter of the message.
     * @return message
     * @author Lorenzo
     */
    public String message() {
        return message;
    }

    /**
     * Getter of the id of the receiver.
     * @return id of the receiver if the message is for a specific client, empty optional otherwise.
     * @author Lorenzo
     */
    public Optional<String> idReceiver() {
        return Optional.ofNullable(idReceiver);
    }
}


