package Interface;

import DAO.UsuarioDao;
import Escola.Usuario;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
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
    private JTextField campoLogin;
    private JLabel amostraCor;
    private JButton btnModoClaro;
    private JButton btnModoEscuro;

    private boolean modoEscuroSelecionado;
    private Color corSelecionada;

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
        this.modoEscuroSelecionado = isModoEscuro();
        this.corSelecionada = this.modoEscuroSelecionado ? COR_PRETA : COR_BRANCA;

        setSize(600, 620);
        setMinimumSize(new Dimension(520, 560));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(pai);

        JPanel principal = new JPanel(new BorderLayout(0, 20));
        principal.setBackground(modoEscuroSelecionado ? COR_PRETA_PAINEL : Color.WHITE);
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
        cabecalho.setBackground(modoEscuroSelecionado ? new Color(20, 20, 24) : BORDA);
        cabecalho.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(modoEscuroSelecionado ? new Color(45, 45, 52) : BORDA),
                        BorderFactory.createEmptyBorder(16, 22, 16, 22)
                )
        );

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 4));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Configurações");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(modoEscuroSelecionado ? Color.WHITE : TEXTO);

        JLabel subtitulo = new JLabel("Gerencie sua conta e a aparência do sistema");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(CINZA);

        textos.add(titulo);
        textos.add(subtitulo);

        JLabel labelUsuario = new JLabel("Usuário: " + usuario.getNome());
        labelUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelUsuario.setForeground(modoEscuroSelecionado ? Color.WHITE : TEXTO);

        cabecalho.add(textos, BorderLayout.CENTER);
        cabecalho.add(labelUsuario, BorderLayout.EAST);

        return cabecalho;
    }

    private JPanel criarFormulario() {
        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setOpaque(false);

        JLabel tituloConta = criarTituloSecao("Sua conta");

        campoNome = criarCampo(usuario.getNome());
        campoLogin = criarCampo(usuario.getLogin());

        JLabel labelNome = criarLabel("Nome de exibição");
        JLabel labelLogin = criarLabel("Nome de usuário (login)");

        formulario.add(tituloConta);
        formulario.add(Box.createVerticalStrut(14));

        formulario.add(labelNome);
        formulario.add(Box.createVerticalStrut(6));
        formulario.add(campoNome);
        formulario.add(Box.createVerticalStrut(12));

        formulario.add(labelLogin);
        formulario.add(Box.createVerticalStrut(6));
        formulario.add(campoLogin);
        formulario.add(Box.createVerticalStrut(20));

        JSeparator separador = new JSeparator();
        separador.setForeground(modoEscuroSelecionado ? new Color(55, 65, 81) : BORDA);
        separador.setAlignmentX(Component.LEFT_ALIGNMENT);

        formulario.add(separador);
        formulario.add(Box.createVerticalStrut(18));

        JLabel tituloAparencia = criarTituloSecao("Aparência e Modo de Cor");

        JLabel descricaoCor = criarLabel(
                "Escolha a cor do tema (apenas Branco ou Preto):"
        );

        JPanel painelBotoesTema = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        painelBotoesTema.setOpaque(false);
        painelBotoesTema.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnModoClaro = new JButton("☀️ Modo Claro (Branco)");
        btnModoClaro.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnModoClaro.setFocusPainted(false);
        btnModoClaro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModoClaro.setPreferredSize(new Dimension(210, 44));
        btnModoClaro.putClientProperty(FlatClientProperties.STYLE, "arc:12;focusWidth:0");

        btnModoEscuro = new JButton("🌙 Modo Escuro (Preto)");
        btnModoEscuro.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnModoEscuro.setFocusPainted(false);
        btnModoEscuro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModoEscuro.setPreferredSize(new Dimension(210, 44));
        btnModoEscuro.putClientProperty(FlatClientProperties.STYLE, "arc:12;focusWidth:0");

        btnModoClaro.addActionListener(e -> alternarTema(false));
        btnModoEscuro.addActionListener(e -> alternarTema(true));

        painelBotoesTema.add(btnModoClaro);
        painelBotoesTema.add(btnModoEscuro);

        amostraCor = new JLabel();
        amostraCor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        amostraCor.setOpaque(true);
        amostraCor.setAlignmentX(Component.LEFT_ALIGNMENT);
        amostraCor.setHorizontalAlignment(SwingConstants.CENTER);
        amostraCor.setPreferredSize(new Dimension(430, 42));
        amostraCor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        amostraCor.putClientProperty(FlatClientProperties.STYLE, "arc:10");

        formulario.add(tituloAparencia);
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(descricaoCor);
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(painelBotoesTema);
        formulario.add(Box.createVerticalStrut(12));
        formulario.add(amostraCor);

        atualizarVisualBotoesTema();

        return formulario;
    }

    private void alternarTema(boolean modoEscuro) {
        this.modoEscuroSelecionado = modoEscuro;
        this.corSelecionada = modoEscuro ? COR_PRETA : COR_BRANCA;
        atualizarVisualBotoesTema();
    }

    private void atualizarVisualBotoesTema() {
        if (!modoEscuroSelecionado) {
            btnModoClaro.setBackground(Color.WHITE);
            btnModoClaro.setForeground(AZUL);
            btnModoClaro.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AZUL, 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));

            btnModoEscuro.setBackground(new Color(241, 245, 249));
            btnModoEscuro.setForeground(CINZA);
            btnModoEscuro.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDA, 1),
                    BorderFactory.createEmptyBorder(9, 13, 9, 13)
            ));

            amostraCor.setText("⚪ Cor selecionada: BRANCA (Modo Claro)");
            amostraCor.setBackground(Color.WHITE);
            amostraCor.setForeground(TEXTO);
            amostraCor.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDA, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
        } else {
            btnModoClaro.setBackground(new Color(241, 245, 249));
            btnModoClaro.setForeground(CINZA);
            btnModoClaro.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDA, 1),
                    BorderFactory.createEmptyBorder(9, 13, 9, 13)
            ));

            btnModoEscuro.setBackground(new Color(24, 24, 27));
            btnModoEscuro.setForeground(Color.WHITE);
            btnModoEscuro.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AZUL, 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));

            amostraCor.setText("🌙 Cor selecionada: PRETA (Modo Escuro / Dark)");
            amostraCor.setBackground(new Color(18, 18, 18));
            amostraCor.setForeground(Color.WHITE);
            amostraCor.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(55, 65, 81), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
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
        label.setForeground(modoEscuroSelecionado ? Color.WHITE : TEXTO);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(modoEscuroSelecionado ? new Color(180, 185, 195) : CINZA);
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
        String login = campoLogin.getText().trim();

        if (nome.isEmpty() || login.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Preencha o nome e o login.",
                    "Campos obrigatórios",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        usuario.setNome(nome);
        usuario.setLogin(login);

        UsuarioDao usuarioDao = new UsuarioDao();
        if (!usuarioDao.atualizarDados(usuario)) {
            return;
        }

        salvarModoEscuro(modoEscuroSelecionado);
        aplicarTema(modoEscuroSelecionado);

        if (aoMudarCor != null) {
            aoMudarCor.accept(corSelecionada);
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
        return PREFERENCIAS.getBoolean(CHAVE_MODO_ESCURO, false);
    }

    public static void salvarModoEscuro(boolean escuro) {
        PREFERENCIAS.putBoolean(CHAVE_MODO_ESCURO, escuro);
        salvarCorFundo(escuro ? COR_PRETA : COR_BRANCA);
    }

    public static Color carregarCorFundo() {
        if (isModoEscuro()) {
            return COR_PRETA;
        }
        int rgb = PREFERENCIAS.getInt(
                CHAVE_COR_FUNDO,
                COR_BRANCA.getRGB()
        );
        return new Color(rgb, true);
    }

    private static void salvarCorFundo(Color cor) {
        PREFERENCIAS.putInt(
                CHAVE_COR_FUNDO,
                cor.getRGB()
        );
    }

    public static void aplicarTema(boolean escuro) {
        if (escuro) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }
        FlatLaf.updateUI();
    }

    public static void aplicarTemaSalvo() {
        aplicarTema(isModoEscuro());
    }

    public static Color getCorFundo() {
        return isModoEscuro() ? COR_PRETA : COR_BRANCA;
    }

    public static Color getCorPainel() {
        return isModoEscuro() ? COR_PRETA_PAINEL : Color.WHITE;
    }

    public static Color getCorTexto() {
        return isModoEscuro() ? Color.WHITE : new Color(23, 43, 77);
    }

    public static Color getCorSubtexto() {
        return isModoEscuro() ? new Color(160, 165, 175) : new Color(100, 116, 139);
    }

    public static Color getCorBorda() {
        return isModoEscuro() ? new Color(45, 45, 52) : new Color(226, 232, 240);
    }

    public static Color getCorDestaque() {
        return isModoEscuro() ? new Color(30, 41, 59) : new Color(239, 246, 255);
    }

    public static Color getCorTextoDestaque() {
        return isModoEscuro() ? new Color(96, 165, 250) : new Color(37, 99, 235);
    }
}
