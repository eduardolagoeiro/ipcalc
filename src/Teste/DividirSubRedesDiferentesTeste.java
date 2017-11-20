package Teste;

import java.util.ArrayList;
import java.util.List;

import model.IPv4;

public class DividirSubRedesDiferentesTeste {
	public static void main(String[] args) {
		List<Integer> inteiros = new ArrayList<>();
		inteiros.add(39);//64
		inteiros.add(10);//16
		inteiros.add(4);//4
		inteiros.add(3);//4
		inteiros.add(53);//64
		
		IPv4 ipv4 = new IPv4("192.168.0.1", 24);
		ipv4.generateAndPrintSubNet(inteiros);
	}
}
