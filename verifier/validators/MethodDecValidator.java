package ex5.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class providing regex validation and matching for method declarations in s-Java.
 * This class isolates the structural pattern of a method header to ensure consistency
 * during both the first pass (signature collection) and the second pass (scope entry).
 * * @author Lihi Martziano
 */
public class MethodDecValidator {

    /** * Regex pattern for a valid s-Java method declaration.
     * It captures the method name (group 1) and the raw parameter string (group 2).
     * Example: void foo(int a, double b) {
     */
    private static final String METHOD_DEC_LINE="^void\\s+(\\w+)\\s*\\((.*)\\)\\s*\\{$";

    /** Compiled pattern for efficient matching of method headers. */
    private static final Pattern METHOD_DEC_PATTERN= Pattern.compile(METHOD_DEC_LINE);

    /**
     * Creates a Matcher object for a given line of code against the method declaration pattern.
     * * @param sentence The line of code to be tested.
     * @return A Matcher object that can be used to check for a match and extract capturing groups.
     */
    public static Matcher methodDecMatcher(String sentence){
        return METHOD_DEC_PATTERN.matcher(sentence);
    }
}