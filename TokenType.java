package com.garfild63.pascalabc.parser;

/**
 *
 * @author Garfild63
 */
public final class TokenType {
	
	public static final TokenType REAL_NUMBER = new TokenType("REAL_NUMBER"),
	INT_NUMBER = new TokenType("INT_NUMBER"),
	WORD = new TokenType("WORD"),
	TEXT = new TokenType("TEXT"),
	CHAR = new TokenType("CHAR"),
	
	// keyword
	PRINT = new TokenType("PRINT"), // Deprecated
	IF = new TokenType("IF"),
	THEN = new TokenType("THEN"),
	ELSE = new TokenType("ELSE"),
	WHILE = new TokenType("WHILE"),
	FOR = new TokenType("FOR"),
	TO = new TokenType("TO"),
	DOWNTO = new TokenType("DOWNTO"),
	DO = new TokenType("DO"),
	REPEAT = new TokenType("REPEAT"),
	UNTIL = new TokenType("UNTIL"),
	BREAK = new TokenType("BREAK"),
	CONTINUE = new TokenType("CONTINUE"),
	EXIT = new TokenType("EXIT"),
	FUNCTION = new TokenType("FUNCTION"),
	PROCEDURE = new TokenType("PROCEDURE"),
	BEGIN = new TokenType("BEGIN"),
	END = new TokenType("END"),
	USES = new TokenType("USES"),
	CONST = new TokenType("CONST"),
	VAR = new TokenType("VAR"),
	TYPE = new TokenType("TYPE"),
	INTEGER = new TokenType("INTEGER"),
	WORD = new TokenType("WORD"),
	BYTE = new TokenType("BYTE"),
	CHAR = new TokenType("CHAR"),
	BOOLEAN = new TokenType("BOOLEAN"),
	REAL = new TokenType("REAL"),
	COMPLEX = new TokenType("COMPLEX"),
	STRING = new TokenType("STRING"),
	ARRAY = new TokenType("ARRAY"),
	SET = new TokenType("SET"),
	FILE = new TokenType("FILE"),
	TEXT = new TokenType("TEXT"),
	POINTER = new TokenType("POINTER"),
	RECORD = new TokenType("RECORD"),
	CLASS = new TokenType("CLASS"),
	OF = new TokenType("OF"),
	PROGRAM = new TokenType("PROGRAM"),
	TRUE = new TokenType("TRUE"),
	FALSE = new TokenType("FALSE"),
	NIL = new TokenType("NIL"),
	
	PLUS = new TokenType("PLUS"), // +
	MINUS = new TokenType("MINUS"), // -
	STAR = new TokenType("STAR"), // *
	SLASH = new TokenType("SLASH"), // /
	DIV = new TokenType("DIV"),
	MOD = new TokenType("MOD"),
	SHL = new TokenType("SHL"),
	SHR = new TokenType("SHR"),
	NOT = new TokenType("NOT"),
	XOR = new TokenType("XOR"),
	OR = new TokenType("OR"),
	AND = new TokenType("AND"),
	ASSIGN = new TokenType("ASSIGN"), // :=
	EQ = new TokenType("EQ"), // =
	LT = new TokenType("LT"), // <
	LTEQ = new TokenType("LTEQ"), // <=
	GT = new TokenType("GT"), // >
	GTEQ = new TokenType("GTEQ"), // >=
	LTGT = new TokenType("LTGT"), // <>
	
	LPAREN = new TokenType("LPAREN"), // (
	RPAREN = new TokenType("RPAREN"), // )
	LBRACKET = new TokenType("LBRACKET"), // [
	RBRACKET = new TokenType("RBRACKET"), // ]
	COMMA = new TokenType("COMMA"), // ,
	SEMICOLON = new TokenType("SEMICOLON"), // ;
	DOT = new TokenType("DOT"), // .
	DOUBLE_DOT = new TokenType("DOUBLE_DOT"), // ..
	COLON = new TokenType("COLON"), // :
	AT = new TokenType("AT"), // @
	CARET = new TokenType("CARET"), // ^
	
	EOF = new TokenType("EOF");
	
	private String name;
	
	private TokenType(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
}
