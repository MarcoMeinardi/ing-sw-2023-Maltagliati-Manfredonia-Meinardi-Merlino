package network.socket.protoGabibbo;

public class Ack extends Message {
    private boolean outcome;
    private String message;

    public Ack(boolean outcome, String message) {
        this.outcome = outcome;
        this.message = message;
    }

    public boolean getOutcome() {
        return this.outcome;
    }

    public String getMessage() {
        return this.message;
    }
}
