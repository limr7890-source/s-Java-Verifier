package ex5.variables;

import ex5.code_processing.InvalidCodeException;

/**
 * Represents the supported data types in s-Java and defines their compatibility rules.
 * This enum handles the mapping between type keywords and their actual types,
 * as well as the identification of literal values through regular expressions.
 * * @author Lihi Martziano
 */
public enum VariableType {

    /** Integer type (e.g., 5, -10). */
    INT("int"),

    /** Double-precision floating point type (e.g., 5.5, .3). */
    DOUBLE("double"),

    /** Single character type (e.g., 'a'). */
    CHAR("char"),

    /** Boolean type (true, false, or numeric values in specific contexts). */
    BOOLEAN("boolean"),

    /** String literal type (e.g., "hello"). */
    STRING("String");

    /** The string representation of the type in s-Java code. */
    private final String typeName;

    // --- Regex patterns for literal validation ---
    private static final String INT_REGEX="[+-]?\\d+";
    private static final String DOUBLE_REGEX="[+-]?(\\d+\\.|\\.\\d+)\\d*";
    private static final String CHAR_REGEX= "'.'";
    private static final String STRING_REGEX= "\"[^\"]*\"";

    /** Error message for unrecognized type keywords. */
    private static final String ERR_MSG="Invalid variable type";

    /** Boolean literal constants. */
    private static final String FALSE="false";
    private static final String TRUE="true";

    /**
     * Constructs a VariableType with its corresponding code keyword.
     * * @param typeName The keyword used in s-Java.
     */
    VariableType(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Maps a string keyword to its corresponding VariableType enum constant.
     * * @param type The keyword string (e.g., "int").
     * @return The matching VariableType.
     * @throws InvalidCodeException If the provided string is not a valid s-Java type.
     */
    public static VariableType fromString(String type) throws InvalidCodeException {
        for (VariableType enumType : VariableType.values()) {
            if (enumType.typeName.equals(type)) {
                return enumType;
            }
        }
        throw new InvalidCodeException(ERR_MSG);
    }

    /**
     * Determines if this type can be assigned to a target type according to s-Java rules.
     * Rules include:
     * <ul>
     * <li>Every type is compatible with itself.</li>
     * <li>An INT can be assigned to a DOUBLE.</li>
     * <li>INT and DOUBLE are compatible with BOOLEAN.</li>
     * </ul>
     * * @param target The type we are attempting to assign to.
     * @return true if assignment is legal, false otherwise.
     */
    public boolean isCompatibleTo(VariableType target) {
        if (this == target) {
            return true;
        }
        return switch (target) {
            case DOUBLE -> (this == INT);
            case BOOLEAN -> (this == INT || this == DOUBLE);
            default -> false;
        };
    }

    /**
     * Identifies the VariableType of a literal value based on predefined regex patterns.
     * * @param word The literal string to check (e.g., "5.0" or "true").
     * @return The identified VariableType, or null if the string does not match any literal format.
     */
    public static VariableType getTypeFromString(String word) {
        if (word.matches(INT_REGEX)){return INT;}
        if (word.matches(DOUBLE_REGEX)){return DOUBLE;}
        if (word.matches(STRING_REGEX)){return STRING;}
        if (word.equals(TRUE) || word.equals(FALSE)){return BOOLEAN;}
        if (word.matches(CHAR_REGEX)){return CHAR;}
        return null;
    }
}