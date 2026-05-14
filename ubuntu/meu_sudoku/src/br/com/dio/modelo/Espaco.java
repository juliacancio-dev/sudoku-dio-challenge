package br.com.dio.modelo;

public class Espaco {

    private Integer atual;
    private final int esperado;
    private final boolean fixo;
    private final int linha;
    private final int coluna;

    public Espaco(final int esperado, final boolean fixo, final int linha, final int coluna) {
        this.esperado = esperado;
        this.fixo = fixo;
        this.linha = linha;
        this.coluna = coluna;
        if (fixo){
            atual = esperado;
        }
    }

    public int obterLinha() {
        return linha;
    }

    public int obterColuna() {
        return coluna;
    }

    public Integer obterAtual() {
        return atual;
    }

    public void definirAtual(final Integer atual) {
        if (fixo) return;
        this.atual = atual;
    }

    public void limparEspaco(){
        definirAtual(null);
    }

    public int obterEsperado() {
        return esperado;
    }

    public boolean ehFixo() {
        return fixo;
    }
}
