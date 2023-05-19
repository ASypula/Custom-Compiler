package tkom.common.ParserComponentTypes;

import tkom.common.tokens.TokenMap;
import tkom.common.tokens.TokenType;

import java.util.HashMap;

public class OperatorMap {
    public static final HashMap<TokenType, OperatorType> MAP_OPERATORS;
    static {
        MAP_OPERATORS = new HashMap<>();
        MAP_OPERATORS.put(TokenType.T_EQUALS, OperatorType.O_EQUALS);
        MAP_OPERATORS.put(TokenType.T_GREATER, OperatorType.O_GREATER);
        MAP_OPERATORS.put(TokenType.T_GREATER_OR_EQ, OperatorType.O_GREATER_OR_EQ);
        MAP_OPERATORS.put(TokenType.T_LESS, OperatorType.O_LESS);
        MAP_OPERATORS.put(TokenType.T_LESS_OR_EQ, OperatorType.O_LESS_OR_EQ);
        MAP_OPERATORS.put(TokenType.T_NOT_EQ, OperatorType.O_NOT_EQ);
    }
}
