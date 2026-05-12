package ex5.methods;

import ex5.code_processing.InvalidCodeException;

/**
 * Exception thrown when a semantic or structural error related to methods is detected.
 * This includes errors such as:
 * Duplicate method declarations.
 * Invalid method call signatures (wrong number or type of arguments).
 * Illegal method names or return types.
 * Attempting to define a method inside another scope.
 * * @author Lihi Martziano
 */
public class MethodException extends InvalidCodeException {

    /**
     * Constructs a new MethodException with the specified detail message.
     * * @param message The specific error message describing the method-related violation.
     */
    public MethodException(String message) {
        super(message);
    }
}