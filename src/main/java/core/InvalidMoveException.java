package core;

public class InvalidMoveException extends Exception{
    public InvalidMoveException(String comment) {
        super(comment);
    }
}
