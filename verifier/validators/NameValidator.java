package ex5.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class responsible for validating identifier names in s-Java.
 * It ensures that variable and method names adhere to the specific naming conventions
 * defined in the s-Java language specification.
 * * @author Lihi Martziano
 */
public class NameValidator {

    /** * Regex for a valid s-Java variable name.
     * Rules: Must start with a letter or an underscore followed by at least one character.
     */
    private static final String VAR_NAME_REGEX="^((_[a-zA-Z0-9]+\\w*)|([a-zA-Z]\\w*))$";

    /** Compiled pattern for variable name validation. */
    private static final Pattern VAR_NAME_PATTERN=Pattern.compile(VAR_NAME_REGEX);

    /** * Regex for a valid s-Java method name.
     * Rules: Must start with a letter and can be followed by letters, digits, or underscores.
     */
    private static final String METHOD_NAME_REGEX="^([a-zA-Z]+\\w*)$";

    /** Compiled pattern for method name validation. */
    private static final Pattern METHOD_NAME_PATTERN = Pattern.compile(METHOD_NAME_REGEX);

    /**
     * Validates if a given string is a legal variable name in s-Java.
     * * @param name The string to validate.
     * @return The original name if valid, or null if it violates naming rules.
     */
    public static String varNameValid(String name) {
        Matcher matcher=VAR_NAME_PATTERN.matcher(name);
        if( matcher.matches())
            return name;
        return null;
    }

    /**
     * Validates if a given string is a legal method name in s-Java.
     * * @param name The string to validate.
     * @return The original name if valid, or null if it violates naming rules.
     */
    public static String methodNameValid(String name) {
        Matcher matcher=METHOD_NAME_PATTERN.matcher(name);
        if( matcher.matches())
            return name;
        return null;
    }
}