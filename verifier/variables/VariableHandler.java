package ex5.variables;

import ex5.code_analyzing.info_storage.RegexAndPatterns;
import ex5.code_processing.InvalidCodeException;
import ex5.validators.NameValidator;
import ex5.validators.ValueValidator;
import java.util.regex.Matcher;

/**
 * Responsible for parsing and processing variable declarations and assignments.
 * It ensures that variable operations follow s-Java rules, such as finality constraints,
 * type compatibility, and valid identifier naming.
 * * @author Lihi Martziano
 */
public class VariableHandler {

    // --- Error Message Constants ---
    private static final String ASSIGNMENT_STRUCTURE_ERR = "Invalid assignment structure in line: ";
    private static final String NOT_DEFINED_ERR = "Error: Variable '";
    private static final String FINAL_ASSIGN_ERR = "Error: Cannot assign a new value to final variable '";
    private static final String INVALID_DEC_FORMAT_ERR = "Invalid variable declaration format: ";
    private static final String INVALID_PART_ERR = "Invalid declaration part '";
    private static final String INVALID_NAME_ERR = "Error: '";
    private static final String FINAL_INIT_ERR = "Error: final variable '";
    private static final String INVALID_ASSIGN_FORMAT_ERR = "Invalid assignment format for variable '";

    /** Validator for checking type compatibility and initialization state. */
    private final ValueValidator valueValidator;

    /** Manager for scopes and variable storage. */
    private final VariablesTable variablesTable;

    /**
     * Constructs a VariableHandler with the necessary validation and storage dependencies.
     * * @param valueValidator The validator used to check assignment values.
     * @param variablesTable The table managing current variable scopes.
     */
    public VariableHandler(ValueValidator valueValidator, VariablesTable variablesTable){
        this.valueValidator = valueValidator;
        this.variablesTable = variablesTable;
    }

    /**
     * Processes standalone variable assignments (e.g., "a = 5, b = c;").
     * Supports multiple assignments in a single line separated by commas.
     * * @param line The raw assignment line from the source code.
     * @throws InvalidCodeException If a variable is undefined, final, or the assignment is malformed.
     */
    public void handleAssignment(String line) throws InvalidCodeException {
        line = line.replaceAll(";", "");
        String[] content = line.split(",");

        for (String assignment : content) {
            String[] parts = assignment.split("=");
            if (parts.length != 2){
                throw new VariableException(ASSIGNMENT_STRUCTURE_ERR + line);
            }

            String name = parts[0].trim();
            String value = parts[1].trim();

            // Retrieve variable from current or outer scopes
            Variable variable = variablesTable.getVariable(name);
            if (variable == null) {
                throw new VariableException(NOT_DEFINED_ERR + name + "' is not defined in this scope.");
            }

            // s-Java rules: Cannot reassign to a final variable
            if (variable.isFinal()) {
                throw new VariableException(FINAL_ASSIGN_ERR + name + "'.");
            }

            // Validate that the new value is compatible with the variable's type
            if (valueValidator.validate(variable.getType(), value)) {
                variable.updateInitializationState();
            }
        }
    }

    /**
     * Parses a variable declaration line (e.g., "final int a = 5, b;").
     * Extracts the type and finality status, then processes each individual assignment.
     * * @param line The raw declaration line.
     * @param isGlobal True if the declaration occurs in the global scope.
     * @throws InvalidCodeException If the declaration format is illegal or naming rules are violated.
     */
    public void parseDeclaration(String line, boolean isGlobal) throws InvalidCodeException {
        Matcher matcher = RegexAndPatterns.VAR_DEC_PAT.matcher(line);
        if (!matcher.matches()) {
            throw new InvalidCodeException(INVALID_DEC_FORMAT_ERR + line);
        }

        boolean isFinal = (matcher.group(1) != null);
        String typeStr = matcher.group(2);
        String content = matcher.group(3);

        VariableType type = VariableType.fromString(typeStr);
        // Remove semicolon and handle potential multiple declarations
        String[] assignments = content.replace(";", "").split(",");

        for (String assignment : assignments) {
            assignment = assignment.trim();
            Matcher assignmentMatcher = RegexAndPatterns.VAR_ASSIGN_PATTERN.matcher(assignment);
            if (!assignmentMatcher.matches()){
                throw new VariableException(INVALID_PART_ERR + assignment + "' in line: " + line);
            }
            handleSingleAssignment(type, isFinal, assignment.trim(), isGlobal);
        }
    }

    /**
     * Helper method to process a single variable declaration part.
     * Checks if the variable name is legal and manages initial values if present.
     * * @param type The type shared by all variables in this declaration.
     * @param isFinal True if the 'final' keyword was present.
     * @param trim The individual assignment segment (e.g., "a = 5").
     * @param isGlobal True if defining in global scope.
     * @throws InvalidCodeException If final variables are not initialized or naming is invalid.
     */
    private void handleSingleAssignment(VariableType type, boolean isFinal, String trim, boolean isGlobal)
            throws InvalidCodeException {
        String[] parts = trim.split("=");
        String variableName = parts[0].trim();

        // Validate variable identifier
        if (NameValidator.varNameValid(variableName) == null) {
            throw new VariableException(INVALID_NAME_ERR +
                    variableName + "' is not a legal s-Java variable name.");
        }

        Variable newVar = new Variable(type, isFinal);

        // Case: Declaration without initialization (e.g., "int a;")
        if (parts.length == 1) {
            // s-Java rules: final variables MUST be initialized at declaration
            if (isFinal) {
                throw new VariableException(FINAL_INIT_ERR +
                        variableName + "' must be initialized during declaration.");
            }
            variablesTable.addVariable(newVar, variableName, isGlobal);
            return;
        }

        // Case: Declaration with initialization (e.g., "int a = 5;")
        if (parts.length == 2) {
            String varValue = parts[1].trim();
            if (valueValidator.validate(type, varValue)) {
                newVar.updateInitializationState();
            }
        } else {
            throw new VariableException(INVALID_ASSIGN_FORMAT_ERR + variableName + "'.");
        }

        variablesTable.addVariable(newVar, variableName, isGlobal);
    }
}