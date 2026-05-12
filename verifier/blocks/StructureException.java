package ex5.blocks;

import ex5.code_processing.InvalidCodeException;

/**
 * Exception thrown when a structural violation is detected in the s-Java code.
 * This includes issues with block headers, unclosed braces, or illegal placement
 * of code segments (e.g., nested methods or invalid condition syntax).
 * * @author Lihi Martziano
 */
public class StructureException extends InvalidCodeException {

    /**
     * Constructs a new StructureException with the specified detail message.
     * * @param message The specific error message describing the structural violation.
     */
    public StructureException(String message) {
        super(message);
    }
}
