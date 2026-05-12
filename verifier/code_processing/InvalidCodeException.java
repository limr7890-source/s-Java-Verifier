package ex5.code_processing;

/**
 * The base exception class for all semantic and structural errors identified
 * during the s-Java code analysis process.
 * This exception is caught by the main application to return the status code '1'
 * as required by the project specifications.
 * * @author Lihi Martziano
 */
public class InvalidCodeException extends Exception {

    /**
     * Constructs a new InvalidCodeException with the specified detail message.
     * * @param message The specific error message describing the violation of s-Java rules.
     */
    public InvalidCodeException(String message) {
        super(message);
    }
}