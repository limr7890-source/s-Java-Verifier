package ex5.methods;

import ex5.variables.Variable;
import ex5.variables.VariableType;

/**
 * Represents a single parameter within a method signature in s-Java.
 * This class encapsulates the parameter's name and its underlying variable properties
 * such as type and finality.
 * * It employs composition by holding a Variable object to manage its state.
 * * @author Lihi Martziano
 */
public class MethodParam {

    /** The identifier name of the parameter. */
    private final String name;

    /** The variable object representing the parameter's data properties. */
    private final Variable var;

    /**
     * Constructs a new MethodParam with a name, type, and finality status.
     * Parameters are automatically marked as initialized since they receive values
     * upon method invocation.
     * * @param name The name of the parameter.
     * @param type The VariableType of the parameter.
     * @param isFinal True if the parameter is declared as final, false otherwise.
     */
    public MethodParam(String name, VariableType type, boolean isFinal) {
        this.name = name;
        // Method parameters are considered initialized by the caller
        this.var = new Variable(type, isFinal, true);
    }

    /**
     * Gets the name of the parameter.
     * @return The parameter name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the data type of the parameter.
     * @return The VariableType associated with this parameter.
     */
    public VariableType getType() {
        return var.getType();
    }

    /**
     * Checks if the parameter is declared as final.
     * @return True if the parameter is final, false otherwise.
     */
    public boolean isFinal() {
        return var.isFinal();
    }
}