package ex5.validators;

import ex5.code_processing.InvalidCodeException;
import java.util.regex.Pattern;

/**
 * Provides static utility methods for basic syntax validation of s-Java code lines.
 * This validator acts as a pre-processor during tokenization to catch illegal
 * characters, incorrect line endings, and improper comments.
 * * @author Lihi Martziano
 */
public class BasicSyntaxValidator {

    /** * Regex pattern to identify illegal operators within a code line.
     * s-Java does not support arithmetic operations (*, /, %) or
     * relational comparisons (!, >, <) outside specific contexts,
     * nor does it support array syntax ([ , ]).
     */
    private static final String OPERATORS_REGEX = "\"([*%!\\[\\]><]|[+-=]{2})\"";

    /** * Pre-compiled Pattern for efficient searching of illegal operators
     * across multiple code lines.
     */
    private static final Pattern OPERATORS_PATTERN = Pattern.compile(OPERATORS_REGEX);
    /** Regex to ensure a line ends with either a semicolon or an opening curly brace. */
    private static final String END_LINE_REGEX = ".*[;{]$";

    /** Compiled pattern for validating the end of a code statement or block header. */
    private static final Pattern END_LINE_PATTERN = Pattern.compile(END_LINE_REGEX);

    /** Constant representing a standalone closing curly brace. */
    private static final String CURLY_BRACE_REGEX = "}";

    /** Error message for illegal inline or multi-line comments. */
    private static final String INLINE_COMMENT_MSG = "Found an illegal comment in code line";

    /** Error message for characters and operators not supported in s-Java (e.g., *, /, [, ]). */
    private static final String ILLEGAL_OPERATOR_MSG = "Found an illegal operator used";

    /** Error message for lines that do not terminate with a valid s-Java character. */
    private static final String ILLEGAL_LINE_END_MSG = "line was not ending with a { or ;";

    /**
     * Performs a structural validation of a single line of code.
     * It checks for illegal comments, disallowed operators, and correct termination.
     * * @param maskedLine The code line with string literals masked to avoid false positives.
     * @return true if the line structure is valid according to s-Java rules.
     * @throws InvalidCodeException If any structural violation is detected.
     */
    public static boolean validateLineStructure(String maskedLine) throws InvalidCodeException {
        // s-Java only allows comments at the start of a line, handled by the Tokenizer
        if (maskedLine.contains("//") || maskedLine.contains("/*") || maskedLine.contains("*/")) {
            throw new InvalidCodeException(INLINE_COMMENT_MSG);
        }

        // Validate that the line ends with a statement terminator or scope operator
        if (!isEndingValid(maskedLine)) {
            throw new InvalidCodeException(ILLEGAL_LINE_END_MSG);
        }

        // Check for operators and symbols not defined in the s-Java specification
        if (OPERATORS_PATTERN.matcher(maskedLine).find()) {
            throw new InvalidCodeException("Error: Illegal operator found in line.");
        }

        return true;
    }

    /**
     * Checks if the line ending conforms to s-Java requirements.
     * A valid line must end with ';', '{', or be a single '}'.
     * * @param line The line to check.
     * @return true if the ending is valid, false otherwise.
     */
    private static boolean isEndingValid(String line) {
        return END_LINE_PATTERN.matcher(line).matches() ||
                line.equals(CURLY_BRACE_REGEX);
    }
}