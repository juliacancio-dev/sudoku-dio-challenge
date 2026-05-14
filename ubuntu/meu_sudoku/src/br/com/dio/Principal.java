package br.com.dio;

import br.com.dio.modelo.Tabuleiro;
import br.com.dio.modelo.Espaco;
import br.com.dio.util.GeradorSudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.dio.util.ModeloTabuleiro.MODELOTABULEIRO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Principal {

    private final static Scanner scanner = new Scanner(System.in);

    private static Tabuleiro tabuleiro;

    private final static int LIMITE_TABULEIRO = 9;

    public static void main(String[] argumentos) {
        Map<String, String> posicoes;
        if (argumentos.length == 0) {
            posicoes = gerarQuebraCabecaAleatorio(35);
            System.out.println("Novo jogo com quebra-cabeça aleatório iniciado!");
        } else {
            posicoes = Stream.of(argumentos)
                    .collect(Collectors.toMap(k -> k.split(";")[0], v -> v.split(";")[1]));
        }
        var opcao = -1;
        while (true){
            System.out.println("Selecione uma das opções a seguir");
            System.out.println("1 - Iniciar um novo Jogo");
            System.out.println("2 - Colocar um novo número");
            System.out.println("3 - Remover um número");
            System.out.println("4 - Visualizar jogo atual");
            System.out.println("5 - Verificar status do jogo");
            System.out.println("6 - limpar jogo");
            System.out.println("7 - Finalizar jogo");
            System.out.println("8 - Sair");

            opcao = lerOpcaoMenu();

            switch (opcao){
                case 1 -> iniciarJogo(posicoes);
                case 2 -> inserirNumero();
                case 3 -> removerNumero();
                case 4 -> exibirJogoAtual();
                case 5 -> exibirStatusJogo();
                case 6 -> limparJogo();
                case 7 -> finalizarJogo();
                case 8 -> System.exit(0);
                default -> System.out.println("Opção inválida, selecione uma das opções do menu");
            }
        }
    }

    private static int lerOpcaoMenu() {
        while (true) {
            var linha = scanner.nextLine().trim();
            if (linha.isEmpty()) {
                System.out.println("Entrada inválida! Digite apenas o número da opção desejada.");
                continue;
            }
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException excecao) {
                System.out.println("Entrada inválida! Digite apenas o número da opção desejada.");
            }
        }
    }

    private static Map<String, String> gerarQuebraCabecaAleatorio(int celulasPreenchidas) {
        GeradorSudoku gerador = new GeradorSudoku();
        int[][] quebraCabeca = gerador.criarQuebraCabeca(celulasPreenchidas);
        Map<String, String> configuracao = new java.util.HashMap<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (quebraCabeca[i][j] != 0) {
                    configuracao.put(String.format("%d,%d", i, j), String.format("%d,true", quebraCabeca[i][j]));
                }
            }
        }
        return configuracao;
    }

    private static void iniciarJogo(final Map<String, String> posicoes) {
        if (nonNull(tabuleiro)){
            System.out.println("O jogo já foi iniciado");
            return;
        }

        List<List<Espaco>> espacos = new ArrayList<>();
        for (int i = 0; i < LIMITE_TABULEIRO; i++) {
            espacos.add(new ArrayList<>());
            for (int j = 0; j < LIMITE_TABULEIRO; j++) {
                var configPosicao = posicoes.get("%s,%s".formatted(i, j));
                if (configPosicao == null || configPosicao.trim().isEmpty()) {
                    configPosicao = "0,false";
                }
                var esperado = Integer.parseInt(configPosicao.split(",")[0]);
                var fixo = Boolean.parseBoolean(configPosicao.split(",")[1]);
                var espacoAtual = new Espaco(esperado, fixo, j, i);
                espacos.get(i).add(espacoAtual);
            }
        }

        tabuleiro = new Tabuleiro(espacos);
        System.out.println("O jogo está pronto para começar");
    }


    private static void inserirNumero() {
        if (isNull(tabuleiro)){
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        System.out.println("Informe a coluna em que o número será inserido");
        var coluna = obterNumeroValido(0, 8);
        System.out.println("Informe a linha em que o número será inserido");
        var linha = obterNumeroValido(0, 8);
        System.out.printf("Informe o número que vai entrar na posição [%s,%s]\n", coluna, linha);
        var valor = obterNumeroValido(1, 9);
        if (!tabuleiro.mudarValor(coluna, linha, valor)){
            System.out.printf("A posição [%s,%s] tem um valor fixo\n", coluna, linha);
        }
    }

    private static void removerNumero() {
        if (isNull(tabuleiro)){
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        System.out.println("Informe a coluna em que o número será removido");
        var coluna = obterNumeroValido(0, 8);
        System.out.println("Informe a linha em que o número será removido");
        var linha = obterNumeroValido(0, 8);
        if (!tabuleiro.limparValor(coluna, linha)){
            System.out.printf("A posição [%s,%s] tem um valor fixo\n", coluna, linha);
        }
    }

    private static void exibirJogoAtual() {
        if (isNull(tabuleiro)){
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        Object[] valores = new Object[81];
        var posicaoArg = 0;
        for (int i = 0; i < LIMITE_TABULEIRO; i++) {
            for (var coluna : tabuleiro.obterEspacos()){
                valores[posicaoArg ++] = " " + ((isNull(coluna.get(i).obterAtual())) ? " " : coluna.get(i).obterAtual());
            }
        }
        System.out.println("Seu jogo se encontra da seguinte forma");
        System.out.printf((MODELOTABULEIRO) + "\n", valores);
    }

    private static void exibirStatusJogo() {
        if (isNull(tabuleiro)){
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        System.out.printf("O jogo atualmente se encontra no status %s\n", tabuleiro.obterStatus().obterRotulo());
        if(tabuleiro.temErros()){
            System.out.println("O jogo contém erros");
        } else {
            System.out.println("O jogo não contém erros");
        }
    }

    private static void limparJogo() {
        if (isNull(tabuleiro)){
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        System.out.println("Tem certeza que deseja limpar seu jogo e perder todo seu progresso?");
        var confirmar = scanner.next();
        while (!confirmar.equalsIgnoreCase("sim") && !confirmar.equalsIgnoreCase("não")){
            System.out.println("Informe sim ou não");
            confirmar = scanner.next();
        }

        if(confirmar.equalsIgnoreCase("sim")){
            tabuleiro.reiniciar();
        }
    }

    private static void finalizarJogo() {
        if (isNull(tabuleiro)){
            System.out.println("O jogo ainda não foi iniciado");
            return;
        }

        if (tabuleiro.jogoConcluido()){
            System.out.println("Parabéns você concluiu o jogo");
            exibirJogoAtual();
            tabuleiro = null;
        } else if (tabuleiro.temErros()) {
            System.out.println("Seu jogo contém erros, verifique seu tabuleiro e ajuste-o");
        } else {
            System.out.println("Você ainda precisa preencher algum espaço");
        }
    }


    private static int obterNumeroValido(final int min, final int max){
        while (true) {
            var linha = scanner.nextLine().trim();
            if (linha.isEmpty()) {
                System.out.printf("Informe um número entre %s e %s\n", min, max);
                continue;
            }
            try {
                var atual = Integer.parseInt(linha);
                if (atual < min || atual > max) {
                    System.out.printf("Informe um número entre %s e %s\n", min, max);
                    continue;
                }
                return atual;
            } catch (NumberFormatException excecao) {
                System.out.printf("Entrada inválida! Informe um número entre %s e %s\n", min, max);
            }
        }
    }
}
