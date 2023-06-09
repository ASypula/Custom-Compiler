package tkom.parser;

import tkom.common.ExceptionHandler;
import tkom.common.ParserComponentTypes.ValueType;
import tkom.common.tokens.Token;
import tkom.common.tokens.TokenType;
import tkom.components.*;
import tkom.components.expressions.*;
import tkom.components.statements.*;
import tkom.exception.*;
import tkom.lexer.ILexer;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static tkom.common.ParserComponentTypes.OperatorMap.MAP_OPERATORS;
import static tkom.common.tokens.TokenMap.T_KEYWORDS;

public class Parser {
    ILexer lexer;
    Token currToken;

    TokenType[] relationOpArray = {TokenType.T_EQUALS, TokenType.T_GREATER, TokenType.T_GREATER_OR_EQ,
            TokenType.T_LESS, TokenType.T_LESS_OR_EQ, TokenType.T_NOT_EQ};

    public ExceptionHandler excHandler;
    public Parser(ILexer lex, ExceptionHandler eh){
        lexer = lex;
        excHandler = eh;
    }

    /**
     * Get next token from lexer but omit the comment tokens
     */
    public void nextToken() throws InvalidTokenException, IOException, ExceededLimitsException {
        currToken = lexer.getToken();
        while (currToken.getType() == TokenType.T_COMMENT)
            currToken = lexer.getToken();
    }

    /**
     * Compare type of current token
     * @param tType     token type for comparison
     * @return          true if the types are equal, false otherwise
     */
    private boolean isCurrToken(TokenType tType){
        return currToken.getType() == tType;
    }

    private boolean consumeIfToken(TokenType tType) throws InvalidTokenException, ExceededLimitsException, IOException {
        if (!isCurrToken(tType))
            return false;
        nextToken();
        return true;
    }

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * Parse: value = bool | integer | double | string | identifier;
     */
    public Value parseValue(){
        if (isCurrToken(TokenType.T_INT))
            return new Value(currToken.getIntValue());
        else if (isCurrToken(TokenType.T_DOUBLE))
            return new Value(currToken.getDoubleValue());
        else if (isCurrToken(TokenType.T_STRING))
            return new Value(currToken.getStringValue(), ValueType.V_STRING);
        else if (isCurrToken(TokenType.T_IDENT))
            return new Value(currToken.getStringValue(), ValueType.V_IDENT);
        else if (isCurrToken(TokenType.T_TRUE))
            return new Value(true);
        else if (isCurrToken(TokenType.T_FALSE))
            return new Value(false);
        else
            return null;
    }

    /**
     * Parse: mult_expr = prim_expr, { (“*” | “/“) prim_expr };
     */
    private IExpression parseMultExpression() throws InvalidTokenException, IOException, ExceededLimitsException, MissingPartException {
        IExpression left = parsePrimExpression();
        while (isCurrToken(TokenType.T_MULT) || isCurrToken(TokenType.T_DIV)){
            Token signToken = currToken;
            nextToken();
            IExpression right = parsePrimExpression();
            if (right == null)
                throw new MissingPartException(currToken, "right PrimExpression", "MultExpression");
            if (signToken.getType() == TokenType.T_MULT)
                left = new MultExpression(left, right, false);
            else
                left = new MultExpression(left, right, true);
        }
        return left;
    }


    /**
     * Parse: arithm_expr = mult_expr, { (“+” | “-“) mult_expr };
     */
    private IExpression parseArithmExpression() throws InvalidTokenException, ExceededLimitsException, IOException, MissingPartException {
        IExpression left = parseMultExpression();
        while (isCurrToken(TokenType.T_PLUS) || isCurrToken(TokenType.T_MINUS)){
            Token signToken = currToken;
            nextToken();
            IExpression right = parseMultExpression();
            if (right == null)
                throw new MissingPartException(currToken, "right MultExpression", "ArithmExpression");
            if (signToken.getType() == TokenType.T_PLUS)
                left = new ArithmExpression(left, right, false);
            else
                left = new ArithmExpression(left, right, true);
        }
        return left;
    }

    /**
     * Parse: rel_expr = arithm_expr, { rel_operator, arithm_expr };
     */
    private IExpression parseRelExpression() throws InvalidTokenException, ExceededLimitsException, IOException, MissingPartException {
        IExpression left = parseArithmExpression();
        if (Arrays.asList(relationOpArray).contains(currToken.getType())){
            Token relToken = currToken;
            nextToken();
            IExpression right = parseArithmExpression();
            if (right == null)
                throw new MissingPartException(currToken, "right ArithmExpression", "RelExpression");
            left = new RelExpression(left, right, MAP_OPERATORS.get(relToken.getType()));
        }
        return left;
    }

    /**
     * Parse: and_expr = rel_expr, {“&&”, rel_expr };
     */
    private IExpression parseAndExpression() throws InvalidTokenException, ExceededLimitsException, IOException, MissingPartException {
        IExpression left = parseRelExpression();
        while (consumeIfToken(TokenType.T_AND)){
            IExpression right = parseRelExpression();
            if (right == null)
                throw new MissingPartException(currToken, "right RelExpression", "AndCondition");
            left = new AndExpression(left, right);
        }
        return left;
    }

    /**
     * Parse: expr = and_expr, { “||”, and_expr };
     */
    public IExpression parseExpression() throws InvalidTokenException, ExceededLimitsException, IOException, MissingPartException {
        IExpression left = parseAndExpression();
        while (consumeIfToken(TokenType.T_OR)){
            IExpression right = parseAndExpression();
            if (right == null)
                throw new MissingPartException(currToken, "right AndExpression", "OrCondition");
            left = new Expression(left, right);
        }
        return left;
    }

    /**
     * Parse: prim_expr = [ negation ], ( value | ident_start_stmt | “(“, expr, “)” );
     */
    private IExpression parsePrimExpression() throws InvalidTokenException, IOException, ExceededLimitsException, MissingPartException {
        boolean isNegated = false;
        if (isCurrToken(TokenType.T_NOT) || isCurrToken(TokenType.T_MINUS)){
            isNegated = true;
            nextToken();
        }
        IStatement stmt = parseIdentStartStmt();
        if (stmt != null){
            if (stmt instanceof LiteralStatement)
                return new PrimExpression(isNegated, new Value(((LiteralStatement) stmt).getIdentifier(), ValueType.V_IDENT));
            return (IExpression) stmt;
        }
        Value value = parseValue();
        if (value != null){
            nextToken();
            return new PrimExpression(isNegated, value);
        }
        else if (consumeIfToken(TokenType.T_REG_BRACKET_L)){
            IExpression expr = parseExpression();
            if (!consumeIfToken(TokenType.T_REG_BRACKET_R))
                throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_R);
            return new PrimExpression(isNegated, expr);
        }
        else
            return null;
    }

    /**
     * Parse: “if”, “(“, expr, “)”, block, [“else”, block];
     */
    private IStatement parseIfStatement() throws InvalidTokenException, IOException, ExceededLimitsException, MissingPartException {
        if (!consumeIfToken(TokenType.T_IF))
            return null;
        if (!consumeIfToken( TokenType.T_REG_BRACKET_L))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_L);
        IExpression expr = parseExpression();
        if (expr == null)
            throw new MissingPartException(currToken, "expression", "IfStatement");
        if (!consumeIfToken(TokenType.T_REG_BRACKET_R))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_R);
        Block blockTrue = parseBlock();
        Block blockElse = null;
        if (consumeIfToken(TokenType.T_ELSE))
            blockElse = parseBlock();
        return new IfStatement(expr, blockTrue, blockElse);
    }

    /**
     * Parse: “while”, “(“, expr, “)”, block;
     */
    private IStatement parseWhileStatement() throws InvalidTokenException, IOException, ExceededLimitsException, MissingPartException {
        if (!consumeIfToken(TokenType.T_WHILE))
            return null;
        if (!consumeIfToken(TokenType.T_REG_BRACKET_L))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_L);
        IExpression expr = parseExpression();
        if (expr == null)
            throw new MissingPartException(currToken, "expression", "WhileStatement");
        if (!consumeIfToken(TokenType.T_REG_BRACKET_R))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_R);
        Block block = parseBlock();
        return new WhileStatement(expr, block);
    }

    /**
     * Parse: “return”, expr, “;”;
     */
    private IStatement parseReturnStatement() throws InvalidTokenException, IOException, ExceededLimitsException, MissingPartException {
        if (!consumeIfToken(TokenType.T_RETURN))
            return null;
        IExpression expr = parseExpression();
        if (!consumeIfToken(TokenType.T_SEMICOLON))
            throw new InvalidTokenException(currToken, TokenType.T_SEMICOLON);
        return new ReturnStatement(expr);
    }

    /**
     * Parse: “=”, expr;
     */
    private IStatement parseAssignStatement(String identifier) throws InvalidTokenException, IOException, ExceededLimitsException, MissingPartException {
        if (!consumeIfToken(TokenType.T_ASSIGN))
            return null;
        IExpression expr = parseExpression();
        return new AssignStatement(identifier, expr);
    }

    /**
     * Parse rest_func_call	= ‘(‘, [args], ‘)’, ';' ;
     * args = expr, { “,”, expr } ;
     */
    private IStatement parseRestFuncCall(String name) throws InvalidTokenException, ExceededLimitsException, IOException, MissingPartException {
        if (!consumeIfToken(TokenType.T_REG_BRACKET_L))
            return null;
        ArrayList<IExpression> expressionArrayList = new ArrayList<>();
        IExpression expr = parseExpression();
        if (expr != null)
            expressionArrayList.add(expr);
            while (consumeIfToken(TokenType.T_COLON)) {
                expr = parseExpression();
                if (expr == null)
                    throw new MissingPartException(currToken, "function argument", "FunctionCall");
                expressionArrayList.add(expr);
            }
        if (!consumeIfToken(TokenType.T_REG_BRACKET_R))
            throw new MissingPartException(currToken, "bracket )", "FunctionCall arguments");
        return new FunctionCall(name, expressionArrayList);
    }

    /**
     * Parse: rest_obj_access 	=  ‘.’, identifier, [ rest_func_call ], { ‘.’, identifier, [ rest_func_call ] }  ;
     */
    private IStatement parseObjectAccess(String ident) throws InvalidTokenException, ExceededLimitsException, IOException, MissingPartException {
        if (!consumeIfToken(TokenType.T_DOT))
            return null;
        IExpression expr = parsePrimExpression();
        ObjectAccess objAccess = new ObjectAccess(ident, expr);
        while (consumeIfToken(TokenType.T_DOT)){
            expr = parsePrimExpression();
            objAccess = new ObjectAccess(objAccess, expr);
        }
        return objAccess;
    }

    /**
     * Parse ident_start_stmt = identifier, { assign_stmt | rest_func_call | rest_obj_access }, ';';
     */
    public IStatement parseIdentStartStmt() throws InvalidTokenException, ExceededLimitsException, IOException, MissingPartException {
        if (!isCurrToken(TokenType.T_IDENT))
            return null;
        String identifier;
        if (isCurrToken(TokenType.T_IDENT))
            identifier = currToken.getStringValue();
        else
            identifier = getKeysByValue(T_KEYWORDS, currToken.getType()).iterator().next();
        nextToken();
        IStatement stmt = parseAssignStatement(identifier);
        if (stmt != null)
            return stmt;
        stmt = parseRestFuncCall(identifier);
        if (stmt != null)
            return stmt;
        stmt = parseObjectAccess(identifier);
        if (stmt != null)
            return stmt;
        stmt = new LiteralStatement(identifier);
        return stmt;
    }

    /**
     * Parse: stmt = if_stmt | while_stmt | return_stmt | ident_start_stmt;
     */
    public IStatement parseStatement() throws InvalidTokenException, IOException, ExceededLimitsException, MissingPartException {
        IStatement stmt = parseIfStatement();
        if (stmt != null)
            return stmt;
        stmt = parseWhileStatement();
        if (stmt != null)
            return stmt;
        stmt = parseReturnStatement();
        if (stmt != null)
            return stmt;
        stmt = parseIdentStartStmt();
        if (stmt != null){
            if (!consumeIfToken( TokenType.T_SEMICOLON))
                throw new MissingPartException(currToken, "semicolon ';'", "the end of a statement");
            return stmt;
        }
        return null;
    }

    /**
     * Parse block: “{“, { statement }, “}”;
     */
    public Block parseBlock() throws InvalidTokenException, IOException, ExceededLimitsException, MissingPartException {
        if (!consumeIfToken(TokenType.T_CURLY_BRACKET_L))
            return null;
        ArrayList<IStatement> statements = new ArrayList<>();
        IStatement stmt;
        stmt = parseStatement();
        while (stmt!=null){
            statements.add(stmt);
            stmt = parseStatement();
        }
        if (!consumeIfToken(TokenType.T_CURLY_BRACKET_R))
            throw new InvalidTokenException(currToken, TokenType.T_CURLY_BRACKET_R);
        return new Block(statements);
    }

    /**
     * Checks if given name is present in the provided parameters list
     */
    private boolean containsName(ArrayList<Parameter> list, String name){
        List<String> containsList = list.stream()
                .map(s -> s.name)
                .filter(s -> s.equals(name)).toList();
        return !(containsList.size() == 0);
    }

    /**
     * Checks if given name is present in the provided map of function definitions
     */
    private boolean containsNameFunc(HashMap<String, FunctionDef> map, String name){
        ArrayList<String> listOfKeys
                = map.keySet().stream().collect(
                Collectors.toCollection(ArrayList::new));
        return listOfKeys.contains(name);
    }

    /**
     * Parses parameters to function definition: identifier, { “,”, identifier };
     */
    public ArrayList<Parameter> parseParameters() throws Exception {
        ArrayList<Parameter> params = new ArrayList<>();
        if (isCurrToken(TokenType.T_IDENT)) {
            params.add(new Parameter(currToken.getStringValue()));
            nextToken();
            while (consumeIfToken(TokenType.T_COLON)) {
                if (isCurrToken(TokenType.T_IDENT)) {
                    String name = currToken.getStringValue();
                    if (containsName(params, name))
                        throw new DuplicatedElementException("parameter", name, "parseParameters");
                    params.add(new Parameter(name));
                    nextToken();
                }
                else
                    throw new InvalidMethodException("Parameter ", " identifier value");
            }
        }
        return params;
    }

    /**
     * Parses: “function”, identifier, “(“, [ params ], “)”, block;
     */
    public boolean parseFuncDef(HashMap<String, FunctionDef> functions) throws Exception {
        if (isCurrToken(TokenType.T_EOF))
            return false;
        if (!consumeIfToken(TokenType.T_FUNCTION))
            throw new InvalidTokenException(currToken, TokenType.T_FUNCTION);
        if (!isCurrToken(TokenType.T_IDENT))
            throw new InvalidTokenException(currToken, TokenType.T_IDENT);
        String name = currToken.getStringValue();
        if (containsNameFunc(functions, name))
            throw new DuplicatedElementException("function definition", name, "parseFuncDef");
        nextToken();
        if (!consumeIfToken(TokenType.T_REG_BRACKET_L))
            throw new MissingPartException(currToken, "left bracket '('", "function definition");
        ArrayList<Parameter> params;
        params = parseParameters();
        if (!consumeIfToken(TokenType.T_REG_BRACKET_R))
            throw new MissingPartException(currToken, "right bracket ')'", "function definition");
        Block block = parseBlock();
        functions.put(name, new FunctionDef(name, params, block));
        return true;
    }

    /**
     * Parse the given program as long as the EOF is not encountered
     * @return      parsed program
     */
    public Program parse() throws Exception {
        HashMap<String, FunctionDef> functions = new HashMap<>();
        nextToken();
        while (parseFuncDef(functions));
        return new Program(functions);
    }
}
