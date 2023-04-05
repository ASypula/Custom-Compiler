package tkom.exception;

import tkom.common.Position;

public class InvalidTokenException extends Exception {
    public InvalidTokenException(Position pos, String invalidToken) {
        super("Invalid token " + invalidToken + " at the position: " + pos.toString());
    }

    public InvalidTokenException(Position pos, String invalidToken, int maxLength) {
        super("Too long token " + invalidToken + " at the position: " + pos.toString() +
                " max size: " + maxLength);
    }
}
