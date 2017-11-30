package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Classe que guarda 4 bytes e tamanho de prefixo da sub-rede para representar
 * endereços no formato a.b.c.d/x
 */
public class IPv4 extends GenericByte {

	/**
	 * tamanho do prefixo da sub-rede
	 */
	private int x;
	/**
	 * Máscara de sub-rede
	 */
	private GenericByte mascaraSubRede;
	/**
	 * Endereço da rede
	 */
	private GenericByte endDaRede;
	/**
	 * Endereço de broadcast
	 */
	private GenericByte endBroadCast;
	/**
	 * Menor endereço atribuível
	 */
	private GenericByte firstEnd;

	/**
	 * Maior endereço atribuível
	 */
	private GenericByte lastEnd;
	/**
	 * Número máximo de endereços atribuíveis
	 */
	private Integer maxSubNets;

	/**
	 * @return O número total de endereços atribuíveis a interfaces nessa
	 *         sub-rede.
	 */
	public Integer getMaxSubNets() {
		return maxSubNets;
	}

	/**
	 * Construtor da classe ipv4
	 * 
	 * @param abcd
	 *            ip válido no formato a.b.c.d
	 * @param x
	 *            tamanho do prefixo da sub-rede.
	 */
	public IPv4(String abcd, int x) {
		super(abcd);
		if (x > 30 || x < 0)
			throw new IllegalArgumentException("Tamanho de prefixo de sub-rede deve ser entre 0 e 30");
		this.x = x;
		maxSubNets = (int) Math.pow(2, 32 - x) - 2;
		gerarMascSubRede();
		gerarEndDaRede();
		gerarEndBroadCast();
		gerarFirstEnd();
		gerarLastEnd();
	}

	/**
	 * Gera último endereço possível nessa sub-rede diminuindo 1 do valor do
	 * endereço de broadcast
	 */
	private void gerarLastEnd() {
		//primeiro endereço é o de broadcast - 1
		Long endDec = this.endBroadCast.toLong() - 1;
		//parseia para decimal formatado a.b.c.d
		//seta objeto GenericByte com o endereço correto
		String bin = Long.toBinaryString(endDec);
		String stringBin = binToDecFormated(bin);
		this.lastEnd = new GenericByte(stringBin);
	}

	/**
	 * Gera primeiro endereço possível nessa sub-rede somando 1 do valor do
	 * endereço de sub-rede
	 */
	private void gerarFirstEnd() {
		//primeiro endereço é o de rede + 1
		Long endDec = this.endDaRede.toLong() + 1;
		//parseia para decimal formatado a.b.c.d
		//seta objeto GenericByte com o endereço correto
		String bin = Long.toBinaryString(endDec);
		String stringBin = binToDecFormated(bin);
		this.firstEnd = new GenericByte(stringBin);
	}

	/**
	 * Gera endereço de broadcast trocando os bits de sufixo para 1
	 */
	private void gerarEndBroadCast() {
		//endereço de broadcast é o de rede com os bits de sufixo setados para 1
		this.endBroadCast = trocarXBitsADireta(super.toString(), "1");
	}

	/**
	 * Gera endereço de sub-rede trocando os bits de sufixo para 0
	 */
	private void gerarEndDaRede() {
		//endereço de broadcast é o de rede com os bits de sufixo setados para 0
		this.endDaRede = trocarXBitsADireta(super.toString(), "0");
	}

	/**
	 * Gera máscara de sub-rede trocando os bits de sufixo para 0 de um ip
	 * 255.255.255.255
	 */
	private void gerarMascSubRede() {
		this.mascaraSubRede = trocarXBitsADireta("255.255.255.255", "0");
	}

	/**
	 * Recurso para trocar os bits de sufixo para 0 ou 1
	 * 
	 * @param ipformatado
	 *            ip formatdo a.b.c.d em decimal
	 * @param bin
	 *            string representando valor a colocar a direita 0 ou 1
	 * @return GenericByte a.b.c.d com os bits de sufixo trocados para bin
	 */
	private GenericByte trocarXBitsADireta(String ipformatado, String bin) {
		GenericByte gb = new GenericByte(ipformatado);
		StringBuilder sb = new StringBuilder();
		//coloca os bits que você nao quer trocar a esquerda
		sb.append(gb.toBinary().substring(0, x));
		//vai colocar o que foi passado no bin a direita até dar 32 bits
		for (int i = 0; i < 32 - x; i++) {
			sb.append(bin);
		}
		//parseia para decimal formatado a.b.c.d
		//devolve objeto GenericByte com as mudanças
		String abcd = sb.toString();
		String s = binToDecFormated(abcd);
		gb = new GenericByte(s);
		return gb;

	}

	/**
	 * Representação em string da classe
	 * 
	 * @return String que representa os 4 bytes e o tamanho do prefixo no
	 *         formato a.b.c.d/x
	 */
	@Override
	public String toString() {
		return super.toString() + "/" + x;
	}

	private String spaces(int n) {
		//colocar numero diferentes de espaços de acordo com n
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 20 - n; i++) {
			sb.append(" ");

		}
		return sb.toString();
	}

	private String formatSpaces(String string) {
		//colocar numero diferentes de espaços de acordo com o tamanho da string
		return string + spaces(string.length());
	}

	/**
	 * Função para imprimir informações pedidas no trabalho , o endereço de
	 * sub-rede (em notação decimal e em binário) , o endereço de broadcast (em
	 * notação decimal e em binário) , a máscara de sub-rede (em notação decimal
	 * e em binário) , o tamanho do prefixo da sub-rede. , o primeiro (i.e.,
	 * menor) endereço atribuível a uma interface (em notação decimal e em
	 * binário) , o último (i.e., maior) endereço atribuível a uma interface (em
	 * notação decimal e em binário) e o número total de endereços atribuíveis a
	 * interfaces naquela sub-rede.
	 */
	public void print() {
		System.out.println("O endereço de sub-rede (em notação decimal e em binário).");
		System.out.println(formatSpaces(this.endDaRede.toString()) + " " + this.toBinaryFormated());
		System.out.println("\nO endereço de broadcast (em notação decimal e em binário).");
		System.out.println(formatSpaces(this.endBroadCast.toString()) + " " + this.endBroadCast.toBinaryFormated());
		System.out.println("\nA máscara de sub-rede (em notação decimal e em binário).");
		System.out.println(formatSpaces(this.mascaraSubRede.toString()) + " " + this.mascaraSubRede.toBinaryFormated());
		System.out.println("\nO tamanho do prefixo da sub-rede.");
		System.out.println(this.x);
		System.out.println(
				"\nO primeiro (i.e., menor) endereço atribuível a uma interface (em notação decimal e em binário).");
		System.out.println(formatSpaces(this.firstEnd.toString()) + " " + this.firstEnd.toBinaryFormated());
		System.out.println(
				"\nO último (i.e., maior) endereço atribuível a uma interface (em notação decimal e em binário).");
		System.out.println(formatSpaces(this.lastEnd.toString()) + " " + this.lastEnd.toBinaryFormated());
		System.out.println("\nO número total de endereços atribuíveis a interfaces naquela sub-rede.");
		System.out.println(maxSubNets + "\n");
	}

	/**
	 * Gera e imprime as informações das sub-redes de acordo com a lista de
	 * entrada do Modo de Divisão em Sub-redes de Tamanho Variável
	 * 
	 * @param valores
	 *            lista com os valores requeridos para as sub-redes
	 */
	public void generateAndPrintSubNet(List<Integer> valores) {
		if (valores.isEmpty())
			return;
		else {
			//caso em que é requerido só um endereço e esse endereço é o maxSubNets+2 é a própria subrede
			if (valores.size() == 1 && valores.get(0).equals(maxSubNets+2)) {
				generateAndPrintSubNet(x);
				return;
			}
		}
		//organiza a lista de forma decrescente e potencia de dois
		valores = parseListaPotenciasOrdenadas(valores);
		//instancia l como o num binário que começa do endereço endereço da rede
		Long l = this.endDaRede.toLong();
		List<IPv4> generatedSubNets = new ArrayList<>();
		for (Integer integer : valores) {
			//pega o expoente da menor potencia de dois maior ou igual ao numero de endereços de subrede
			//pois esse valor é o num de sufixo
			//exemplo 64 tem expoente 6
			int expoente = expoenteDaMenorPotenciaMaiorOuIgual(integer);
			//gera as subnets a partir de l e com num de prefixo
			//sufixo = 32 - prefixo
			//no exemplo do 64, temos o prefixo /26, pois 32-6 = 26
			//parseamos l que é um valor decimal do tipo long para binary string
			//parseamos a binary string para decimal formatado em a.b.c.d
			//criamos um ipv4 com a.b.c.d/ prefixo calculado
			//adicionamos na lista generatedSubNets
			generatedSubNets.add(new IPv4(binToDecFormated(Long.toBinaryString(l)), 32 - expoente));
			//incrementamos l com o inteiro da lista, dessa forma na proxima execução do loop o endereço de subrede
			//incial será o anterior + o numero de interfaces+2, ou seja o próximo endereço
			//exemplo, 192.168.0.0 como long é 3232235520, se pedirmos algum numero entre 31 e 62 estamos requisitando
			//um endereço que precisa de 64 endereços, ele vai usar o 3232235520 para converter em binary
			//que converte em decimal formatado e usa o expoente de 64 que é 6 para criar o ipv4 192.168.0.0/26
			//depois disso ele soma 3232235520 com 64 que gera 3232235584, que é o próxima sub rede criada
			//que vai gerar um ipv4 192.168.0.64/alguma coisa dependendo da proxima requisição
			//a lógica para não haver overflow das subredes geradas está no main
			l += integer;
		}
		printSubNet(generatedSubNets);
	}

	/**
	 * Lista em ordem descrescentes com as menores potências de dois que são
	 * maior ou igual aos valores passados na entrada no Modo de Divisão em
	 * Sub-redes de Tamanho Variável
	 * 
	 * @param valores
	 *            lista com os valores de entrada
	 * @return lista em ordem descrescente com as menores potências de dois que
	 *         são maior ou igual aos valores da lista inicial
	 */
	public List<Integer> parseListaPotenciasOrdenadas(List<Integer> valores) {
		List<Integer> list = new ArrayList<>();

		//transforma todos valroes em potência de dois
		for (Integer integer : valores) {
			list.add(menorPotenciaMaiorOuIgual(integer));
		}

		//sort de collections e comparator para deixar ordenado de forma decrescente
		Collections.sort(list, new Comparator<Integer>() {
			@Override
			public int compare(Integer arg0, Integer arg1) {
				return arg1 - arg0;
			}

		});

		return list;
	}

	/**
	 * Gera e imprime as informações das sub-redes de acordo com o tamanho de
	 * prefixo passado
	 * 
	 * @param tam
	 *            Tamanho do prefixo das sub-redes
	 * @return verdadeiro caso função tenha executado e falso para exceções
	 *         lançadas
	 * @exception IllegalArgumentException
	 *                quando o tamanho do prefixo das sub-redes é menor que o da
	 *                rede a ser dividida
	 * @exception IllegalArgumentException
	 *                quando o tamanho é inválido (menor que 0 ou maior que 30)
	 */
	public boolean generateAndPrintSubNet(int tam) {
		if (x == tam) {
			System.out.println("1 sub-rede (a própria rede):\n");
			System.out.println("Sub-rede #1:");
			this.print();
			return true;
		}
		if (x > tam) {
			throw new IllegalArgumentException("Sua sub-rede tem prefixo " + this.x
					+ ", você não pode dividí-la em subredes de prefixo com tamanho " + tam + ".");
		}
		if (tam > 30 || tam < 0)
			throw new IllegalArgumentException("Tamanho de prefixo de sub-rede deve ser entre 0 e 30");
		int numDeSubRedesPossiveis = (int) Math.pow(2, tam - x);
		List<IPv4> generatedSubNet = generateSubNet(numDeSubRedesPossiveis);
		printSubNet(generatedSubNet);
		return true;
	}

	/**
	 * Imprime sub-redes na mesma formatação do print()
	 * 
	 * @param generatedSubNet
	 *            lista de sub-redes geradas
	 * @see #print()
	 */
	protected void printSubNet(List<IPv4> generatedSubNet) {
		int n = 1;
		if (generatedSubNet.size() == 1)
			System.out.println(generatedSubNet.size() + " sub-rede:\n");
		else
			System.out.println(generatedSubNet.size() + " sub-redes:\n");
		for (IPv4 iPv4 : generatedSubNet) {
			System.out.println("Sub-rede #" + n + ":");
			iPv4.print();
			n++;
		}
	}

	/**
	 * Gera um número de subredes com o melhor aproveitamento possível, sempre
	 * subdividindo nas sub-redes que podem atribuir o maior número possível
	 * endereços
	 * 
	 * @param num
	 *            número de sub-redes que se pretende criar
	 * @return lista de sub-redes geradas
	 */
	protected List<IPv4> generateSubNet(int num) {
		String prefixo = endDaRede.toBinary().substring(0, x);
		String sufixo = endDaRede.toBinary().substring(x);

		HashMap<String, Integer> mapa = new HashMap<>();
		mapa.put(sufixo, 0);
		function(mapa, num);

		List<IPv4> listIps = new ArrayList<>();
		List<String> list = new ArrayList<>(mapa.keySet());
		Collections.sort(list);

		for (String sufixoSubNet : list) {
			String ip = prefixo + sufixoSubNet;
			String ipDecFormated = binToDecFormated(ip);
			IPv4 iPv4 = new IPv4(ipDecFormated, x + mapa.get(sufixoSubNet));
			listIps.add(iPv4);
		}
		return listIps;
	}

	/**
	 * Função que gera novos sufixos a partir do sufixo antigo(K) e um valor
	 * associado(V) que indica a posição do sufixo que podemos dividir em duas,
	 * a função é recursiva e para quando temos valores no mapa que sejam
	 * suficientes de acordo com o parâmetro n
	 * 
	 * @param mapa
	 *            Mapa Key,Value: K = sufixo, V = posição que podemos alterar;
	 * @param n
	 *            número de sub-redes que queremos
	 */
	public void function(HashMap<String, Integer> mapa, int n) {
		//enquanto o mapa não tem o n que você quer ele fica rodando recursivamente
		if (mapa.size() < n) {
			//cria um set com as chaves que são valores binários
			Set<String> set = new HashSet<>(mapa.keySet());
			for (String stringUm : set) {
				//pra cada chave pega os valores que so o índice que pode ser trocado
				Integer i = mapa.get(stringUm);
				//gera um segundo valor binário trocando de 0 para 1 de acordo com o indice
				String stringDois = "";
				if (i == 0)
					stringDois = "1" + stringUm.substring(1);
				else
					stringDois = stringUm.substring(0, i) + "1" + stringUm.substring(i + 1);
				//coloca a string antiga com o índice + 1 e a nova com o índice + 1
				mapa.put(stringUm, i + 1);
				mapa.put(stringDois, i + 1);
				//isso nunca será usado pois só precisamos quebrar em potências 2,
				//porem essa função serve para divises não potências de 2
				if (mapa.size() >= n)
					break;
			}
			function(mapa, n);
		}
	}

	public static int menorPotenciaMaiorOuIgual(int i) {
		return (int) Math.pow(2, expoenteDaMenorPotenciaMaiorOuIgual(i));
	}

	public static int expoenteDaMenorPotenciaMaiorOuIgual(int i) {
		return (int) Math.ceil(Math.log(i) / Math.log(2));
	}

}
