package Interface;

import DAO.UsuarioDao;
import Escola.Usuario;
import Servico.EmailServico;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo modal para confirmação de exclusão de conta via token de e-mail.
 * Dispara um código de segurança de 6 dígitos para o e-mail do usuário e,
 * ao confirmar o token correto, remove permanentemente o usuário do banco e
 * encerra a sessão, retornando para a tela de login.
 */
public class DialogoTokenExclusaoConta extends JDialog {

    private final JDialog paiDialogo;
    private final JFrame framePrincipal;
    private final Usuario usuario;

    private String tokenAtual;
    private JLabel labelStatus;
    private JLabel labelInstrucao;
    private JProgressBar progressBar;
    private JTextField campoToken;
    private JButton btnConfirmar;
    private JButton btnReenviar;
    private JButton btnCancelar;

    private final Color FUNDO = Configuracoes.getCorFundo();
    private final Color TEXTO = Configuracoes.getCorTexto();
    private final Color CINZA = Configuracoes.getCorSubtexto();
    private final Color VERMELHO = new Color(220, 38, 38);
    private final Color VERMELHO_FUNDO = new Color(254, 242, 242);
    private final Color VERDE = new Color(22, 163, 74);
    private final Color AMARELO = new Color(202, 138, 4);

    public DialogoTokenExclusaoConta(JDialog paiDialogo, JFrame framePrincipal, Usuario usuario) {
        super(paiDialogo, "Confirmação de Exclusão de Conta", true);

        this.paiDialogo = paiDialogo;
        this.framePrincipal = framePrincipal;
        this.usuario = usuario;

        setSize(520, 530);
        setResizable(false);
        setLocationRelativeTo(paiDialogo);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout(0, 16));
        principal.setBackground(FUNDO);
        principal.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        principal.add(criarCabecalho(), BorderLayout.NORTH);
        principal.add(criarCorpo(), BorderLayout.CENTER);
        principal.add(criarBotoes(), BorderLayout.SOUTH);

        setContentPane(principal);

        // Dispara o envio do token imediatamente ao abrir a janela
        iniciarEnvioToken();
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setOpaque(false);

        JLabel icone = new JLabel("🗑️", SwingConstants.CENTER);
        icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        icone.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Excluir Conta do Usuário", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(VERMELHO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Enviando código de confirmação para:", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(CINZA);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        String email = usuario != null && usuario.getEmail() != null ? usuario.getEmail() : "";
        JLabel badgeEmail = new JLabel("  " + email + "  ", SwingConstants.CENTER);
        badgeEmail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        badgeEmail.setForeground(VERMELHO);
        badgeEmail.setOpaque(true);
        badgeEmail.setBackground(VERMELHO_FUNDO);
        badgeEmail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(252, 165, 165), 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        badgeEmail.putClientProperty(FlatClientProperties.STYLE, "arc:8");
        badgeEmail.setAlignmentX(Component.CENTER_ALIGNMENT);

        cabecalho.add(icone);
        cabecalho.add(Box.createVerticalStrut(8));
        cabecalho.add(titulo);
        cabecalho.add(Box.createVerticalStrut(6));
        cabecalho.add(subtitulo);
        cabecalho.add(Box.createVerticalStrut(6));
        cabecalho.add(badgeEmail);

        return cabecalho;
    }

    private JPanel criarCorpo() {
        JPanel corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setOpaque(false);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(320, 6));
        progressBar.setMaximumSize(new Dimension(360, 6));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        labelStatus = new JLabel("Conectando ao servidor de e-mail...", SwingConstants.CENTER);
        labelStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelStatus.setForeground(CINZA);
        labelStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        labelInstrucao = new JLabel("Digite abaixo o código de 6 dígitos recebido para autorizar a exclusão:", SwingConstants.CENTER);
        labelInstrucao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelInstrucao.setForeground(TEXTO);
        labelInstrucao.setAlignmentX(Component.CENTER_ALIGNMENT);

        campoToken = new JTextField();
        campoToken.setFont(new Font("Consolas", Font.BOLD, 26));
        campoToken.setHorizontalAlignment(JTextField.CENTER);
        campoToken.setPreferredSize(new Dimension(200, 48));
        campoToken.setMaximumSize(new Dimension(220, 48));
        campoToken.setAlignmentX(Component.CENTER_ALIGNMENT);
        campoToken.putClientProperty("JTextField.placeholderText", "000000");
        campoToken.putClientProperty(FlatClientProperties.STYLE, "arc:12;focusWidth:2;borderWidth:1");
        campoToken.addActionListener(e -> confirmarExclusao());

        JPanel avisoPerigo = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        avisoPerigo.setBackground(VERMELHO_FUNDO);
        avisoPerigo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(254, 202, 202), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        avisoPerigo.putClientProperty(FlatClientProperties.STYLE, "arc:8");
        avisoPerigo.setMaximumSize(new Dimension(420, 44));
        avisoPerigo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel txtAviso = new JLabel("⚠️ Esta ação é irreversível e excluirá permanentemente seu acesso.");
        txtAviso.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtAviso.setForeground(new Color(153, 27, 27));
        avisoPerigo.add(txtAviso);

        corpo.add(Box.createVerticalStrut(6));
        corpo.add(progressBar);
        corpo.add(Box.createVerticalStrut(6));
        corpo.add(labelStatus);
        corpo.add(Box.createVerticalStrut(14));
        corpo.add(labelInstrucao);
        corpo.add(Box.createVerticalStrut(8));
        corpo.add(campoToken);
        corpo.add(Box.createVerticalStrut(12));
        corpo.add(avisoPerigo);

        return corpo;
    }

    private JPanel criarBotoes() {
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new BoxLayout(painelBotoes, BoxLayout.Y_AXIS));
        painelBotoes.setOpaque(false);

        btnConfirmar = new JButton("Confirmar e Excluir Minha Conta");
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConfirmar.setBackground(VERMELHO);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirmar.setPreferredSize(new Dimension(320, 44));
        btnConfirmar.setMaximumSize(new Dimension(360, 44));
        btnConfirmar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnConfirmar.putClientProperty(FlatClientProperties.STYLE,
                "arc:12;focusWidth:0;borderWidth:0;hoverBackground:#B91C1C;pressedBackground:#991B1B");
        btnConfirmar.addActionListener(e -> confirmarExclusao());

        btnReenviar = new JButton("Reenviar código para o e-mail");
        btnReenviar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnReenviar.setForeground(new Color(37, 99, 235));
        btnReenviar.setContentAreaFilled(false);
        btnReenviar.setBorderPainted(false);
        btnReenviar.setFocusPainted(false);
        btnReenviar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReenviar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReenviar.addActionListener(e -> iniciarEnvioToken());

        btnCancelar = new JButton("Cancelar e Manter Minha Conta");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancelar.setForeground(CINZA);
        btnCancelar.setContentAreaFilled(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCancelar.addActionListener(e -> dispose());

        painelBotoes.add(btnConfirmar);
        painelBotoes.add(Box.createVerticalStrut(6));
        painelBotoes.add(btnReenviar);
        painelBotoes.add(Box.createVerticalStrut(2));
        painelBotoes.add(btnCancelar);

        return painelBotoes;
    }

    private void iniciarEnvioToken() {
        tokenAtual = EmailServico.gerarToken();
        progressBar.setVisible(true);
        btnConfirmar.setEnabled(false);
        btnReenviar.setEnabled(false);
        labelStatus.setForeground(CINZA);
        labelStatus.setText("Enviando código de exclusão para o seu e-mail...");

        String email = usuario != null ? usuario.getEmail() : "";
        String nome = usuario != null ? usuario.getNome() : "";

        SwingWorker<EmailServico.ResultadoEnvio, Void> worker = new SwingWorker<>() {
            @Override
            protected EmailServico.ResultadoEnvio doInBackground() {
                return EmailServico.enviarTokenExclusaoConta(email, nome, tokenAtual);
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);

                try {
                    EmailServico.ResultadoEnvio resultado = get();
                    if (resultado.sucesso) {
                        // Envio OK — libera o botão confirmar
                        btnConfirmar.setEnabled(true);
                        btnReenviar.setEnabled(true);
                        campoToken.setEnabled(true);
                        campoToken.requestFocus();
                        labelStatus.setForeground(VERDE);
                        labelStatus.setText("<html><center>✅ <b>Código enviado!</b> Verifique a caixa de entrada ou spam.</center></html>");
                    } else if (!resultado.configurado) {
                        // E-mail não configurado — bloqueia
                        btnConfirmar.setEnabled(false);
                        btnReenviar.setEnabled(true);
                        campoToken.setEnabled(false);
                        labelStatus.setForeground(AMARELO);
                        labelStatus.setText("<html><center>⚠️ <b>Envio de e-mail não configurado.</b><br>Configure o <code>email.properties</code> e clique em <b>Reenviar</b>.</center></html>");
                        JOptionPane.showMessageDialog(DialogoTokenExclusaoConta.this,
                                "O servidor de e-mail não está configurado.\nConfigure o arquivo email.properties e tente reenviar o código.",
                                "Configuração de e-mail necessária",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        // Falha no envio (sem internet, etc.) — bloqueia
                        btnConfirmar.setEnabled(false);
                        btnReenviar.setEnabled(true);
                        campoToken.setEnabled(false);
                        labelStatus.setForeground(VERMELHO);
                        labelStatus.setText("<html><center>❌ <b>Falha ao enviar o e-mail.</b><br>Verifique sua conexão e clique em <b>Reenviar código</b>.</center></html>");
                        JOptionPane.showMessageDialog(DialogoTokenExclusaoConta.this,
                                "Não foi possível enviar o código para o e-mail:\n" + email +
                                "\n\nDetalhe: " + resultado.detalhe +
                                "\n\nVerifique sua conexão com a internet e clique em \"Reenviar código\".",
                                "Falha no envio do e-mail",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    btnConfirmar.setEnabled(false);
                    btnReenviar.setEnabled(true);
                    campoToken.setEnabled(false);
                    labelStatus.setForeground(VERMELHO);
                    labelStatus.setText("<html><center>❌ <b>Erro ao conectar ao servidor de e-mail.</b><br>Verifique sua conexão e clique em <b>Reenviar código</b>.</center></html>");
                    JOptionPane.showMessageDialog(DialogoTokenExclusaoConta.this,
                            "Erro inesperado ao tentar enviar o código para:\n" + email +
                            "\n\nVerifique sua conexão com a internet e tente reenviar.",
                            "Erro no envio",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void confirmarExclusao() {
        String tokenDigitado = campoToken.getText().trim();

        if (tokenDigitado.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor, digite o código de 6 dígitos recebido no seu e-mail!",
                    "Token obrigatório",
                    JOptionPane.WARNING_MESSAGE
            );
            campoToken.requestFocus();
            return;
        }

        if (tokenAtual == null || !tokenAtual.equals(tokenDigitado)) {
            JOptionPane.showMessageDialog(
                    this,
                    "O token digitado está incorreto!\nConfira o código recebido no seu e-mail e tente novamente.",
                    "Token inválido",
                    JOptionPane.WARNING_MESSAGE
            );
            campoToken.selectAll();
            campoToken.requestFocus();
            return;
        }

        // Token válido! Realiza a exclusão do usuário no banco de dados MySQL
        if (usuario == null || usuario.getId() <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível identificar o usuário atual.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        UsuarioDao dao = new UsuarioDao();
        boolean excluido = dao.deletar(usuario.getId());

        if (excluido) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sua conta foi excluída com sucesso do sistema.\nVocê será desconectado e redirecionado para a tela de login.",
                    "Conta Excluída",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

            if (paiDialogo != null) {
                paiDialogo.dispose();
            }

            if (framePrincipal != null) {
                framePrincipal.dispose();
            }

            // Retorna para a tela de Login
            SwingUtilities.invokeLater(() -> new Tela());

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao excluir a conta no banco de dados. Tente novamente.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
