package Interface;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Tela de Cadastro de Usuário.
 * O usuário preenche seus dados (nome, e-mail do Gmail, login, senha e CAPTCHA)
 * e, ao clicar em "Criar conta", abre a tela de "Envio de token para o gmail"
 * que dispara o e-mail real e solicita a confirmação do token de 6 dígitos.
 */
public class Cadastro extends JFrame {

    private JTextField camponome;
    private JTextField campoEmail;
    private JTextField campoLogin;
    private JPasswordField campoSenha;
    private JTextField campoRespostaCaptcha;
    private JLabel labelImagemCaptcha;

    private static final String CARACTERES_CAPTCHA = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private String codigoCaptchaCorreto;

    private final Random random = new Random();

    private final Color FUNDO = Configuracoes.getCorFundo();
    private final Color TEXTO = Configuracoes.getCorTexto();
    private final Color AZUL = new Color(37, 99, 235);

    public Cadastro() {

        setTitle("Cadastro de Usuário");
        setSize(520, 580);
        setMinimumSize(new Dimension(480, 540));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(FUNDO);
        painel.setBorder(
                BorderFactory.createEmptyBorder(24, 35, 24, 35)
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

        // NOME COMPLETO
        JLabel labelNome = criarLabel("Nome completo:");
        camponome = criarCampo("Digite seu nome completo");

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        painel.add(labelNome, gbc);

        gbc.gridx = 1;
        painel.add(camponome, gbc);

        // E-MAIL (GMAIL)
        JLabel labelEmail = criarLabel("E-mail (Gmail):");
        campoEmail = criarCampo("seuemail@gmail.com");

        gbc.gridx = 0;
        gbc.gridy = 2;
        painel.add(labelEmail, gbc);

        gbc.gridx = 1;
        painel.add(campoEmail, gbc);

        // LOGIN
        JLabel labelLogin = criarLabel("Login:");
        campoLogin = criarCampo("Escolha seu login");

        gbc.gridx = 0;
        gbc.gridy = 3;
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
        gbc.gridy = 4;
        painel.add(labelSenha, gbc);

        gbc.gridx = 1;
        painel.add(campoSenha, gbc);

        // CAPTCHA VISUAL
        JLabel labelCaptcha = criarLabel("Segurança (CAPTCHA):");

        labelImagemCaptcha = new JLabel();
        labelImagemCaptcha.setPreferredSize(new Dimension(140, 40));
        labelImagemCaptcha.setToolTipText("Código de segurança visual");

        JButton btnRecarregarCaptcha = new JButton("🔄");
        btnRecarregarCaptcha.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btnRecarregarCaptcha.setPreferredSize(new Dimension(40, 40));
        btnRecarregarCaptcha.setFocusPainted(false);
        btnRecarregarCaptcha.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRecarregarCaptcha.setToolTipText("Gerar outro código CAPTCHA");
        btnRecarregarCaptcha.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:0;borderWidth:1");
        btnRecarregarCaptcha.addActionListener(e -> gerarCaptcha());

        JPanel painelImagemEBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelImagemEBotoes.setOpaque(false);
        painelImagemEBotoes.add(labelImagemCaptcha);
        painelImagemEBotoes.add(btnRecarregarCaptcha);

        campoRespostaCaptcha = criarCampo("Digite os 5 caracteres");
        campoRespostaCaptcha.setPreferredSize(new Dimension(230, 40));

        JPanel painelCaptcha = new JPanel();
        painelCaptcha.setLayout(new BoxLayout(painelCaptcha, BoxLayout.Y_AXIS));
        painelCaptcha.setOpaque(false);
        painelCaptcha.add(painelImagemEBotoes);
        painelCaptcha.add(Box.createVerticalStrut(6));
        painelCaptcha.add(campoRespostaCaptcha);

        gbc.gridx = 0;
        gbc.gridy = 5;
        painel.add(labelCaptcha, gbc);

        gbc.gridx = 1;
        painel.add(painelCaptcha, gbc);

        // BOTÃO CADASTRAR / CRIAR CONTA
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
                new Dimension(220, 44)
        );
        botaoCadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(18, 8, 5, 8);

        painel.add(botaoCadastrar, gbc);

        add(painel);

        // Gera o primeiro captcha ao abrir
        gerarCaptcha();

        // Ação do botão: valida o formulário e abre "Envio de token para o gmail"
        botaoCadastrar.addActionListener(e -> validarECadastrar());

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
        campo.putClientProperty("JTextField.placeholderText", placeholder);
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
        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            codigo.append(CARACTERES_CAPTCHA.charAt(random.nextInt(CARACTERES_CAPTCHA.length())));
        }
        codigoCaptchaCorreto = codigo.toString();

        int largura = 140;
        int altura = 40;
        BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagem.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean dark = Configuracoes.isModoEscuro();
        g.setColor(dark ? new Color(28, 28, 35) : new Color(241, 245, 249));
        g.fillRect(0, 0, largura, altura);

        // Linhas de ruído
        for (int i = 0; i < 6; i++) {
            g.setColor(new Color(
                    random.nextInt(120) + (dark ? 50 : 100),
                    random.nextInt(120) + (dark ? 50 : 100),
                    random.nextInt(120) + (dark ? 50 : 100)
            ));
            int x1 = random.nextInt(largura);
            int y1 = random.nextInt(altura);
            int x2 = random.nextInt(largura);
            int y2 = random.nextInt(altura);
            g.drawLine(x1, y1, x2, y2);
        }

        Font[] fontes = new Font[]{
                new Font("Segoe UI", Font.BOLD, 21),
                new Font("Arial", Font.BOLD, 22),
                new Font("Verdana", Font.BOLD, 20)
        };

        // Letras com rotação aleatória
        for (int i = 0; i < codigoCaptchaCorreto.length(); i++) {
            char c = codigoCaptchaCorreto.charAt(i);
            g.setFont(fontes[random.nextInt(fontes.length)]);
            g.setColor(dark
                    ? new Color(200 + random.nextInt(55), 200 + random.nextInt(55), 255)
                    : new Color(15 + random.nextInt(70), 30 + random.nextInt(70), 130 + random.nextInt(90)));

            double angulo = (random.nextDouble() - 0.5) * 0.4;
            int x = 14 + (i * 24);
            int y = 28;

            Graphics2D gChar = (Graphics2D) g.create();
            gChar.rotate(angulo, x, y);
            gChar.drawString(String.valueOf(c), x, y);
            gChar.dispose();
        }

        g.setColor(dark ? new Color(55, 65, 81) : new Color(203, 213, 225));
        g.drawRect(0, 0, largura - 1, altura - 1);
        g.dispose();

        labelImagemCaptcha.setIcon(new ImageIcon(imagem));
        campoRespostaCaptcha.setText("");
    }

    private void validarECadastrar() {
        String nome = camponome.getText().trim();
        String email = campoEmail.getText().trim();
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword());
        String respostaCaptcha = campoRespostaCaptcha.getText().trim();

        // 1. CAMPOS OBRIGATÓRIOS
        if (nome.isEmpty() || email.isEmpty() || login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Preencha todos os campos obrigatórios!",
                    "Campos obrigatórios",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 2. VALIDAÇÃO DO E-MAIL
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe um endereço de e-mail válido (ex: seuemail@gmail.com)!",
                    "E-mail inválido",
                    JOptionPane.WARNING_MESSAGE
            );
            campoEmail.requestFocus();
            return;
        }

        // 3. VALIDAÇÃO DA SENHA
        if (senha.length() <= 8) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sua senha precisa ter pelo menos 9 caracteres!",
                    "Senha inválida",
                    JOptionPane.WARNING_MESSAGE
            );
            campoSenha.requestFocus();
            return;
        }

        // 4. VALIDAÇÃO DO CAPTCHA VISUAL
        if (respostaCaptcha.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Digite o código de verificação CAPTCHA!",
                    "Verificação necessária",
                    JOptionPane.WARNING_MESSAGE
            );
            campoRespostaCaptcha.requestFocus();
            return;
        }

        if (!respostaCaptcha.equalsIgnoreCase(codigoCaptchaCorreto)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Código CAPTCHA incorreto! Um novo código foi gerado.",
                    "Verificação falhou",
                    JOptionPane.WARNING_MESSAGE
            );
            gerarCaptcha();
            campoRespostaCaptcha.requestFocus();
            return;
        }

        // 5. ABRE A TELA "Envio de token para o gmail"
        // Dispara o envio real para o Gmail e solicita que o usuário insira o token
        DialogoTokenGmail dialogo = new DialogoTokenGmail(
                this,
                nome,
                email,
                login,
                senha
        );
        dialogo.setVisible(true);
    }
}
