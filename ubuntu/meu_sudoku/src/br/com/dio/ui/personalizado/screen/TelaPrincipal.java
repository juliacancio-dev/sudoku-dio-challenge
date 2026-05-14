package br.com.dio.ui.personalizado.screen;

import br.com.dio.modelo.Espaco;
import br.com.dio.servico.ServicoTabuleiro;
import br.com.dio.servico.EventoEnum;
import br.com.dio.servico.ServicoNotificador;
import br.com.dio.ui.personalizado.button.BotaoVerificarStatus;
import br.com.dio.ui.personalizado.button.BotaoFinalizarJogo;
import br.com.dio.ui.personalizado.button.BotaoReiniciar;
import br.com.dio.ui.personalizado.frame.JanelaPrincipal;
import br.com.dio.ui.personalizado.input.CampoNumero;
import br.com.dio.ui.personalizado.panel.PainelPrincipal;
import br.com.dio.ui.personalizado.panel.SetorSudoku;
import br.com.dio.ui.personalizado.panel.PainelStatus;

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

import static br.com.dio.servico.EventoEnum.LIMPAR_ESPACO;
import static br.com.dio.modelo.StatusJogoEnum.COMPLETO;
import static br.com.dio.modelo.StatusJogoEnum.INCOMPLETO;
import static br.com.dio.modelo.StatusJogoEnum.NAO_INICIADO;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;

public class TelaPrincipal {

    private final static Dimension dimensao = new Dimension(700, 750);
    private static final int ATRASO_TEMPORIZADOR = 1000;

    private final ServicoTabuleiro servicoTabuleiro;
    private final ServicoNotificador servicoNotificador;
    private final List<SetorSudoku> setores = new ArrayList<>();

    private JButton botaoVerificarStatus;
    private JButton botaoFinalizarJogo;
    private JButton botaoReiniciar;

    private PainelStatus painelStatus;
    private Timer temporizador;
    private int segundosCorridos;

    public TelaPrincipal(final Map<String, String> configuracaoJogo) {
        this.servicoTabuleiro = new ServicoTabuleiro(configuracaoJogo);
        this.servicoNotificador = new ServicoNotificador();
        this.segundosCorridos = 0;
    }

    public void construirTelaPrincipal() {
        JPanel painelPrincipal = new PainelPrincipal(dimensao);
        JFrame janelaPrincipal = new JanelaPrincipal(dimensao, painelPrincipal);

        painelPrincipal.setLayout(new BorderLayout());

        painelStatus = new PainelStatus(servicoTabuleiro);
        painelPrincipal.add(painelStatus, BorderLayout.NORTH);

        JPanel painelTabuleiro = new JPanel();
        painelTabuleiro.setBackground(new Color(60, 60, 80));
        setores.clear();
        for (int r = 0; r < 9; r += 3) {
            for (int c = 0; c < 9; c += 3) {
                List<CampoNumero> campos = new ArrayList<>();
                for (int linha = r; linha <= r + 2; linha++) {
                    for (int coluna = c; coluna <= c + 2; coluna++) {
                        Espaco espaco = servicoTabuleiro.obterEspacos().get(coluna).get(linha);
                        campos.add(new CampoNumero(espaco, servicoTabuleiro, linha, coluna));
                    }
                }
                campos.forEach(t -> servicoNotificador.inscrever(LIMPAR_ESPACO, t));
                SetorSudoku setor = new SetorSudoku(campos);
                setores.add(setor);
                painelTabuleiro.add(setor);
            }
        }
        painelPrincipal.add(painelTabuleiro, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setBackground(new Color(60, 60, 80));
        adicionarBotaoReiniciar(painelBotoes);
        adicionarBotaoVerificarStatus(painelBotoes);
        adicionarBotaoFinalizarJogo(painelBotoes);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        iniciarTemporizador();

        janelaPrincipal.revalidate();
        janelaPrincipal.repaint();
    }

    private void iniciarTemporizador() {
        temporizador = new Timer(ATRASO_TEMPORIZADOR, e -> {
            segundosCorridos++;
            painelStatus.atualizarTempo(segundosCorridos);
        });
        temporizador.start();
    }

    private void pararTemporizador() {
        if (temporizador != null) {
            temporizador.stop();
        }
    }

    private void adicionarBotaoFinalizarJogo(final JPanel painelPrincipal) {
        botaoFinalizarJogo = new BotaoFinalizarJogo(e -> {
            if (servicoTabuleiro.jogoConcluido()) {
                pararTemporizador();
                showMessageDialog(null, "Parabéns você concluiu o jogo em " + formatarTempo(segundosCorridos));
                reiniciarBotoes();
            } else {
                var mensagem = servicoTabuleiro.temErros()
                        ? "Seu jogo contém erros. Corrija-os antes de finalizar."
                        : "Seu jogo está incompleto. Continue jogando!";
                showMessageDialog(null, mensagem);
            }
        });
        painelPrincipal.add(botaoFinalizarJogo);
    }

    private void adicionarBotaoVerificarStatus(final JPanel painelPrincipal) {
        botaoVerificarStatus = new BotaoVerificarStatus(e -> {
            if (!servicoTabuleiro.temVidas()) {
                showMessageDialog(null, "Fim de Jogo! Suas vidas acabaram.\n" + getMensagemFinal());
                pararTemporizador();
                desativarTodasEntradas();
                return;
            }

            var temErros = servicoTabuleiro.temErros();
            var statusJogo = servicoTabuleiro.obterStatus();

            if (temErros) {
                servicoTabuleiro.perderVida();
                painelStatus.atualizarVidas(servicoTabuleiro.obterVidas());

                var contadorErros = contarErros();
                showMessageDialog(null,
                        String.format("❌ Erros encontrados: %d\nVidas restantes: %d\nCélulas com erro estão marcadas em VERMELHO",
                                contadorErros, servicoTabuleiro.obterVidas()));

                if (!servicoTabuleiro.temVidas()) {
                    showMessageDialog(null, "⚠️  ÚLTIMA VIDA PERDIDA! Fim de Jogo!");
                    pararTemporizador();
                    desativarTodasEntradas();
                }
            } else {
                var mensagem = switch (statusJogo) {
                    case NAO_INICIADO -> "O jogo não foi iniciado";
                    case INCOMPLETO -> "O jogo está incompleto, mas sem erros. Continue!";
                    case COMPLETO -> "O jogo está completo e sem erros! Parabéns!";
                };
                showMessageDialog(null, mensagem);
            }
        });
        painelPrincipal.add(botaoVerificarStatus);
    }

    private int contarErros() {
        int contador = 0;
        for (CampoNumero cn : CampoNumero.obterTodasInstancias()) {
            if (!cn.ehFixo() && cn.temErro()) {
                contador++;
            }
        }
        return contador;
    }

    private void adicionarBotaoReiniciar(final JPanel painelPrincipal) {
        botaoReiniciar = new BotaoReiniciar(e -> {
            var resultadoDialogo = showConfirmDialog(
                    null,
                    "Deseja realmente reiniciar o jogo?",
                    "Limpar o jogo",
                    YES_NO_OPTION,
                    QUESTION_MESSAGE
            );
            if (resultadoDialogo == 0) {
                pararTemporizador();
                servicoTabuleiro.reiniciar();
                segundosCorridos = 0;
                painelStatus.atualizarVidas(servicoTabuleiro.obterVidas());
                painelStatus.atualizarTempo(0);
                servicoNotificador.notificar(LIMPAR_ESPACO);
                habilitarTodasEntradas();
            }
        });
        painelPrincipal.add(botaoReiniciar);
    }

    private void desativarTodasEntradas() {
        for (CampoNumero cn : CampoNumero.obterTodasInstancias()) {
            if (!cn.ehFixo()) {
                cn.setEnabled(false);
            }
        }
        botaoVerificarStatus.setEnabled(false);
        botaoReiniciar.setEnabled(false);
        botaoFinalizarJogo.setEnabled(false);
    }

    private void habilitarTodasEntradas() {
        for (CampoNumero cn : CampoNumero.obterTodasInstancias()) {
            cn.setEnabled(!cn.ehFixo());
        }
        botaoVerificarStatus.setEnabled(true);
        botaoReiniciar.setEnabled(true);
        botaoFinalizarJogo.setEnabled(true);
    }

    private void reiniciarBotoes() {
        botaoVerificarStatus.setEnabled(false);
        botaoReiniciar.setEnabled(false);
        botaoFinalizarJogo.setEnabled(false);
    }

    private String formatarTempo(int segundos) {
        int mins = segundos / 60;
        int secs = segundos % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    private String getMensagemFinal() {
        var status = servicoTabuleiro.obterStatus();
        return switch (status) {
            case COMPLETO -> "Jogo completo!";
            case INCOMPLETO -> "Jogo incompleto.";
            case NAO_INICIADO -> "Jogo não iniciado.";
        };
    }
}


