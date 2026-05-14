package br.com.dio.ui.personalizado.input;

import br.com.dio.modelo.Espaco;
import br.com.dio.servico.ServicoTabuleiro;
import br.com.dio.servico.EventoEnum;
import br.com.dio.servico.EscutadorEvento;
import br.com.dio.servico.ServicoNotificador;
import br.com.dio.util.ValidadorSudoku;

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

import static br.com.dio.servico.EventoEnum.LIMPAR_ESPACO;
import static java.awt.Font.PLAIN;

public class CampoNumero extends JTextField implements EscutadorEvento {

    private static final List<CampoNumero> todasInstancias = new ArrayList<>();
    private static final Color COR_INICIAL_FORTE = new Color(0, 50, 150);
    private static final Color COR_ERRO = new Color(220, 20, 60);
    private static final Color COR_ERRO_BRILHANTE = new Color(255, 100, 100);
    private static final Color COR_DESTAQUE = new Color(255, 255, 150);
    private static final Color COR_NORMAL = Color.WHITE;
    private static final Color COR_FIXO = new Color(230, 240, 255);

    private final Espaco espaco;
    private final ServicoTabuleiro servicoTabuleiro;
    private final int linha;
    private final int coluna;
    private boolean temErro = false;
    private boolean piscando = false;

    public CampoNumero(final Espaco espaco, final ServicoTabuleiro servicoTabuleiro, final int linha, final int coluna) {
        this.espaco = espaco;
        this.servicoTabuleiro = servicoTabuleiro;
        this.linha = linha;
        this.coluna = coluna;
        todasInstancias.add(this);

        Dimension dimensao = new Dimension(55, 55);
        this.setSize(dimensao);
        this.setPreferredSize(dimensao);
        this.setVisible(true);
        this.setFont(new Font("Arial", PLAIN, 22));
        this.setHorizontalAlignment(CENTER);
        this.setDocument(new LimiteCampoNumero());
        this.setBorder(new LineBorder(Color.DARK_GRAY, 1));
        this.setBackground(COR_NORMAL);
        this.setEnabled(!espaco.ehFixo());

        if (espaco.ehFixo() && espaco.obterAtual() != null) {
            this.setText(espaco.obterAtual().toString());
            this.setFont(new Font("Arial", Font.BOLD, 24));
            this.setForeground(COR_INICIAL_FORTE);
            this.setDisabledTextColor(COR_INICIAL_FORTE);
            this.setBackground(COR_FIXO);
        }

        this.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                destacarMesmoValor();
                setBorder(new LineBorder(new Color(255, 215, 0), 2));
            }
            public void focusLost(FocusEvent e) {
                limparTodosDestaques();
                setBorder(new LineBorder(Color.DARK_GRAY, 1));
            }
        });

        this.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(final DocumentEvent e) { mudarEspaco(); }
            public void removeUpdate(final DocumentEvent e) { mudarEspaco(); }
            public void changedUpdate(final DocumentEvent e) { mudarEspaco(); }
            private void mudarEspaco() {
                String texto = getText();
                if (texto.isEmpty()) {
                    servicoTabuleiro.limparCelula(linha, coluna);
                    limparDestaqueErro();
                    atualizarTodosErros(servicoTabuleiro.obterTabuleiro());
                    return;
                }
                int valor = Integer.parseInt(texto);
                Integer valorAntigo = espaco.obterAtual();
                boolean sucesso = servicoTabuleiro.definirValorCelula(linha, coluna, valor);
                if (sucesso) {
                    limparDestaqueErro();
                } else {
                    setText(valorAntigo != null ? valorAntigo.toString() : "");
                }
                atualizarTodosErros(servicoTabuleiro.obterTabuleiro());
                if (!sucesso) {
                    definirEstadoErro(true);
                }
            }
        });
    }

    private void destacarMesmoValor() {
        if (getText().isEmpty() || piscando) return;
        try {
            int val = Integer.parseInt(getText());
            for (CampoNumero cn : todasInstancias) {
                if (!cn.getText().isEmpty() && cn != this) {
                    try {
                        int outroVal = Integer.parseInt(cn.getText());
                        if (outroVal == val) {
                            cn.setBackground(COR_DESTAQUE);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (NumberFormatException ignored) {}
    }

    private static void limparTodosDestaques() {
        for (CampoNumero cn : todasInstancias) {
            if (!cn.espaco.ehFixo()) {
                cn.setBackground(cn.temErro ? COR_ERRO : COR_NORMAL);
            } else {
                cn.setBackground(COR_FIXO);
            }
        }
    }

    public void definirEstadoErro(boolean erro) {
        this.temErro = erro;
        if (!espaco.ehFixo()) {
            setBackground(erro ? COR_ERRO : COR_NORMAL);
            if (erro) {
                iniciarEfeitoPiscar();
            }
        }
    }

    private void iniciarEfeitoPiscar() {
        if (piscando) return;
        piscando = true;

        new Thread(() -> {
            int piscas = 6;
            Color original = getBackground();
            try {
                for (int i = 0; i < piscas; i++) {
                    Thread.sleep(150);
                    setBackground(COR_ERRO_BRILHANTE);
                    Thread.sleep(150);
                    setBackground(original);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                piscando = false;
            }
        }).start();
    }

    public void limparDestaqueErro() {
        this.temErro = false;
        if (!espaco.ehFixo()) {
            this.setBackground(COR_NORMAL);
        }
    }

    public boolean temErro() {
        return temErro;
    }

    public boolean ehFixo() {
        return espaco.ehFixo();
    }

    public void atualizar(final EventoEnum tipoEvento) {
        if (tipoEvento.equals(LIMPAR_ESPACO) && (this.isEnabled())) {
            this.setText("");
            limparDestaqueErro();
        }
    }

    public Espaco obterEspaco() {
        return espaco;
    }

    public static List<CampoNumero> obterTodasInstancias() {
        return new ArrayList<>(todasInstancias);
    }

    public static void atualizarTodosErros(ServicoTabuleiro servicoTabuleiro) {
        atualizarTodosErros(servicoTabuleiro.obterTabuleiro());
    }

    public static void atualizarTodosErros(br.com.dio.modelo.Tabuleiro tabuleiro) {
        for (CampoNumero cn : todasInstancias) {
            if (cn.espaco.ehFixo()) {
                cn.definirEstadoErro(false);
                continue;
            }
            Integer val = cn.espaco.obterAtual();
            if (val != null) {
                boolean conflito = ValidadorSudoku.temConflitos(tabuleiro, cn.linha, cn.coluna, val);
                cn.definirEstadoErro(conflito);
            } else {
                cn.definirEstadoErro(false);
            }
        }
    }
}
