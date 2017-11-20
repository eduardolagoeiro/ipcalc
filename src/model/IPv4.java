package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class IPv4 extends GenericByte {

	private int x;
	private GenericByte mascaraSubRede;
	private GenericByte endDaRede;
	private GenericByte endBroadCast;
	private GenericByte firstEnd;
	private GenericByte lastEnd;
	private Integer maxSubNets;

	public IPv4(String abcd, int x) {
		super(abcd);
		if(x > 30 || x < 0)
			throw new IllegalArgumentException("Tamanho de prefixo de sub-rede deve ser entre 0 e 30");
		this.x = x;
		maxSubNets = (int) Math.pow(2, 32 - x) - 2;
		gerarMascSubRede();
		gerarEndDaRede();
		gerarEndBroadCast();
		gerarFirstEnd();
		gerarLastEnd();
	}

	private void gerarLastEnd() {
		Long endDec = this.endBroadCast.toLong() - 1;
		String bin = Long.toBinaryString(endDec);
		String stringBin = binToDecFormated(bin);
		this.lastEnd = new GenericByte(stringBin);
	}

	private void gerarFirstEnd() {
		Long endDec = this.endDaRede.toLong() + 1;
		String bin = Long.toBinaryString(endDec);
		String stringBin = binToDecFormated(bin);
		this.firstEnd = new GenericByte(stringBin);
	}

	private void gerarEndBroadCast() {
		this.endBroadCast = trocarXBitsADireta(super.toString(), "1");
	}

	private void gerarEndDaRede() {
		this.endDaRede = trocarXBitsADireta(super.toString(), "0");
	}

	private void gerarMascSubRede() {
		this.mascaraSubRede = trocarXBitsADireta("255.255.255.255", "0");
	}

	private GenericByte trocarXBitsADireta(String ipformatado, String bin) {
		GenericByte gb = new GenericByte(ipformatado);
		StringBuilder sb = new StringBuilder();
		sb.append(gb.toBinary().substring(0, x));
		for (int i = 0; i < 32 - x; i++) {
			sb.append(bin);
		}

		String abcd = sb.toString();
		String s = binToDecFormated(abcd);

		gb = new GenericByte(s);
		return gb;

	}

	@Override
	public String toString() {
		return super.toString() + "/" + x;
	}

	private String spaces(String string) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 20 - string.length(); i++) {
			sb.append(" ");

		}
		return sb.toString();
	}

	private String formatSpaces(String string) {
		return string + spaces(string);
	}

	public void print() {
		System.out.println("O endereço de sub-rede (em notação decimal e em binário).");
		System.out.println(formatSpaces(super.toString()) + " " + this.toBinaryFormated());
		System.out.println("\nO endereço de broadcast (em notação decimal e em binário).");
		System.out.println(formatSpaces(this.endBroadCast.toString()) + " " + this.endBroadCast.toBinaryFormated());
		System.out.println("\nA máscara de sub-rede (em notação decimal e em binário).");
		System.out.println(formatSpaces(this.mascaraSubRede.toString()) + this.mascaraSubRede.toBinaryFormated());
		System.out.println("\nO tamanho do prefixo da sub-rede.");
		System.out.println(this.x);
		System.out.println(
				"\nO primeiro (i.e., menor) endereço atribuível a uma interface (em notação decimal e em binário).");
		System.out.println(formatSpaces(this.firstEnd.toString()) + " " + this.firstEnd.toBinaryFormated());
		System.out.println(
				"\nO último (i.e., maior) endereço atribuível a uma interface (em notação decimal e em binário).");
		System.out.println(formatSpaces(this.lastEnd.toString()) + " " + this.lastEnd.toBinaryFormated());
		System.out.println("\nO número total de endereços atribuíveis a interfaces naquela sub-rede.");
		System.out.println(maxSubNets+"\n\n");
	}

	public void printSubNet(int tamanhoDoPrefixoDasSubRedes) {
		if(x == tamanhoDoPrefixoDasSubRedes){
			System.out.println("1 sub-rede (a própria rede):\n");
			System.out.println("Sub-rede #1:");
			this.print();
			return;
		}
		if (x > tamanhoDoPrefixoDasSubRedes) {
			throw new IllegalArgumentException("Sua sub-rede tem prefixo "+this.x+", você não pode dividí-la em subredes de prefixo com tamanho "+ tamanhoDoPrefixoDasSubRedes +".");
		}
		if(tamanhoDoPrefixoDasSubRedes > 30 || tamanhoDoPrefixoDasSubRedes < 0)
			throw new IllegalArgumentException("Tamanho de prefixo de sub-rede deve ser entre 0 e 30");
		int dif = (int) Math.pow(2, tamanhoDoPrefixoDasSubRedes-x);
		generateAndPrintSubNet(menorPotenciaMaiorOuIgual(dif));
	}

	protected void generateAndPrintSubNet(int num) {
		String prefixo = endDaRede.toBinary().substring(0, x);
		String sufixo = endDaRede.toBinary().substring(x);
		
		HashMap<String, Integer> mapa = new HashMap<>();
		mapa.put(sufixo, 0);
		function(mapa, num);
		
		ArrayList<String> list = new ArrayList<>(mapa.keySet());
		Collections.sort(list);
		System.out.println(num + " sub-redes:\n");
		int n = 1;
		for (String	sufixoSubNet : list) {
			System.out.println("Sub-rede #"+n+":");
			String ip = prefixo+sufixoSubNet;
			String ipDecFormated = binToDecFormated(ip);
			new IPv4(ipDecFormated, x+mapa.get(sufixoSubNet)).print();
			n++;
		}
	}
	
	public HashMap<String, Integer> function(HashMap<String, Integer> mapa, int n){
		if(mapa.size() < n){
			Set<String> set = new HashSet<>(mapa.keySet());
			for(String stringUm : set){
				Integer i = mapa.get(stringUm);
				String stringDois = "";
				if(i == 0)
					stringDois = "1" + stringUm.substring(1);
				else
					stringDois = stringUm.substring(0, i) + "1" + stringUm.substring(i+1);
				mapa.put(stringUm, i+1);
				mapa.put(stringDois, i+1);
				if(mapa.size() >= n)
					break;
			}
			function(mapa, n);
		}
		return mapa;
	}
	
	private int menorPotenciaMaiorOuIgual(int i){
		return (int) Math.pow(2, Math.ceil(Math.log(i)/Math.log(2)));
	}

}
