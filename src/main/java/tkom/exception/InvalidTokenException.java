package tkom.exception;

import tkom.common.Position;

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

}
