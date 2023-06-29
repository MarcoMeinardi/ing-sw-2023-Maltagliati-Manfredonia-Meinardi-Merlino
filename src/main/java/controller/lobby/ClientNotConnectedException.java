package controller.lobby;

/**
 * Exception thrown when a game is started with a disconnected client in lobby,
 * or when trying to send a message to a disconnected client.
 */
public class ClientNotConnectedException extends Exception {
}
