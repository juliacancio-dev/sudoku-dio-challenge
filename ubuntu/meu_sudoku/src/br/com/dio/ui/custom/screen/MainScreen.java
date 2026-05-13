package br.com.dio.ui.custom.screen;

import br.com.dio.model.Space;
import br.com.dio.service.BoardService;
import br.com.dio.service.EventEnum;
import br.com.dio.service.NotifierService;
import br.com.dio.ui.custom.button.CheckGameStatusButton;
import br.com.dio.ui.custom.button.FinishGameButton;
import br.com.dio.ui.custom.button.ResetButton;
import br.com.dio.ui.custom.frame.MainFrame;
import br.com.dio.ui.custom.input.NumberText;
import br.com.dio.ui.custom.panel.MainPanel;
import br.com.dio.ui.custom.panel.SudokuSector;
import br.com.dio.ui.custom.panel.StatusPanel;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static br.com.dio.service.EventEnum.CLEAR_SPACE;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;

public class MainScreen {

    private final static Dimension dimension = new Dimension(700, 750);
    private static final int TIMER_DELAY = 1000;

    private final BoardService boardService;
    private final NotifierService notifierService;
    private final List<SudokuSector> sectors = new ArrayList<>();

    private JButton checkGameStatusButton;
    private JButton finishGameButton;
    private JButton resetButton;

    private StatusPanel statusPanel;
    private Timer gameTimer;
    private int elapsedSeconds;

    public MainScreen(final Map<String, String> gameConfig) {
        this.boardService = new BoardService(gameConfig);
        this.notifierService = new NotifierService();
        this.elapsedSeconds = 0;
    }

    public void buildMainScreen() {
        JPanel mainPanel = new MainPanel(dimension);
        JFrame mainFrame = new MainFrame(dimension, mainPanel);

        mainPanel.setLayout(new BorderLayout());

        statusPanel = new StatusPanel(boardService);
        mainPanel.add(statusPanel, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel();
        boardPanel.setBackground(new Color(60, 60, 80));
        sectors.clear();
        for (int r = 0; r < 9; r += 3) {
            for (int c = 0; c < 9; c += 3) {
                List<NumberText> fields = new ArrayList<>();
                for (int row = r; row <= r + 2; row++) {
                    for (int col = c; col <= c + 2; col++) {
                        Space space = boardService.getSpaces().get(col).get(row);
                        fields.add(new NumberText(space, boardService, row, col));
                    }
                }
                fields.forEach(t -> notifierService.subscribe(CLEAR_SPACE, t));
                SudokuSector sector = new SudokuSector(fields);
                sectors.add(sector);
                boardPanel.add(sector);
            }
        }
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(60, 60, 80));
        addResetButton(buttonPanel);
        addCheckGameStatusButton(buttonPanel);
        addFinishGameButton(buttonPanel);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        startTimer();

        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private void startTimer() {
        gameTimer = new Timer(TIMER_DELAY, e -> {
            elapsedSeconds++;
            statusPanel.updateTime(elapsedSeconds);
        });
        gameTimer.start();
    }

    private void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    private void addFinishGameButton(final JPanel mainPanel) {
        finishGameButton = new FinishGameButton(e -> {
            if (boardService.gameIsFinished()) {
                stopTimer();
                showMessageDialog(null, "ParabÃ©ns vocÃª concluiu o jogo em " + formatTime(elapsedSeconds));
                resetButtons();
            } else {
                var message = boardService.hasErrors()
                        ? "Seu jogo contÃ©m erros. Corrija-os antes de finalizar."
                        : "Seu jogo estÃ¡ incompleto. Continue jogando!";
                showMessageDialog(null, message);
            }
        });
        mainPanel.add(finishGameButton);
    }

    private void addCheckGameStatusButton(final JPanel mainPanel) {
        checkGameStatusButton = new CheckGameStatusButton(e -> {
            if (!boardService.hasLives()) {
                showMessageDialog(null, "Game Over! Suas vidas acabaram.\n" + getFinalMessage());
                stopTimer();
                disableAllInputs();
                return;
            }

            var hasErrors = boardService.hasErrors();
            var gameStatus = boardService.getStatus();

            if (hasErrors) {
                boardService.loseLife();
                statusPanel.updateLives(boardService.getLives());

                var errorCount = countErrors();
                showMessageDialog(null,
                        String.format("âŒ Erros encontrados: %d\nVidas restantes: %d\nCÃ©lulas com erro estÃ£o marcadas em VERMELHO",
                                errorCount, boardService.getLives()));

                if (!boardService.hasLives()) {
                    showMessageDialog(null, "âš ï¸  ÃšLTIMA VIDA PERDIDA! Game Over!");
                    stopTimer();
                    disableAllInputs();
                }
            } else {
                var message = switch (gameStatus) {
                    case NON_STARTED -> "O jogo nÃ£o foi iniciado";
                    case INCOMPLETE -> "O jogo estÃ¡ incompleto, mas sem erros. Continue!";
                    case COMPLETE -> "O jogo estÃ¡ completo e sem erros! ParabÃ©ns!";
                };
                showMessageDialog(null, message);
            }
        });
        mainPanel.add(checkGameStatusButton);
    }

    private int countErrors() {
        int count = 0;
        for (NumberText nt : NumberText.getAllInstances()) {
            if (!nt.isFixed() && nt.hasError()) {
                count++;
            }
        }
        return count;
    }

    private void addResetButton(final JPanel mainPanel) {
        resetButton = new ResetButton(e -> {
            var dialogResult = showConfirmDialog(
                    null,
                    "Deseja realmente reiniciar o jogo?",
                    "Limpar o jogo",
                    YES_NO_OPTION,
                    QUESTION_MESSAGE
            );
            if (dialogResult == 0) {
                stopTimer();
                boardService.reset();
                elapsedSeconds = 0;
                statusPanel.updateLives(boardService.getLives());
                statusPanel.updateTime(0);
                notifierService.notify(CLEAR_SPACE);
                enableAllInputs();
            }
        });
        mainPanel.add(resetButton);
    }

    private void disableAllInputs() {
        for (NumberText nt : NumberText.getAllInstances()) {
            if (!nt.isFixed()) {
                nt.setEnabled(false);
            }
        }
        checkGameStatusButton.setEnabled(false);
        resetButton.setEnabled(false);
        finishGameButton.setEnabled(false);
    }

    private void enableAllInputs() {
        for (NumberText nt : NumberText.getAllInstances()) {
            nt.setEnabled(!nt.isFixed());
        }
        checkGameStatusButton.setEnabled(true);
        resetButton.setEnabled(true);
        finishGameButton.setEnabled(true);
    }

    private void resetButtons() {
        checkGameStatusButton.setEnabled(false);
        resetButton.setEnabled(false);
        finishGameButton.setEnabled(false);
    }

    private String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    private String getFinalMessage() {
        var status = boardService.getStatus();
        return switch (status) {
            case COMPLETE -> "Jogo completo!";
            case INCOMPLETE -> "Jogo incompleto.";
            case NON_STARTED -> "Jogo nÃ£o iniciado.";
        };
    }
}


