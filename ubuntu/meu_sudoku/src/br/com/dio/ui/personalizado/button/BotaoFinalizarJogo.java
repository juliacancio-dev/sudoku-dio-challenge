package br.com.dio.ui.personalizado.button;

import javax.swing.JButton;
import java.awt.event.ActionListener;

public class BotaoFinalizarJogo extends JButton {

    public BotaoFinalizarJogo(final ActionListener acaoListener){
        this.setText("Concluir");
        this.addActionListener(acaoListener);
    }

}
