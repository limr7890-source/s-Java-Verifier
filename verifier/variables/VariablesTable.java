package ex5.variables;

import ex5.code_processing.InvalidCodeException;
import java.util.*;

/**
 * Manages the variable scopes and lifecycle during s-Java analysis.
 * This class uses a stack-based approach to handle nested scopes (if/while blocks)
 * and implements the Memento pattern to preserve the state of global variables
 * across different method analyses.
 * * @author Lihi Martziano
 */
public class VariablesTable {

    /** Error message thrown when attempting to add a variable outside of an active scope. */
    private static final String ERR_MSG="Error: No active scope found to add variable ";

    /** Stack of scopes, where the top represents the most inner scope. */
    private final ArrayDeque<Scope> scopeStack = new ArrayDeque<>();

    /** Tracks the current number of active scopes in the stack. */
    private int stackSize;

    /**
     * Initializes the table with a single global scope.
     */
    public VariablesTable() {
        Scope globalScope = new Scope(null);
        scopeStack.push(globalScope);
        stackSize = 1;
    }

    /**
     * Creates and enters a new local scope (e.g., when entering an if/while block).
     * The new scope points to the current top scope as its outer scope.
     */
    public void addScope() {
        Scope newScope = new Scope(scopeStack.peek());
        scopeStack.push(newScope);
        stackSize++;
    }

    /**
     * Removes the current inner scope and returns to the outer scope.
     * Ensures that the global scope is never popped during local scope management.
     */
    public void removeScope() {
        if (!scopeStack.isEmpty() && stackSize > 1) {
            scopeStack.pop();
            stackSize--;
        }
    }

    /**
     * Searches for a variable starting from the current inner scope up to the global scope.
     * * @param variableName The name of the variable to look up.
     * @return The Variable object if found, or null otherwise.
     */
    public Variable getVariable(String variableName) {
        if (scopeStack.peek() == null) {
            return null;
        }
        return scopeStack.peek().getVariable(variableName);
    }

    /**
     * Adds a new variable to either the current scope or the global scope.
     * * @param variable The Variable object to add.
     * @param name The identifier name for the variable.
     * @param isGlobal If true, the variable is added to the global scope (bottom of the stack).
     * @throws InvalidCodeException If the scope is missing or the variable is already defined locally.
     */
    public void addVariable(Variable variable, String name, boolean isGlobal) throws InvalidCodeException {
        // peekLast() retrieves the global scope, peek() retrieves the current local scope
        Scope specificScope = (isGlobal) ? scopeStack.peekLast() : scopeStack.peek();
        if (specificScope == null) {
            throw new VariableException(ERR_MSG + name);
        }
        specificScope.addVariableToScope(variable, name);
    }

    /**
     * Creates a Memento object capturing the current state of global variables.
     * * @return A Memento object representing the global variables state.
     */
    public Memento saveToMemento() {
        if (scopeStack.isEmpty()) {
            return null;
        }
        return new Memento(scopeStack.peekLast());
    }

    /**
     * Restores the global scope variables from a provided Memento.
     * * @param memento The Memento object containing the saved state.
     */
    public void restoreFromMemento(Memento memento) {
        if (memento == null) {
            return;
        }
        Scope globalScope = scopeStack.peekLast();
        if (globalScope != null) {
            globalScope.setVariables(memento.getSavedVariables());
        }
    }

    /**
     * Inner class implementing the Memento pattern to store a snapshot of global variables.
     * Ensures deep copy of variables to prevent unintended state mutation.
     */
    public static class Memento {
        private final Map<String, Variable> savedVariables;

        /**
         * Private constructor to create a deep copy of the global scope variables.
         * * @param globalScope The scope to save.
         */
        private Memento(Scope globalScope) {
            this.savedVariables = new HashMap<>();
            if (globalScope != null && globalScope.getVariables() != null) {
                for (Map.Entry<String, Variable> entry : globalScope.getVariables().entrySet()) {
                    Variable oldVar = entry.getValue();
                    if (oldVar != null) {
                        // Deep copy to ensure restoration doesn't affect future passes
                        Variable copyVar = new Variable(oldVar.getType(),
                                oldVar.isFinal(), oldVar.isInitialized());
                        this.savedVariables.put(entry.getKey(), copyVar);
                    }
                }
            }
        }

        /**
         * Provides the saved variables to the VariablesTable for restoration.
         * * @return The map of saved variables.
         */
        private Map<String, Variable> getSavedVariables() {
            return savedVariables;
        }
    }
}