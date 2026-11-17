package Interface;

import DAO.UsuarioDao;
import Escola.Usuario;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

public class Configuracoes extends JDialog {

    public static final Color COR_BRANCA = new Color(248, 250, 253);
    public static final Color COR_BRANCA_PAINEL = Color.WHITE;
    public static final Color COR_PRETA = new Color(18, 18, 18);
    public static final Color COR_PRETA_PAINEL = new Color(28, 28, 30);

    private static final String CHAVE_COR_FUNDO = "corFundo";
    private static final String CHAVE_MODO_ESCURO = "modoEscuro";

    private static final Preferences PREFERENCIAS =
            Preferences.userNodeForPackage(Configuracoes.class);

    private final JFrame pai;
    private final Usuario usuario;
    private final Consumer<Color> aoMudarCor;
    private final Runnable aoAtualizarUsuario;

    private JTextField campoNome;
    private JTextField campoEmail;
    private JTextField campoLogin;

    private final Color AZUL = new Color(37, 99, 235);
    private final Color TEXTO = new Color(23, 43, 77);
    private final Color CINZA = new Color(100, 116, 139);
    private final Color BORDA = new Color(226, 232, 240);

    public Configuracoes(
            JFrame pai,
            Usuario usuario,
            Consumer<Color> aoMudarCor,
            Runnable aoAtualizarUsuario
    ) {
        super(pai, "Configurações", true);

        this.pai = pai;
        this.usuario = usuario;
        this.aoMudarCor = aoMudarCor;
        this.aoAtualizarUsuario = aoAtualizarUsuario;

        setSize(600, 720);
        setMinimumSize(new Dimension(520, 640));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(pai);

        JPanel principal = new JPanel(new BorderLayout(0, 20));
        principal.setBackground(Color.WHITE);
        principal.setBorder(
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        );

        principal.add(criarCabecalho(), BorderLayout.NORTH);
        principal.add(criarFormulario(), BorderLayout.CENTER);
        principal.add(criarRodape(), BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(new Color(248, 250, 252));
        cabecalho.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA),
                        BorderFactory.createEmptyBorder(16, 22, 16, 22)
                )
        );

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 4));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Configurações");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel("Gerencie sua conta e visualize os padrões do sistema");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(CINZA);

        textos.add(titulo);
        textos.add(subtitulo);

        JLabel labelUsuario = new JLabel("Usuário: " + (usuario != null ? usuario.getNome() : ""));
        labelUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelUsuario.setForeground(TEXTO);

        cabecalho.add(textos, BorderLayout.CENTER);
        cabecalho.add(labelUsuario, BorderLayout.EAST);

        return cabecalho;
    }

    private JPanel criarFormulario() {
        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setOpaque(false);

        JLabel tituloConta = criarTituloSecao("Sua conta");

        campoNome = criarCampo(usuario != null ? usuario.getNome() : "");
        campoEmail = criarCampo(usuario != null && usuario.getEmail() != null ? usuario.getEmail() : "");
        campoLogin = criarCampo(usuario != null ? usuario.getLogin() : "");

        JLabel labelNome = criarLabel("Nome de exibição");
        JLabel labelEmail = criarLabel("E-mail");
        JLabel labelLogin = criarLabel("Nome de usuário (login)");

        formulario.add(tituloConta);
        formulario.add(Box.createVerticalStrut(14));

        formulario.add(labelNome);
        formulario.add(Box.createVerticalStrut(6));
        formulario.add(campoNome);
        formulario.add(Box.createVerticalStrut(12));

        formulario.add(labelEmail);
        formulario.add(Box.createVerticalStrut(6));
        formulario.add(campoEmail);
        formulario.add(Box.createVerticalStrut(12));

        formulario.add(labelLogin);
        formulario.add(Box.createVerticalStrut(6));
        formulario.add(campoLogin);
        formulario.add(Box.createVerticalStrut(20));

        JSeparator separador1 = new JSeparator();
        separador1.setForeground(BORDA);
        separador1.setAlignmentX(Component.LEFT_ALIGNMENT);

        formulario.add(separador1);
        formulario.add(Box.createVerticalStrut(24));

        // SEÇÃO: EXCLUIR CONTA (COM CONFIRMAÇÃO DE TOKEN VIA E-MAIL)

        JLabel tituloZonaPerigo = criarTituloSecao("Excluir Conta");
        tituloZonaPerigo.setForeground(new Color(185, 28, 28));

        JLabel descricaoPerigo = criarLabel("Para excluir sua conta definitivamente, um código de confirmação será enviado para seu e-mail:");

        JButton btnExcluirConta = new JButton("🗑️ Excluir Minha Conta");
        btnExcluirConta.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExcluirConta.setForeground(new Color(185, 28, 28));
        btnExcluirConta.setBackground(new Color(254, 242, 242));
        btnExcluirConta.setFocusPainted(false);
        btnExcluirConta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExcluirConta.setPreferredSize(new Dimension(210, 40));
        btnExcluirConta.setMaximumSize(new Dimension(230, 40));
        btnExcluirConta.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExcluirConta.putClientProperty(FlatClientProperties.STYLE,
                "arc:10;focusWidth:0;borderWidth:1;borderColor:#fca5a5;hoverBackground:#fee2e2;pressedBackground:#fecaca");
        btnExcluirConta.addActionListener(e -> solicitarExclusaoConta());

        formulario.add(tituloZonaPerigo);
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(descricaoPerigo);
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(btnExcluirConta);

        return formulario;
    }

    private void solicitarExclusaoConta() {
        if (usuario == null) {
            JOptionPane.showMessageDialog(this, "Usuário não identificado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String email = campoEmail.getText().trim();
        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe um endereço de e-mail válido no campo de e-mail acima para receber o código de exclusão!",
                    "E-mail obrigatório",
                    JOptionPane.WARNING_MESSAGE
            );
            campoEmail.requestFocus();
            return;
        }

        usuario.setEmail(email);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "⚠️ ATENÇÃO: Confirmação de Exclusão de Conta\n\n"
                        + "Você está solicitando a exclusão definitiva da sua conta (" + usuario.getLogin() + ").\n"
                        + "Um código de verificação (token de 6 dígitos) será enviado para:\n"
                        + email + "\n\n"
                        + "Deseja continuar e enviar o código para o seu e-mail?",
                "Excluir Conta",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            DialogoTokenExclusaoConta dialogo = new DialogoTokenExclusaoConta(this, pai, usuario);
            dialogo.setVisible(true);
        }
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rodape.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnSalvar = new JButton("Salvar alterações");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSalvar.setBackground(AZUL);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalvar.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:10;focusWidth:0;borderWidth:0"
        );

        btnSalvar.addActionListener(e -> salvarConfiguracoes());

        rodape.add(btnCancelar);
        rodape.add(btnSalvar);

        return rodape;
    }

    private JLabel criarTituloSecao(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(TEXTO);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(CINZA);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField criarCampo(String valor) {
        JTextField campo = new JTextField(valor == null ? "" : valor);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        campo.setPreferredSize(new Dimension(400, 42));
        campo.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:10;focusWidth:1"
        );
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return campo;
    }

    private void salvarConfiguracoes() {
        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();
        String login = campoLogin.getText().trim();

        if (nome.isEmpty() || login.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Preencha o nome, e-mail e login.",
                    "Campos obrigatórios",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (usuario != null) {
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setLogin(login);

            UsuarioDao usuarioDao = new UsuarioDao();
            if (!usuarioDao.atualizarDados(usuario)) {
                return;
            }
        }

        salvarModoEscuro(false);
        aplicarTema(false);

        if (aoMudarCor != null) {
            aoMudarCor.accept(COR_BRANCA);
        }

        if (aoAtualizarUsuario != null) {
            aoAtualizarUsuario.run();
        }

        JOptionPane.showMessageDialog(
                this,
                "Configurações salvas com sucesso!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
    }

    public static boolean isModoEscuro() {
        return false; // Tema branco fixo conforme solicitado
    }

    public static void salvarModoEscuro(boolean escuro) {
        PREFERENCIAS.putBoolean(CHAVE_MODO_ESCURO, false);
        salvarCorFundo(COR_BRANCA);
    }

    public static Color carregarCorFundo() {
        return COR_BRANCA;
    }

    private static void salvarCorFundo(Color cor) {
        PREFERENCIAS.putInt(
                CHAVE_COR_FUNDO,
                COR_BRANCA.getRGB()
        );
    }

    public static void aplicarTema(boolean escuro) {
        FlatLightLaf.setup();
        FlatLaf.updateUI();
    }

    public static void aplicarTemaSalvo() {
        aplicarTema(false);
    }

    public static Color getCorFundo() {
        return COR_BRANCA;
    }

    public static Color getCorPainel() {
        return Color.WHITE;
    }

    public static Color getCorTexto() {
        return new Color(23, 43, 77);
    }

    public static Color getCorSubtexto() {
        return new Color(100, 116, 139);
    }

    public static Color getCorBorda() {
        return new Color(226, 232, 240);
    }

    public static Color getCorDestaque() {
        return new Color(239, 246, 255);
    }

    public static Color getCorTextoDestaque() {
        return new Color(37, 99, 235);
    }
}
