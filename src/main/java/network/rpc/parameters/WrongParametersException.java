package network.rpc.parameters;

public class WrongParametersException extends Exception{
    public WrongParametersException(String expected, String actual, String variableName) {
        super("Expected " + expected + " but got " + actual + " for " + variableName);
    }
}
