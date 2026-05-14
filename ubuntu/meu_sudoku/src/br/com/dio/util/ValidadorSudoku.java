package br.com.dio.util;

import br.com.dio.modelo.Tabuleiro;
import br.com.dio.modelo.Espaco;

import java.util.List;

public class ValidadorSudoku {

    private static final int TAMANHO = 9;
    private static final int SUBGRADE = 3;

    public static boolean eValido(Tabuleiro tabuleiro) {
        List<List<Espaco>> espacos = tabuleiro.obterEspacos();

        // Validar todas as linhas
        for (int linha = 0; linha < TAMANHO; linha++) {
            if (!linhaValida(espacos, linha)) {
                return false;
            }
        }

        // Validar todas as colunas
        for (int coluna = 0; coluna < TAMANHO; coluna++) {
            if (!colunaValida(espacos, coluna)) {
                return false;
            }
        }

        // Validar todos os blocos 3x3
        for (int blLinha = 0; blLinha < TAMANHO; blLinha += SUBGRADE) {
            for (int blColuna = 0; blColuna < TAMANHO; blColuna += SUBGRADE) {
                if (!blocoValido(espacos, blLinha, blColuna)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean linhaValida(List<List<Espaco>> espacos, int linha) {
        boolean[] vistos = new boolean[TAMANHO + 1]; // índices 1-9
        for (int coluna = 0; coluna < TAMANHO; coluna++) {
            Espaco espaco = espacos.get(coluna).get(linha);
            Integer valor = espaco.obterAtual();
            if (valor != null && valor >= 1 && valor <= 9) {
                if (vistos[valor]) {
                    return false; // Duplicata na linha
                }
                vistos[valor] = true;
            }
        }
        return true;
    }

    private static boolean colunaValida(List<List<Espaco>> espacos, int coluna) {
        boolean[] vistos = new boolean[TAMANHO + 1]; // índices 1-9
        for (int linha = 0; linha < TAMANHO; linha++) {
            Espaco espaco = espacos.get(coluna).get(linha);
            Integer valor = espaco.obterAtual();
            if (valor != null && valor >= 1 && valor <= 9) {
                if (vistos[valor]) {
                    return false; // Duplicata na coluna
                }
                vistos[valor] = true;
            }
        }
        return true;
    }

    private static boolean blocoValido(List<List<Espaco>> espacos, int inicioLinha, int inicioColuna) {
        boolean[] vistos = new boolean[TAMANHO + 1]; // índices 1-9
        for (int r = inicioLinha; r < inicioLinha + SUBGRADE; r++) {
            for (int c = inicioColuna; c < inicioColuna + SUBGRADE; c++) {
                Espaco espaco = espacos.get(c).get(r);
                Integer valor = espaco.obterAtual();
                if (valor != null && valor >= 1 && valor <= 9) {
                    if (vistos[valor]) {
                        return false; // Duplicata no bloco
                    }
                    vistos[valor] = true;
                }
            }
        }
        return true;
    }

    public static boolean temDuplicatasNaLinha(List<List<Espaco>> espacos, int linha) {
        return !linhaValida(espacos, linha);
    }

    public static boolean temDuplicatasNaColuna(List<List<Espaco>> espacos, int coluna) {
        return !colunaValida(espacos, coluna);
    }

    public static boolean temDuplicatasNoBloco(List<List<Espaco>> espacos, int linha, int coluna) {
        int blLinha = (linha / SUBGRADE) * SUBGRADE;
        int blColuna = (coluna / SUBGRADE) * SUBGRADE;
        return !blocoValido(espacos, blLinha, blColuna);
    }

    public static boolean temConflitos(Tabuleiro tabuleiro, int linha, int coluna, int valor) {
        List<List<Espaco>> espacos = tabuleiro.obterEspacos();

        // Verificar linha
        for (int c = 0; c < TAMANHO; c++) {
            if (c != coluna) {
                Espaco outro = espacos.get(c).get(linha);
                if (outro.obterAtual() != null && outro.obterAtual() == valor) {
                    return true;
                }
            }
        }

        // Verificar coluna
        for (int r = 0; r < TAMANHO; r++) {
            if (r != linha) {
                Espaco outro = espacos.get(coluna).get(r);
                if (outro.obterAtual() != null && outro.obterAtual() == valor) {
                    return true;
                }
            }
        }

        // Verificar bloco 3x3
        int blLinha = (linha / SUBGRADE) * SUBGRADE;
        int blColuna = (coluna / SUBGRADE) * SUBGRADE;
        for (int r = blLinha; r < blLinha + SUBGRADE; r++) {
            for (int c = blColuna; c < blColuna + SUBGRADE; c++) {
                if (r != linha || c != coluna) {
                    Espaco outro = espacos.get(c).get(r);
                    if (outro.obterAtual() != null && outro.obterAtual() == valor) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
