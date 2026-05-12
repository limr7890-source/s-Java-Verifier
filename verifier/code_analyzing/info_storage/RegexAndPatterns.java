package ex5.code_analyzing.info_storage;

import java.util.regex.Pattern;

/**
 * A central utility class that defines and compiles the regular expressions used
 * to parse and validate s-Java syntax.
 * This class ensures that all regex patterns are maintained in one location
 * for consistency throughout the analysis process.
 * * @author Lihi Martziano
 */
public class RegexAndPatterns {

    /** Regex for a standard return statement: "return ;". */
    public static final String RETURN_REGEX="^\\s*return\\s*;$";

    /** Compiled pattern for validating return statements. */
    public static final Pattern RETURN_PATTERN = Pattern.compile(RETURN_REGEX);

    /** Regex for method calls, capturing the method name and its arguments. */
    public static final String METHOD_CALL_REGEX="(\\w+)\\s*\\(([^)]*)\\)\\s*;";

    /** Compiled pattern for identifying and parsing method calls. */
    public static final Pattern METHOD_CALL_PATTERN = Pattern.compile(METHOD_CALL_REGEX);

    /** Regex for 'if' and 'while' block headers, capturing the condition. */
    public static final String CONDITION_REGEX="^(if|while)\\s*\\(([^)]+)\\)\\s*\\{$";

    /** Compiled pattern for identifying logical control blocks. */
    public static final Pattern CONDITION_PATTERN = Pattern.compile(CONDITION_REGEX);

    /** Regex for variable declarations, capturing optional 'final', type, and content. */
    public static final String VAR_DEC_REGEX="^(final\\s+)?(int|double|String|boolean|char)\\s+(.+)$";

    /** Compiled pattern for validating variable declaration lines. */
    public static final Pattern VAR_DEC_PAT =Pattern.compile(VAR_DEC_REGEX);

    /** Regex for individual variable assignments or declarations within a line. */
    public static final String VAR_ASSIGN_REGEX="(\\w+\\s*=\\s*(.+)|\\w+)";

    /** Compiled pattern for parsing single assignment components. */
    public static final Pattern VAR_ASSIGN_PATTERN=Pattern.compile(VAR_ASSIGN_REGEX);
}
