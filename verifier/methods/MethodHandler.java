package ex5.methods;

import ex5.variables.VariableType;
import ex5.variables.VariablesTable;
import ex5.code_processing.InvalidCodeException;
import ex5.validators.MethodDecValidator;
import ex5.validators.ValueValidator;
import ex5.variables.Variable;
import java.util.ArrayList;
import java.util.regex.Matcher;

/**
 * Handles the semantic validation and processing of method-related operations.
 * This includes verifying that method calls match their signatures and managing
 * the transition of formal parameters into the method's local scope.
 * * @author Lihi Martziano
 */
public class MethodHandler {

    /** Error message for calling a method that has not been defined. */
    private static final String UNDEFINED_METHOD_ERR = "Error: Method '";

    /** Error message for providing the wrong number of arguments in a method call. */
    private static final String PARAM_COUNT_ERR = "Error: Invalid number of parameters for ";

    /** Base error message for type mismatches in method arguments. */
    private static final String INCOMPATIBLE_PARAM_ERR = "Error in method call '";

    /** Error message for duplicate parameter names within a method signature. */
    private static final String DUPLICATE_PARAM_ERR = "Error in method '";

    /** Error message for malformed method declaration syntax. */
    private static final String INVALID_DEC_ERR = "Invalid method declaration structure: '";

    /** Validator used to check if argument values match expected parameter types. */
    private final ValueValidator valueValidator;

    /** Table managing the current scopes and variables. */
    private final VariablesTable variablesTable;

    /** Registry containing all valid method signatures collected in the first pass. */
    private final MethodsTable methodsTable;

    /**
     * Constructs a MethodHandler with necessary dependencies for validation.
     * * @param valueValidator The validator for checking type compatibility.
     * @param variablesTable The manager for variable scopes.
     * @param methodsTable The registry of defined methods.
     */
    public MethodHandler(ValueValidator valueValidator,
                         VariablesTable variablesTable,
                         MethodsTable methodsTable) {
        this.valueValidator = valueValidator;
        this.variablesTable = variablesTable;
        this.methodsTable = methodsTable;
    }

    /**
     * Validates a method call by checking the method's existence and argument compatibility.
     * * @param funcName The name of the method being invoked.
     * @param parameters The raw string containing the arguments passed in the call.
     * @throws InvalidCodeException If the method is undefined, the parameter count is wrong,
     * or an argument type is incompatible.
     */
    public void handleMethodCall(String funcName, String parameters) throws InvalidCodeException {
        // Retrieve the expected parameters for this method
        ArrayList<MethodParam> params = methodsTable.getMethodParams(funcName);
        if (params == null) {
            throw new MethodException(UNDEFINED_METHOD_ERR + funcName + "' is not defined.");
        }

        // Handle the case where both signature and call have no parameters
        if (params.isEmpty() && parameters.isEmpty()) {
            return;
        }

        // Split arguments by comma and verify the count matches the signature
        String[] parts = parameters.split(",");
        if (parts.length != params.size()) {
            throw new MethodException(PARAM_COUNT_ERR + funcName);
        }

        // Validate each provided argument against the expected type in the signature
        for (int i = 0; i < params.size(); i++) {
            String callParam = parts[i].trim();
            VariableType expectedType = params.get(i).getType();

            try {
                valueValidator.validate(expectedType, callParam);
            } catch (InvalidCodeException e) {
                throw new MethodException(INCOMPATIBLE_PARAM_ERR + funcName + "': Parameter " + (i + 1) +
                        " (" + callParam + ") is incompatible with expected type " + expectedType + ".");
            }
        }
    }

    /**
     * Loads the formal parameters of a method into its new local scope.
     * This is called during the second pass when entering a method block.
     * * @param line The raw method declaration line.
     * @throws InvalidCodeException If the declaration is malformed or contains duplicate parameters.
     */
    public void loadMethodParamsToScope(String line) throws InvalidCodeException {
        Matcher matcher = MethodDecValidator.methodDecMatcher(line);
        if (matcher.matches()) {
            String methodName = matcher.group(1);
            ArrayList<MethodParam> params = methodsTable.getMethods().get(methodName);

            if (params != null) {
                for (MethodParam param : params) {
                    // Create a new local variable for each parameter
                    Variable variable = new Variable(param.getType(), param.isFinal());
                    // Parameters are considered initialized by the caller
                    variable.updateInitializationState();

                    try {
                        // Add parameter to the current (local) scope
                        variablesTable.addVariable(variable, param.getName(), false);
                    } catch (InvalidCodeException e) {
                        throw new MethodException(DUPLICATE_PARAM_ERR +
                                methodName + "': Duplicate parameter name '" +
                                param.getName() + "'.");
                    }
                }
            }
        } else {
            throw new MethodException(INVALID_DEC_ERR + line + "'.");
        }
    }
}