package br.com.dio.ui.personalizado.button;

import javax.swing.JButton;
import java.awt.event.ActionListener;

public class BotaoVerificarStatus extends JButton {

    public BotaoVerificarStatus(final ActionListener acaoListener){
        this.setText("Verificar jogo");
        this.addActionListener(acaoListener);
    }

}
