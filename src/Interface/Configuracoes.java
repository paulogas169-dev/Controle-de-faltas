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

        this.usuario = usuario;
        this.aoMudarCor = aoMudarCor;
        this.aoAtualizarUsuario = aoAtualizarUsuario;

        setSize(580, 640);
        setMinimumSize(new Dimension(500, 580));
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

        JSeparator separador = new JSeparator();
        separador.setForeground(BORDA);
        separador.setAlignmentX(Component.LEFT_ALIGNMENT);

        formulario.add(separador);
        formulario.add(Box.createVerticalStrut(18));

        JLabel tituloAparencia = criarTituloSecao("Aparência e Tema");
        JLabel descricaoCor = criarLabel("Padrão visual configurado permanentemente para o sistema:");

        JPanel painelTemaFixo = new JPanel(new BorderLayout(14, 0));
        painelTemaFixo.setBackground(new Color(248, 250, 252));
        painelTemaFixo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        painelTemaFixo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        painelTemaFixo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelTemaFixo.putClientProperty(FlatClientProperties.STYLE, "arc:10");

        JLabel iconeSol = new JLabel("☀️");
        iconeSol.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        JLabel infoTema = new JLabel("Modo Claro / Tema Branco Fixo (Padrão Ativo)");
        infoTema.setFont(new Font("Segoe UI", Font.BOLD, 13));
        infoTema.setForeground(new Color(30, 41, 59));

        JLabel tagFixo = new JLabel("  PADRÃO FIXO  ");
        tagFixo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tagFixo.setForeground(AZUL);
        tagFixo.setBackground(new Color(239, 246, 255));
        tagFixo.setOpaque(true);
        tagFixo.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        tagFixo.putClientProperty(FlatClientProperties.STYLE, "arc:6");

        painelTemaFixo.add(iconeSol, BorderLayout.WEST);
        painelTemaFixo.add(infoTema, BorderLayout.CENTER);
        painelTemaFixo.add(tagFixo, BorderLayout.EAST);

        formulario.add(tituloAparencia);
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(descricaoCor);
        formulario.add(Box.createVerticalStrut(12));
        formulario.add(painelTemaFixo);

        return formulario;
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
