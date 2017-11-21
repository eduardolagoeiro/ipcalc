package model;

import java.util.InputMismatchException;

/**
 * Classe que guarda 4 bytes para representar endereços no formato a.b.c.d
 */
public class GenericByte {

	/**
	 * primeiro byte
	 */
	protected Integer a;
	/**
	 * segundo byte
	 */
	protected Integer b;
	/**
	 * terceiro byte
	 */
	protected Integer c;
	/**
	 * quarto byte
	 */
	protected Integer d;

	/**
	 * Construtor da classe GenericByte
	 * 
	 * @param abcd
	 *            ip válido no formato a.b.c.d
	 */
	public GenericByte(String abcd) {
		String[] valores = abcd.split("\\.");
		if (valores.length != 4)
			throw new InputMismatchException("São 4 valores de endereço: a, b, c e d.");
		this.a = validaNum(valores[0]);
		this.b = validaNum(valores[1]);
		this.c = validaNum(valores[2]);
		this.d = validaNum(valores[3]);
	}

	/**
	 * Valida entrada
	 * @param num
	 *            byte em decimal na forma de string
	 * @return número inteiro decimal válido para um dos bytes.
	 */
	private Integer validaNum(String num) {
		Integer valueOf = Integer.valueOf(num);
		if (valueOf > 255 || valueOf < 0) {
			throw new IllegalArgumentException("Cada casa deve estar entre 0 e 255.");
		}
		return valueOf;
	}

	/**
	 * Representação em string da classe
	 * @return String que representa os 4 bytes no formato a.b.c.d
	 */
	@Override
	public String toString() {
		return a + "." + b + "." + c + "." + d;
	}

	/**
	 * Converte objeto em único valor decimal
	 * @return os 4 bytes como um único valor decimal
	 */
	public Long toLong() {
		String s = this.toBinary();
		Long l = Long.parseLong(s, 2);
		return l;
	}

	/**
	 * Converte objeto em único valor binário
	 * @return os 4 bytes como um único valor binário
	 */
	public String toBinary() {
		String stringa = decToBin8Bits(a);
		String stringb = decToBin8Bits(b);
		String stringc = decToBin8Bits(c);
		String stringd = decToBin8Bits(d);
		String string = stringa + stringb + stringc + stringd;
		return string;
	}

	/**
	 * Converte objeto em único valor binário formatado
	 * @return os 4 bytes como um único valor binário formatado a.b.c.d
	 */
	public String toBinaryFormated() {
		String stringa = decToBin8Bits(a);
		String stringb = decToBin8Bits(b);
		String stringc = decToBin8Bits(c);
		String stringd = decToBin8Bits(d);
		String string = stringa + "." + stringb + "." + stringc + "." + stringd;
		return string;
	}

	/**
	 * Converte número decimal em binário de 8 bits
	 * @param n
	 *            número decimal que representa um binário 8 bits
	 * @return string binária de 8 bits
	 */
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

	/**
	 * Converte número binário em binário de 32 bits
	 * @param n
	 *            string binária
	 * @return string binária de 32 bits
	 */
	public static String binTo32bits(String n) {
		StringBuilder sb = new StringBuilder();
		int zeros = 32 - n.length();
		for (int i = 0; i < zeros; i++) {
			sb.append("0");
		}
		return sb.append(n).toString();
	}

	/**
	 * Converte número binário em binário formatado de 32 bits
	 * @param abcd
	 *            string binária menor que 32 bits
	 * @return decimal formatado a.b.c.d
	 */
	protected String binToDecFormated(String abcd) {
		abcd = binTo32bits(abcd);
		Integer valueOf1 = Integer.valueOf(abcd.substring(0, 8), 2);
		Integer valueOf2 = Integer.valueOf(abcd.substring(8, 16), 2);
		Integer valueOf3 = Integer.valueOf(abcd.substring(16, 24), 2);
		Integer valueOf4 = Integer.valueOf(abcd.substring(24, 32), 2);
		String s = "" + valueOf1 + "." + valueOf2 + "." + valueOf3 + "." + valueOf4;
		return s;
	}
}
