package br.com.dio.ui.custom.input;

import br.com.dio.model.Space;
import br.com.dio.service.EventEnum;
import br.com.dio.service.EventListener;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import static br.com.dio.service.EventEnum.CLEAR_SPACE;
import static java.awt.Font.PLAIN;

public class NumberText extends JTextField implements EventListener {

    private static final List<NumberText> allInstances = new ArrayList<NumberText>();
    private static final Color COLOR_INITIAL_STRONG = new Color(0, 0, 180);
    private static final Color COLOR_ERROR = new Color(220, 20, 60);
    private static final Color COLOR_HIGHLIGHT = new Color(255, 255, 150);
    private static final Color COLOR_NORMAL = Color.WHITE;

    private final Space space;
    private boolean hasError = false;

    public NumberText(final Space space) {
        this.space = space;
        allInstances.add(this);
        Dimension dimension = new Dimension(50, 50);
        this.setSize(dimension);
        this.setPreferredSize(dimension);
        this.setVisible(true);
        this.setFont(new Font("Arial", PLAIN, 20));
        this.setHorizontalAlignment(CENTER);
        this.setDocument(new NumberTextLimit());
        this.setBackground(COLOR_NORMAL);
        this.setEnabled(!space.isFixed());
        if (space.isFixed()){
            this.setText(space.getActual().toString());
            this.setFont(new Font("Arial", Font.BOLD, 24));
            this.setForeground(COLOR_INITIAL_STRONG);
            this.setDisabledTextColor(COLOR_INITIAL_STRONG);
        }

        this.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) { highlightSameValue(); }
            public void focusLost(FocusEvent e) { clearAllHighlights(); }
        });

        this.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(final DocumentEvent e) { changeSpace(); }
            public void removeUpdate(final DocumentEvent e) { changeSpace(); }
            public void changedUpdate(final DocumentEvent e) { changeSpace(); }
            private void changeSpace(){
                if (getText().isEmpty()){
                    space.clearSpace();
                    clearErrorHighlight();
                    return;
                }
                space.setActual(Integer.parseInt(getText()));
                clearErrorHighlight();
            }
        });
    }

    private void highlightSameValue() {
        if (getText().isEmpty()) return;
        try {
            int val = Integer.parseInt(getText());
            for (NumberText nt : allInstances) {
                if (!nt.getText().isEmpty()) {
                    try {
                        int otherVal = Integer.parseInt(nt.getText());
                        if (otherVal == val) {
                            nt.setBackground(COLOR_HIGHLIGHT);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (NumberFormatException ignored) {}
    }

    private static void clearAllHighlights() {
        for (NumberText nt : allInstances) {
            if (!nt.space.isFixed()) {
                nt.setBackground(nt.hasError ? COLOR_ERROR : COLOR_NORMAL);
            } else {
                nt.setBackground(COLOR_NORMAL);
            }
        }
    }

    public void setErrorState(boolean error) {
        this.hasError = error;
        if (!space.isFixed()) {
            this.setBackground(error ? COLOR_ERROR : COLOR_NORMAL);
        }
    }

    public void update(final EventEnum eventType) {
        if (eventType.equals(CLEAR_SPACE) && (this.isEnabled())){
            this.setText("");
            clearErrorHighlight();
        }
    }

    private void clearErrorHighlight() {
        this.hasError = false;
        if (!space.isFixed()) {
            this.setBackground(COLOR_NORMAL);
        }
    }

    public Space getSpace() {
        return space;
    }

    public static void clearAllHighlightsStatic() {
        clearAllHighlights();
    }
}