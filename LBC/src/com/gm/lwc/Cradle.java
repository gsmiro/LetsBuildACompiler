package com.gm.lwc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Cradle {

	public Cradle() {
		this(new InputStreamReader(System.in), new OutputStreamWriter(
				System.out), new OutputStreamWriter(System.err));
	}

	public Cradle(Reader in, Writer out, Writer err) {
		this.in = in;
		this.out = new PrintWriter(out);
		this.err = new PrintWriter(err);
	}

	public static final char TAB = '	';
	public static final char PLUS = '+';
	public static final char MINUS = '-';
	public static final char MULT = '*';
	public static final char DIV = '/';
	public static final char LPAR = '(';
	public static final char RPAR = ')';

	private Reader in;
	private PrintWriter out;
	private PrintWriter err;
	private char look;

	public void getChar() {
		try {
			look = (char) in.read();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void error(String m) {
		err.write(m);
		err.flush();
	}

	public void abort(String s) {
		error(s);
		System.exit(-1);
	}

	public void expected(String s) {
		abort("Expected " + s);
	}

	public void match(int chr) {
		if (look == chr)
			getChar();
		else
			expected(String.valueOf(chr));
	}

	public boolean isAlpha(int c) {
		return (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
	}

	public boolean isDigit(int c) {
		return c >= 48 && c <= 57;
	}

	public int getName() {
		if (!isAlpha(look))
			expected("Name");
		int l = look;
		getChar();
		return l;
	}

	public int getNum() {
		if (!isDigit(look))
			expected("Integer");
		int l = look;
		getChar();
		return l;
	}

	public String emit(String s) {
		s = TAB + s;
		out.print(s);
		out.flush();
		return s;
	}

	public String emitln(String s) {
		s = emit(s);
		out.println();
		return s;
	}

	public void init() {
		getChar();
	}

	public void factor() {
		if (look == LPAR) {
			match(LPAR);
			expression();
			match(RPAR);
		} else
			emitln("MOVE #" + getNum() + ",D0");
	}

	public void mult() {
		match(MULT);
		factor();
		emitln("MULS (SP)+,D0");
	}

	public void div() {
		match(DIV);
		factor();
		emitln("MOVE (SP)+,D1");
		emitln("DIVS D1,D0");
	}

	public void term() {
		factor();
		while (Arrays.asList(MULT, DIV).contains(look)) {
			emitln("MOVE D0,-(SP)");
			switch (look) {
			case MULT:
				mult();
				break;
			case DIV:
				div();
				break;
			default:
				expected("Mulop");
			}
		}
	}

	public void add() {
		match(PLUS);
		term();
		emitln("ADD (SP)+,D0");
	}

	public void sub() {
		match(MINUS);
		term();
		emitln("SUB (SP)+,D0");
		emitln("NEG D0");
	}

	public void expression() {
		term();
		while (Arrays.asList(PLUS, MINUS).contains(look)) {
			emitln("MOVE D0,-(SP)");
			switch (look) {
			case PLUS:
				add();
				break;
			case MINUS:
				sub();
				break;
			default:
				expected("Addop");
			}
		}
	}

	public static void main(String[] args) {
		while (true)
			try {
				Cradle c = new Cradle();
				c.init();
				c.expression();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
	}
}
