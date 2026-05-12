package ex5.variables;

import ex5.code_processing.InvalidCodeException;

/**
 * Exception thrown when a semantic violation related to variable management is detected.
 * This exception covers several s-Java rule violations, including:
 * Attempting to use or assign an undefined variable.
 * Redeclaring a variable name within the same local scope.
 * Attempting to reassign a value to a 'final' variable.
 * Accessing a variable that has not been initialized.
 * * @author Lihi Martziano
 */
public class VariableException extends InvalidCodeException {

    /**
     * Constructs a new VariableException with a descriptive error message.
     * * @param message The specific error message detailing the variable violation.
     */
    public VariableException(String message) {
        super(message);
    }
}