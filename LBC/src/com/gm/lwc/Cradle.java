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
		TAB("\t");

		private String v;

		private Cons(String c) {
			this.v = c;
		}

		public String getValue() {
			return v;
		}
	}

	public static class Halt extends Exception {

		public Halt() {
			super();
		}

		public Halt(String arg0, Throwable arg1) {
			super(arg0, arg1);
		}

		public Halt(String arg0) {
			super(arg0);
		}

		public Halt(Throwable arg0) {
			super(arg0);
		}

	}

	private Reader in;
	private PrintWriter out;
	private PrintWriter err;
	private int look;

	public void getChar() throws IOException {
		look = in.read();
	}

	public void error(String m) throws IOException {
		out.write(m);
	}

	public void abort(String s) throws IOException, Halt {
		error(s);
		throw new Halt(s);
	}

	public void expected(String s) throws IOException, Halt {
		abort("Expected " + s);
	}

	public void match(int chr) throws IOException, Halt {
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

	public int getName() throws IOException, Halt {
		if (!isAlpha(look))
			expected("Name");
		int l = look;
		getChar();
		return l;
	}

	public int getNum() throws IOException, Halt {
		if (!isDigit(look))
			expected("Integer");
		int l = look;
		getChar();
		return l;
	}

	public String emit(String s) throws IOException {
		s = Cons.TAB + s;
		out.write(s);
		return s;
	}

	public String emitLn(String s) throws IOException {
		s = emit(s);
		out.println();
		return s;
	}

	public void init() throws IOException {
		getChar();
	}

	public static void main(String[] args) {
		try {
			new Cradle().init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
