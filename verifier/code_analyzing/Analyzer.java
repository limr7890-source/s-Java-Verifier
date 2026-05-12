package ex5.code_analyzing;
import ex5.blocks.IfWhileHandler;
import ex5.code_analyzing.info_storage.RegexAndPatterns;
import ex5.methods.MethodHandler;
import ex5.methods.MethodsTable;
import ex5.variables.VariablesTable;
import ex5.main.BadFileFormatException;
import ex5.code_processing.InvalidCodeException;
import ex5.code_processing.Tokenizer;
import ex5.validators.ValueValidator;
import ex5.variables.VariableHandler;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

/**
 * The main orchestrator of the s-Java code analysis process.
 * This class implements a two-pass analysis strategy to validate s-Java files:
 * 1. First Pass: Collects global variable declarations and method signatures.
 * 2. Second Pass: Performs deep semantic analysis, including local scopes, method calls,
 * and control flow validation.
 * * It utilizes the Facade pattern to provide a simple interface for the main application.
 * * @author Lihi Martziano
 * */
public class Analyzer {
    /** Error message for nested method declarations. */
    private static final String METHOD_INSIDE_SCOPE_ERR = "Error: Method" +
            " declaration is not allowed inside another scope";

    /** Error message for unclosed braces at the end of the file. */
    private static final String EOF_DEPTH_ERR = "Error: Reached end of file with depth ";

    /** Error message for lines containing multiple semicolons. */
    private static final String SINGLE_STATEMENT_ERR = "Only one statement per line is allowed!";

    /** Error message for methods missing a return statement before the closing brace. */
    private static final String MISSING_RETURN_ERR = "Error: Method must end with 'return ;'";

    /** Error message for method declarations not starting with the 'void' keyword. */
    private static final String VOID_START_ERR = "Error: Method must start with a void";

    /** Error message for malformed if/while conditions. */
    private static final String INVALID_CONDITION_ERR = "Invalid condition structure";

    /** Error message for method calls attempted in the global scope. */
    private static final String GLOBAL_METHOD_CALL_ERR = "Error:method call not allowed in global scope";

    /** Base error message for any unrecognized line of code. */
    private static final String INVALID_LINE_ERR = "Not a valid s-java code line: ";

    /** Keyword constant for method return type. */
    private static final String VOID = "void";

    /** List of cleaned code tokens provided by the Tokenizer. */
    private final List<String> tokens;

    /** Registry for method signatures. */
    private final MethodsTable methodsTable = new MethodsTable();

    /** Registry and scope manager for variables. */
    private final VariablesTable variablesTable = new VariablesTable();

    /** Current nesting level of scopes. */
    private int depth = 0;

    /** Specialized handler for method-related logic. */
    private final MethodHandler methodHandler;

    /** Specialized handler for variable declarations and assignments. */
    private final VariableHandler variableHandler;

    /** Specialized handler for if/while blocks. */
    private final IfWhileHandler ifWhileHandler;

    /**
     * Initializes the Analyzer by tokenizing the input file and setting up handlers.
     * * @param fileName Path to the s-Java source file.
     * @throws IOException If file access fails.
     * @throws BadFileFormatException If the file format or extension is invalid.
     * @throws InvalidCodeException If initialization errors occur.
     */
    public Analyzer(String fileName) throws IOException, BadFileFormatException, InvalidCodeException {
        Tokenizer tokenizer = new Tokenizer(fileName);
        tokens = tokenizer.getTokens();
        ValueValidator validator = new ValueValidator(variablesTable);
        methodHandler = new MethodHandler(validator, variablesTable, methodsTable);
        variableHandler = new VariableHandler(validator, variablesTable);
        ifWhileHandler = new IfWhileHandler(validator);
    }

    /**
     * Executes the full two-pass analysis process.
     * * @throws InvalidCodeException If any semantic or structural error is found in the code.
     */
    public void analyze() throws InvalidCodeException {
        firstPass();
        // Save the state of global variables before analyzing individual methods
        VariablesTable.Memento globalVarsSnapshot = variablesTable.saveToMemento();
        secondPass(globalVarsSnapshot);
    }

    /**
     * Performs the first pass: identifies global variables and registers method signatures.
     * * @throws InvalidCodeException If nested methods or invalid global declarations are found.
     */
    private void firstPass() throws InvalidCodeException {
        depth = 0;
        for (String line : tokens) {
            if (line.endsWith("{")) {
                if (line.startsWith(VOID)) {
                    if (depth > 0) {
                        throw new InvalidCodeException(METHOD_INSIDE_SCOPE_ERR);
                    }
                    methodsTable.addMethod(line);
                }
                depth++;
                continue;
            }
            if (line.equals("}")) {
                depth--;
                continue;
            }
            if (depth == 0 && RegexAndPatterns.VAR_DEC_PAT.matcher(line).matches()) {
                variableHandler.parseDeclaration(line, true);
            }
        }
    }

    /**
     * Performs the second pass: validates the internal logic of methods and local scopes.
     * * @param globalVarsSnapshot Memento containing the initial state of global variables.
     * @throws InvalidCodeException If semantic errors, unclosed scopes, or illegal logic are found.
     */
    private void secondPass(VariablesTable.Memento globalVarsSnapshot) throws InvalidCodeException {
        depth = 0;
        boolean isLastLineReturn = false;
        for (String line : tokens) {
            line = line.trim();
            if (line.isEmpty()) continue;
            validateLineStructure(line);

            if (line.equals("}")) {
                isLastLineReturn = handleClosingBrace(isLastLineReturn);
                continue;
            }
            if (line.endsWith("{")) {
                handleOpeningBrace(line, globalVarsSnapshot);
                isLastLineReturn = false;
                continue;
            }
            if (RegexAndPatterns.VAR_DEC_PAT.matcher(line).matches()) {
                if (depth > 0) variableHandler.parseDeclaration(line, false);
                isLastLineReturn = false;
                continue;
            }
            isLastLineReturn = processGeneralStatement(line, isLastLineReturn);
        }
        if (depth != 0) {
            throw new InvalidCodeException(EOF_DEPTH_ERR + depth);
        }
    }

    /**
     * Helper to count occurrences of a character in a string.
     * * @param str The string to search.
     * @param c The character to count.
     * @return The number of occurrences.
     */
    private int countOccurrences(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) count++;
        }
        return count;
    }

    /**
     * Ensures each line contains only one statement.
     * * @param line The line to validate.
     * @throws InvalidCodeException If multiple semicolons are present.
     */
    private void validateLineStructure(String line) throws InvalidCodeException {
        if (countOccurrences(line, ';') > 1) {
            throw new InvalidCodeException(SINGLE_STATEMENT_ERR);
        }
    }

    /**
     * Handles scope termination and ensures methods end with a return statement.
     * * @param isLastLineReturn True if the previous line was a return statement.
     * @return Always false to reset the return tracking for the next line.
     * @throws InvalidCodeException If a method scope closes without a return statement.
     */
    private boolean handleClosingBrace(boolean isLastLineReturn) throws InvalidCodeException {
        if (depth == 1 && !isLastLineReturn) {
            throw new InvalidCodeException(MISSING_RETURN_ERR);
        }
        variablesTable.removeScope();
        depth--;
        return false;
    }

    /**
     * Handles scope entry for methods and control blocks.
     * Uses Memento to restore global state when entering a method.
     * * @param line The line opening the scope.
     * @param globalVarsSnapshot The memento of global variables.
     * @throws InvalidCodeException If block headers or conditions are invalid.
     */
    private void handleOpeningBrace(String line, VariablesTable.Memento globalVarsSnapshot)
            throws InvalidCodeException {
        if (depth == 0) {
            if (!line.startsWith(VOID)){
                throw new InvalidCodeException(VOID_START_ERR);
            }
            variablesTable.restoreFromMemento(globalVarsSnapshot);
            variablesTable.addScope();
            methodHandler.loadMethodParamsToScope(line);
        } else {
            Matcher matcher1 = RegexAndPatterns.CONDITION_PATTERN.matcher(line);
            if (matcher1.matches()) {
                variablesTable.addScope();
                ifWhileHandler.handleIfWhile(matcher1.group(2).trim());
            } else {
                throw new InvalidCodeException(INVALID_CONDITION_ERR);
            }
        }
        depth++;
    }

    /**
     * Processes standard statements like assignments, method calls, and return statements.
     * * @param line The line to process.
     * @param isLastLineReturn Current return tracking state.
     * @return True if this line is a return statement, false otherwise.
     * @throws InvalidCodeException If the statement is
     * invalid or out of scope.
     */
    private boolean processGeneralStatement(String line, boolean isLastLineReturn)
            throws InvalidCodeException {
        if (depth > 0) {
            Matcher matcherRet = RegexAndPatterns.RETURN_PATTERN.matcher(line);
            if (matcherRet.matches()) {
                return true;
            }
            if (line.contains("=") && !line.startsWith("if") && !line.startsWith("while")) {
                variableHandler.handleAssignment(line);
                return false;
            }
            Matcher matcher0 = RegexAndPatterns.METHOD_CALL_PATTERN.matcher(line);
            if (matcher0.matches()) {
                methodHandler.handleMethodCall(matcher0.group(1), matcher0.group(2));
                return false;
            }
        } else {
            if (RegexAndPatterns.METHOD_CALL_PATTERN.matcher(line).matches()) {
                throw new InvalidCodeException(GLOBAL_METHOD_CALL_ERR);
            }
            if (RegexAndPatterns.VAR_DEC_PAT.matcher(line).matches()) {
                return false;
            }
        }
        throw new InvalidCodeException(INVALID_LINE_ERR + line);
    }
}