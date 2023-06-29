package model;

/**
 * Exception thrown when a player attempts to make an invalid move
 */
public class InvalidMoveException extends Exception {
    public InvalidMoveException(String comment) {
        super(comment);
    }
}
