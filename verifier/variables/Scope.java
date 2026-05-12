package ex5.variables;

import ex5.code_processing.InvalidCodeException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single scope level within the s-Java program.
 * Each Scope object maintains its own local variables and keeps a reference to its
 * parent (outer) scope, enabling hierarchical variable lookups.
 * * @author Lihi Martziano
 */
public class Scope {

    /** Error message thrown when a variable is redeclared in the same scope. */
    private static final String ERR_MSG = "Error: Variable is already defined in the current scope: ";

    /** A map storing the variables defined within this specific scope. */
    Map<String, Variable> variables = new HashMap<>();

    /** Reference to the enclosing scope (e.g., a method scope is outer to an 'if' scope). */
    private final Scope outerScope;

    /**
     * Constructs a new Scope with a reference to its parent scope.
     * * @param outerScope The parent scope, or null if this is the global scope.
     */
    public Scope(Scope outerScope) {
        this.outerScope = outerScope;
    }

    /**
     * Adds a new variable to the current scope.
     * * @param variable The Variable object to add.
     * @param name The identifier name of the variable.
     * @throws InvalidCodeException If the variable name already exists in this local scope.
     */
    public void addVariableToScope(Variable variable, String name) throws InvalidCodeException {
        if (variables.containsKey(name)) {
            throw new VariableException(ERR_MSG + name);
        }
        variables.put(name, variable);
    }

    /**
     * Returns the map of variables defined in this scope level.
     * * @return A map of variable names to Variable objects.
     */
    public Map<String, Variable> getVariables() {
        return this.variables;
    }

    /**
     * Updates the current scope's variables using a provided map, creating deep copies
     * of the Variable objects.
     * This is primarily used by the Memento pattern to restore global state.
     * * @param newVariables The map of variables to restore from.
     */
    public void setVariables(Map<String, Variable> newVariables) {
        this.variables.clear();
        for (Map.Entry<String, Variable> entry : newVariables.entrySet()) {
            Variable varFromMemento = entry.getValue();
            // Create a new Variable instance to ensure state isolation
            this.variables.put(entry.getKey(), new Variable(varFromMemento.getType(),
                    varFromMemento.isFinal(),
                    varFromMemento.isInitialized()));
        }
    }

    /**
     * Searches for a variable by name in this scope and recursively in outer scopes.
     * * @param name The name of the variable to find.
     * @return The Variable object if found in this or any parent scope, or null otherwise.
     */
    public Variable getVariable(String name) {
        // Check local scope first
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        // If not found locally, delegate the search to the outer scope
        if (outerScope != null) {
            return outerScope.getVariable(name);
        }
        return null;
    }
}