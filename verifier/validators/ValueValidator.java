package ex5.validators;
import ex5.code_processing.InvalidCodeException;
import ex5.variables.Variable;
import ex5.variables.VariableType;
import ex5.variables.VariablesTable;

/**
 * Responsible for validating the compatibility of values and variables during assignments
 * and method calls.
 * This class ensures that a given value (either a literal or another variable)
 * matches the required target type according to s-Java's type-casting rules.
 * * @author Lihi Martziano
 */
public class ValueValidator {

    // --- Constants for Error Messages ---
    private static final String TYPE_MISMATCH_ERR = "Type mismatch, cannot assign";
    private static final String UNDEFINED_VAR_ERR = "An undefined symbol was assigned: ";
    private static final String NOT_INIT_ERR = "Variable %s is not initialized";
    private static final String DETAILED_MISMATCH_ERR =
            "Type mismatch: Variable %s is %s but needs to be %s";

    /** The variables table used to look up existing variables for cross-validation. */
    private final VariablesTable variablesTable;

    /**
     * Constructs a ValueValidator with a reference to the current variables table.
     * * @param variablesTable The registry of variables used to check for existence and state.
     */
    public ValueValidator(VariablesTable variablesTable) {
        this.variablesTable = variablesTable;
    }

    /**
     * Validates if a value can be assigned to a specific type.
     * The value can be a literal (e.g., "5", "true") or a variable name.
     * * @param type The target VariableType required.
     * @param value The raw string representation of the value or variable name.
     * @return true if the value is compatible with the type.
     * @throws InvalidCodeException If there is a type mismatch or the value is invalid.
     */
    public boolean validate(VariableType type, String value) throws InvalidCodeException {
        // Attempt to identify the type of the value as a literal
        VariableType literalType = VariableType.getTypeFromString(value.trim());

        if (literalType != null) {
            // Check if the literal's type is compatible with the target type
            if (literalType.isCompatibleTo(type)) {
                return true;
            }
            throw new TypeMismatchException(TYPE_MISMATCH_ERR);
        }

        // If it's not a literal, it must be a reference to another variable
        return validateVariable(type, value);
    }

    /**
     * Validates if an existing variable can be used as a value for a target type.
     * Checks for existence, initialization state, and type compatibility.
     * * @param targetType The type required for the assignment or parameter.
     * @param varName The name of the source variable being referenced.
     * @return true if the variable exists, is initialized, and has a compatible type.
     * @throws InvalidCodeException If the variable is undefined, uninitialized, or type-incompatible.
     */
    private boolean validateVariable(VariableType targetType, String varName) throws InvalidCodeException {
        // Look up the variable in the current scopes
        Variable sourceVar = variablesTable.getVariable(varName);

        if (sourceVar == null) {
            throw new InvalidCodeException(UNDEFINED_VAR_ERR + varName);
        }

        // s-Java requires variables to be initialized before they are assigned to others
        if (!sourceVar.isInitialized()) {
            throw new InvalidCodeException(String.format(NOT_INIT_ERR, varName));
        }

        // Check if the source variable's type can be assigned to the target type
        if (!sourceVar.getType().isCompatibleTo(targetType)) {
            throw new TypeMismatchException(String.format(DETAILED_MISMATCH_ERR,
                    varName, sourceVar.getType(), targetType));
        }

        return true;
    }
}

