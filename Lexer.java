package com.garfild63.pascalabc.parser;

import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author Garfild63
 */
public final class Lexer {
	
	private static final String OPERATOR_CHARS = "+-*/()[]{}:=<>,;.@^";
	
	private static final Hashtable OPERATORS;
	static {
		OPERATORS = new Hashtable();
		OPERATORS.put("+", TokenType.PLUS);
		OPERATORS.put("-", TokenType.MINUS);
		OPERATORS.put("*", TokenType.STAR);
		OPERATORS.put("/", TokenType.SLASH);
		OPERATORS.put("(", TokenType.LPAREN);
		OPERATORS.put(")", TokenType.RPAREN);
		OPERATORS.put("[", TokenType.LBRACKET);
		OPERATORS.put("]", TokenType.RBRACKET);
		OPERATORS.put(":=", TokenType.ASSIGN);
		OPERATORS.put("=", TokenType.EQ);
		OPERATORS.put("<", TokenType.LT);
		OPERATORS.put("<=", TokenType.LTEQ);
		OPERATORS.put(">", TokenType.GT);
		OPERATORS.put(">=", TokenType.GTEQ);
		OPERATORS.put("<>", TokenType.LTGT);
		OPERATORS.put(",", TokenType.COMMA);
		OPERATORS.put(";", TokenType.SEMICOLON);
		OPERATORS.put(".", TokenType.DOT);
		OPERATORS.put("..", TokenType.DOUBLE_DOT);
		OPERATORS.put(":", TokenType.COLON);
		OPERATORS.put("@", TokenType.AT);
		OPERATORS.put("^", TokenType.CARET);
	}
	
	private static final Hashtable KEYWORDS;
	static {
		KEYWORDS = new Hashtable();
		KEYWORDS.put("if", TokenType.IF);
		KEYWORDS.put("then", TokenType.THEN);
		KEYWORDS.put("else", TokenType.ELSE);
		KEYWORDS.put("while", TokenType.WHILE);
		KEYWORDS.put("for", TokenType.FOR);
		KEYWORDS.put("to", TokenType.TO);
		KEYWORDS.put("downto", TokenType.DOWNTO);
		KEYWORDS.put("do", TokenType.DO);
		KEYWORDS.put("repeat", TokenType.REPEAT);
		KEYWORDS.put("until", TokenType.UNTIL);
		KEYWORDS.put("break", TokenType.BREAK);
		KEYWORDS.put("continue", TokenType.CONTINUE);
		KEYWORDS.put("exit", TokenType.EXIT);
		KEYWORDS.put("function", TokenType.FUNCTION);
		KEYWORDS.put("procedure", TokenType.PROCEDURE);
		KEYWORDS.put("not", TokenType.NOT);
		KEYWORDS.put("xor", TokenType.XOR);
		KEYWORDS.put("or", TokenType.OR);
		KEYWORDS.put("and", TokenType.AND);
		KEYWORDS.put("div", TokenType.DIV);
		KEYWORDS.put("mod", TokenType.MOD);
		KEYWORDS.put("shl", TokenType.SHL);
		KEYWORDS.put("shr", TokenType.SHR);
		KEYWORDS.put("begin", TokenType.BEGIN);
		KEYWORDS.put("end", TokenType.END);
		KEYWORDS.put("uses", TokenType.USES);
		KEYWORDS.put("const", TokenType.CONST);
		KEYWORDS.put("var", TokenType.VAR);
		KEYWORDS.put("type", TokenType.TYPE);
		KEYWORDS.put("integer", TokenType.INTEGER);
		KEYWORDS.put("word", TokenType.WORD);
		KEYWORDS.put("byte", TokenType.BYTE);
		KEYWORDS.put("char", TokenType.CHAR);
		KEYWORDS.put("boolean", TokenType.BOOLEAN);
		KEYWORDS.put("real", TokenType.REAL);
		KEYWORDS.put("complex", TokenType.COMPLEX);
		KEYWORDS.put("string", TokenType.STRING);
		KEYWORDS.put("array", TokenType.ARRAY);
		KEYWORDS.put("set", TokenType.SET);
		KEYWORDS.put("file", TokenType.FILE);
		KEYWORDS.put("text", TokenType.TEXT);
		KEYWORDS.put("pointer", TokenType.POINTER);
		KEYWORDS.put("record", TokenType.RECORD);
		KEYWORDS.put("class", TokenType.CLASS);
		KEYWORDS.put("of", TokenType.OF);
		KEYWORDS.put("program", TokenType.PROGRAM);
		KEYWORDS.put("true", TokenType.TRUE);
		KEYWORDS.put("false", TokenType.FALSE);
		KEYWORDS.put("nil", TokenType.NIL);
	}
	
	private final String input;
	private final int length;
	
	private final Vector tokens;
	
	private int pos;
	
	public Lexer(String input) {
		this.input = input;
		length = input.length();
		
		tokens = new Vector();
	}
	
	public Vector tokenize() {
		while (pos < length) {
			final char current = peek(0);
			if (Character.isDigit(current)) tokenizeNumber();
			else if (isLetter(current)) tokenizeWord();
			else if (current == '$') {
				next();
				tokenizeHexNumber();
			}
			else if (current == '#') {
				next();
				tokenizeChar();
			}
			else if (current == '\'') {
				tokenizeText();
			}
			else if (OPERATOR_CHARS.indexOf(current) != -1) {
				tokenizeOperator();
			} else {
				// whitespaces
				next();
			}
		}
		return tokens;
	}
	
	private void tokenizeNumber() {
		final StringBuffer buffer = new StringBuffer();
		char current = peek(0);
		boolean isReal = false;
		while (true) {
			if (current == '.') {
				isReal = true;
				if (buffer.toString().indexOf(".") != -1)
					throw new RuntimeException("Invalid float number");
			} else if (current == 'e' || current == 'E') {
				isReal = true;
				if (buffer.toString().toLowerCase().indexOf("e") != -1)
					throw new RuntimeException("Invalid float number");
				buffer.append(current);
				current = next();
				if (current != '+' && current != '-' && !Character.isDigit(current))
					throw new RuntimeException("Invalid float number");
			} else if (!Character.isDigit(current)) {
				break;
			}
			buffer.append(current);
			current = next();
		}
		addToken(isReal ? TokenType.REAL_NUMBER : TokenType.INT_NUMBER, buffer.toString());
	}
	
	private void tokenizeHexNumber() {
		final StringBuffer buffer = new StringBuffer();
		char current = peek(0);
		while (Character.isDigit(current) || isHexNumber(current)) {
			buffer.append(current);
			current = next();
		}
		addToken(TokenType.INT_NUMBER, Long.parseLong(buffer.toString(), 16).toString());
	}
	
	private void tokenizeChar() {
		final StringBuffer buffer = new StringBuffer();
		char current = peek(0);
		while (Character.isDigit(current)) {
			buffer.append(current);
			current = next();
		}
		addToken(TokenType.CHAR, String.valueOf((char) Integer.parseInt(buffer.toString())));
	}
	
	private static boolean isHexNumber(char current) {
		return "abcdef".indexOf(Character.toLowerCase(current)) != -1;
	}
	
	private static boolean isLetter(char current) {
		return "abcdefghijklmnopqrstuvwxyz".indexOf(Character.toLowerCase(current)) != -1;
	}
	
	private static boolean isLetterOrDigit(char current) {
		return "abcdefghijklmnopqrstuvwxyz0123456789".indexOf(Character.toLowerCase(current)) != -1;
	}
	
	private void tokenizeOperator() {
		char current = peek(0);
		if (current == '/') {
			if (peek(1) == '/') {
				next();
				next();
				tokenizeComment();
				return;
			}
		} else if (current == '{') {
			next();
			tokenizeMultilineComment();
			return;
		}
		final StringBuffer buffer = new StringBuffer();
		while (true) {
			final String text = buffer.toString();
			if (!OPERATORS.containsKey(text + current) && text.length() != 0) {
				addToken((TokenType) OPERATORS.get(text));
				return;
			}
			buffer.append(current);
			current = next();
		}
	}
	
	private void tokenizeWord() {
		final StringBuffer buffer = new StringBuffer();
		char current = peek(0);
		while (true) {
			if (!isLetterOrDigit(current) && (current != '_')) {
				break;
			}
			buffer.append(current);
			current = next();
		}
		
		final String word = buffer.toString().toLowerCase(); // case doesn't matter
		if (KEYWORDS.containsKey(word)) {
            addToken((TokenType) KEYWORDS.get(word));
        } else {
            addToken(TokenType.WORD, word);
        }
	}
	
	private void tokenizeText() {
		next(); // skip '
		final StringBuffer buffer = new StringBuffer();
		char current = peek(0);
		while (true) {
			if (current == '\'') break;
			buffer.append(current);
			current = next();
		}
		next(); // skip closing '
		if (buffer.length() == 1) {
			addToken(TokenType.CHAR, buffer.toString());
		} else {
			addToken(TokenType.TEXT, buffer.toString());
		}
	}
	
	private void tokenizeComment() {
		char current = peek(0);
		while ("\r\n\0".indexOf(current) == -1) {
			current = next();
		}
	}
	
	private void tokenizeMultilineComment() {
		char current = peek(0);
		while (true) {
			if (current == '\0') throw new RuntimeException("Missing close tag");
			if (current == '}') break;
			current = next();
		}
		next(); // }
	}
	
	private char next() {
		pos++;
		return peek(0);
	}
	
	private char peek(int relativePosition) {
		final int position = pos + relativePosition;
		if (position >= length) return '\0';
		return input.charAt(position);
	}
	
	private void addToken(TokenType type) {
		addToken(type, "");
	}
	
	private void addToken(TokenType type, String text) {
		tokens.addElement(new Token(type, text));
	}
}
