package ex5.blocks;

import ex5.code_processing.InvalidCodeException;
import ex5.validators.ValueValidator;
import ex5.variables.VariableType;

/**
 * The IfWhileHandler class is responsible for validating the logical conditions
 * within 'if' and 'while' blocks in s-Java.
 * It ensures that conditions are well-formed and that each component of a
 * compound condition evaluates to a boolean-compatible type.
 * * @author Lihi Martziano
 */
public class IfWhileHandler {

    /** Validator used to check the type compatibility of condition components. */
    private final ValueValidator valueValidator;

    /** Error message for conditions starting or ending with logical operators. */
    private static final String OPERATOR_ERROR = "Condition cannot start or end with an operator";

    /** Error message for empty expressions found between logical operators. */
    private static final String EMPTY_EXPRESSION_ERROR =
            "Invalid condition structure: empty expression between operators";

    /** Regex pattern used to split compound conditions by logical AND (&&) or OR (||). */
    private static final String CONDITION_ERGEX="(\\|\\||&&)";

    /**
     * Constructs a new IfWhileHandler with a specified ValueValidator.
     * * @param valueValidator The validator used to verify boolean compatibility of expressions.
     */
    public IfWhileHandler(ValueValidator valueValidator) {
        this.valueValidator = valueValidator;
    }

    /**
     * Validates the structure and content of an 'if' or 'while' condition.
     * The method checks for illegal operator placement, empty expressions,
     * and ensures all parts are boolean-compatible.
     * * @param condition The raw condition string extracted from the code block header.
     * @throws InvalidCodeException If the condition is structurally invalid or contains
     * incompatible types.
     */
    public void handleIfWhile(String condition) throws InvalidCodeException {

        // Check if the condition starts or ends with an illegal operator
        if (condition.isEmpty()||
                condition.startsWith("||") ||
                condition.startsWith("&&")||
                condition.endsWith("&&") ||
                condition.endsWith("||")){
            throw new StructureException(OPERATOR_ERROR);
        }

        // Split the condition into individual expressions and validate each
        String[] parts = condition.split(CONDITION_ERGEX);
        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) {
                throw new StructureException(EMPTY_EXPRESSION_ERROR);
            }
            // Verify each part is a valid boolean, int, or double per s-Java rules
            valueValidator.validate(VariableType.BOOLEAN, part);
        }
    }
}