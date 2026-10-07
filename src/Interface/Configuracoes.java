package Interface;

import DAO.UsuarioDao;
import Escola.Usuario;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

public class Configuracoes extends JDialog {


        private final Usuario usuario;
        private final Consumer<Color> aoMudarCor;
        private final Runnable aoAtualizarUsuario;

        private JTextField campoNome;
        private JTextField campoLogin;
        private JLabel amostraCor;

        private Color corSelecionada;

        private final Color AZUL = new Color(37, 99, 235);
        private final Color TEXTO = new Color(23, 43, 77);
        private final Color CINZA = new Color(100, 116, 139);
        private final Color BORDA = new Color(226, 232, 240);

    private JPanel painelPrincipal;
    private JPanel areaPrincipal;
    private JPanel conteudoPrincipal;
    private JLabel labelUsuario;



    private static final Preferences PREFERENCIAS =
                Preferences.userNodeForPackage(Configuracoes.class);

        private static final String CHAVE_COR_FUNDO = "corFundo";

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
            this.corSelecionada = carregarCorFundo();

            setSize(620, 620);
            setMinimumSize(new Dimension(540, 560));
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(pai);

            JPanel principal = new JPanel(new BorderLayout(0, 20));
            principal.setBackground(Color.WHITE);
            principal.setBorder(
                    BorderFactory.createEmptyBorder(28, 30, 28, 30)
            );

            principal.add(criarCabecalho(), BorderLayout.NORTH);
            principal.add(criarFormulario(), BorderLayout.CENTER);
            principal.add(criarRodape(), BorderLayout.SOUTH);

            setContentPane(principal);
        }


    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(BORDA);
        cabecalho.setBorder(
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
        );

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 4));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Painel Principal");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel("Gerencie os alunos e as frequências");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(CINZA);

        textos.add(titulo);
        textos.add(subtitulo);

        labelUsuario = new JLabel("Olá, " + usuario.getNome());
        labelUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelUsuario.setForeground(TEXTO);

        JButton botaoConfiguracoes = new JButton("⚙ Configurações");
        botaoConfiguracoes.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botaoConfiguracoes.setForeground(AZUL);
        botaoConfiguracoes.setBackground(new Color(239, 246, 255));
        botaoConfiguracoes.setFocusPainted(false);
        botaoConfiguracoes.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        botaoConfiguracoes.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:10;focusWidth:0;borderWidth:0"
        );

        // Evento do botão


        JPanel ladoDireito = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 0)
        );
        ladoDireito.setOpaque(false);

        ladoDireito.add(labelUsuario);
        ladoDireito.add(botaoConfiguracoes);

        cabecalho.add(textos, BorderLayout.CENTER);
        cabecalho.add(ladoDireito, BorderLayout.EAST);

        return cabecalho;
    }



        private JPanel criarFormulario() {

            JPanel formulario = new JPanel();
            formulario.setLayout(
                    new BoxLayout(formulario, BoxLayout.Y_AXIS)
            );
            formulario.setOpaque(false);

            JLabel tituloConta = criarTituloSecao("Sua conta");

            campoNome = criarCampo(usuario.getNome());
            campoLogin = criarCampo(usuario.getLogin());

            JLabel labelNome = criarLabel("Nome de exibição");
            JLabel labelLogin = criarLabel("Nome de usuário (login)");

            formulario.add(tituloConta);
            formulario.add(Box.createVerticalStrut(16));

            formulario.add(labelNome);
            formulario.add(Box.createVerticalStrut(7));
            formulario.add(campoNome);
            formulario.add(Box.createVerticalStrut(15));

            formulario.add(labelLogin);
            formulario.add(Box.createVerticalStrut(7));
            formulario.add(campoLogin);
            formulario.add(Box.createVerticalStrut(25));

            JSeparator separador = new JSeparator();
            separador.setForeground(BORDA);
            separador.setAlignmentX(Component.LEFT_ALIGNMENT);

            formulario.add(separador);
            formulario.add(Box.createVerticalStrut(20));

            JLabel tituloAparencia = criarTituloSecao("Aparência");

            JLabel descricaoCor = criarLabel(
                    "Cor de fundo do painel principal"
            );

            JPanel painelCor = new JPanel(
                    new FlowLayout(FlowLayout.LEFT, 12, 0)
            );
            painelCor.setOpaque(false);
            painelCor.setAlignmentX(Component.LEFT_ALIGNMENT);
            painelCor.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 55)
            );

            amostraCor = new JLabel("   Prévia da cor   ");
            amostraCor.setOpaque(true);
            amostraCor.setBackground(corSelecionada);
            amostraCor.setForeground(TEXTO);
            amostraCor.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDA),
                            BorderFactory.createEmptyBorder(10, 5, 10, 5)
                    )
            );

            JButton btnEscolherCor = new JButton("Escolher cor");
            btnEscolherCor.setFont(
                    new Font("Segoe UI", Font.BOLD, 12)
            );
            btnEscolherCor.setForeground(AZUL);
            btnEscolherCor.setCursor(
                    new Cursor(Cursor.HAND_CURSOR)
            );
            btnEscolherCor.putClientProperty(
                    FlatClientProperties.STYLE,
                    "arc:10;focusWidth:0"
            );

            btnEscolherCor.addActionListener(e -> escolherCor());

            painelCor.add(amostraCor);
            painelCor.add(btnEscolherCor);

            formulario.add(tituloAparencia);
            formulario.add(Box.createVerticalStrut(12));
            formulario.add(descricaoCor);
            formulario.add(Box.createVerticalStrut(10));
            formulario.add(painelCor);

            return formulario;
        }

        private JPanel criarRodape() {

            JPanel rodape = new JPanel(
                    new FlowLayout(FlowLayout.RIGHT, 10, 0)
            );
            rodape.setOpaque(false);

            JButton btnCancelar = new JButton("Cancelar");
            btnCancelar.setFont(
                    new Font("Segoe UI", Font.PLAIN, 13)
            );
            btnCancelar.addActionListener(e -> dispose());

            JButton btnSalvar = new JButton("Salvar alterações");
            btnSalvar.setFont(
                    new Font("Segoe UI", Font.BOLD, 13)
            );
            btnSalvar.setBackground(AZUL);
            btnSalvar.setForeground(Color.WHITE);
            btnSalvar.setFocusPainted(false);
            btnSalvar.setCursor(
                    new Cursor(Cursor.HAND_CURSOR)
            );
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
            label.setFont(
                    new Font("Segoe UI", Font.BOLD, 16)
            );
            label.setForeground(TEXTO);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);

            return label;
        }

        private JLabel criarLabel(String texto) {

            JLabel label = new JLabel(texto);
            label.setFont(
                    new Font("Segoe UI", Font.PLAIN, 12)
            );
            label.setForeground(CINZA);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);

            return label;
        }

        private JTextField criarCampo(String valor) {

            JTextField campo = new JTextField(valor == null ? "" : valor);
            campo.setFont(
                    new Font("Segoe UI", Font.PLAIN, 14)
            );
            campo.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 42)
            );
            campo.setPreferredSize(new Dimension(400, 42));

            campo.putClientProperty(
                    FlatClientProperties.STYLE,
                    "arc:10;focusWidth:1"
            );

            campo.setAlignmentX(Component.LEFT_ALIGNMENT);

            return campo;
        }

        private void escolherCor() {

            Color novaCor = JColorChooser.showDialog(
                    this,
                    "Escolha a cor de fundo",
                    corSelecionada
            );

            if (novaCor != null) {
                corSelecionada = novaCor;
                amostraCor.setBackground(novaCor);
                amostraCor.repaint();
            }
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

            salvarCorFundo(corSelecionada);

            aoMudarCor.accept(corSelecionada);
            aoAtualizarUsuario.run();

            JOptionPane.showMessageDialog(
                    this,
                    "Configurações salvas com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();
        }

        public static Color carregarCorFundo() {

            int rgb = PREFERENCIAS.getInt(
                    CHAVE_COR_FUNDO,
                    new Color(248, 250, 253).getRGB()
            );

            return new Color(rgb, true);
        }

        private static void salvarCorFundo(Color cor) {

            PREFERENCIAS.putInt(
                    CHAVE_COR_FUNDO,
                    cor.getRGB()
            );
        }
    }



