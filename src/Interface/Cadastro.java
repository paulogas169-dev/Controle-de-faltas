
package Interface;

import DAO.UsuarioDao;
import Escola.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

import com.formdev.flatlaf.FlatClientProperties;

public class Cadastro extends JFrame {

    private JTextField camponome;
    private JTextField campoLogin;
    private JPasswordField campoSenha;
    private JTextField campoRespostaCaptcha;

    private JLabel perguntaCaptcha;
    private int respostaCorreta;

    private final Random random = new Random();

    private final Color FUNDO = Configuracoes.getCorFundo();
    private final Color TEXTO = Configuracoes.getCorTexto();
    private final Color AZUL = new Color(37, 99, 235);
    private final Color CINZA = Configuracoes.getCorSubtexto();

    public Cadastro() {

        setTitle("Cadastro de Usuário");
        setSize(540, 460);
        setMinimumSize(new Dimension(500, 440));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(FUNDO);
        painel.setBorder(
                BorderFactory.createEmptyBorder(25, 35, 25, 35)
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // TÍTULO
        JLabel titulo = new JLabel(
                "Criar uma conta",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(TEXTO);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        painel.add(titulo, gbc);

        // NOME
        JLabel labelNome = criarLabel("Nome completo:");

        camponome = criarCampo("Digite seu nome");

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        painel.add(labelNome, gbc);

        gbc.gridx = 1;
        painel.add(camponome, gbc);

        // LOGIN
        JLabel labelLogin = criarLabel("Login:");

        campoLogin = criarCampo("Escolha seu login");

        gbc.gridx = 0;
        gbc.gridy = 2;
        painel.add(labelLogin, gbc);

        gbc.gridx = 1;
        painel.add(campoLogin, gbc);

        // SENHA
        JLabel labelSenha = criarLabel("Senha:");

        campoSenha = new JPasswordField();
        estilizarCampo(campoSenha);
        campoSenha.putClientProperty(
                "JTextField.placeholderText",
                "Mínimo de 9 caracteres"
        );

        gbc.gridx = 0;
        gbc.gridy = 3;
        painel.add(labelSenha, gbc);

        gbc.gridx = 1;
        painel.add(campoSenha, gbc);

        // CAPTCHA
        JLabel labelCaptcha = criarLabel(
                "Verificação de segurança:"
        );

        perguntaCaptcha = new JLabel();
        perguntaCaptcha.setFont(
                new Font("Segoe UI", Font.BOLD, 15)
        );
        perguntaCaptcha.setForeground(AZUL);

        campoRespostaCaptcha = criarCampo("Sua resposta");

        JPanel painelCaptcha = new JPanel();
        painelCaptcha.setLayout(
                new BoxLayout(painelCaptcha, BoxLayout.Y_AXIS)
        );
        painelCaptcha.setOpaque(false);

        perguntaCaptcha.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoRespostaCaptcha.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelCaptcha.add(perguntaCaptcha);
        painelCaptcha.add(Box.createVerticalStrut(10));
        painelCaptcha.add(campoRespostaCaptcha);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        painel.add(labelCaptcha, gbc);

        gbc.gridy = 5;
        painel.add(painelCaptcha, gbc);

        // BOTÃO CADASTRAR
        JButton botaoCadastrar = new JButton("Criar conta");

        botaoCadastrar.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:12;focusWidth:0;borderWidth:0;" +
                        "hoverBackground:#1D4ED8;" +
                        "pressedBackground:#1E40AF"
        );

        botaoCadastrar.setBackground(AZUL);
        botaoCadastrar.setForeground(Color.WHITE);
        botaoCadastrar.setFont(
                new Font("Segoe UI", Font.BOLD, 15)
        );
        botaoCadastrar.setFocusPainted(false);
        botaoCadastrar.setPreferredSize(
                new Dimension(200, 44)
        );

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(18, 8, 5, 8);

        painel.add(botaoCadastrar, gbc);

        add(painel);

        // GERA A PRIMEIRA CONTA
        gerarCaptcha();

        // AÇÃO DO BOTÃO
        botaoCadastrar.addActionListener(
                e -> cadastrarUsuario()
        );

        getRootPane().setDefaultButton(botaoCadastrar);
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXTO);
        return label;
    }

    private JTextField criarCampo(String placeholder) {
        JTextField campo = new JTextField();

        campo.putClientProperty(
                "JTextField.placeholderText",
                placeholder
        );

        estilizarCampo(campo);

        return campo;
    }

    private void estilizarCampo(JTextField campo) {
        campo.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:12;focusWidth:1"
        );

        campo.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );

        campo.setPreferredSize(
                new Dimension(230, 40)
        );
    }

    private void gerarCaptcha() {
        int numero1 = random.nextInt(8) + 2;
        int numero2 = random.nextInt(8) + 2;

        respostaCorreta = numero1 + numero2;

        perguntaCaptcha.setText(
                "Quanto é " + numero1 + " + " + numero2 + "?"
        );

        campoRespostaCaptcha.setText("");
    }

    private void cadastrarUsuario() {

        String nome = camponome.getText().trim();
        String login = campoLogin.getText().trim();
        String senha = new String(
                campoSenha.getPassword()
        );

        String resposta = campoRespostaCaptcha
                .getText()
                .trim();

        // VALIDAÇÃO DOS CAMPOS
        if (nome.isEmpty()
                || login.isEmpty()
                || senha.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Preencha todos os campos!",
                    "Campos obrigatórios",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // VALIDAÇÃO DA SENHA
        if (senha.length() <= 8) {

            JOptionPane.showMessageDialog(
                    this,
                    "Sua senha precisa ter pelo menos 9 caracteres!",
                    "Senha inválida",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // VALIDAÇÃO DO CAPTCHA
        int respostaUsuario;

        try {
            respostaUsuario = Integer.parseInt(resposta);
        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Digite a resposta numérica do CAPTCHA!",
                    "Verificação necessária",
                    JOptionPane.WARNING_MESSAGE
            );

            gerarCaptcha();
            return;
        }

        if (respostaUsuario != respostaCorreta) {

            JOptionPane.showMessageDialog(
                    this,
                    "Resposta incorreta! Tente novamente.",
                    "Verificação falhou",
                    JOptionPane.WARNING_MESSAGE
            );

            gerarCaptcha();
            return;
        }

        // MANTÉM O CADASTRO NO BANCO DE DADOS
        Usuario usuario = new Usuario();

        usuario.setNome(nome);
        usuario.setLogin(login);
        usuario.setSenha(senha);

        UsuarioDao usuarioDao = new UsuarioDao();

        if (usuarioDao.salvar(usuario)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Usuário cadastrado com sucesso!"
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível cadastrar o usuário.",
                    "Erro no cadastro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}