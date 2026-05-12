package ex5.main;
import ex5.code_analyzing.Analyzer;
import ex5.code_processing.InvalidCodeException;

import java.io.File;
import java.io.IOException;

/**
 * The entry point of the s-Java static analyzer application.
 * This class implements the Facade pattern by interacting solely with the Analyzer
 * class to hide the complexity of the compilation process.
 * It manages the top-level execution flow and ensures that the program outputs
 * the mandatory status codes: 0 for valid code, 1 for invalid code,
 * and 2 for IO or usage errors.
 * * @author Lihi Martziano
 */
public class Sjavac {
    /** The mandatory file extension for s-Java source files. */
    private static final String FILE_SUFFIX = ".sjava";

    /** Error message for when the file doesn't exist or is impossible to open*/
    private static final String FILE_NOT_AVAIABLE="Error: File not found or invalid path.";

    /** Error message for files missing the required .sjava suffix. */
    private static final String FILE_FORMAT_MSG = "Error: File must have .sjava suffix.";

    private static final String ARGUMENTS_ERR_MSG ="Illegal number of arguments";
    /**
     * The main method that initiates the s-Java analysis process.
     * It receives the file path from the command line, triggers the analyzer,
     * and catches all exceptions to print the appropriate exit status.
     * * @param args Command-line arguments. Expects exactly one argument: the path to the .sjava file.
     */
    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                throw new IOException(ARGUMENTS_ERR_MSG);
            }

            File file = new File(args[0]);

            if (!file.exists() || !file.isFile()) {
                throw new BadFileFormatException(FILE_NOT_AVAIABLE);
            }

            if (!args[0].endsWith(FILE_SUFFIX)) {
                throw new BadFileFormatException(FILE_FORMAT_MSG);
            }
            Analyzer analyzer = new Analyzer(args[0]);
            analyzer.analyze();
            System.out.println(0);

        } catch (IOException e) {
            System.out.println(2);
            System.err.println(e.getMessage());

        } catch (InvalidCodeException e) {
            System.out.println(1);
            System.err.println(e.getMessage());

        } catch (Exception e) {
            System.out.println(2);
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
