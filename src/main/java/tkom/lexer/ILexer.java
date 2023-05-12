package tkom.lexer;

import tkom.common.tokens.Token;
import tkom.exception.ExceededLimitsException;
import tkom.exception.InvalidTokenException;

import java.io.IOException;

public interface ILexer {
    public Token getToken() throws IOException, InvalidTokenException, ExceededLimitsException;
}
