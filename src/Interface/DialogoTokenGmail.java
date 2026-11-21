package Interface;

import DAO.UsuarioDao;
import Escola.Usuario;
import Servico.EmailServico;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo modal exibido assim que o usuário clica em "Cadastrar".
 * Título e fluxo: "Envio de token para o gmail", enviando o código real
 * para a caixa de entrada do Gmail e solicitando a confirmação do token de 6 dígitos.
 */
public class DialogoTokenGmail extends JDialog {

    private final JFrame pai;
    private final String nome;
    private final String email;
    private final String login;
    private final String senha;

    private String tokenAtual;
    private JLabel labelStatus;
    private JLabel labelInstrucao;
    private JProgressBar progressBar;
    private JTextField campoToken;
    private JButton btnConfirmar;
    private JButton btnReenviar;
    private JButton btnVoltar;

    private final boolean modoEscuro = Configuracoes.isModoEscuro();
    private final Color FUNDO = Configuracoes.getCorFundo();
    private final Color TEXTO = Configuracoes.getCorTexto();
    private final Color CINZA = Configuracoes.getCorSubtexto();
    private final Color AZUL = new Color(37, 99, 235);
    private final Color VERDE = new Color(22, 163, 74);
    private final Color AMARELO = new Color(202, 138, 4);

    public DialogoTokenGmail(JFrame pai, String nome, String email, String login, String senha) {
        super(pai, "Envio de token para o gmail", true);

        this.pai = pai;
        this.nome = nome;
        this.email = email;
        this.login = login;
        this.senha = senha;

        setSize(520, 500);
        setResizable(false);
        setLocationRelativeTo(pai);
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
        System.out.println("JANELA DE TOKEN ABERTA!");
        System.out.println("E-mail destinatário: " + email);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setOpaque(false);

        JLabel icone = new JLabel("📧", SwingConstants.CENTER);
        icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        icone.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Envio de token para o gmail", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXTO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Enviando código de verificação para:", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(CINZA);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel badgeEmail = new JLabel("  " + email + "  ", SwingConstants.CENTER);
        badgeEmail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        badgeEmail.setForeground(AZUL);
        badgeEmail.setOpaque(true);
        badgeEmail.setBackground(modoEscuro ? new Color(30, 41, 59) : new Color(239, 246, 255));
        badgeEmail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(modoEscuro ? new Color(59, 130, 246) : new Color(191, 219, 254), 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        badgeEmail.setAlignmentX(Component.CENTER_ALIGNMENT);

        cabecalho.add(icone);
        cabecalho.add(Box.createVerticalStrut(8));
        cabecalho.add(titulo);
        cabecalho.add(Box.createVerticalStrut(6));
        cabecalho.add(subtitulo);
        cabecalho.add(Box.createVerticalStrut(4));
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

        labelStatus = new JLabel("Conectando ao Gmail...", SwingConstants.CENTER);
        labelStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelStatus.setForeground(CINZA);
        labelStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        labelInstrucao = new JLabel("Digite abaixo o código de 6 dígitos recebido:", SwingConstants.CENTER);
        labelInstrucao.setFont(new Font("Segoe UI", Font.BOLD, 13));
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

        // Submeter ao pressionar Enter
        campoToken.addActionListener(e -> confirmarToken());

        corpo.add(Box.createVerticalStrut(10));
        corpo.add(progressBar);
        corpo.add(Box.createVerticalStrut(6));
        corpo.add(labelStatus);
        corpo.add(Box.createVerticalStrut(16));
        corpo.add(labelInstrucao);
        corpo.add(Box.createVerticalStrut(10));
        corpo.add(campoToken);

        return corpo;
    }

    private JPanel criarBotoes() {
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new BoxLayout(painelBotoes, BoxLayout.Y_AXIS));
        painelBotoes.setOpaque(false);

        btnConfirmar = new JButton("Confirmar e Criar Conta");
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirmar.setBackground(AZUL);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirmar.setPreferredSize(new Dimension(320, 44));
        btnConfirmar.setMaximumSize(new Dimension(360, 44));
        btnConfirmar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnConfirmar.putClientProperty(FlatClientProperties.STYLE, "arc:12;focusWidth:0");
        btnConfirmar.addActionListener(e -> confirmarToken());

        btnReenviar = new JButton("Reenviar código para o Gmail");
        btnReenviar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnReenviar.setForeground(AZUL);
        btnReenviar.setContentAreaFilled(false);
        btnReenviar.setBorderPainted(false);
        btnReenviar.setFocusPainted(false);
        btnReenviar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReenviar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReenviar.addActionListener(e -> iniciarEnvioToken());

        btnVoltar = new JButton("Voltar e Corrigir Dados");
        btnVoltar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVoltar.setForeground(CINZA);
        btnVoltar.setContentAreaFilled(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVoltar.addActionListener(e -> dispose());

        painelBotoes.add(btnConfirmar);
        painelBotoes.add(Box.createVerticalStrut(6));
        painelBotoes.add(btnReenviar);
        painelBotoes.add(Box.createVerticalStrut(2));
        painelBotoes.add(btnVoltar);

        return painelBotoes;
    }

    /**
     * Envia o token para o Gmail em segundo plano (SwingWorker) para não travar a interface.
     * O token NUNCA é exibido na tela — apenas enviado para o e-mail cadastrado.
     * Em caso de falha, o cadastro é bloqueado até que o envio funcione.
     */
    private void iniciarEnvioToken() {
        tokenAtual = EmailServico.gerarToken();
        progressBar.setVisible(true);
        btnConfirmar.setEnabled(false);
        btnReenviar.setEnabled(false);
        labelStatus.setForeground(CINZA);
        labelStatus.setText("Enviando token para a sua caixa de entrada do Gmail...");

        SwingWorker<EmailServico.ResultadoEnvio, Void> worker = new SwingWorker<>() {
            @Override
            protected EmailServico.ResultadoEnvio doInBackground() {
                return EmailServico.enviarTokenGmail(email, nome, tokenAtual);
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);

                try {
                    EmailServico.ResultadoEnvio resultado = get();
                    if (resultado.sucesso) {
                        // Envio OK — libera o campo e o botão confirmar
                        btnConfirmar.setEnabled(true);
                        btnReenviar.setEnabled(true);
                        campoToken.setEnabled(true);
                        campoToken.requestFocus();
                        labelStatus.setForeground(VERDE);
                        labelStatus.setText("<html><center>✅ <b>Código enviado para seu Gmail!</b> Verifique a caixa de entrada ou spam.</center></html>");
                    } else if (!resultado.configurado) {
                        // E-mail não configurado no servidor — bloqueia
                        btnConfirmar.setEnabled(false);
                        btnReenviar.setEnabled(true);
                        campoToken.setEnabled(false);
                        labelStatus.setForeground(AMARELO);
                        labelStatus.setText("<html><center>⚠️ <b>Envio de e-mail não configurado.</b><br>Configure o <code>email.properties</code> e clique em <b>Reenviar</b>.</center></html>");
                        JOptionPane.showMessageDialog(DialogoTokenGmail.this,
                                "O servidor de e-mail não está configurado.\nConfigure o arquivo email.properties e tente reenviar o código.",
                                "Configuração de e-mail necessária",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        // Falha no envio (sem internet, credencial errada, etc.) — bloqueia
                        btnConfirmar.setEnabled(false);
                        btnReenviar.setEnabled(true);
                        campoToken.setEnabled(false);
                        labelStatus.setForeground(new Color(220, 38, 38));
                        labelStatus.setText("<html><center>❌ <b>Falha ao enviar o e-mail.</b><br>Verifique sua conexão e clique em <b>Reenviar código</b>.</center></html>");
                        JOptionPane.showMessageDialog(DialogoTokenGmail.this,
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
                    labelStatus.setForeground(new Color(220, 38, 38));
                    labelStatus.setText("<html><center>❌ <b>Erro ao conectar ao servidor de e-mail.</b><br>Verifique sua conexão e clique em <b>Reenviar código</b>.</center></html>");
                    JOptionPane.showMessageDialog(DialogoTokenGmail.this,
                            "Erro inesperado ao tentar enviar o código para:\n" + email +
                            "\n\nVerifique sua conexão com a internet e tente reenviar.",
                            "Erro no envio",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void confirmarToken() {
        String tokenDigitado = campoToken.getText().trim();

        if (tokenDigitado.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor, digite o código de 6 dígitos recebido no seu Gmail!",
                    "Token obrigatório",
                    JOptionPane.WARNING_MESSAGE
            );
            campoToken.requestFocus();
            return;
        }

        if (tokenAtual == null || !tokenAtual.equals(tokenDigitado)) {
            JOptionPane.showMessageDialog(
                    this,
                    "O token digitado está incorreto!\nConfira o código de 6 dígitos recebido e tente novamente.",
                    "Token inválido",
                    JOptionPane.WARNING_MESSAGE
            );
            campoToken.selectAll();
            campoToken.requestFocus();
            return;
        }

        // Token correto! Salvar usuário no banco de dados MySQL
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setLogin(login);
        usuario.setSenha(senha);

        UsuarioDao dao = new UsuarioDao();
        if (dao.salvar(usuario)) {
            JOptionPane.showMessageDialog(
                    this,
                    "🎉 Conta criada com sucesso!\nSeu e-mail (" + email + ") foi confirmado e sua conta está ativa.",
                    "Cadastro Concluído",
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
            if (pai != null) {
                pai.dispose();
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao salvar a conta no banco de dados. Tente novamente.",
                    "Erro no banco de dados",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
