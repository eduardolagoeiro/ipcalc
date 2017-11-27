package main;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import model.GenericByte;
import model.IPv4;

public class Main {
	private static Scanner scan = new Scanner(System.in);;

	public static void main(String[] args) {

		System.out.println("Modo de Informações(1)");
		System.out.println("Modo de Divisão em Sub-redes de Tamanho Fixo(2)");
		System.out.println("Modo de Divisão em Sub-redes de Tamanho Variável(3)");

		int menu = readPositiveInt("\nEntre com 1/2/3\n->", 1, 3);

		IPv4 ipv4 = null;
		do {
			System.out.println("Por favor, entre com um ip na forma a.b.c.d/x.");
			try {
				String abcdx = scan.next();
				ipv4 = validaEntrada(abcdx);
			} catch (InputMismatchException e) {
				System.out.println(e.getMessage());
			} catch (NumberFormatException e) {
				System.out.println(e.getMessage());
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
			}
		} while (ipv4 == null);

		switch (menu) {
		case 1:
			ipv4.print();
			break;
		case 2:
			boolean b2 = false;
			do {
				int prefixoSubRede = readPositiveInt("\nEntre com o tamanho do prefixo das subredes\n->", 0, 30);
				try {
					b2 = ipv4.generateAndPrintSubNet(prefixoSubRede);
				} catch (IllegalArgumentException e) {
					System.out.println(e.getMessage());
					scan.nextLine();
				}
			} while (!b2);

			break;

		case 3:
			List<Integer> inteiros = new ArrayList<>();
			int inteiro = -1;
			Integer maxSubNets = ipv4.getMaxSubNets();
			int numDeSubRedes = 1;
			boolean first = true;
			do {
				int limiteSuperior;
				if(first){
					limiteSuperior = maxSubNets;
					first = false;
				}else
					limiteSuperior = IPv4.menorPotenciaMaiorOuIgual(maxSubNets)/2 - 2;
				if(limiteSuperior <= 0)
					limiteSuperior = maxSubNets;
				System.out.println("\nVocê possui "+maxSubNets+" endereços atribuíveis a interfaces e o maior endereço possível é "
						   + limiteSuperior + ".");
				String message = "\nInsira o número de endereços atribuíveis a interfaces para a sub-rede num#"
						+ numDeSubRedes + " (Para sair do menu de inserção entre 0)\n->";
				String complemento = "Lembrando que sua rede tem um total de " + maxSubNets
						+ " interfaces atribuíveis disponíveis e o maior endereço possível é " +
					limiteSuperior + ".";
				inteiro = readPositiveInt(message, 0, limiteSuperior, complemento);
				int menorPotenciaMaiorOuIgual = IPv4.menorPotenciaMaiorOuIgual(inteiro+2);
				if (inteiro != 0) {
					inteiros.add(menorPotenciaMaiorOuIgual);
					maxSubNets -= menorPotenciaMaiorOuIgual;
					numDeSubRedes++;
				}
				if(maxSubNets <= 0)
					break;
			} while (inteiro != 0);
			
			if(inteiros.isEmpty())
				System.out.println("Nenhuma sub-rede para calcular.");
			ipv4.generateAndPrintSubNet(inteiros);
			
			break;
		}
	}

	protected static int readPositiveInt(String string, int min, int max) {
		return readPositiveInt(string, min, max, "");
	}

	protected static int readPositiveInt(String string, int min, int max, String complemento) {
		if(max < min) return 0;
		int menu = -1;
		do {
			System.out.print(string);
			try {
				menu = scan.nextInt();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Entre números positivos.");
				scan.nextLine();
			}
			if (menu < min || menu > max) {
				System.out.println("Valores entre " + min + " e " + max + ".");
				if (!("".equals(complemento)))
					System.out.println(complemento);
			}

		} while (menu < min || menu > max);
		return menu;
	}

	private static IPv4 validaEntrada(String abcdx) {
		String[] s = abcdx.split("/");
		if (s.length != 2)
			throw new InputMismatchException("Você deve entrar com ip no formato a.b.c.d/x.");
		String abcd = s[0];
		Integer valueOf = null;
		try {
			valueOf = Integer.valueOf(s[1]);
		} catch (NumberFormatException e) {
			String[] subnetMask = s[1].split("\\.");
			if(subnetMask.length != 4 && s[1].contains("."))
				throw new IllegalArgumentException("Máscara de sub-rede "+s[1]+" não é válida.");
			else if(validaMask(s[1])){
				GenericByte gb = new GenericByte(s[1]);
				String bin = gb.toBinary();
				String sufix = bin.substring(bin.indexOf("0"));
				valueOf = 32 - sufix.length();
				if (valueOf > 30 || valueOf < 0)
					throw new IllegalArgumentException("Tamanho de prefixo de sub-rede deve ser entre 0 e 30");
			}
			else
				throw new NumberFormatException("Os valores de a, b, c, d e x são numericos.");
		}
		return new IPv4(abcd, valueOf);
	}
			   
	private static boolean validaMask(String s){
		
		GenericByte gb = null;
		try{
			gb = new GenericByte(s);
		} catch(InputMismatchException e){
			throw new InputMismatchException("Máscara de sub-rede "+s+" não é válida, valores não estão entre 0 e 255.");
		}
		String bin = gb.toBinary();
		boolean resp = false;
		try{
			String sufix = bin.substring(bin.indexOf("0"));
			resp = !sufix.contains("1");
		}catch(StringIndexOutOfBoundsException e){
			throw new IllegalArgumentException("Tamanho de prefixo de sub-rede deve ser entre 0 e 30");
		}
		return resp;
	}
}
