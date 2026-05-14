package br.com.dio.ui.personalizado.panel;

import br.com.dio.ui.personalizado.input.CampoNumero;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.Dimension;
import java.util.List;

import static java.awt.Color.black;

public class SetorSudoku extends JPanel {

    private final List<CampoNumero> campos;

    public SetorSudoku(final List<CampoNumero> campos){
        this.campos = campos;
        var dimensao = new Dimension(170, 170);
        this.setSize(dimensao);
        this.setPreferredSize(dimensao);
        this.setBorder(new LineBorder(black, 2, true));
        this.setVisible(true);
        campos.forEach(this::add);
    }

    public List<CampoNumero> getCampos() {
        return campos;
    }
}
