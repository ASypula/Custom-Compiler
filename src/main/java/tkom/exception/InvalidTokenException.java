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
     * Exception thrown when maximum possible length of token was exceeded.
     * @param pos           position of the first character of invalid token
     * @param invalidToken  actual string-text of the token
     * @param maxLength     maximum length available for given type of token
     */
    public InvalidTokenException(Position pos, String invalidToken, int maxLength) {
        super("Too long token " + invalidToken + " at the position: " + pos.toString() +
                " max size: " + maxLength);
    }

    public InvalidTokenException(Token token, TokenType tType) {
        super("Missing token: " + tType.toString() + ". Got: " + token.toString() + "at position: " + token.getPosition().toString());
    }
}
