package Interface;

import javax.swing.*;
import java.awt.*;

import Escola.Usuario;
import java.util.Objects;

import com.formdev.flatlaf.FlatClientProperties;

public class Alunos extends JFrame {

    private final Color AZUL_ESCURO = new Color(20, 43, 74);
    private final Color AZUL = new Color(37, 99, 235);
    private final Color FUNDO = Configuracoes.carregarCorFundo();
    private final Color TEXTO = new Color(23, 43, 77);
    private final Color CINZA = new Color(100, 116, 139);
    private final Color BRANCO = Color.WHITE;
    private final Usuario usuario;

    private JPanel painelPrincipal;
    private JPanel areaPrincipal;
    private JPanel conteudoPrincipal;


    private JLabel labelUsuario;

    public Alunos(Usuario usuario) {
        this.usuario = Objects.requireNonNull(usuario);


        setTitle("Controle de Faltas | Painel Principal");
        setSize(1050, 700);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(FUNDO);

        JPanel menuLateral = criarMenuLateral();

        areaPrincipal = new JPanel(new BorderLayout());
        areaPrincipal.setBackground(FUNDO);

        JPanel cabecalho = criarCabecalho();
        conteudoPrincipal = criarConteudo();

        areaPrincipal.add(cabecalho, BorderLayout.NORTH);
        areaPrincipal.add(conteudoPrincipal, BorderLayout.CENTER);

        painelPrincipal.add(menuLateral, BorderLayout.WEST);
        painelPrincipal.add(areaPrincipal, BorderLayout.CENTER);

        setContentPane(painelPrincipal);

        setVisible(true);
    }

    private JPanel criarMenuLateral() {

        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(AZUL_ESCURO);
        menu.setPreferredSize(new Dimension(235, 700));
        menu.setBorder(
                BorderFactory.createEmptyBorder(30, 18, 25, 18)
        );

        ImageIcon iconeOriginal = new ImageIcon(
                getClass().getResource("/imagens/chapeu-de-formatura.png")
        );

        Image imagemRedimensionada = iconeOriginal.getImage()
                .getScaledInstance(60, 60, Image.SCALE_SMOOTH);

        JLabel simbolo = new JLabel(
                new ImageIcon(imagemRedimensionada)
        );
        simbolo.setFont(new Font("Segoe UI", Font.BOLD, 35));
        simbolo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nomeSistema = new JLabel(
                "<html>Controle<br>de Faltas</html>"
        );
        nomeSistema.setFont(
                new Font("Segoe UI", Font.BOLD, 23)
        );
        nomeSistema.setForeground(BRANCO);
        nomeSistema.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("GESTÃO ESCOLAR");
        subtitulo.setFont(
                new Font("Segoe UI", Font.BOLD, 10)
        );
        subtitulo.setForeground(new Color(148, 180, 210));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        menu.add(simbolo);
        menu.add(Box.createVerticalStrut(12));
        menu.add(nomeSistema);
        menu.add(Box.createVerticalStrut(8));
        menu.add(subtitulo);
        menu.add(Box.createVerticalStrut(45));

        JLabel labelMenu = new JLabel("MENU PRINCIPAL");
        labelMenu.setFont(
                new Font("Segoe UI", Font.BOLD, 11)
        );
        labelMenu.setForeground(new Color(148, 180, 210));
        labelMenu.setAlignmentX(Component.LEFT_ALIGNMENT);

        menu.add(labelMenu);
        menu.add(Box.createVerticalStrut(15));

        JButton botaoInicio = criarBotaoMenu("⌂   Início", true);
        JButton botaoAlunos = criarBotaoMenu("♙   Alunos", false);
        JButton botaoFrequencia = criarBotaoMenu("✓   Frequência", false);

        menu.add(botaoInicio);
        menu.add(Box.createVerticalStrut(10));
        menu.add(botaoAlunos);
        menu.add(Box.createVerticalStrut(10));
        menu.add(botaoFrequencia);

        menu.add(Box.createVerticalGlue());

        JLabel rodape = new JLabel(
                "<html>CONTROLE ESCOLAR<br>Organização e eficiência</html>"
        );
        rodape.setFont(
                new Font("Segoe UI", Font.PLAIN, 11)
        );
        rodape.setForeground(new Color(180, 197, 216));
        rodape.setAlignmentX(Component.LEFT_ALIGNMENT);

        menu.add(rodape);

        // AÇÕES DO MENU
        botaoAlunos.addActionListener(e -> abrirConsulta());

        botaoFrequencia.addActionListener(e -> abrirFrequencia());

        return menu;
    }

    private JButton criarBotaoMenu(String texto, boolean selecionado) {

        JButton botao = new JButton(texto);

        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setMaximumSize(new Dimension(210, 45));
        botao.setPreferredSize(new Dimension(210, 45));
        botao.setAlignmentX(Component.LEFT_ALIGNMENT);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (selecionado) {
            botao.setBackground(new Color(37, 99, 235));
            botao.setForeground(BRANCO);
        } else {
            botao.setBackground(AZUL_ESCURO);
            botao.setForeground(new Color(220, 231, 243));
        }

        botao.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:12;focusWidth:0;borderWidth:0"
        );

        return botao;
    }


    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(BRANCO);
        cabecalho.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 0,
                                new Color(226, 232, 240)
                        ),
                        BorderFactory.createEmptyBorder(22, 30, 22, 30)
                )
        );

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Painel de gerenciamento");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 23));
        titulo.setForeground(TEXTO);

        JLabel descricao = new JLabel(
                "Gerencie alunos e acompanhe a frequência escolar."
        );
        descricao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descricao.setForeground(CINZA);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(6));
        textos.add(descricao);

        // Nome do usuário conectado
        labelUsuario = new JLabel("Olá, " + usuario.getNome());
        labelUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelUsuario.setForeground(TEXTO);

        // Botão de configurações
        JButton botaoConfiguracoes = new JButton("⚙ Configurações");
        botaoConfiguracoes.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botaoConfiguracoes.setForeground(AZUL);
        botaoConfiguracoes.setBackground(new Color(239, 246, 255));
        botaoConfiguracoes.setFocusPainted(false);
        botaoConfiguracoes.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botaoConfiguracoes.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:10;focusWidth:0;borderWidth:0"
        );

        // Evento: abre a janela de configurações
        botaoConfiguracoes.addActionListener(
                e -> abrirConfiguracoes()
        );

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

    private JPanel criarConteudo() {

        JPanel conteudo = new JPanel(new BorderLayout(0, 22));
        conteudo.setBackground(FUNDO);
        conteudo.setBorder(
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        JLabel tituloSecao = new JLabel("Acesso rápido");
        tituloSecao.setFont(
                new Font("Segoe UI", Font.BOLD, 19)
        );
        tituloSecao.setForeground(TEXTO);

        JLabel descricaoSecao = new JLabel(
                "Selecione uma opção para continuar."
        );
        descricaoSecao.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );
        descricaoSecao.setForeground(CINZA);

        JPanel textos = new JPanel();
        textos.setLayout(
                new BoxLayout(textos, BoxLayout.Y_AXIS)
        );
        textos.setOpaque(false);
        textos.add(tituloSecao);
        textos.add(Box.createVerticalStrut(6));
        textos.add(descricaoSecao);

        // CARTÕES DAS FUNCIONALIDADES
        JPanel cartoes = new JPanel(new GridLayout(2, 2, 18, 18));
        cartoes.setOpaque(false);

        cartoes.add(criarCartao(
                "01",
                "Cadastrar aluno",
                "Adicione novos alunos ao sistema escolar.",
                "Abrir cadastro",
                () -> abrirCadastro()
        ));

        cartoes.add(criarCartao(
                "02",
                "Consultar alunos",
                "Pesquise e consulte os alunos cadastrados.",
                "Ver alunos",
                () -> abrirConsulta()
        ));

        cartoes.add(criarCartao(
                "03",
                "Frequência escolar",
                "Acesse o controle de presença e faltas.",
                "Gerenciar frequência",
                () -> abrirFrequencia()
        ));

        cartoes.add(criarCartao(
                "04",
                "Organização escolar",
                "Área preparada para futuras funcionalidades.",
                "Em desenvolvimento",
                () -> JOptionPane.showMessageDialog(
                        this,
                        "Esta funcionalidade será implementada futuramente.",
                        "Em desenvolvimento",
                        JOptionPane.INFORMATION_MESSAGE
                )
        ));

        conteudo.add(textos, BorderLayout.NORTH);
        conteudo.add(cartoes, BorderLayout.CENTER);

        return conteudo;
    }

    private JPanel criarCartao(
            String numero,
            String titulo,
            String descricao,
            String textoBotao,
            Runnable acao
    ) {

        JPanel cartao = new JPanel(new BorderLayout(0, 12));
        cartao.setBackground(BRANCO);
        cartao.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(226, 232, 240)
                        ),
                        BorderFactory.createEmptyBorder(
                                22, 20, 20, 20
                        )
                )
        );

        JLabel identificador = new JLabel(numero);
        identificador.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );
        identificador.setForeground(AZUL);
        identificador.setOpaque(true);
        identificador.setBackground(new Color(239, 246, 255));
        identificador.setBorder(
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        );

        identificador.setHorizontalAlignment(SwingConstants.CENTER);
        identificador.setPreferredSize(new Dimension(42, 34));

        JLabel tituloCartao = new JLabel(titulo);
        tituloCartao.setFont(
                new Font("Segoe UI", Font.BOLD, 17)
        );
        tituloCartao.setForeground(TEXTO);

        JLabel descricaoCartao = new JLabel(
                "<html><div style='width:210px'>"
                        + descricao
                        + "</div></html>"
        );
        descricaoCartao.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );
        descricaoCartao.setForeground(CINZA);

        JPanel parteSuperior = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 0, 0)
        );
        parteSuperior.setOpaque(false);
        parteSuperior.add(identificador);

        JPanel informacoes = new JPanel();
        informacoes.setLayout(
                new BoxLayout(informacoes, BoxLayout.Y_AXIS)
        );
        informacoes.setOpaque(false);
        informacoes.add(tituloCartao);
        informacoes.add(Box.createVerticalStrut(9));
        informacoes.add(descricaoCartao);

        JButton botao = new JButton(textoBotao);
        botao.setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );
        botao.setForeground(AZUL);
        botao.setBackground(new Color(239, 246, 255));
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:10;focusWidth:0;borderWidth:0"
        );
        botao.addActionListener(e -> acao.run());

        JPanel rodape = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 0, 0)
        );
        rodape.setOpaque(false);
        rodape.add(botao);

        cartao.add(parteSuperior, BorderLayout.NORTH);
        cartao.add(informacoes, BorderLayout.CENTER);
        cartao.add(rodape, BorderLayout.SOUTH);

        return cartao;
    }

    // MÉTODOS PARA ABRIR AS TELAS EXISTENTES

    private void abrirCadastro() {
        CadastroAluno cadastro = new CadastroAluno();
        cadastro.setVisible(true);
    }

    private void abrirConsulta() {
        ConsultarAlunos consulta = new ConsultarAlunos();
        consulta.setVisible(true);
    }

    private void abrirFrequencia() {
        FrequenciaTela frequencia = new FrequenciaTela();
        frequencia.setVisible(true);
    }

    private void abrirConfiguracoes() {
       Configuracoes configuracoes = new Configuracoes(
                this,
                usuario,
                this::aplicarCorFundo,
                this::atualizarNomeUsuario


        );

        configuracoes.setVisible(true);
    }

    private void atualizarNomeUsuario() {
        labelUsuario.setText("Olá, " + usuario.getNome());
    }

    private void aplicarCorFundo(Color cor) {
        painelPrincipal.setBackground(cor);
        areaPrincipal.setBackground(cor);
        conteudoPrincipal.setBackground(cor);

        painelPrincipal.revalidate();
        painelPrincipal.repaint();
    }

}

