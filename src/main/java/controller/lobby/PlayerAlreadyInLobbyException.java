package controller.lobby;

/**
 * Exception thrown when someone tries to join a lobby where he is already in
 * If this exception is thrown, it is an indicator of a bug.
 */
public class PlayerAlreadyInLobbyException extends Exception{
}
