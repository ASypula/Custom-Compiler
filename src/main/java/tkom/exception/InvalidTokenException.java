package tkom.exception;

import tkom.common.Position;
import tkom.common.tokens.Token;
import tkom.common.tokens.TokenType;

public class InvalidTokenException extends Exception {
    /**
     * Exception thrown when either an unknown character was passed or a token cannot be created.
     * @param pos           position of the first character of invalid token
     * @param invalidToken  actual string-text of the token
     */
    public InvalidTokenException(Position pos, String invalidToken) {
        super("Invalid token " + invalidToken + " at the position: " + pos.toString());
    }

    /**
     * Exception thrown when either an unknown character was passed or a token cannot be created.
     * @param pos           position of the first character of invalid token
     * @param invalidToken  actual string-text of the token
     * @param expectedToken expected string-text of the token
     */
    public InvalidTokenException(Position pos, String invalidToken, String expectedToken) {
        super("Invalid token " + invalidToken + " at the position: " + pos.toString() + " expected: " + expectedToken);
    }

    public InvalidTokenException(Token token, TokenType tType) {
        super("Missing token: " + tType.toString() + ". Got: " + token.toString() + "at position: " + token.getPosition().toString());
    }

    public InvalidTokenException(Token token, TokenType tType) {
        super("Missing token: " + tType.toString() + ". Got: " + token.toString() + "at position: " + token.getPosition().toString());
    }
}
