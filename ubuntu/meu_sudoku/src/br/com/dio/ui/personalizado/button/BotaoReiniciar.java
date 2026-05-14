package br.com.dio.ui.personalizado.button;

import javax.swing.JButton;
import java.awt.event.ActionListener;

public class BotaoReiniciar extends JButton {

    public BotaoReiniciar(final ActionListener acaoListener){
        this.setText("Reiniciar jogo");
        this.addActionListener(acaoListener);
    }

}
