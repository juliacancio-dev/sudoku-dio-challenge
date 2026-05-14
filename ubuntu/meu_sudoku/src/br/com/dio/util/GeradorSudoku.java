package br.com.dio.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GeradorSudoku {
    private static final int TAMANHO = 9;
    private static final int SUBGRADE = 3;
    private Random aleatorio = new Random();

    public int[][] gerarCompleto() {
        int[][] tab = new int[TAMANHO][TAMANHO];
        preencherTabuleiro(tab);
        return tab;
    }

    private boolean preencherTabuleiro(int[][] tab) {
        for (int linha = 0; linha < TAMANHO; linha++) {
            for (int coluna = 0; coluna < TAMANHO; coluna++) {
                if (tab[linha][coluna] == 0) {
                    List<Integer> numeros = obterEmbaralhados();
                    for (int num : numeros) {
                        if (valido(tab, linha, coluna, num)) {
                            tab[linha][coluna] = num;
                            if (preencherTabuleiro(tab)) {
                                return true;
                            }
                            tab[linha][coluna] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private List<Integer> obterEmbaralhados() {
        List<Integer> numeros = new ArrayList<>();
        for (int i = 1; i <= TAMANHO; i++) {
            numeros.add(i);
        }
        Collections.shuffle(numeros, this.aleatorio);
        return numeros;
    }

    private boolean valido(int[][] tab, int linha, int coluna, int num) {
        for (int i = 0; i < TAMANHO; i++) {
            if (tab[linha][i] == num) return false;
        }
        for (int i = 0; i < TAMANHO; i++) {
            if (tab[i][coluna] == num) return false;
        }
        int blLinha = linha - linha % SUBGRADE;
        int blColuna = coluna - coluna % SUBGRADE;
        for (int r = blLinha; r < blLinha + SUBGRADE; r++) {
            for (int c = blColuna; c < blColuna + SUBGRADE; c++) {
                if (tab[r][c] == num) return false;
            }
        }
        return true;
    }

    public int[][] criarQuebraCabeca(int preenchidas) {
        int[][] solucao = gerarCompleto();
        int[][] quebraCabeca = new int[TAMANHO][TAMANHO];
        for (int i = 0; i < TAMANHO; i++) {
            quebraCabeca[i] = solucao[i].clone();
        }
        List<Integer> posicoes = new ArrayList<>();
        for (int i = 0; i < TAMANHO * TAMANHO; i++) {
            posicoes.add(i);
        }
        Collections.shuffle(posicoes, this.aleatorio);
        int remover = TAMANHO * TAMANHO - preenchidas;
        int removidas = 0;
        for (int indice = 0; indice < posicoes.size() && removidas < remover; indice++) {
            int pos = posicoes.get(indice);
            int linha = pos / TAMANHO;
            int coluna = pos % TAMANHO;
            int valorAnterior = quebraCabeca[linha][coluna];
            quebraCabeca[linha][coluna] = 0;
            int[][] copia = new int[TAMANHO][TAMANHO];
            for (int i = 0; i < TAMANHO; i++) {
                copia[i] = quebraCabeca[i].clone();
            }
            int solucoes = contarSolucoes(copia);
            if (solucoes != 1) {
                quebraCabeca[linha][coluna] = valorAnterior;
            } else {
                removidas++;
            }
        }
        return quebraCabeca;
    }

    private int contarSolucoes(int[][] tabuleiro) {
        int[] contador = new int[1];
        contador[0] = 0;
        resolverContar(tabuleiro, contador);
        return contador[0];
    }

    private void resolverContar(int[][] tabuleiro, int[] contador) {
        if (contador[0] >= 2) return;
        for (int r = 0; r < TAMANHO; r++) {
            for (int c = 0; c < TAMANHO; c++) {
                if (tabuleiro[r][c] == 0) {
                    for (int n = 1; n <= TAMANHO; n++) {
                        if (valido(tabuleiro, r, c, n)) {
                            tabuleiro[r][c] = n;
                            resolverContar(tabuleiro, contador);
                            tabuleiro[r][c] = 0;
                        }
                    }
                    return;
                }
            }
        }
        contador[0]++;
    }
}
