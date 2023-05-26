package network.parameters;
import java.io.Serializable;
import java.util.Optional;

public class Message implements Serializable {
    private String idSender;
    private String message;
    private String idReceiver;

    public Message(String idSender, String message, String idReceiver) {
        this.idSender = idSender;
        this.message = message;
        this.idReceiver = idReceiver;
    }

    public Message(String idSender, String message) {
        this.idSender = idSender;
        this.message = message;
        this.idReceiver = null;
    }

    public String idSender() {
        return idSender;
    }

    public String message() {
        return message;
    }
    public Optional<String> idReceiver() {
        return Optional.ofNullable(idReceiver);
    }
}


