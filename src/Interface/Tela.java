
package Interface;

import DAO.UsuarioDao;
import Escola.Usuario;

import javax.swing.*;
import java.awt.*;

import com.formdev.flatlaf.FlatClientProperties;

public class Tela extends JFrame {

    private final Color AZUL_ESCURO = new Color(20, 43, 74);
    private final Color AZUL = new Color(37, 99, 235);
    private final Color FUNDO = new Color(248, 250, 253);
    private final Color TEXTO = new Color(23, 43, 77);
    private final Color CINZA = new Color(100, 116, 139);

    private JTextField campologin;
    private JPasswordField camposenha;
    private JButton botaoentrar;

    public Tela() {
        setTitle("LALACK 1.0");
        setSize(900, 600);
        setMinimumSize(new Dimension(760, 520));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(FUNDO);

        // PAINEL LATERAL
        JPanel lateral = new JPanel();
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setBackground(AZUL_ESCURO);
        lateral.setBorder(
                BorderFactory.createEmptyBorder(45, 35, 45, 35)
        );
        lateral.setPreferredSize(new Dimension(320, 600));

        ImageIcon iconeOriginal = new ImageIcon(
                getClass().getResource("/imagens/chapeu-de-formatura.png")
        );

        Image imagemRedimensionada = iconeOriginal.getImage()
                .getScaledInstance(80, 80, Image.SCALE_SMOOTH);

        JLabel simbolo = new JLabel(
                new ImageIcon(imagemRedimensionada)
        );
        simbolo.setFont(new Font("Segoe UI", Font.BOLD, 38));
        simbolo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel marca = new JLabel("LALACK");
        marca.setFont(new Font("Segoe UI", Font.BOLD, 25));
        marca.setForeground(Color.WHITE);
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descricao = new JLabel(
                "<html>Gestão escolar simples,<br>" +
                        "organizada e eficiente.</html>"
        );
        descricao.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descricao.setForeground(new Color(208, 221, 234));
        descricao.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rodape = new JLabel(
                "<html>GESTÃO<br>FREQUÊNCIA<br>EDUCAÇÃO</html>"
        );
        rodape.setFont(new Font("Segoe UI", Font.BOLD, 11));
        rodape.setForeground(new Color(117, 226, 208));
        rodape.setAlignmentX(Component.LEFT_ALIGNMENT);

        lateral.add(Box.createVerticalGlue());
        lateral.add(simbolo);
        lateral.add(Box.createVerticalStrut(20));
        lateral.add(marca);
        lateral.add(Box.createVerticalStrut(15));
        lateral.add(descricao);
        lateral.add(Box.createVerticalGlue());
        lateral.add(rodape);

        // ÁREA DO LOGIN
        JPanel areaLogin = new JPanel();
        areaLogin.setLayout(new BoxLayout(areaLogin, BoxLayout.Y_AXIS));
        areaLogin.setBackground(FUNDO);
        areaLogin.setBorder(
                BorderFactory.createEmptyBorder(45, 45, 45, 45)
        );

        JLabel titulo = new JLabel("Bem-vindo de volta!");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 27));
        titulo.setForeground(TEXTO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel(
                "Entre com seus dados para continuar."
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(CINZA);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        campologin = new JTextField();
        campologin.putClientProperty(
                "JTextField.placeholderText", "Digite seu login"
        );
        campologin.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:12;focusWidth:1"
        );
        campologin.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        campologin.setMaximumSize(new Dimension(360, 46));
        campologin.setPreferredSize(new Dimension(360, 46));
        campologin.setAlignmentX(Component.CENTER_ALIGNMENT);

        camposenha = new JPasswordField();
        camposenha.putClientProperty(
                "JTextField.placeholderText", "Digite sua senha"
        );
        camposenha.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:12;focusWidth:1"
        );
        camposenha.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        camposenha.setMaximumSize(new Dimension(360, 46));
        camposenha.setPreferredSize(new Dimension(360, 46));
        camposenha.setAlignmentX(Component.CENTER_ALIGNMENT);

        botaoentrar = new JButton("Entrar");
        botaoentrar.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:12;focusWidth:0;borderWidth:0;" +
                        "hoverBackground:#1D4ED8;" +
                        "pressedBackground:#1E40AF"
        );
        botaoentrar.setBackground(AZUL);
        botaoentrar.setForeground(Color.WHITE);
        botaoentrar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        botaoentrar.setFocusPainted(false);
        botaoentrar.setMaximumSize(new Dimension(360, 48));
        botaoentrar.setPreferredSize(new Dimension(360, 48));
        botaoentrar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel cadastro = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        cadastro.setOpaque(false);
        cadastro.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textoCadastro = new JLabel("Não possui cadastro?");
        textoCadastro.setForeground(CINZA);
        textoCadastro.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel linkCadastro = new JLabel("Cadastrar");
        linkCadastro.setForeground(AZUL);
        linkCadastro.setFont(new Font("Segoe UI", Font.BOLD, 13));
        linkCadastro.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        cadastro.add(textoCadastro);
        cadastro.add(linkCadastro);

        areaLogin.add(Box.createVerticalGlue());
        areaLogin.add(titulo);
        areaLogin.add(Box.createVerticalStrut(10));
        areaLogin.add(subtitulo);
        areaLogin.add(Box.createVerticalStrut(35));
        areaLogin.add(campologin);
        areaLogin.add(Box.createVerticalStrut(15));
        areaLogin.add(camposenha);
        areaLogin.add(Box.createVerticalStrut(25));
        areaLogin.add(botaoentrar);
        areaLogin.add(Box.createVerticalStrut(20));
        areaLogin.add(cadastro);
        areaLogin.add(Box.createVerticalGlue());

        principal.add(lateral, BorderLayout.WEST);
        principal.add(areaLogin, BorderLayout.CENTER);
        setContentPane(principal);

        // AÇÕES DOS COMPONENTES
        botaoentrar.addActionListener(e -> fazerLogin());
        getRootPane().setDefaultButton(botaoentrar);

        linkCadastro.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e) {
                        abrirCadastro();
                    }
                }
        );

        setVisible(true);
    }

    private void fazerLogin() {
        String login = campologin.getText();
        String senha = new String(camposenha.getPassword());

        UsuarioDao usuarioDao = new UsuarioDao();
        Usuario usuario = usuarioDao.login(login, senha);

        if (usuario != null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Login realizado com sucesso!\nBem-vindo(a), "
                            + usuario.getNome()
            );

            new Alunos(usuario);
            dispose();

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Login ou senha incorretos!"
            );
        }
    }

    private void abrirCadastro() {
        Cadastro cadastro = new Cadastro();
        cadastro.setVisible(true);
    }
}