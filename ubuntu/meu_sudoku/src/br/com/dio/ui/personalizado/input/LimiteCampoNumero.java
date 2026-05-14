package br.com.dio.ui.personalizado.input;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.util.List;

import static java.util.Objects.isNull;

public class LimiteCampoNumero extends PlainDocument {

    private final List<String> NUMEROS = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9");

    @Override
    public void insertString(final int posicao, final String texto, final AttributeSet conjuntoAtributos) throws BadLocationException {
        if (isNull(texto) || (!NUMEROS.contains(texto))) return;

        if (getLength() + texto.length() <= 1){
            super.insertString(posicao, texto, conjuntoAtributos);
        }
    }
}
