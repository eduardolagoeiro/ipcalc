package model;

import java.util.InputMismatchException;

public class GenericByte {

	protected Integer a;
	protected Integer b;
	protected Integer c;
	protected Integer d;

	public GenericByte(String abcd) {
		String[] valores = abcd.split("\\.");
		if(valores.length != 4)
			throw new InputMismatchException("São 4 valores de endereço: a, b, c e d.");
		this.a = validaNum(valores[0]);
		this.b = validaNum(valores[1]);
		this.c = validaNum(valores[2]);
		this.d = validaNum(valores[3]);
	}

	private Integer validaNum(String num) {
		Integer valueOf = Integer.valueOf(num);
		if (valueOf > 255 || valueOf < 0) {
			throw new IllegalArgumentException("Cada casa deve estar entre 0 e 255.");
		}
		return valueOf;
	}

	// a.b.c.d
	@Override
	public String toString() {
		return a + "." + b + "." + c + "." + d;
	}

	// abcd de binário para um long
	public Long toLong() {
		String s = this.toBinary();
		Long l = Long.parseLong(s, 2);
		return l;
	}

	// abcd em binário
	public String toBinary() {
		String stringa = decToBin8Bits(a);
		String stringb = decToBin8Bits(b);
		String stringc = decToBin8Bits(c);
		String stringd = decToBin8Bits(d);
		String string = stringa + stringb + stringc + stringd;
		return string;
	}

	// a.b.c.d em binário
	public String toBinaryFormated() {
		String stringa = decToBin8Bits(a);
		String stringb = decToBin8Bits(b);
		String stringc = decToBin8Bits(c);
		String stringd = decToBin8Bits(d);
		String string = stringa + "." + stringb + "." + stringc + "." + stringd;
		return string;
	}

	// n decimal para binario 8 bits
	public static String decToBin8Bits(Integer n) {
		if (n > 255 || n < 0)
			throw new IllegalArgumentException("Cada casa deve estar entre 0 e 255");
		String binaryNum = Integer.toBinaryString(n);
		StringBuilder zeros = new StringBuilder();
		while (zeros.length() + binaryNum.length() != 8) {
			zeros.append("0");
		}
		return zeros.toString() + binaryNum;
	}
	
	// n binario para n de 32 bits
		public static String binTo32bits(String n) {
			StringBuilder sb = new StringBuilder();
			int zeros = 32 - n.length();
			for (int i = 0; i < zeros; i++) {
				sb.append("0");
			}
			return sb.append(n).toString();
		}

	//bin para decimal formatado
	protected String binToDecFormated(String abcd) {
		abcd = binTo32bits(abcd);
		Integer valueOf1 = Integer.valueOf(abcd.substring(0, 8), 2);
		Integer valueOf2 = Integer.valueOf(abcd.substring(8, 16), 2);
		Integer valueOf3 = Integer.valueOf(abcd.substring(16, 24), 2);
		Integer valueOf4 = Integer.valueOf(abcd.substring(24, 32), 2);
		String s = "" + valueOf1 + "." + valueOf2 + "."
				+ valueOf3 + "." + valueOf4;
		return s;
	}
}
