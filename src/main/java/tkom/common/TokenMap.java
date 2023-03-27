package tkom.common;

import java.util.HashMap;

public class TokenMap {

    static final HashMap<String, TokenType> T_KEYS;

    static {
        T_KEYS = new HashMap<>();
        T_KEYS.put("&&", TokenType.T_AND);
        T_KEYS.put("=", TokenType.T_ASSIGN);
        T_KEYS.put(",", TokenType.T_COLON);
        T_KEYS.put("{", TokenType.T_CURLY_BRACKET_L);
        T_KEYS.put("}", TokenType.T_CURLY_BRACKET_R);
        T_KEYS.put("\\", TokenType.T_DIV);
        T_KEYS.put(".", TokenType.T_DOT);
        T_KEYS.put("else", TokenType.T_ELSE);
        T_KEYS.put("==", TokenType.T_EQUALS);
        T_KEYS.put(">", TokenType.T_GREATER);
        T_KEYS.put(">=", TokenType.T_GREATER_OR_EQ);
        T_KEYS.put("if", TokenType.T_IF);
        T_KEYS.put("<", TokenType.T_LESS);
        T_KEYS.put("<=", TokenType.T_LESS_OR_EQ);
        T_KEYS.put("-", TokenType.T_MINUS);
        T_KEYS.put("*", TokenType.T_MULT);
        T_KEYS.put("!", TokenType.T_NOT);
        T_KEYS.put("!=", TokenType.T_NOT_EQ);
        T_KEYS.put("||", TokenType.T_OR);
        T_KEYS.put("+", TokenType.T_PLUS);
        T_KEYS.put("print", TokenType.T_PRINT);
        T_KEYS.put("(", TokenType.T_REG_BRACKET_L);
        T_KEYS.put(")", TokenType.T_REG_BRACKET_R);
        T_KEYS.put("return", TokenType.T_RETURN);
        T_KEYS.put(";", TokenType.T_SEMICOLON);
        T_KEYS.put("while", TokenType.T_WHILE);
    }
}
