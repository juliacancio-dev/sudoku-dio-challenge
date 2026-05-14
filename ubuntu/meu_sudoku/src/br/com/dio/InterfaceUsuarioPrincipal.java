package br.com.dio;

import br.com.dio.ui.personalizado.screen.TelaPrincipal;
import br.com.dio.util.GeradorSudoku;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InterfaceUsuarioPrincipal {

    public static void main(String[] argumentos) {
        Map<String, String> configuracaoJogo;
        if (argumentos.length == 0) {
            configuracaoJogo = gerarQuebraCabecaAleatorio(35);
        } else {
            configuracaoJogo = Stream.of(argumentos)
                    .collect(Collectors.toMap(k -> k.split(";")[0], v -> v.split(";")[1]));
        }
        var telaPrincipal = new TelaPrincipal(configuracaoJogo);
        telaPrincipal.construirTelaPrincipal();
    }

    private static Map<String, String> gerarQuebraCabecaAleatorio(int celulasPreenchidas) {
        GeradorSudoku gerador = new GeradorSudoku();
        int[][] quebraCabeca = gerador.criarQuebraCabeca(celulasPreenchidas);
        Map<String, String> configuracao = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (quebraCabeca[i][j] != 0) {
                    configuracao.put(String.format("%d,%d", i, j), String.format("%d,true", quebraCabeca[i][j]));
                }
            }
        }
        return configuracao;
    }
}
