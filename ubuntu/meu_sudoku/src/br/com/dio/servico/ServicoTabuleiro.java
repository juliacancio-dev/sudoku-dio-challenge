package br.com.dio.servico;

import br.com.dio.modelo.Tabuleiro;
import br.com.dio.modelo.StatusJogoEnum;
import br.com.dio.modelo.Espaco;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServicoTabuleiro {

    private final static int LIMITE_TABULEIRO = 9;
    private static final int MAXIMO_VIDAS = 3;

    private final Tabuleiro tabuleiro;
    private int vidas;

    public ServicoTabuleiro(final Map<String, String> configuracaoJogo) {
        this.tabuleiro = new Tabuleiro(inicializarTabuleiro(configuracaoJogo));
        this.vidas = MAXIMO_VIDAS;
    }

    public List<List<Espaco>> obterEspacos(){
        return tabuleiro.obterEspacos();
    }

    public Tabuleiro obterTabuleiro() {
        return tabuleiro;
    }

    public void reiniciar(){
        tabuleiro.reiniciar();
        this.vidas = MAXIMO_VIDAS;
    }

    public boolean temErros(){
        return tabuleiro.temErros();
    }

    public StatusJogoEnum obterStatus(){
        return tabuleiro.obterStatus();
    }

    public boolean jogoConcluido(){
        return tabuleiro.jogoConcluido();
    }

    public boolean definirValorCelula(int linha, int coluna, int valor) {
        return tabuleiro.mudarValor(coluna, linha, valor);
    }

    public boolean limparCelula(int linha, int coluna) {
        return tabuleiro.limparValor(coluna, linha);
    }

    public int obterVidas() {
        return vidas;
    }

    public void perderVida() {
        if (vidas > 0) {
            vidas--;
        }
    }

    public boolean temVidas() {
        return vidas > 0;
    }

    private List<List<Espaco>> inicializarTabuleiro(final Map<String, String> configuracaoJogo) {
        List<List<Espaco>> espacos = new ArrayList<>();
        for (int coluna = 0; coluna < LIMITE_TABULEIRO; coluna++) {
            espacos.add(new ArrayList<>());
            for (int linha = 0; linha < LIMITE_TABULEIRO; linha++) {
                var configPosicao = configuracaoJogo.get("%s,%s".formatted(linha, coluna));
                if (configPosicao == null || configPosicao.trim().isEmpty()) {
                    configPosicao = "0,false";
                }
                var esperado = Integer.parseInt(configPosicao.split(",")[0]);
                var fixo = Boolean.parseBoolean(configPosicao.split(",")[1]);
                var espacoAtual = new Espaco(esperado, fixo, linha, coluna);
                espacos.get(coluna).add(espacoAtual);
            }
        }

        return espacos;
    }
}
