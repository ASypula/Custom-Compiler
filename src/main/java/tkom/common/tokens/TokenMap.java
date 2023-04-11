package tkom.common.tokens;

import java.util.HashMap;

public class TokenMap {

    public static final HashMap<String, TokenType> T_KEYWORDS;
    public static final HashMap<String, TokenType> T_SIGNS;

    static {
        T_KEYWORDS = new HashMap<>();
        T_KEYWORDS.put("else", TokenType.T_ELSE);
        T_KEYWORDS.put("Figure", TokenType.T_FIGURE);
        T_KEYWORDS.put("FigCollection", TokenType.T_FIG_COLL);
        T_KEYWORDS.put("if", TokenType.T_IF);
        T_KEYWORDS.put("Line", TokenType.T_LINE);
        T_KEYWORDS.put("List", TokenType.T_LIST);
        T_KEYWORDS.put("Point", TokenType.T_POINT);
        T_KEYWORDS.put("print", TokenType.T_PRINT);
        T_KEYWORDS.put("return", TokenType.T_RETURN);
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
}
