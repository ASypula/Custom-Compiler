package tkom.common.tokens;

import java.util.HashMap;

public class TokenMap {

    public static final HashMap<String, TokenType> T_KEYWORDS;
    public static final HashMap<String, TokenType> T_SIGNS;

    public static final HashMap<Character, Character> T_ESCAPECHARS;

    static {
        T_KEYWORDS = new HashMap<>();
        T_KEYWORDS.put("else", TokenType.T_ELSE);
        T_KEYWORDS.put("false", TokenType.T_FALSE);
        T_KEYWORDS.put("function", TokenType.T_FUNCTION);
        T_KEYWORDS.put("if", TokenType.T_IF);
        T_KEYWORDS.put("return", TokenType.T_RETURN);
        T_KEYWORDS.put("true", TokenType.T_TRUE);
        T_KEYWORDS.put("while", TokenType.T_WHILE);
    }

    static {
        T_SIGNS = new HashMap<>();
        T_SIGNS.put("&&", TokenType.T_AND);
        T_SIGNS.put("=", TokenType.T_ASSIGN);
        T_SIGNS.put(",", TokenType.T_COLON);
        T_SIGNS.put("{", TokenType.T_CURLY_BRACKET_L);
        T_SIGNS.put("}", TokenType.T_CURLY_BRACKET_R);
        T_SIGNS.put("/", TokenType.T_DIV);
        T_SIGNS.put(".", TokenType.T_DOT);
        T_SIGNS.put("==", TokenType.T_EQUALS);
        T_SIGNS.put(">", TokenType.T_GREATER);
        T_SIGNS.put(">=", TokenType.T_GREATER_OR_EQ);
        T_SIGNS.put("<", TokenType.T_LESS);
        T_SIGNS.put("<=", TokenType.T_LESS_OR_EQ);
        T_SIGNS.put("-", TokenType.T_MINUS);
        T_SIGNS.put("*", TokenType.T_MULT);
        T_SIGNS.put("!", TokenType.T_NOT);
        T_SIGNS.put("!=", TokenType.T_NOT_EQ);
        T_SIGNS.put("||", TokenType.T_OR);
        T_SIGNS.put("+", TokenType.T_PLUS);
        T_SIGNS.put("(", TokenType.T_REG_BRACKET_L);
        T_SIGNS.put(")", TokenType.T_REG_BRACKET_R);
        T_SIGNS.put(";", TokenType.T_SEMICOLON);
    }

    static {
        T_ESCAPECHARS = new HashMap<>();
        T_ESCAPECHARS.put('r', '\r');
        T_ESCAPECHARS.put('n', '\n');
        T_ESCAPECHARS.put('t', '\t');
        T_ESCAPECHARS.put('\"', '\"');
        T_ESCAPECHARS.put('\\', '\\');
    }


}
