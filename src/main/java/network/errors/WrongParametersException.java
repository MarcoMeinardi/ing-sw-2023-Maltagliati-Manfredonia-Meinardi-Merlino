package network.errors;


/**
 * Exception thrown when the parameters of a method are not the expected ones
 */
public class WrongParametersException extends Exception{

    /**
     * WrongParametersException is used to throw an exception when the parameters of a method are wrong.
     * It contains the expected value, the actual value and the name of the variable.
     * @param expected expected value
     * @param actual actual value
     * @param variableName name of the variable that has the wrong value
     * @author Lorenzo
     */
    public WrongParametersException(String expected, String actual, String variableName) {
        super("Expected " + expected + " but got " + actual + " for " + variableName);
    }
}
