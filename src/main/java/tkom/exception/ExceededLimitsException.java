package tkom.exception;

import tkom.common.Position;

public class ExceededLimitsException extends Exception {

    /**
     * Exception thrown when maximum possible length of token was exceeded.
     * @param pos           position of the first character of invalid token
     * @param invalidToken  actual string-text of the token
     * @param maxLength     maximum length available for given type of token
     */
    public ExceededLimitsException(Position pos, String invalidToken, int maxLength) {
        super("Too long token " + invalidToken + " at the position: " + pos.toString() +
                " max size: " + maxLength);
    }

    /**
     * Exception thrown when maximum possible length of token was exceeded.
     * @param pos           position of the first character of invalid token
     * @param number  actual string-text of the token
     */
    public ExceededLimitsException(Position pos, String number) {
        super("Number that will exceed possible limit: " + number + " at the position: " + pos.toString());
    }

    public ExceededLimitsException(String place, String limit){
        super("Exceeded limit " + limit + " in " + place);
    }

}
