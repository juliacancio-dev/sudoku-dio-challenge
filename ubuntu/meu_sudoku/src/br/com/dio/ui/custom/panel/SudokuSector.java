package br.com.dio.ui.custom.panel;

import br.com.dio.ui.custom.input.NumberText;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.Dimension;
import java.util.List;

import static java.awt.Color.black;

public class SudokuSector extends JPanel {

    private final List<NumberText> textFields;

    public SudokuSector(final List<NumberText> textFields){
        this.textFields = textFields;
        var dimension = new Dimension(170, 170);
        this.setSize(dimension);
        this.setPreferredSize(dimension);
        this.setBorder(new LineBorder(black, 2, true));
        this.setVisible(true);
        textFields.forEach(this::add);
    }

    public List<NumberText> getTextFields() {
        return textFields;
    }
}

}
