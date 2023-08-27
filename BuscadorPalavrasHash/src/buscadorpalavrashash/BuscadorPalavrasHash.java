package buscadorpalavrashash;

import comparator.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class BuscadorPalavrasHash {

    static String PATH_FILE_STOP_WORDS = "src/arquivos/stopWordsPT-BR.txt"; //https://gist.github.com/alopes/5358189
    static ArrayList<String> LIST_STOP_WORDS = new ArrayList();
    static int HASHMOD = 701;
    static int BASE = 128; //base de acordo com a tabela ascii

    //seleciona um diretório raiz por meio de uma janela de seleção de arquivo e retorna o diretório selecionado
    public static File selectRootDirectory() {
        JFileChooser janelaSelecao = new JFileChooser(".");
        janelaSelecao.setControlButtonsAreShown(true);

        //conf. do filtro de selecao
        janelaSelecao.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File arquivo) {
                return arquivo.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Diretório";
            }
        });

        janelaSelecao.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        //avaliando a ação do usuario na selecao da pasta de inicio da busca
        int acao = janelaSelecao.showOpenDialog(null);

        if (acao == JFileChooser.APPROVE_OPTION) {
            return janelaSelecao.getSelectedFile();
        } else {
            return null;
        }
    }

    //inserir stop Words do arq. txt dentro no projeto para um ArrayList 
    public static void insertListStopWord() {
        try {
            //variáveis para permitir a leitura do arq. txt
            FileReader marcaLeitura = new FileReader(PATH_FILE_STOP_WORDS);
            BufferedReader bufLeitura = new BufferedReader(marcaLeitura);

            String linha = bufLeitura.readLine();
            while (linha != null) {
                //separar a linha em palavras sempere que houver espaços em branco
                String palavras[] = linha.split("[\\s+]");

                for (String palavrasLinha : palavras) {
                    LIST_STOP_WORDS.add(palavrasLinha);
                }

                linha = bufLeitura.readLine(); //ler próxima linha
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo");
        }
    }

    //nova hash function que transforma a palavra String encontrada em um id inteiro
    //método utiliza a lógica do material da USP
    public static int keyStringToInt(String palavra) {
        int id = 0;
        for (int i = 0; i < palavra.length(); i++) {
            id = (id * BASE + palavra.charAt(i)) % HASHMOD;
        }
        return id;
    }

    /*public static int hashFunction(int id) {
        return id % HASHMOD;
    }*/
    //inserir um objeto do tipo Palavra na hashTable
    public static void insertHash(Palavra palavra, LinkedList<Palavra>[] hashTable) {
        int hashValue = keyStringToInt(palavra.getPalavra());

        if (hashTable[hashValue] == null) {
            hashTable[hashValue] = new LinkedList<>();
        }

        hashTable[hashValue].add(palavra);
    }

    //vericair se uma palavra String está dentro da hash
    public static boolean searchWord(String palavraBusca, LinkedList<Palavra>[] hashTable) {
        int hashValue = keyStringToInt(palavraBusca);

        //palavra não encontrada na hash 
        if (hashTable[hashValue] == null) {
            return false;
        }

        //palavra encontrada na hash 
        for (Palavra elemento : hashTable[hashValue]) {
            if (palavraBusca.equalsIgnoreCase(elemento.getPalavra())) {
                return true;
            }
        }

        return false;
    }

    //imprimir todos os arquivos e mostrar o n° de ocorrências que uma palavra String aparece na hash
    public static void printSearchWord(LinkedList<Palavra>[] hashTable, String palavraBusca) {
        ArrayList<Palavra> palavrasEncontradas = new ArrayList<>();

        //adicionar todos os objetos Palavra dentro de um ArrayList se ele corresponder com a palavra String informada no console
        for (LinkedList<Palavra> list : hashTable) {
            if (list == null) {
                continue;
            }
            for (Palavra palavra : list) {
                if (palavra.getPalavra().equalsIgnoreCase(palavraBusca)) {
                    palavrasEncontradas.add(palavra);
                }
            }
        }

        //ordena objetos Palavra do array com base no número de ocorrências
        Collections.sort(palavrasEncontradas, new OrdenarOcorrencias());

        //imprimir em que arq. txt estão e qual o n° ocorrências 
        for (Palavra palavra : palavrasEncontradas) {
            System.out.println(palavra.printArquivoOcorrencias());
        }
    }

    //imprimir toda a tabela hash
    public static void printHash(LinkedList<Palavra>[] hashTable) {
        int c = 0;
        for (LinkedList<Palavra> list : hashTable) {
            System.out.print("[" + c + "] ");
            c++;
            if (list == null) {
                System.out.println();
                continue;
            }
            for (Palavra i : list) {
                System.out.print(i + "->");
            }
            System.out.println();
        }

    }

    public static void main(String[] args) {

        LinkedList<Palavra>[] hashTable = new LinkedList[HASHMOD];//criando a hash table

        insertListStopWord(); //chamar método para inserir as stop words do arq. txt em um ArrayList

        //seleção de um diretorio para iniciar a busca
        File pastaInicial = selectRootDirectory();

        if (pastaInicial == null) {
            JOptionPane.showMessageDialog(null, "Você deve selecionar uma pasta para o processamento",
                    "Selecione o arquivo", JOptionPane.WARNING_MESSAGE);
        } else {
            //array para adicionar o diretório principal no topo da pilha
            ArrayDeque<File> explorar = new ArrayDeque<>();
            explorar.push(pastaInicial);

            //array para armazenar todos os arq. txt encontrados dentro do diretório principal 
            ArrayList<File> listaArquivosTxt = new ArrayList<>();

            //processo de busca pelo arquivo
            while (!explorar.isEmpty()) {
                File diretorioAtual = explorar.pop(); //desempilha o diretorio do topo

                //array que contém os arquivos e diretórios dentro do diretório atual
                File arquivosDir[] = diretorioAtual.listFiles();

                //passando por todos os arquivos e subDiretórios
                for (File arq : arquivosDir) {
                    if (arq.isDirectory()) {
                        explorar.push(arq); //se for diretório, seus subdiretórios precisam ser analisados
                    } else if (arq.getAbsolutePath().endsWith(".txt")) {
                        listaArquivosTxt.add(arq);
                    }
                }

            }

            File arqTexto = null;

            //continuar enquanto houver arq. txt a serem lidos 
            while (!listaArquivosTxt.isEmpty()) {
                arqTexto = listaArquivosTxt.remove(0); //"peguei" o valor da primeira posição

                //armazenar todas as palavras lidas no arq. em um ArrayList
                ArrayList<String> palavrasEncontradasArquivo = new ArrayList<>();

                if (arqTexto != null) {

                    //acessar o conteúdo do arquivo
                    try {
                        //variáveis para permitir a leitura do arq. txt
                        FileReader marcaLeitura = new FileReader(arqTexto);
                        BufferedReader bufLeitura = new BufferedReader(marcaLeitura);

                        //leitura das linhas do arquivo
                        String linha = bufLeitura.readLine();

                        while (linha != null) {
                            //separar a linha em palavras sempere que houver espaços em branco
                            String palavrasLinha[] = linha.split("[\\s+]");

                            //palavra ainda precisa ser tratada para tirar os acentos e os caracteres que possam estar junto dela
                            for (String tratarPalavrasLinha : palavrasLinha) {
                                //remove acentos e caracteres especiais da String 
                                tratarPalavrasLinha = Normalizer.normalize(tratarPalavrasLinha, Normalizer.Form.NFD);

                                //troca qualquer caractere que não está na faixa especificada por uma String vazia
                                palavrasEncontradasArquivo.add(tratarPalavrasLinha.replaceAll("[^A-Za-z0-9]", ""));
                            }

                            linha = bufLeitura.readLine(); //ler próxima linha
                        }

                        //terminou de ler um arquivo a partir daqui
                        //encontrou palavras dentro de um arquivo
                        if (!palavrasEncontradasArquivo.isEmpty()) {

                            //ordenar as palavras ancontradas em ordem alfabética
                            Collections.sort(palavrasEncontradasArquivo, new OrdenarOrdemAlfabetica());

                            //remover as stop words das palavras encontradas no arq texto lido
                            for (String palavra : LIST_STOP_WORDS) { //percorre todo o arq. das stop words
                                if (palavrasEncontradasArquivo.contains(palavra)) {
                                    for (int i = 0; i < palavrasEncontradasArquivo.size(); i++) { //remover palavras repetidas
                                        palavrasEncontradasArquivo.remove(palavra);
                                    }
                                }
                            }
                        }

                        //remover as palavras repetidas do array e contar quantas vezes cada palavra diferente aparece
                        while (!palavrasEncontradasArquivo.isEmpty()) {

                            int ocorrencias = 1;
                            String palavraVerifica = palavrasEncontradasArquivo.remove(0); //verificando palavra da 1° posição

                            for (int i = 0; i < palavrasEncontradasArquivo.size(); i++) {
                                if (palavraVerifica.equalsIgnoreCase(palavrasEncontradasArquivo.get(i))) {
                                    palavrasEncontradasArquivo.remove(i);
                                    ocorrencias++;
                                    i--;
                                }
                            }

                            Palavra novaPalavraHash = new Palavra(palavraVerifica.toLowerCase(), arqTexto, ocorrencias);
                            insertHash(novaPalavraHash, hashTable); //inserindo novo objeto Palavra dentro da hash table
                        }

                    } catch (FileNotFoundException ex) {
                        System.err.println("Arquivo não existe no dir.");
                    } catch (IOException ex) {
                        System.err.println("Seu arquivo está corrompido");
                    }
                }

            }

            //menu no teclado para buscar uma palavra na hash
            Scanner in = new Scanner(System.in);
            String palavraBusca;
            int op = 0;

            //menu em loop
            while (op != 3) {
                System.out.println("\n-------------------\n- MENU -\n\n1)Buscar uma palavra\n2)Imprimir tabela hash\n3)Sair");
                System.out.print("\nEscolha uma opção: ");
                op = in.nextInt();
                in.nextLine(); //limpar buffer

                switch (op) {
                    case 1: //buscar uma palavra
                        System.out.print("\nInforme a palavra que deseja buscar na hash: ");
                        palavraBusca = in.nextLine();
                        
                        //tratando a palavra busca para tirar o acento dela e conseguir achar na tabela hash
                        palavraBusca = Normalizer.normalize(palavraBusca, Normalizer.Form.NFD);
                        palavraBusca = palavraBusca.replaceAll("[^A-Za-z0-9]", "");

                        if (!searchWord(palavraBusca.toLowerCase(), hashTable)) {
                            System.out.println("Palavra não encontrada");
                        } else {
                            printSearchWord(hashTable, palavraBusca.toLowerCase());
                        }

                        break;
                    case 2: //imprimir toda a hash table
                        printHash(hashTable);
                        break;
                    case 3: //sair
                        System.out.println("Fim do programa");
                        break;
                    default:
                        System.err.println("\nErro");
                }
            }
            in.close(); //fechar scanner
        }
    }
}
