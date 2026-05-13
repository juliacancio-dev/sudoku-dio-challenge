package br.com.dio.ui.custom.input;

import br.com.dio.model.Space;
import br.com.dio.service.BoardService;
import br.com.dio.service.EventEnum;
import br.com.dio.service.EventListener;
import br.com.dio.service.NotifierService;
import br.com.dio.util.SudokuValidator;

import javax.swing.JTextField;
import javax.swing.border.LineBorder;
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

    private static final List<NumberText> allInstances = new ArrayList<>();
    private static final Color COLOR_INITIAL_STRONG = new Color(0, 50, 150);
    private static final Color COLOR_ERROR = new Color(220, 20, 60);
    private static final Color COLOR_ERROR_BRIGHT = new Color(255, 100, 100);
    private static final Color COLOR_HIGHLIGHT = new Color(255, 255, 150);
    private static final Color COLOR_NORMAL = Color.WHITE;
    private static final Color COLOR_FIXED = new Color(230, 240, 255);

    private final Space space;
    private final BoardService boardService;
    private final int row;
    private final int col;
    private boolean hasError = false;
    private boolean isFlashing = false;

    public NumberText(final Space space, final BoardService boardService, final int row, final int col) {
        this.space = space;
        this.boardService = boardService;
        this.row = row;
        this.col = col;
        allInstances.add(this);

        Dimension dimension = new Dimension(55, 55);
        this.setSize(dimension);
        this.setPreferredSize(dimension);
        this.setVisible(true);
        this.setFont(new Font("Arial", PLAIN, 22));
        this.setHorizontalAlignment(CENTER);
        this.setDocument(new NumberTextLimit());
        this.setBorder(new LineBorder(Color.DARK_GRAY, 1));
        this.setBackground(COLOR_NORMAL);
        this.setEnabled(!space.isFixed());

        if (space.isFixed() && space.getActual() != null) {
            this.setText(space.getActual().toString());
            this.setFont(new Font("Arial", Font.BOLD, 24));
            this.setForeground(COLOR_INITIAL_STRONG);
            this.setDisabledTextColor(COLOR_INITIAL_STRONG);
            this.setBackground(COLOR_FIXED);
        }

        this.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                highlightSameValue();
                setBorder(new LineBorder(new Color(255, 215, 0), 2));
            }
            public void focusLost(FocusEvent e) {
                clearAllHighlights();
                setBorder(new LineBorder(Color.DARK_GRAY, 1));
            }
        });

        this.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(final DocumentEvent e) { changeSpace(); }
            public void removeUpdate(final DocumentEvent e) { changeSpace(); }
            public void changedUpdate(final DocumentEvent e) { changeSpace(); }
            private void changeSpace() {
                String text = getText();
                if (text.isEmpty()) {
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
        if (getText().isEmpty() || isFlashing) return;
        try {
            int val = Integer.parseInt(getText());
            for (NumberText nt : allInstances) {
                if (!nt.getText().isEmpty() && nt != this) {
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
                nt.setBackground(COLOR_FIXED);
            }
        }
    }

    public void setErrorState(boolean error) {
        this.hasError = error;
        if (!space.isFixed()) {
            setBackground(error ? COLOR_ERROR : COLOR_NORMAL);
            if (error) {
                startFlashEffect();
            }
        }
    }

    private void startFlashEffect() {
        if (isFlashing) return;
        isFlashing = true;

        new Thread(() -> {
            int flashes = 6;
            Color original = getBackground();
            try {
                for (int i = 0; i < flashes; i++) {
                    Thread.sleep(150);
                    setBackground(COLOR_ERROR_BRIGHT);
                    Thread.sleep(150);
                    setBackground(original);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                isFlashing = false;
            }
        }).start();
    }

    public void clearErrorHighlight() {
        this.hasError = false;
        if (!space.isFixed()) {
            this.setBackground(COLOR_NORMAL);
        }
    }

    public boolean hasError() {
        return hasError;
    }

    public boolean isFixed() {
        return space.isFixed();
    }

    public void update(final EventEnum eventType) {
        if (eventType.equals(CLEAR_SPACE) && (this.isEnabled())) {
            this.setText("");
            clearErrorHighlight();
        }
    }

    public Space getSpace() {
        return space;
    }

    public static List<NumberText> getAllInstances() {
        return new ArrayList<>(allInstances);
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
