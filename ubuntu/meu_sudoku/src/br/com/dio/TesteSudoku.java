package br.com.dio;

import br.com.dio.util.GeradorSudoku;
import java.lang.reflect.Method;

public class TesteSudoku {

    public static void main(String[] argumentos) throws Exception {
        GeradorSudoku gerador = new GeradorSudoku();

        // Gera um quebra-cabeça com 35 células preenchidas
        int[][] quebraCabeca = gerador.criarQuebraCabeca(35);

        // Imprime o quebra-cabeça como uma grade 9x9
        System.out.println("Quebra-Cabeça Sudoku Gerado (35 células preenchidas):");
        System.out.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
        for (int r = 0; r < 9; r++) {
            System.out.print("| ");
            for (int c = 0; c < 9; c++) {
                System.out.print((quebraCabeca[r][c] == 0 ? "." : quebraCabeca[r][c]) + " ");
                if ((c + 1) % 3 == 0) System.out.print("| ");
            }
            System.out.println();
            if ((r + 1) % 3 == 0 && r < 8) {
                System.out.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
            }
        }
        System.out.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");

        // Usa reflexão para acessar o método privado contarSolucoes
        Method metodo = GeradorSudoku.class.getDeclaredMethod("contarSolucoes", int[][].class);
        metodo.setAccessible(true);

        // Cria uma cópia do quebra-cabeça para passar ao contarSolucoes
        int[][] copia = new int[9][9];
        for (int i = 0; i < 9; i++) {
            copia[i] = quebraCabeca[i].clone();
        }

        int solucoes = (Integer) metodo.invoke(gerador, (Object) copia);

        boolean valido = (solucoes == 1);
        System.out.println();
        System.out.println("Número de soluções: " + solucoes);
        System.out.println("Quebra-cabeça está " + (valido ? "VÁLIDO (solução única)" : "INVÁLIDO (não tem solução única)"));
    }
}
