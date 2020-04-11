package com.garfild63.pascalabc.parser;

import com.garfild63.pascalabc.parser.ast.*;
import java.util.Vector;

/**
 *
 * @author Garfild63
 */
public final class Parser {
	
	private static final Token EOF = new Token(TokenType.EOF, "");
	
	private final Vector tokens;
	private final int size;
	private int pos;
	private String executingFunction;
	
	public Parser(Vector tokens) {
		this.tokens = tokens;
		size = tokens.size();
	}
	
	public Statement parse() {
/**
		final BlockStatement result = new BlockStatement();
		while (!match(TokenType.EOF)) {
			result.add(statement());
		}
		return result;
 */
		final BlockStatement result = new BlockStatement();
		if (match(TokenType.PROGRAM)) {
			String programName = consume(TokenType.WORD).getText();
			consume(TokenType.SEMICOLON);
		}
		if (match(TokenType.USES)) {
			while(!match(TokenType.SEMICOLON)) {
				String moduleName = consume(TokenType.WORD).getText();
				result.add(new UsesStatement(moduleName));
				match(TokenType.COMMA);
			}
		}
		while (!lookMatch(0, TokenType.BEGIN)) {
			if (match(TokenType.FUNCTION)) {
				result.add(functionDefine(false));
			}
			if (match(TokenType.PROCEDURE)) {
				result.add(functionDefine(true));
			}
			if (match(TokenType.CONST)) {
				//
			}
			if (match(TokenType.VAR)) {
				do {
					Vector vars = new Vector();
					while(!match(TokenType.COLON)) {
						String varName = consume(TokenType.WORD).getText();
						vars.addElement(varName);
						match(TokenType.COMMA);
					}
					Type varType = type();
					for (int i = 0; i < vars.size(); i++) {
						String varName = (String) vars.elementAt(i);
						Variables.setType(varName, varType);
					}
					consume(TokenType.SEMICOLON);
				} while (lookMatch(0, TokenType.WORD));
			}
			if (match(TokenType.TYPE)) {
				//
			}
		}
		result.add(block());
		consume(TokenType.DOT);
		return result;
	}
	
	private Vector args() {
		Vector arguments = new Vector();
		do {
			Vector vars = new Vector();
			int status = Argument.NONE;
			if (match(TokenType.VAR)) {
				status = Argument.VAR;
			} else if (match(TokenType.CONST)) {
				status = Argument.CONST;
			}
			while (!match(TokenType.COLON)) {
				String varName = consume(TokenType.WORD).getText();
				vars.addElement(varName);
				match(TokenType.COMMA);
			}
			Type varType = type();
			for (int i = 0; i < vars.size(); i++) {
				String varName = (String) vars.elementAt(i);
				arguments.addElement(new Argument(varName, varType, status));
			}
			try {
				consume(TokenType.SEMICOLON);
			} catch (RuntimeException re) {
				break;
			}
		} while (true);
		return arguments;
	}
	
	private Type array(Vector args, int index, Type baseType) {
		final Range range = (Range) args.elementAt(index);
		final int last = args.size() - 1;
		Type arr;
		if (index == last) {
			arr = new Type(Types.ARRAY, range, baseType);
		} else if (index < last) {
			arr = new Type(Types.ARRAY, range, array(args, index + 1, baseType));
		}
		return arr;
	}
	
	private Type type() {
		TokenType varType = get(0).getType();
		String varTypeName = get(0).getText();
		pos++;
		if (varType == TokenType.STRING) {
			if (match(TokenType.LBRACKET)) {
				int n = expression().eval().asInt();
				if (n < 1 || n > 255)
					throw new RuntimeException("Invalid length of string");
				consume(TokenType.RBRACKET);
				return new Type(Types.STRING, n);
			} else {
				return Type.STRING;
			}
		}
		if (varType == TokenType.ARRAY) {
			consume(TokenType.LBRACKET);
			final Vector elements = new Vector();
			while(!match(TokenType.RBRACKET)) {
				elements.addElement(range());
				match(TokenType.COMMA);
			}
			consume(TokenType.OF);
			return array(elements, 0, type());
		}
		if (varType == TokenType.SET) {
			consume(TokenType.OF);
			return new Type(Types.SET, type());
		}
		if (varType == TokenType.FILE) {
			consume(TokenType.OF);
			return new Type(Types.FILE, type());
		}
		if (varType == TokenType.FUNCTION) {
			consume(TokenType.LPAREN);
			Vector arguments = args();
			consume(TokenType.RPAREN);
			consume(TokenType.COLON);
			Type retType = type();
			return new Type(Types.FUNCTION, arguments, retType);
		}
		if (varType == TokenType.PROCEDURE) {
			consume(TokenType.LPAREN);
			Vector arguments = args();
			consume(TokenType.RPAREN);
			return new Type(Types.PROCEDURE, arguments);
		}
		if (varType == TokenType.WORD) {
			
		}
		if (varType == TokenType.LPAREN) {
			Vector enums = new Vector();
			while(!match(TokenType.RPAREN)) {
				enums.addElement(consume(TokenType.WORD).getText());
				match(TokenType.COMMA);
			}
			long idEnum = Type.addEnumeration((String) enums.elementAt(0), enums);
			return new Type(Types.ENUMERABLE, idEnum);
		}
		if (lookMatch(0, TokenType.DOUBLE_DOT)) {
			pos--;
			return new Type(range());
		}
	}
	
	private Range ordinal() {
		Expression expr = expression();
		if (expr.getType().isInteger()) {
			return new Range(expr.eval().asInt(), expr.eval().asInt(), Type.INTEGER);
		}
		if (expr.getType() == Type.CHAR) {
			return new Range((int) expr.eval().asString().charAt(0), (int) expr.eval().asString().charAt(0), Type.CHAR);
		} else if (expr.getType().getType() == Types.ENUMERABLE) {
			return new Range(expr.eval().asInt(), expr.eval().asInt(), expr.getType());
		} else {
			throw new RuntimeException("Invalid type");
		}
	}
	
	private Range ordinalOrRange() {
		Range a = ordinal();
		if (match(TokenType.DOUBLE_DOT)) {
			Range b = ordinal();
			if (a.getBoundsType().equals(b.getBoundsType())) {
				return new Range(a.getLeftBound(), b.getRightBound(), a.getBoundsType());
			} else {
				throw new RuntimeException("Different types of bounds of range");
			}
		} else {
			return a;
		}
	}
	
	private Range range() {
		Range a = ordinal();
		consume(TokenType.DOUBLE_DOT);
		Range b = ordinal();
		if (a.getBoundsType().equals(b.getBoundsType())) {
			return new Range(a.getLeftBound(), b.getRightBound(), a.getBoundsType());
		} else {
			throw new RuntimeException("Different types of bounds of range");
		}
	}
	
	private Statement block() {
		final BlockStatement block = new BlockStatement();
		consume(TokenType.BEGIN);
		while (!match(TokenType.END)) {
			block.add(statement());
			if (!lookMatch(0, TokenType.END)) {
				consume(TokenType.SEMICOLON);
			}
		}
		return block;
	}
	
	private Statement statementOrBlock() {
		if (lookMatch(0, TokenType.BEGIN)) return block();
		return statement();
	}
	
	private Statement statement() {
/**
		if (match(TokenType.PRINT) && (lookMatch(0, TokenType.LPAREN)) {
			consume(TokenType.LPAREN);
			Expression result = expression();
			consume(TokenType.RPAREN);
			return new PrintStatement(result);
		}
 */
		if (match(TokenType.IF)) {
			return ifElse();
		}
		if (match(TokenType.WHILE)) {
			return whileStatement();
		}
		if (match(TokenType.REPEAT)) {
			return repeatUntilStatement();
		}
		if (match(TokenType.BREAK)) {
			return new BreakStatement();
		}
		if (match(TokenType.CONTINUE)) {
			return new ContinueStatement();
		}
		if (match(TokenType.EXIT)) {
			return new ExitStatement();
		}
		if (match(TokenType.FOR)) {
			return forStatement();
		}
		if (lookMatch(0, TokenType.WORD) && lookMatch(1, TokenType.LPAREN)) {
			return new ProcedureStatement(function(true));
		}
		return assignmentStatement();
	}
	
	private Statement assignmentStatement() {
		// WORD ASSIGN
		if (lookMatch(0, TokenType.WORD) && lookMatch(1, TokenType.ASSIGN)) {
			String variable = consume(TokenType.WORD).getText();
			if (variable.equals(executingFunction)) variable = "result";
			consume(TokenType.ASSIGN);
			return new AssignmentStatement(variable, expression());
		}
		if (lookMatch(0, TokenType.WORD) && lookMatch(1, TokenType.LBRACKET)) {
			ArrayAccessExpression array = element();
			consume(TokenType.ASSIGN);
			return new ArrayAssignmentStatement(array, expression());
		}
		throw new RuntimeException("Unknown statement");
	}
	
	private Statement ifElse() {
		final Expression condition = expression();
		consume(TokenType.THEN);
		final Statement ifStatement = statementOrBlock();
		final Statement elseStatement;
		if (match(TokenType.ELSE)) {
			elseStatement = statementOrBlock();
		} else {
			elseStatement = null;
		}
		return new IfStatement(condition, ifStatement, elseStatement);
	}
	
	private Statement whileStatement() {
		final Expression condition = expression();
		consume(TokenType.DO);
		final Statement statement = statementOrBlock();
		return new WhileStatement(condition, statement);
	}
	
	private Statement repeatUntilStatement() {
		final BlockStatement statement = new BlockStatement();
		while (!match(TokenType.UNTIL)) {
			statement.add(statement());
			if (!lookMatch(0, TokenType.UNTIL)) {
				consume(TokenType.SEMICOLON);
			}
		}
		final Expression condition = expression();
		return new RepeatUntilStatement(condition, statement);
	}
	
	private Statement forStatement() {
		final AssignmentStatement initialization = (AssignmentStatement) assignmentStatement();
		final Expression finalValue;
		final boolean isCountDown;
		if (match(TokenType.TO)) {
			isCountDown = false;
		} else {
			consume(TokenType.DOWNTO);
			isCountDown = true;
		}
		finalValue = expression();
		consume(TokenType.DO);
		final Statement statement = statementOrBlock();
		return new ForStatement(initialization, finalValue, isCountDown, statement);
	}
	
	private FunctionDefineStatement functionDefine(boolean isProcedure) {
		final String name = consume(TokenType.WORD).getText();
		consume(TokenType.LPAREN);
		final Vector arguments = args();
		consume(TokenType.RPAREN);
		Type baseType = null;
		if (!isProcedure) {
			consume(TokenType.COLON);
			baseType = type();
		}
		consume(TokenType.SEMICOLON);
		executingFunction = name;
		final Statement body = statementOrBlock();
		executingFunction = null;
		consume(TokenType.SEMICOLON);
		return new FunctionDefineStatement(isProcedure, name, arguments, baseType, body);
	}
	
	private FunctionalExpression function(boolean isProcedure) {
		final String name = consume(TokenType.WORD).getText();
		consume(TokenType.LPAREN);
		final FunctionalExpression function = new FunctionalExpression(name, isProcedure);
		while(!match(TokenType.RPAREN)) {
			function.addArgument(expression());
			match(TokenType.COMMA);
		}
		return function;
	}
	
	private Expression array() {
		consume(TokenType.LBRACKET);
		final Vector elements = new Vector();
		while (!match(TokenType.RBRACKET)) {
			elements.addElement(expression());
			match(TokenType.COMMA);
		}
		return new ArrayExpression(elements);
	}
	
	private ArrayAccessExpression element() {
		final String variable = consume(TokenType.WORD).getText();
		Vector indices = new Vector();
		do {
			consume(TokenType.LBRACKET);
			while (!match(TokenType.RBRACKET)) {
				indices.addElement(expression());
				match(TokenType.COMMA);
			}
		} while (lookMatch(0, TokenType.LBRACKET));
		return new ArrayAccessExpression(variable, indices);
	}
	
	private Expression expression() {
		return conditional();
	}
	
	private Expression conditional() {
		Expression result = additive();
		
		while (true) {
			if (match(TokenType.EQ)) {
				result = new ConditionalExpression(ConditionalExpression.Operator.EQUALS, result, additive());
				continue;
			}
			if (match(TokenType.LT)) {
				result = new ConditionalExpression(ConditionalExpression.Operator.LT, result, additive());
				continue;
			}
			if (match(TokenType.LTEQ)) {
				result = new ConditionalExpression(ConditionalExpression.Operator.LTEQ, result, additive());
				continue;
			}
			if (match(TokenType.GT)) {
				result = new ConditionalExpression(ConditionalExpression.Operator.GT, result, additive());
				continue;
			}
			if (match(TokenType.GTEQ)) {
				result = new ConditionalExpression(ConditionalExpression.Operator.GTEQ, result, additive());
				continue;
			}
			if (match(TokenType.LTGT)) {
				result = new ConditionalExpression(ConditionalExpression.Operator.NOT_EQUALS, result, additive());
				continue;
			}
			break;
		}
		
		return result;
	}
	
	private Expression additive() {
		Expression result = multiplicative();
		
		while (true) {
			if (match(TokenType.PLUS)) {
				result = new BinaryExpression(BinaryExpression.Operator.ADD, result, multiplicative());
				continue;
			}
			if (match(TokenType.MINUS)) {
				result = new BinaryExpression(BinaryExpression.Operator.SUBTRACT, result, multiplicative());
				continue;
			}
			if (match(TokenType.OR)) {
				result = new BinaryExpression(BinaryExpression.Operator.OR, result, multiplicative());
				continue;
			}
			if (match(TokenType.XOR)) {
				result = new BinaryExpression(BinaryExpression.Operator.XOR, result, multiplicative());
				continue;
			}
			break;
		}
		
		return result;
	}
	
	private Expression multiplicative() {
		Expression result = unary();
		
		while (true) {
			if (match(TokenType.STAR)) {
				result = new BinaryExpression(BinaryExpression.Operator.MULTIPLY, result, unary());
				continue;
			}
			if (match(TokenType.SLASH)) {
				result = new BinaryExpression(BinaryExpression.Operator.DIVIDE, result, unary());
				continue;
			}
			if (match(TokenType.DIV)) {
				result = new BinaryExpression(BinaryExpression.Operator.INTDIVIDE, result, unary());
				continue;
			}
			if (match(TokenType.MOD)) {
				result = new BinaryExpression(BinaryExpression.Operator.REMAINDER, result, unary());
				continue;
			}
			if (match(TokenType.SHL)) {
				result = new BinaryExpression(BinaryExpression.Operator.LSHIFT, result, unary());
				continue;
			}
			if (match(TokenType.SHR)) {
				result = new BinaryExpression(BinaryExpression.Operator.RSHIFT, result, unary());
				continue;
			}
			if (match(TokenType.AND)) {
				result = new BinaryExpression(BinaryExpression.Operator.AND, result, unary());
				continue;
			}
			break;
		}
		
		return result;
	}
	
	private Expression unary() {
		if (match(TokenType.MINUS)) {
			return new UnaryExpression(UnaryExpression.Operator.NEGATE, primary());
		}
		if (match(TokenType.NOT)) {
			return new UnaryExpression(UnaryExpression.Operator.NOT, primary());
		}
		if (match(TokenType.PLUS)) {
			return primary();
		}
		return primary();
	}
	
	private Expression primary() {
		final Token current = get(0);
		if (match(TokenType.REAL_NUMBER)) {
			return new ValueExpression(Double.parseDouble(current.getText()));
		}
		if (match(TokenType.INT_NUMBER)) {
			return new ValueExpression(Integer.parseInt(current.getText()));
		}
		if (match(TokenType.TRUE)) {
			return new ValueExpression(true);
		}
		if (match(TokenType.FALSE)) {
			return new ValueExpression(false);
		}
		if (lookMatch(0, TokenType.WORD) && lookMatch(1, TokenType.LPAREN)) {
			return function(false);
		}
		if (lookMatch(0, TokenType.WORD) && lookMatch(1, TokenType.LBRACKET)) {
			return element();
		}
		if (lookMatch(0, TokenType.LBRACKET)) {
			return array();
		}
		if (match(TokenType.WORD)) {
			String text = current.getText();
			Expression expr;
			if (Variables.isExists(text)) {
				expr = new VariableExpression(current.getText());
			} else {
				long id = Type.findEnumeration(text);
				Type type = new Type(Types.ENUMERABLE, id >> 32);
				expr = new ValueExpression((int) (id & 0xffffffff), type);
			}
			return expr;
		}
		if (match(TokenType.CHAR)) {
			return new ValueExpression(current.getText().charAt(0));
		}
		if (match(TokenType.TEXT)) {
			return new ValueExpression(current.getText());
		}
		if (match(TokenType.LPAREN)) {
			Expression result = expression();
			match(TokenType.RPAREN);
			return result;
		}
		throw new RuntimeException("Unknown expression");
	}
	
	private Token consume(TokenType type) {
		final Token current = get(0);
		if (type != current.getType()) throw new RuntimeException("Token " + current + " doesn't match " + type);
		pos++;
		return current;
	}
	
	private boolean lookMatch(int pos, TokenType type) {
		return get(pos).getType() == type;
	}
	
	private boolean match(TokenType type) {
		final Token current = get(0);
		if (type != current.getType()) return false;
		pos++;
		return true;
	}
	
	private Token get(int relativePosition) {
		final int position = pos + relativePosition;
		if (position >= size) return EOF;
		return (Token) tokens.elementAt(position);
	}
}
