package network.rpc.parameters;

import java.io.Serializable;

public record Login(String username) implements Serializable {
}
