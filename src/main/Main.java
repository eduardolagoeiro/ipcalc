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
				//valida entrada a.b.c.d/x decimal e tam de subrede ou mascara e devolve obj ipv4
				ipv4 = validaEntrada(abcdx);
			//trata casos com mensages para cada tipo de entrada errada
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
			//imprime informações
			ipv4.print();
			break;
		case 2:
			boolean b2 = false;
			do {
				try {
					//le o tamanho do prefixo ou mascara de subrede convertida em tamanho de prefixo
					int prefixoSubRede = readPrefixoSubRede("\nEntre com o tamanho do prefixo das subredes ou máscara de sub-rede\n->", 0, 30);
					//tenta criar as subNets de acordo com o prefixo de subRede e devolve true caso consiga
					b2 = ipv4.generateAndPrintSubNet(prefixoSubRede);
				}catch (InputMismatchException e) {
					//mensagem de erro e limpa buffer
					System.out.println(e.getMessage());
					scan.nextLine();
				}catch (NumberFormatException e) {
					//mensagem de erro e limpa buffer
					System.out.println(e.getMessage());
					scan.nextLine();
				}catch (IllegalArgumentException e){
					//mensagem de erro e limpa buffer
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
			do {
				//limite superior de maior sub rede possível que o usuário pde pedir
				int limiteSuperior;
				//caso maxSubNets+2 seja uma potencia de 2 o limite é o próprio max
				if(IPv4.menorPotenciaMaiorOuIgual(maxSubNets) == maxSubNets+2)
					limiteSuperior = maxSubNets;
				else{
				//caso não seja ele é a metade da menor potencia maior ou igual a maxSubNets -2 (reservados para end de broad e de subrede)
				//exemplo, se começamos com 254 endereços validos e foi requerido 2 endereços (4), temos agora 250, porém o maior num que pode ser pedido
				//a menor potencia maior ou igual a 250 é 254, logo só podemos dar 128 para uma unica rede para não dar overflow e sairmos da nossa subrede
				//quando o maxSubNet + 2 é uma potencia de 2 isso não é vdd, ele é o próprio limite superior
				//basicamente queremos a maior potencia de 2 menor ou igual a max subnets, porém para não implementar outra função usei a divisão para facilitar
					limiteSuperior = IPv4.menorPotenciaMaiorOuIgual(maxSubNets)/2 - 2;
				}
				//caso quando as redes estão acabando para não bugar e calcular num negativo o limite superior tem que ser maxSubNets
				if(limiteSuperior <= 0)
					limiteSuperior = maxSubNets;
				System.out.println("\nVocê possui "+maxSubNets+" endereços atribuíveis a interfaces e o maior endereço possível é "
						   + limiteSuperior + ".");
				String message = "\nInsira o número de endereços atribuíveis a interfaces para a sub-rede num#"
						+ numDeSubRedes + " (Para sair do menu de inserção entre 0)\n->";
				String complemento = "Lembrando que sua rede tem um total de " + maxSubNets
						+ " interfaces atribuíveis disponíveis e o maior endereço possível é " +
					limiteSuperior + ".";
				//lê inteiro entre 0 e a maior subrede possivel
				inteiro = readPositiveInt(message, 0, limiteSuperior, complemento);
				//calcula a partir do inteiro + 2 (broadcast e endereço de rede) a menor potencia de 2 maior ou igual o numero
				int menorPotenciaMaiorOuIgual = IPv4.menorPotenciaMaiorOuIgual(inteiro+2);
				//se inteiro for 0 não adicionamos ele na lista
				if (inteiro != 0) {
					//coloamos a menor potencia de dois maior ou igual ao inteiro na lista
					inteiros.add(menorPotenciaMaiorOuIgual);
					//descontamos do valor anterior de max interfaces disponiveis
					maxSubNets -= menorPotenciaMaiorOuIgual;
					//incrementamos o numero de subredes que o usuário pediu
					numDeSubRedes++;
				}
				//se o numero max de subnets for 0 paramos o loop pois não temos mais interfaces disponíveis
				if(maxSubNets <= 0)
					break;
			} while (inteiro != 0);
			
			if(inteiros.isEmpty())
				System.out.println("Nenhuma sub-rede para calcular.");
			ipv4.generateAndPrintSubNet(inteiros);
			
			break;
		}
	}

	//lê prefixo de sub rede como valor numerico ou mascara de subrede
	protected static int readPrefixoSubRede(String string, int min, int max){
		int entrada = -1;
		int valor = 0;
		do{
			System.out.println(string);
			//lê string
			String stringLida = scan.next().trim();
			try{
				//tenta parsear o valor entrado como um tamanho de prefixo
				entrada = Integer.valueOf(stringLida);
			} catch (NumberFormatException e){
				//caso em que é uma subnet
				String[] subnetMask = stringLida.split("\\.");
				if(subnetMask.length != 4)
					//caso em que a subNet foi escrita de forma inválida
					throw new IllegalArgumentException("Máscara de sub-rede "+stringLida+" não é válida.");
				else if(validaMask(stringLida)){
					//se ela for validada, criamos um obj GenericByte
					GenericByte gb = new GenericByte(stringLida);
					//parseamos pra binário
					String bin = gb.toBinary();
					//usamos métodos da classe String pra ver quantos 0 tem em sequência
					String sufix = bin.substring(bin.indexOf("0"));
					//calculamos o tamanho do prefixo pela quantidade de 0
					valor = 32 - sufix.length();
					if (valor > 30 || valor < 0)
						//caso de tamanho de prefixo é maior que 8 bits
						throw new IllegalArgumentException("Tamanho de prefixo de sub-rede deve ser entre 0 e 30");
					else
						entrada = valor;
				}
			}
		}while(entrada == -1);
		return entrada;
	}
	
	protected static int readPositiveInt(String string, int min, int max) {
		return readPositiveInt(string, min, max, "");
	}

	//função para leitura de numéros positivos entre min e max e usando um complemento de mensagem para comunicar algo
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

	//função para validar a entrada a.b.c.d/x
	private static IPv4 validaEntrada(String abcdx) {
		String[] s = abcdx.split("/");
		//checa se tem uma parte antes e depois da /
		if (s.length != 2)
			throw new InputMismatchException("Você deve entrar com ip no formato a.b.c.d/x.");
		String abcd = s[0];
		Integer valueOf = null;
		try {
			//tenta parsear o valor entrado como um tamanho de prefixo
			valueOf = Integer.valueOf(s[1]);
		} catch (NumberFormatException e) {
			//caso em que é uma subnet
			String[] subnetMask = s[1].split("\\.");
			if(subnetMask.length != 4 && s[1].contains("."))
				//caso em que a subNet foi escrita de forma inválida
				throw new IllegalArgumentException("Máscara de sub-rede "+s[1]+" não é válida.");
			else if(validaMask(s[1])){
				//se ela for validada, criamos um obj GenericByte
				GenericByte gb = new GenericByte(s[1]);
				//parseamos pra binário
				String bin = gb.toBinary();
				//usamos métodos da classe String pra ver quantos 0 tem em sequência
				String sufix = bin.substring(bin.indexOf("0"));
				//calculamos o tamanho do prefixo pela quantidade de 0
				valueOf = 32 - sufix.length();
				if (valueOf > 30 || valueOf < 0)
					//caso de tamanho de prefixo é maior que 8 bits
					throw new IllegalArgumentException("Tamanho de prefixo de sub-rede deve ser entre 0 e 30");
			}
			else
				//caso em que os valores não são numéricos
				throw new NumberFormatException("Os valores de a, b, c, d e x são numericos.");
		}
		return new IPv4(abcd, valueOf);
	}
			   
	private static boolean validaMask(String s){
		
		GenericByte gb = null;
		try{
			gb = new GenericByte(s);
		} catch(NumberFormatException e){
			throw new NumberFormatException("Os valores de a, b, c, d e x são numericos.");
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
