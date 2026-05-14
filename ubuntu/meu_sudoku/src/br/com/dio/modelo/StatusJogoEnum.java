package br.com.dio.modelo;

public enum StatusJogoEnum {

    NAO_INICIADO("não iniciado"),
    INCOMPLETO("incompleto"),
    COMPLETO("completo");

    private String rotulo;

    StatusJogoEnum(final String rotulo){
        this.rotulo = rotulo;
    }

    public String obterRotulo() {
        return rotulo;
    }

}
