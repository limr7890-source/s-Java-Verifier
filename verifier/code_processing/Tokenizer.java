package ex5.code_processing;

import ex5.validators.BasicSyntaxValidator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Tokenizer class is responsible for the initial processing of the s-Java source file.
 * It reads the file, filters out comments and empty lines, and normalizes the code
 * into a list of processable strings (tokens).
 * * @author Lihi Martziano
 */
public class Tokenizer {

    /** A list of processed code lines (tokens) ready for analysis. */
    private final List<String> tokens = new ArrayList<>();

    /** Regex used to normalize multiple whitespace characters into a single space. */
    private static final String WHITE_SPACE_REGEX = "\\s+";

    /** * Regex used to identify and temporarily mask string literals during
     * basic syntax validation.
     */
    private static final String STRING_REGEX = "\"[^\"]*\"";


    /**
     * Constructs a Tokenizer and processes the specified file.
     * It validates the file extension, reads the content line by line,
     * removes comments, and performs basic syntax checks.
     * * @param fileName The path to the s-Java file to be tokenized.
     * @throws IOException If an I/O error occurs while reading the file.
     * @throws InvalidCodeException If a line fails basic structural validation.
     */
    public Tokenizer(String fileName) throws IOException, InvalidCodeException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines and single-line comments
                if (line.trim().isEmpty() || line.startsWith("//")) {
                    continue;
                }

                line = line.trim();

                // Mask strings to prevent content within quotes from interfering with syntax checks
                String maskedLine = line.replaceAll(STRING_REGEX, "#");

                // Validate basic line structure before adding to token list
                if (BasicSyntaxValidator.validateLineStructure(maskedLine)) {
                    // Normalize whitespace for easier regex matching later
                    tokens.add(line.replaceAll(WHITE_SPACE_REGEX, " "));
                }
            }
        }
    }

    /**
     * Returns the list of processed tokens extracted from the source file.
     * * @return A List of strings, where each string is a normalized line of s-Java code.
     */
    public List<String> getTokens() {
        return tokens;
    }
}