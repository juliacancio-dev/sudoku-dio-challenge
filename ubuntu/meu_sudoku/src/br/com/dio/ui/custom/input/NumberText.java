package br.com.dio.ui.custom.input;

import br.com.dio.model.Space;
import br.com.dio.service.BoardService;
import br.com.dio.service.EventEnum;
import br.com.dio.service.EventListener;
import br.com.dio.service.NotifierService;
import br.com.dio.util.SudokuValidator;

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
    private final BoardService boardService;
    private final int row;
    private final int col;
    private boolean hasError = false;

    public NumberText(final Space space, final BoardService boardService, final int row, final int col) {
        this.space = space;
        this.boardService = boardService;
        this.row = row;
        this.col = col;
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
        if (space.isFixed() && space.getActual() != null){
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
                String text = getText();
                if (text.isEmpty()){
                    boardService.clearCell(row, col);
                    clearErrorHighlight();
                    refreshAllErrors(boardService.getBoard());
                    return;
                }
                int value = Integer.parseInt(text);
                Integer oldValue = space.getActual();
                boolean success = boardService.setCellValue(row, col, value);
                if (success) {
                    clearErrorHighlight();
                } else {
                    // Reverter para valor anterior
                    setText(oldValue != null ? oldValue.toString() : "");
                }
                refreshAllErrors(boardService.getBoard());
                if (!success) {
                    setErrorState(true);
                }
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

    public static void refreshAllErrors(BoardService boardService) {
        refreshAllErrors(boardService.getBoard());
    }

    public static void refreshAllErrors(br.com.dio.model.Board board) {
        for (NumberText nt : allInstances) {
            if (nt.space.isFixed()) {
                nt.setErrorState(false);
                continue;
            }
            Integer val = nt.space.getActual();
            if (val != null) {
                boolean conflict = SudokuValidator.hasConflicts(board, nt.row, nt.col, val);
                nt.setErrorState(conflict);
            } else {
                nt.setErrorState(false);
            }
        }
    }
}

