package ex5.main;

import java.io.IOException;

/**
 * Exception thrown when the input file format is invalid or cannot be processed.
 * This typically refers to cases where the file does not have the mandatory '.sjava'
 * extension or when the file is empty or missing.
 * * According to project requirements, errors related to file access or IO should
 * result in a status code of '2'.
 * * @author Lihi Martziano
 */
public class BadFileFormatException extends IOException {

    /**
     * Constructs a new BadFileFormatException with the specified detail message.
     * * @param message The specific error message describing the file format issue.
     */
    public BadFileFormatException(String message) {
        super(message);
    }
}