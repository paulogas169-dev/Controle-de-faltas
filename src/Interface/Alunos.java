package Interface;

import javax.swing.*;
import java.awt.*;

public class Alunos extends JFrame {

    private final Color AZUL_ESCURO = new Color(255, 255, 255, 255);
    private final Color AZUL = new Color(37,99,235);
    private static final Color FUNDO = new Color(18, 18, 27, 255);

    public Alunos() {

        setTitle("Controle De Alunos");
        setSize(600,400);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel titulo = new JLabel("GERENCIAMENTO DE ALUNOS");

        titulo.setFont(new Font("Arial", Font.BOLD,24));
        titulo.setForeground(AZUL_ESCURO);
        titulo.setBorder(BorderFactory.createEmptyBorder(25,10,25,10));

        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        ImageIcon imagemPessoa = new ImageIcon(getClass().getResource("/imagens/pessoa.png"));

        Image imagemRedimensionada = imagemPessoa.getImage().getScaledInstance(
                35, 35, Image.SCALE_SMOOTH
        );

        ImageIcon iconePessoa = new ImageIcon(imagemRedimensionada);

        JButton botaoCadastrar = new JButton("Cadastrar Aluno", iconePessoa);
        botaoCadastrar.setHorizontalTextPosition(SwingConstants.RIGHT);
        botaoCadastrar.setIconTextGap(10);

        ImageIcon imagemLupa = new ImageIcon(getClass().getResource("/imagens/lupa.png"));

        Image imagemRedimensionada1 = imagemLupa.getImage().getScaledInstance(
                35, 35, Image.SCALE_SMOOTH
        );

        ImageIcon iconePessoa1 = new ImageIcon(imagemRedimensionada1);

        JButton botaoConsultar = new JButton("Consultar Alunos", iconePessoa1);
        botaoConsultar.setHorizontalTextPosition(SwingConstants.RIGHT);
        botaoConsultar.setIconTextGap(10);

        ImageIcon imagemata = new ImageIcon(getClass().getResource("/imagens/ata.png"));

        Image imagemRedimensionada2 = imagemata.getImage().getScaledInstance(
                50, 50, Image.SCALE_SMOOTH
        );

        ImageIcon iconePessoa2 = new ImageIcon(imagemRedimensionada2);

        JButton botaoFrequencia = new JButton("Frequência alunos", iconePessoa2);
        botaoFrequencia.setHorizontalTextPosition(SwingConstants.RIGHT);
        botaoFrequencia.setIconTextGap(10);

        botaoCadastrar.setBackground(AZUL);
        botaoConsultar.setBackground(AZUL);
        botaoFrequencia.setBackground(AZUL);

        botaoCadastrar.setForeground(Color.WHITE);
        botaoConsultar.setForeground(Color.WHITE);
        botaoFrequencia.setForeground(Color.WHITE);

        botaoCadastrar.setFocusPainted(false);
        botaoConsultar.setFocusPainted(false);
        botaoFrequencia.setFocusPainted(false);

        botaoCadastrar.setPreferredSize(new Dimension(250, 50));
        botaoConsultar.setPreferredSize(new Dimension(250, 50));
        botaoFrequencia.setPreferredSize(new Dimension(250, 50));

        botaoCadastrar.setFont(new Font("Arial", Font.BOLD, 16));
        botaoConsultar.setFont(new Font("Arial", Font.BOLD, 16));
        botaoFrequencia.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(FUNDO);

        painel.add(titulo, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new GridLayout(3, 1, 0, 15));
        painelBotoes.setBackground(FUNDO);

        painelBotoes.setBorder(
                BorderFactory.createEmptyBorder(20, 60, 30, 60)
        );

        painelBotoes.add(botaoCadastrar);
        painelBotoes.add(botaoConsultar);
        painelBotoes.add(botaoFrequencia);

        painel.add(painelBotoes, BorderLayout.CENTER);

        add(painel);
        setVisible(true);

        botaoCadastrar.addActionListener(e -> {
           CadastroAluno cadastro = new CadastroAluno();
           cadastro.setVisible(true);
        });

        botaoConsultar.addActionListener(e -> {
            ConsultarAlunos consulta = new ConsultarAlunos();
            consulta.setVisible(true);
        });
        botaoFrequencia.addActionListener(e -> {
            FrequenciaTela frequencia = new FrequenciaTela();
            frequencia.setVisible(true);
        });
    }
}
