package com.gm.lwc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

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

	public static enum Cons {
		TAB(9), PLUS(43), MINUS(45);

		private int v;

		private Cons(int c) {
			this.v = c;
		}

		public int val() {
			return v;
		}

		public String str() {
			return new String(new char[] { (char) v });
		}
	}

	private Reader in;
	private PrintWriter out;
	private PrintWriter err;
	private int look;

	public void getChar() {
		try {
			look = in.read();
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
		s = Cons.TAB.str() + s;
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

	public void term() {
		emitln("MOVE #" + getNum() + ", D0");
	}

	public void add() {
		match(Cons.PLUS.val());
		term();
		emitln("ADD D1,D0");
	}

	public void sub() {
		match(Cons.MINUS.val());
		term();
		emitln("SUB D1,D0");
		emitln("NEG D0");
	}

	public void expression() {
		term();
		emitln("MOVE D0,D1");
		if (look == Cons.PLUS.val())
			add();

		else if (look == Cons.MINUS.val())
			sub();

		else
			expected("Addop");
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
