package br.com.dio.modelo;

import br.com.dio.util.ValidadorSudoku;
import java.util.Collection;
import java.util.List;

import static br.com.dio.modelo.StatusJogoEnum.COMPLETO;
import static br.com.dio.modelo.StatusJogoEnum.INCOMPLETO;
import static br.com.dio.modelo.StatusJogoEnum.NAO_INICIADO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Tabuleiro {

    private final List<List<Espaco>> espacos;

    public Tabuleiro(final List<List<Espaco>> espacos) {
        this.espacos = espacos;
    }

    public List<List<Espaco>> obterEspacos() {
        return espacos;
    }

    public StatusJogoEnum obterStatus(){
        if (espacos.stream().flatMap(Collection::stream).noneMatch(s -> !s.ehFixo() && nonNull(s.obterAtual()))){
            return NAO_INICIADO;
        }

        return espacos.stream().flatMap(Collection::stream).anyMatch(s -> isNull(s.obterAtual())) ? INCOMPLETO : COMPLETO;
    }

    public boolean temErros(){
        if(obterStatus() == NAO_INICIADO){
            return false;
        }

        if (!ValidadorSudoku.eValido(this)) {
            return true;
        }

        return espacos.stream().flatMap(Collection::stream)
                .anyMatch(s -> nonNull(s.obterAtual()) && !s.obterAtual().equals(s.obterEsperado()));
    }

    public boolean mudarValor(final int coluna, final int linha, final int valor){
        var espaco = espacos.get(coluna).get(linha);
        if (espaco.ehFixo()){
            return false;
        }

        if (ValidadorSudoku.temConflitos(this, linha, coluna, valor)) {
            return false;
        }

        espaco.definirAtual(valor);
        return true;
    }

    public boolean limparValor(final int coluna, final int linha){
        var espaco = espacos.get(coluna).get(linha);
        if (espaco.ehFixo()){
            return false;
        }

        espaco.limparEspaco();
        return true;
    }

    public void reiniciar(){
        espacos.forEach(c -> c.forEach(Espaco::limparEspaco));
    }

    public boolean jogoConcluido(){
        return !temErros() && obterStatus().equals(COMPLETO);
    }

}

