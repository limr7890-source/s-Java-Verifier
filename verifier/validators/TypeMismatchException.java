package ex5.validators;

import ex5.code_processing.InvalidCodeException;

/**
 * Exception thrown when a type mismatch is detected in s-Java code.
 * This occurs when an attempt is made to assign a value to a variable, or pass
 * an argument to a method, where the source type is incompatible with the
 * target type.
 * *Examples of violations:
 * Assigning a String literal to an int variable.
 * Assigning a double variable to an int variable (not allowed in s-Java).
 * Passing a boolean value to a method expecting a char.
 * * @author Lihi Martziano
 */
public class TypeMismatchException extends InvalidCodeException {

    /**
     * Constructs a new TypeMismatchException with a detailed message.
     * @param message A string describing the specific type mismatch (e.g., "cannot assign double to int").
     */
    public TypeMismatchException(String message) {
        super(message);
    }
}