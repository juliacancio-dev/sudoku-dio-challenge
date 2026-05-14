package br.com.dio.ui.personalizado.panel;

import br.com.dio.servico.ServicoTabuleiro;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PainelStatus extends JPanel {

    private final ServicoTabuleiro servicoTabuleiro;
    private javax.swing.JLabel rotuloVidas;
    private javax.swing.JLabel rotuloTemporizador;
    private Timer temporizador;

    public PainelStatus(ServicoTabuleiro servicoTabuleiro) {
        this.servicoTabuleiro = servicoTabuleiro;
        setPreferredSize(new Dimension(700, 50));
        setBackground(new Color(45, 45, 60));
        setLayout(new BorderLayout());

        // Painel esquerdo - Vidas
        JPanel painelEsquerdo = new JPanel();
        painelEsquerdo.setBackground(new Color(45, 45, 60));
        rotuloVidas = new javax.swing.JLabel(obterTextoVidas());
        rotuloVidas.setFont(new Font("Arial", Font.BOLD, 18));
        rotuloVidas.setForeground(Color.WHITE);
        painelEsquerdo.add(rotuloVidas);

        // Painel central - Título
        JPanel painelCentral = new JPanel();
        painelCentral.setBackground(new Color(45, 45, 60));
        javax.swing.JLabel rotuloTitulo = new javax.swing.JLabel("SUDOKU");
        rotuloTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        rotuloTitulo.setForeground(new Color(255, 215, 0));
        painelCentral.add(rotuloTitulo);

        // Painel direito - Temporizador
        JPanel painelDireito = new JPanel();
        painelDireito.setBackground(new Color(45, 45, 60));
        rotuloTemporizador = new javax.swing.JLabel("00:00");
        rotuloTemporizador.setFont(new Font("Monospaced", Font.BOLD, 18));
        rotuloTemporizador.setForeground(Color.WHITE);
        painelDireito.add(rotuloTemporizador);

        add(painelEsquerdo, BorderLayout.WEST);
        add(painelCentral, BorderLayout.CENTER);
        add(painelDireito, BorderLayout.EAST);

        setBorder(new javax.swing.border.EmptyBorder(5, 10, 5, 10));
    }

    private String obterTextoVidas() {
        int vidas = servicoTabuleiro.obterVidas();
        String coracoes = "❤️".repeat(vidas);
        String vazios = "🖤".repeat(3 - vidas);
        return String.format("Vidas: %s%s", coracoes, vazios);
    }

    public void atualizarVidas(int vidas) {
        rotuloVidas.setText(obterTextoVidas());
        if (vidas == 1) {
            rotuloVidas.setForeground(Color.RED);
        } else if (vidas == 2) {
            rotuloVidas.setForeground(Color.YELLOW);
        } else {
            rotuloVidas.setForeground(Color.WHITE);
        }
    }

    public void atualizarTempo(int segundos) {
        int mins = segundos / 60;
        int secs = segundos % 60;
        rotuloTemporizador.setText(String.format("%02d:%02d", mins, secs));
    }
}
