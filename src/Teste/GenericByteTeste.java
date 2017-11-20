package Teste;

import model.IPv4;

public class GenericByteTeste {
	public static void main(String[] args) {
//		GenericByte gb = new GenericByte("192.168.0.1");
//		GenericByte gb = new GenericByte("255.255.255.255");
//		IPv4 ipv4 = new IPv4("192.168.0.1", 2);
//		IPv4 ipv4 = new IPv4("255.255.255.255", 1);
		IPv4 ipv4 = new IPv4("192.168.0.2", 24);
//		IPv4 ipv4 = new IPv4("0.0.0.0", 4);
		ipv4.print();
	}
}

