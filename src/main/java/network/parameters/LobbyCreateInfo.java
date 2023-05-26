package network.parameters;

import java.io.Serializable;

/**
 * LobbyCreateInfo class is used to send the lobby information from the client to the server to create a new lobby.
 * @param name name of the lobby to create
 * @author Lorenzo
 */
public record LobbyCreateInfo(String name) implements Serializable {};
