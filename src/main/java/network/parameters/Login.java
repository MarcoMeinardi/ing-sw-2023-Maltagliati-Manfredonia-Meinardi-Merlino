package network.parameters;

import java.io.Serializable;

/**
 * Login class is used to send the login info from the client to the server.
 * @param username username of the player that wants to log in
 * @author Lorenzo
 */
public record Login(String username) implements Serializable {
}
