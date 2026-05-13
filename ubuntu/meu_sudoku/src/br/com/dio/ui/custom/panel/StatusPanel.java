package br.com.dio.ui.custom.panel;

import br.com.dio.service.BoardService;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatusPanel extends JPanel {

    private final BoardService boardService;
    private javax.swing.JLabel livesLabel;
    private javax.swing.JLabel timerLabel;
    private Timer timer;

    public StatusPanel(BoardService boardService) {
        this.boardService = boardService;
        setPreferredSize(new Dimension(700, 50));
        setBackground(new Color(45, 45, 60));
        setLayout(new BorderLayout());

        // Painel esquerdo - Vidas
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(45, 45, 60));
        livesLabel = new javax.swing.JLabel(getLivesText());
        livesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        livesLabel.setForeground(Color.WHITE);
        leftPanel.add(livesLabel);

        // Painel central - Título
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(45, 45, 60));
        javax.swing.JLabel titleLabel = new javax.swing.JLabel("SUDOKU");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 215, 0));
        centerPanel.add(titleLabel);

        // Painel direito - Timer
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(45, 45, 60));
        timerLabel = new javax.swing.JLabel("00:00");
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        timerLabel.setForeground(Color.WHITE);
        rightPanel.add(timerLabel);

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        setBorder(new javax.swing.border.EmptyBorder(5, 10, 5, 10));
    }

    private String getLivesText() {
        int lives = boardService.getLives();
        String hearts = "❤️".repeat(lives);
        String empty = "🖤".repeat(3 - lives);
        return String.format("Vidas: %s%s", hearts, empty);
    }

    public void updateLives(int lives) {
        livesLabel.setText(getLivesText());
        if (lives == 1) {
            livesLabel.setForeground(Color.RED);
        } else if (lives == 2) {
            livesLabel.setForeground(Color.YELLOW);
        } else {
            livesLabel.setForeground(Color.WHITE);
        }
    }

    public void updateTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        timerLabel.setText(String.format("%02d:%02d", mins, secs));
    }
}
