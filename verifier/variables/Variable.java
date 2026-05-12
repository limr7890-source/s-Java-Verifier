package ex5.variables;

/**
 * Represents a variable entity in s-Java.
 * This class stores the data type, finality status, and initialization state
 * of a variable, and enforces rules regarding final variable reassignment.
 * * @author Lihi Martziano
 */
public class Variable {

    /** Error message thrown when attempting to reassign a value to a final variable. */
    private static final String ERR_MSG = "Error: Reassignment to final variable is not allowed.";

    /** The data type of the variable (int, double, String, boolean, or char). */
    private final VariableType type;

    /** Tracks whether the variable has been assigned a value. */
    private boolean isInitialized;

    /** Indicates if the variable was declared with the 'final' keyword. */
    private final boolean isFinal;

    /**
     * Constructs a new uninitialized variable.
     * * @param type The VariableType of the variable.
     * @param isFinal True if the variable is constant (final), false otherwise.
     */
    public Variable(VariableType type, boolean isFinal) {
        this.type = type;
        this.isInitialized = false;
        this.isFinal = isFinal;
    }

    /**
     * Constructs a new variable with a specific initialization state.
     * Useful for creating variables from parameters or during state restoration.
     * * @param type The VariableType of the variable.
     * @param isFinal True if the variable is final.
     * @param isInitialized True if the variable is already initialized.
     */
    public Variable(VariableType type, boolean isFinal, boolean isInitialized) {
        this.type = type;
        this.isInitialized = isInitialized;
        this.isFinal = isFinal;
    }

    /**
     * Returns the data type of the variable.
     * @return The VariableType.
     */
    public VariableType getType() {
        return type;
    }

    /**
     * Checks if the variable has been initialized.
     * @return True if initialized, false otherwise.
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Checks if the variable is declared as final.
     * @return True if final, false otherwise.
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Updates the variable's state to initialized.
     * * @throws VariableException If the variable is final and already initialized,
     * preventing illegal reassignment.
     */
    public void updateInitializationState() throws VariableException {
        if (isFinal && isInitialized) {
            // s-Java rules: final variables cannot be reassigned once initialized
            throw new VariableException(ERR_MSG);
        }
        isInitialized = true;
    }
}