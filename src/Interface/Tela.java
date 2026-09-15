package Interface;

import DAO.UsuarioDao;
import Escola.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import com.formdev.flatlaf.FlatClientProperties;

public class Tela extends JFrame {

    private final Color AZUL_ESCURO = new Color(255, 255, 255, 255);
    private final Color AZUL = new Color(37,99,235);
    private static final Color FUNDO = new Color(7, 6, 6, 240);
    private JTextField campologin;
    private JPasswordField camposenha;
    private JButton botaoentrar;
    private JButton botaocadastrar;

    public Tela() {
        setTitle("Controle Faltas");
        setSize(600,400);


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);


        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(30,60,30,60));
        painel.setBackground(FUNDO);



        JLabel titulo = new JLabel("CONTROLE DE FALTAS", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD,24));
        titulo.setForeground(AZUL_ESCURO);
        titulo.setBorder(BorderFactory.createEmptyBorder(25,10,25,10));


        campologin = new JTextField();
        campologin.putClientProperty(FlatClientProperties.STYLE,
                "arc:15;focusWidth:2");
        campologin.setText("Login...");
        campologin.setForeground(Color.GRAY);

        campologin.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (campologin.getText().equals("Login...")) {
                    campologin.setText("");
                    campologin.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (campologin.getText().isEmpty()) {
                    campologin.setText("Login...");
                    campologin.setForeground(Color.GRAY);
                }
            }
        });
        camposenha = new JPasswordField();
        camposenha.putClientProperty(FlatClientProperties.STYLE,
                "arc:15;focusWidth:2");
        camposenha.setEchoChar((char) 0);
        camposenha.setText("Senha...");
        camposenha.setForeground(Color.GRAY);

        camposenha.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(camposenha.getPassword()).equals("Senha...")) {
                    camposenha.setText("");
                    camposenha.setForeground(Color.BLACK);
                    camposenha.setEchoChar('•');
                }
            }

            public void focusLost(FocusEvent e) {
                if (camposenha.getPassword().length == 0) {
                    camposenha.setText("Senha...");
                    camposenha.setForeground(Color.GRAY);
                    camposenha.setEchoChar((char) 0);
                }
            }
        });
        campologin.setText("Login...");
        camposenha.setText("Senha...");

        campologin.setMaximumSize(new Dimension(250, 45));
        camposenha.setMaximumSize(new Dimension(250, 45));

        campologin.setPreferredSize(new Dimension(250, 45));
        camposenha.setPreferredSize(new Dimension(250, 45));

        botaoentrar = new JButton("Entrar");

        botaoentrar.putClientProperty(FlatClientProperties.STYLE,
                "arc:15;" +
                        "focusWidth:0;" +
                        "borderWidth:0;" +
                        "hoverBackground:#2563EB;" +
                        "pressedBackground:#1D4ED8;");

        botaoentrar.setBackground(AZUL);
        botaoentrar.setForeground(Color.WHITE);
        botaoentrar.setFont(new Font("Arial", Font.BOLD, 16));
        botaoentrar.setFocusPainted(false);

        botaoentrar.setPreferredSize(new Dimension(250, 50));
        botaoentrar.setMaximumSize(new Dimension(250, 50));
        botaoentrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        botaocadastrar = new JButton("Cadastrar");


        botaocadastrar.putClientProperty(FlatClientProperties.STYLE,
                "arc:15;focusWidth:0");

        JLabel textoCadastro = new JLabel("Não possui cadastro?");
        JLabel linkCadastro = new JLabel("Cadastrar");

        linkCadastro.setForeground(AZUL);
        linkCadastro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        textoCadastro.setForeground(Color.WHITE);

        botaoentrar.setPreferredSize(new Dimension(250,50));
        botaoentrar.setMaximumSize(new Dimension(250, 50));
        botaocadastrar.setPreferredSize(new Dimension(250,50));
        botaocadastrar.setBackground(AZUL);
        botaoentrar.setBackground(AZUL);

        botaocadastrar.setForeground(Color.WHITE);
        botaoentrar.setForeground(Color.WHITE);

        botaocadastrar.setFocusPainted(false);
        botaoentrar.setFocusPainted(false);

        botaocadastrar.setFont(new Font("Arial", Font.BOLD, 16));
        botaoentrar.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel painelCadastro = new JPanel();
        painelCadastro.setOpaque(false);

        painelCadastro.add(textoCadastro);
        painelCadastro.add(linkCadastro);

        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        campologin.setAlignmentX(Component.CENTER_ALIGNMENT);
        camposenha.setAlignmentX(Component.CENTER_ALIGNMENT);
        botaoentrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCadastro.setAlignmentX(Component.CENTER_ALIGNMENT);

        painel.add(titulo);
        painel.add(Box.createVerticalStrut(10));
        painel.add(campologin);
        painel.add(Box.createVerticalStrut(10));
        painel.add(camposenha);
        painel.add(Box.createVerticalStrut(15));
        painel.add(botaoentrar);
        painel.add(Box.createVerticalStrut(10));
        painel.add(painelCadastro);



        add(painel);

        botaoentrar.addActionListener(e -> fazerLogin());

        botaocadastrar.addActionListener(e -> abrirCadastro());

        linkCadastro.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                abrirCadastro();
            }
        });

        setVisible(true);


    }

    private void fazerLogin() {
        String login = campologin.getText();
        String senha = new String(camposenha.getPassword());

        UsuarioDao usuarioDao = new UsuarioDao();

        Usuario usuario = usuarioDao.login(login, senha);

        if (usuario != null) {

            JOptionPane.showMessageDialog(this, "Login realizado com sucesso! \n Bem-vinda," + usuario.getNome());

            new Alunos();
            dispose();

        } else {
            JOptionPane.showMessageDialog(this,"Login ou senha incorretos!");
        }
    }

    private void abrirCadastro() {

        Cadastro cadastro = new Cadastro();
        cadastro.setVisible(true);

    }


}
