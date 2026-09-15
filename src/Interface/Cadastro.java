package Interface;

import DAO.UsuarioDao;
import Escola.Usuario;

import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatClientProperties;
public class Cadastro extends JFrame {

    JTextField camponome;
    JTextField campoLogin;
    JPasswordField campoSenha;
    private static final Color FUNDO = new Color(7, 6, 6, 240);


    public Cadastro() {

        setTitle("Cadastro do Usuário");
        setSize(400, 300);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLocationRelativeTo(null);


        JLabel labelnome = new JLabel("Nome: ");
        JLabel labelLogin = new JLabel("Login: ");
        JLabel labelSenha = new JLabel("Senha: ");
        labelnome.setForeground(Color.WHITE);
        labelLogin.setForeground(Color.WHITE);
        labelSenha.setForeground(Color.WHITE);


        camponome = new JTextField(20);
        campoLogin = new JTextField(20);
        campoSenha = new JPasswordField(20);

        camponome.putClientProperty(FlatClientProperties.STYLE,
                "arc:15;focusWidth:2");

        campoLogin.putClientProperty(FlatClientProperties.STYLE,
                "arc:15;focusWidth:2");

        campoSenha.putClientProperty(FlatClientProperties.STYLE,
                "arc:15;focusWidth:2");


        JButton botaoCadastrar = new JButton("Cadastrar");



        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        painel.setBorder(BorderFactory.createEmptyBorder(30,60,30,60));
        painel.setBackground(FUNDO);
        painel.setBackground(FUNDO);

        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Cadastro De Usuário");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        painel.add(titulo, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        painel.add(labelnome, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        painel.add(camponome, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        painel.add(labelLogin, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        painel.add(campoLogin, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        painel.add(labelSenha, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        painel.add(campoSenha, gbc);

        GridBagConstraints gbcBotao = new GridBagConstraints();

        gbcBotao.gridx = 0;
        gbcBotao.gridy = 4;
        gbcBotao.gridwidth = 2;
        gbcBotao.anchor = GridBagConstraints.CENTER;
        gbcBotao.fill = GridBagConstraints.NONE;
        gbcBotao.insets = new Insets(15, 8, 8, 8);

        painel.add(botaoCadastrar, gbcBotao);

        add(painel);

        botaoCadastrar.addActionListener(e -> cadastrarUsuario());



    }

    private void cadastrarUsuario() {
        String nome = camponome.getText();
        String login = campoLogin.getText();
        String senha = new String(campoSenha.getPassword());

        if (nome.trim().isEmpty() || login.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nome e login não podem ficar em branco!",
                    "Campos obrigatórios",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (senha.length() <= 8) {
            JOptionPane.showMessageDialog(this, "Sua senha tem que ser maior que 8 caracteres!", "Senha invalida!" ,JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuario = new Usuario();

        usuario.setNome(nome);
        usuario.setLogin(login);
        usuario.setSenha(senha);

        UsuarioDao usuarioDao = new UsuarioDao();

        if (usuarioDao.salvar(usuario)) {
            JOptionPane.showMessageDialog(null,"Usuário cadastrado com sucesso!!");

        }


    }
}
