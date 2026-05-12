package ex5.methods;

import ex5.code_processing.InvalidCodeException;
import ex5.validators.MethodDecValidator;
import ex5.validators.NameValidator;
import ex5.variables.VariableType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Acts as a registry for all methods defined in the s-Java source file.
 * This class stores method signatures (name and parameters) during the first pass
 * and provides them for validation during the second pass.
 * * It ensures that method names are unique and that parameter lists are valid.
 * * @author Lihi Martziano
 */
public class MethodsTable {

    /** Error message for malformed method headers. */
    private static final String INVALID_FORMAT_ERR = "Invalid method declaration format: '";

    /** Error message for re-declaration of an existing method name. */
    private static final String ALREADY_DEFINED_ERR = "Error: Method '";

    /** Error message for illegal method identifiers. */
    private static final String INVALID_NAME_ERR = "Error: '";

    /** Error message for invalid parameter syntax in method signature. */
    private static final String INVALID_PARAM_ERR = "Invalid parameter definition '";

    /** Error message for duplicate parameter names within the same method signature. */
    private static final String DUPLICATE_PARAM_ERR = "Error: Duplicate parameter name '";

    /** Internal storage mapping method names to their list of parameters. */
    private final HashMap<String, ArrayList<MethodParam>> methods;

    /** Regex for individual method parameter: optional final, type, and name. */
    private static final String METHOD_PARAM_REGEX = "(final\\s+)?(int|String|boolean|char|double) (\\w+)";

    /** Compiled pattern for parsing individual parameters. */
    private static final Pattern METHOD_PARAM_NAME_PATTERN = Pattern.compile(METHOD_PARAM_REGEX);

    /**
     * Constructs an empty MethodsTable.
     */
    public MethodsTable() {
        methods = new HashMap<>();
    }

    /**
     * Retrieves the parameter list for a specific method name.
     * * @param methodName The name of the method to look up.
     * @return An ArrayList of MethodParam objects, or null if the method is not defined.
     */
    public ArrayList<MethodParam> getMethodParams(String methodName) {
        if (methods.containsKey(methodName)) {
            return methods.get(methodName);
        }
        return null;
    }

    /**
     * Parses a method declaration line and registers it in the table.
     * * @param methodDec The raw method declaration line (e.g., "void foo(int a) {").
     * @throws InvalidCodeException If the format is invalid, method exists, or parameters are illegal.
     */
    public void addMethod(String methodDec) throws InvalidCodeException {
        Matcher matcher = MethodDecValidator.methodDecMatcher(methodDec);
        if (!matcher.matches()) {
            throw new MethodException(INVALID_FORMAT_ERR +
                    methodDec + "'. Check return type and parentheses.");
        }

        String methodName = matcher.group(1);
        // s-Java does not support method overloading
        if (methods.containsKey(methodName)) {
            throw new MethodException(ALREADY_DEFINED_ERR +
                    methodName + "' is already defined. Method overloading is not supported.");
        }

        validateMethodName(methodName);

        String parameters = matcher.group(2).trim();
        ArrayList<MethodParam> methodParam = getParamArray(parameters, methodName);
        methods.put(methodName, methodParam);
    }

    /**
     * Validates that the method name follows s-Java identifier rules.
     * * @param methodName The identifier to validate.
     * @throws InvalidCodeException If the name is illegal (e.g., starts with a digit).
     */
    private void validateMethodName(String methodName) throws InvalidCodeException {
        if (NameValidator.methodNameValid(methodName) == null) {
            throw new MethodException(INVALID_NAME_ERR +
                    methodName + "' is not a legal method name. It must start with a letter.");
        }
    }

    /**
     * Parses the parameter string and converts it into a list of MethodParam objects.
     * * @param parameters The comma-separated parameter string.
     * @param methodName The name of the method (used for detailed error reporting).
     * @return An ArrayList containing the parsed parameters.
     * @throws InvalidCodeException If any parameter is malformed or duplicate names are used.
     */
    private ArrayList<MethodParam> getParamArray(String parameters,
                                                 String methodName) throws InvalidCodeException {
        ArrayList<MethodParam> methodParam = new ArrayList<>();
        HashSet<String> methodParamNames = new HashSet<>();

        if (!parameters.isEmpty()) {
            String[] parametersList = parameters.split(",");
            for (String parameter : parametersList) {
                parameter = parameter.trim();
                Matcher matcher = METHOD_PARAM_NAME_PATTERN.matcher(parameter);

                if (!matcher.matches()) {
                    throw new MethodException(INVALID_PARAM_ERR + parameter +
                            "' in method '" + methodName + "'.");
                }

                boolean isFinal = matcher.group(1) != null;
                VariableType type = VariableType.fromString(matcher.group(2));
                String name = NameValidator.varNameValid(matcher.group(3));

                // Duplicate parameter names are prohibited in s-Java
                if (name == null || methodParamNames.contains(name)) {
                    throw new MethodException(DUPLICATE_PARAM_ERR +
                            name + "' in method '" + methodName + "'.");
                }

                methodParamNames.add(name);
                methodParam.add(new MethodParam(name, type, isFinal));
            }
        }
        return methodParam;
    }

    /**
     * Returns the internal map of all registered methods.
     * * @return A HashMap where keys are method names and values are parameter lists.
     */
    public HashMap<String, ArrayList<MethodParam>> getMethods() {
        return methods;
    }
}