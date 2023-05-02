package network.rpc.parameters;
import java.io.Serializable;
public record Message(String idPlayer, String message) implements Serializable { }


