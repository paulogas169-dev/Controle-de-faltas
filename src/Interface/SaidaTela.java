package Interface;

import DAO.AlunoDao;
import DAO.FrequenciaDao;
import DAO.SalaDao;
import Escola.Aluno;
import Escola.Frequencia;
import Escola.Sala;
import Servico.FotoServico;
import testeserial.LeitorRfid;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class SaidaTela extends JFrame {

    private JComboBox<Sala> comboSala;
    private JButton btnSalvarManual;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JLabel labelQuantidade;
    private JLabel labelData;

    private CardLayout layoutConteudo;
    private JPanel painelCartoes;

    private JLabel labelFoto;
    private JLabel labelNomeAluno;
    private JLabel labelSalaAluno;
    private JLabel labelHoraSaida;
    private JLabel labelStatusRFID;

    private JButton btnManual;
    private JButton btnVoltarLeitor;

    private LeitorRfid leitorRfid;

    private volatile boolean telaFechada = false;

    private final Color VERDE = new Color(16, 185, 129);
    private final Color VERDE_ESCURO = new Color(5, 150, 105);
    private final Color AZUL_ESCURO = new Color(15, 31, 58);
    private final Color TEXTO = Configuracoes.getCorTexto();
    private final Color CINZA = Configuracoes.getCorSubtexto();
    private final Color FUNDO = Configuracoes.getCorFundo();
    private final Color BRANCO = Configuracoes.getCorPainel();
    private final Color BORDA = Configuracoes.getCorBorda();

    public SaidaTela() {
        setTitle("Controle de Faltas | Registro de Ponto de Saída");
        setSize(1220, 800);
        setMinimumSize(new Dimension(980, 680));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        Configuracoes.aplicarIcone(this);

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(FUNDO);

        JPanel cabecalho = criarCabecalho();

        // Painel de saída manual
        JPanel conteudoManual = criarPainelManual();

        // Painel que alterna entre RFID e saída manual
        layoutConteudo = new CardLayout();
        painelCartoes = new JPanel(layoutConteudo);
        painelCartoes.setBackground(FUNDO);

        painelCartoes.add(criarPainelRFID(), "RFID");
        painelCartoes.add(conteudoManual, "MANUAL");

        principal.add(cabecalho, BorderLayout.NORTH);
        principal.add(painelCartoes, BorderLayout.CENTER);

        setContentPane(principal);

        carregarSalas();

        comboSala.addActionListener(e -> carregarAlunos());

        if (comboSala.getItemCount() > 0) {
            carregarAlunos();
        }

        // Encerra a comunicação serial ao fechar a janela
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                telaFechada = true;
                if (leitorRfid != null) {
                    leitorRfid.parar();
                }
            }
        });

        // Abre a tela já aguardando o cartão no modo RFID
        layoutConteudo.show(painelCartoes, "RFID");
        setVisible(true);

        iniciarLeitorRFID();
    }

    // CABEÇALHO
    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(AZUL_ESCURO);
        cabecalho.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDA),
                        BorderFactory.createEmptyBorder(23, 30, 23, 30)
                )
        );

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Ponto de saída escolar");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel(
                "Sistema inteligente de registro de saída • RFID e modo manual"
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(203, 213, 225));

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(7));
        textos.add(subtitulo);

        JPanel ladoEsquerdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        ladoEsquerdo.setOpaque(false);
        ImageIcon iconeHeader = FotoServico.getIconeApp(54, 54);
        if (iconeHeader != null) {
            JLabel lblIcone = new JLabel(iconeHeader);
            ladoEsquerdo.add(lblIcone);
        }
        ladoEsquerdo.add(textos);

        JLabel etiqueta = new JLabel("  REGISTRO DE SAÍDA  ");
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 11));
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setOpaque(true);
        etiqueta.setBackground(VERDE_ESCURO);
        etiqueta.setBorder(BorderFactory.createEmptyBorder(9, 8, 9, 8));
        etiqueta.putClientProperty(FlatClientProperties.STYLE, "arc:8");

        cabecalho.add(ladoEsquerdo, BorderLayout.CENTER);
        cabecalho.add(etiqueta, BorderLayout.EAST);

        return cabecalho;
    }

    // PAINEL RFID
    private JPanel criarPainelRFID() {
        JPanel pagina = new JPanel(new BorderLayout(0, 22));
        pagina.setBackground(new Color(241, 245, 249));
        pagina.setBorder(BorderFactory.createEmptyBorder(24, 30, 28, 30));

        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        JPanel textosTopo = new JPanel();
        textosTopo.setOpaque(false);
        textosTopo.setLayout(new BoxLayout(textosTopo, BoxLayout.Y_AXIS));

        JLabel tituloPagina = new JLabel("Identificação do estudante - Saída");
        tituloPagina.setFont(new Font("Segoe UI", Font.BOLD, 23));
        tituloPagina.setForeground(AZUL_ESCURO);

        JLabel descricaoPagina = new JLabel("Aproxime o cartão do leitor para registrar o horário de saída automaticamente.");
        descricaoPagina.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descricaoPagina.setForeground(new Color(100, 116, 139));

        textosTopo.add(tituloPagina);
        textosTopo.add(Box.createVerticalStrut(5));
        textosTopo.add(descricaoPagina);

        btnManual = new JButton("Registrar Saída Manualmente?");
        btnManual.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnManual.setForeground(Color.WHITE);
        btnManual.setBackground(VERDE_ESCURO);
        btnManual.setFocusPainted(false);
        btnManual.setBorder(BorderFactory.createEmptyBorder(13, 19, 13, 19));
        btnManual.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnManual.putClientProperty(FlatClientProperties.STYLE,
                "arc:12;focusWidth:0;borderWidth:0;hoverBackground:#047857;pressedBackground:#065F46");
        btnManual.addActionListener(e -> {
            carregarAlunos();
            layoutConteudo.show(painelCartoes, "MANUAL");
        });

        barra.add(textosTopo, BorderLayout.WEST);
        barra.add(btnManual, BorderLayout.EAST);

        JPanel cartao = new JPanel(new BorderLayout(28, 0));
        cartao.setBackground(Color.WHITE);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(28, 30, 28, 30)));

        // Coluna da fotografia
        JPanel colunaFoto = new JPanel(new BorderLayout(0, 14));
        colunaFoto.setOpaque(false);
        colunaFoto.setPreferredSize(new Dimension(340, 430));

        JLabel legendaFoto = new JLabel("FOTO DO ALUNO", SwingConstants.CENTER);
        legendaFoto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        legendaFoto.setForeground(new Color(100, 116, 139));

        labelFoto = new JLabel();
        labelFoto.setIcon(FotoServico.criarPlaceholderAguardando(320, 360));
        labelFoto.setHorizontalAlignment(SwingConstants.CENTER);
        labelFoto.setPreferredSize(new Dimension(320, 360));
        labelFoto.setMinimumSize(new Dimension(320, 360));
        labelFoto.setOpaque(true);
        labelFoto.setBackground(new Color(248, 250, 252));
        labelFoto.setForeground(new Color(100, 116, 139));
        labelFoto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelFoto.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        colunaFoto.add(legendaFoto, BorderLayout.NORTH);
        colunaFoto.add(labelFoto, BorderLayout.CENTER);

        // Coluna de dados e estado da leitura
        JPanel colunaDados = new JPanel();
        colunaDados.setOpaque(false);
        colunaDados.setLayout(new BoxLayout(colunaDados, BoxLayout.Y_AXIS));
        colunaDados.setBorder(BorderFactory.createEmptyBorder(18, 0, 10, 0));

        JLabel selo = new JLabel("  LEITURA RFID - SAÍDA  ");
        selo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        selo.setForeground(new Color(6, 95, 70));
        selo.setOpaque(true);
        selo.setBackground(new Color(209, 250, 229));
        selo.setBorder(BorderFactory.createEmptyBorder(8, 6, 8, 6));
        selo.setAlignmentX(Component.LEFT_ALIGNMENT);
        selo.putClientProperty(FlatClientProperties.STYLE, "arc:6");

        JLabel tituloRFID = new JLabel("Aproxime seu cartão de saída");
        tituloRFID.setFont(new Font("Segoe UI", Font.BOLD, 27));
        tituloRFID.setForeground(AZUL_ESCURO);
        tituloRFID.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel instrucao = new JLabel("O sistema identifica o estudante e registra o horário exato de saída.");
        instrucao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instrucao.setForeground(new Color(100, 116, 139));
        instrucao.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel legendaNome = criarLegendaRFID("NOME DO ESTUDANTE");
        labelNomeAluno = new JLabel("Aguardando identificação...");
        labelNomeAluno.setFont(new Font("Segoe UI", Font.BOLD, 23));
        labelNomeAluno.setForeground(AZUL_ESCURO);
        labelNomeAluno.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel legendaTurma = criarLegendaRFID("TURMA");
        labelSalaAluno = new JLabel("—");
        labelSalaAluno.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        labelSalaAluno.setForeground(new Color(71, 85, 105));
        labelSalaAluno.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel legendaHora = criarLegendaRFID("HORÁRIO DA SAÍDA");
        labelHoraSaida = new JLabel("—:—:—");
        labelHoraSaida.setFont(new Font("Segoe UI", Font.BOLD, 20));
        labelHoraSaida.setForeground(VERDE_ESCURO);
        labelHoraSaida.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel caixaStatus = new JPanel(new BorderLayout(12, 0));
        caixaStatus.setBackground(new Color(240, 253, 244));
        caixaStatus.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 247, 208)),
                BorderFactory.createEmptyBorder(15, 16, 15, 16)));
        JLabel ponto = new JLabel("●");
        ponto.setFont(new Font("Segoe UI", Font.BOLD, 18));
        ponto.setForeground(VERDE_ESCURO);
        labelStatusRFID = new JLabel("Conectando ao leitor RFID...");
        labelStatusRFID.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelStatusRFID.setForeground(VERDE_ESCURO);
        caixaStatus.add(ponto, BorderLayout.WEST);
        caixaStatus.add(labelStatusRFID, BorderLayout.CENTER);
        caixaStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        caixaStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        colunaDados.add(selo);
        colunaDados.add(Box.createVerticalStrut(18));
        colunaDados.add(tituloRFID);
        colunaDados.add(Box.createVerticalStrut(6));
        colunaDados.add(instrucao);
        colunaDados.add(Box.createVerticalStrut(28));
        colunaDados.add(legendaNome);
        colunaDados.add(Box.createVerticalStrut(6));
        colunaDados.add(labelNomeAluno);
        colunaDados.add(Box.createVerticalStrut(18));
        colunaDados.add(legendaTurma);
        colunaDados.add(Box.createVerticalStrut(6));
        colunaDados.add(labelSalaAluno);
        colunaDados.add(Box.createVerticalStrut(18));
        colunaDados.add(legendaHora);
        colunaDados.add(Box.createVerticalStrut(6));
        colunaDados.add(labelHoraSaida);
        colunaDados.add(Box.createVerticalStrut(24));
        colunaDados.add(caixaStatus);
        colunaDados.add(Box.createVerticalGlue());

        cartao.add(colunaFoto, BorderLayout.WEST);
        cartao.add(colunaDados, BorderLayout.CENTER);

        JLabel dica = new JLabel("Dica: os estudantes que não registrarem saída até o encerramento do dia receberão falta automaticamente.");
        dica.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dica.setForeground(new Color(100, 116, 139));

        pagina.add(barra, BorderLayout.NORTH);
        pagina.add(cartao, BorderLayout.CENTER);
        pagina.add(dica, BorderLayout.SOUTH);
        return pagina;
    }

    private JLabel criarLegendaRFID(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(100, 116, 139));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // INICIA A LEITURA DO CARTÃO
    private void iniciarLeitorRFID() {
        leitorRfid = new LeitorRfid();
        labelStatusRFID.setText("Conectando ao leitor RFID...");
        labelStatusRFID.setForeground(VERDE_ESCURO);

        boolean conectado = leitorRfid.iniciar(
                this::processarCartao,
                mensagemErro -> SwingUtilities.invokeLater(() -> {
                    if (!telaFechada) {
                        labelStatusRFID.setText(mensagemErro);
                        labelStatusRFID.setForeground(Color.RED);
                    }
                })
        );

        if (conectado) {
            labelStatusRFID.setText("Leitor conectado. Aproxime o cartão de saída.");
            labelStatusRFID.setForeground(VERDE_ESCURO);
        }
    }

    // PROCESSA O CARTÃO LIDO PARA REGISTRO DE SAÍDA
    private void processarCartao(String uid) {
        if (telaFechada) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            if (!telaFechada) {
                labelStatusRFID.setText("Cartão lido! Identificando aluno...");
                labelStatusRFID.setForeground(new Color(37, 99, 235));
            }
        });

        AlunoDao alunoDao = new AlunoDao();
        Aluno aluno = alunoDao.buscarPorUid(uid);

        if (aluno == null) {
            SwingUtilities.invokeLater(() -> {
                if (!telaFechada) {
                    limparIdentificacao();
                    labelNomeAluno.setText("Aluno não encontrado");
                    labelSalaAluno.setText("Cartão não cadastrado (" + uid + ")");
                    labelHoraSaida.setText("—:—:—");
                    labelStatusRFID.setText("Verifique o cadastro do cartão no sistema.");
                    labelStatusRFID.setForeground(Color.RED);
                }
            });
            return;
        }

        String nomeSala = aluno.getSala();
        if (nomeSala == null || nomeSala.isBlank()) {
            nomeSala = "Sala ID: " + aluno.getSala_id();
        }
        final String salaExibicao = nomeSala;

        // Mostra o aluno e a foto IMEDIATAMENTE após a leitura do cartão
        SwingUtilities.invokeLater(() -> {
            if (!telaFechada) {
                mostrarAluno(aluno, salaExibicao, uid);
                labelStatusRFID.setText("Aluno identificado! Registrando saída...");
                labelStatusRFID.setForeground(VERDE_ESCURO);
            }
        });

        FrequenciaDao frequenciaDao = new FrequenciaDao();
        FrequenciaDao.ResultadoSaida resultado = frequenciaDao.registrarSaidaRFID(aluno.getId());

        SwingUtilities.invokeLater(() -> {
            if (telaFechada) return;

            if (resultado.isSucesso()) {
                labelHoraSaida.setText(resultado.getHoraFormatada());
                labelHoraSaida.setForeground(VERDE_ESCURO);
                labelStatusRFID.setText(resultado.getMensagem());
                labelStatusRFID.setForeground(new Color(22, 163, 74));
            } else if (resultado.isJaRegistrada()) {
                labelHoraSaida.setText(resultado.getHoraFormatada());
                labelHoraSaida.setForeground(new Color(217, 119, 6));
                labelStatusRFID.setText(resultado.getMensagem());
                labelStatusRFID.setForeground(new Color(217, 119, 6));
            } else {
                labelHoraSaida.setText("—:—:—");
                labelStatusRFID.setText(resultado.getMensagem());
                labelStatusRFID.setForeground(Color.RED);
            }
        });
    }

    private void mostrarAluno(Aluno aluno, String nomeSala, String uid) {
        String nome = aluno.getNome() == null ? "Aluno" : aluno.getNome();
        labelNomeAluno.setText(
                "<html><div style='text-align:center; width:420px;'>"
                        + escaparHtml(nome)
                        + "</div></html>"
        );
        labelSalaAluno.setText("Turma: " + escaparHtml(nomeSala));

        ImageIcon foto = FotoServico.obterFotoAluno(aluno, uid, 320, 360);
        if (foto != null) {
            labelFoto.setIcon(foto);
            labelFoto.setText("");
        } else {
            labelFoto.setIcon(FotoServico.criarAvatarPlaceholder(nome, 320, 360));
            labelFoto.setText("");
        }
    }

    private String escaparHtml(String texto) {
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private void limparIdentificacao() {
        labelFoto.setIcon(FotoServico.criarPlaceholderAguardando(320, 360));
        labelFoto.setText("");
        labelNomeAluno.setText("Aguardando identificação...");
        labelSalaAluno.setText("Turma: —");
        labelHoraSaida.setText("—:—:—");
    }

    // PAINEL DA SAÍDA MANUAL
    private JPanel criarPainelManual() {
        JPanel conteudo = new JPanel(new BorderLayout(0, 20));
        conteudo.setBackground(FUNDO);
        conteudo.setBorder(BorderFactory.createEmptyBorder(20, 30, 25, 30));

        JPanel painelControle = criarPainelControle();
        criarTabela();

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(BORDA));
        scroll.getViewport().setBackground(BRANCO);

        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBackground(BRANCO);
        painelTabela.setBorder(BorderFactory.createLineBorder(BORDA));
        painelTabela.add(scroll, BorderLayout.CENTER);

        JPanel rodape = criarRodape();

        conteudo.add(painelControle, BorderLayout.NORTH);
        conteudo.add(painelTabela, BorderLayout.CENTER);
        conteudo.add(rodape, BorderLayout.SOUTH);

        return conteudo;
    }

    private JPanel criarPainelControle() {
        JPanel painel = new JPanel(new BorderLayout(15, 0));
        painel.setBackground(BRANCO);
        painel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)
                )
        );

        JPanel selecao = new JPanel();
        selecao.setLayout(new BoxLayout(selecao, BoxLayout.Y_AXIS));
        selecao.setOpaque(false);

        JLabel titulo = new JLabel("Turma / Sala");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(TEXTO);

        comboSala = new JComboBox<>();
        comboSala.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboSala.setPreferredSize(new Dimension(240, 42));
        comboSala.setMaximumSize(new Dimension(300, 42));
        comboSala.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboSala.putClientProperty(FlatClientProperties.STYLE, "arc:12;focusWidth:1");

        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        selecao.add(titulo);
        selecao.add(Box.createVerticalStrut(9));
        selecao.add(comboSala);

        JPanel informacoes = new JPanel();
        informacoes.setLayout(new BoxLayout(informacoes, BoxLayout.Y_AXIS));
        informacoes.setOpaque(false);
        informacoes.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel legendaData = new JLabel("DATA DE HOJE");
        legendaData.setFont(new Font("Segoe UI", Font.BOLD, 10));
        legendaData.setForeground(CINZA);

        labelData = new JLabel();
        labelData.setFont(new Font("Segoe UI", Font.BOLD, 16));
        labelData.setForeground(VERDE_ESCURO);
        atualizarData();

        informacoes.add(legendaData);
        informacoes.add(Box.createVerticalStrut(7));
        informacoes.add(labelData);

        btnVoltarLeitor = new JButton("Voltar ao leitor RFID");
        btnVoltarLeitor.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVoltarLeitor.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarLeitor.addActionListener(e -> layoutConteudo.show(painelCartoes, "RFID"));

        painel.add(selecao, BorderLayout.WEST);
        painel.add(informacoes, BorderLayout.EAST);
        painel.add(btnVoltarLeitor, BorderLayout.SOUTH);

        return painel;
    }

    private void carregarSalas() {
        SalaDao salaDao = new SalaDao();
        List<Sala> salas = salaDao.listar();

        comboSala.removeAllItems();
        if (salas != null) {
            for (Sala sala : salas) {
                comboSala.addItem(sala);
            }
        }

        if (comboSala.getItemCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nenhuma turma foi encontrada. Verifique as salas cadastradas.",
                    "Turmas indisponíveis",
                    JOptionPane.WARNING_MESSAGE
            );
            if (btnSalvarManual != null) {
                btnSalvarManual.setEnabled(false);
            }
        }
    }

    private void criarTabela() {
        modelo = new DefaultTableModel(
                new Object[]{"ID", "Nome do aluno", "Situação Entrada", "Saída Registrada"},
                0
        ) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };

        tabela = new JTable(modelo);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setForeground(TEXTO);
        tabela.setBackground(BRANCO);
        tabela.setRowHeight(42);
        tabela.setShowVerticalLines(false);
        tabela.setShowHorizontalLines(true);
        tabela.setGridColor(new Color(241, 245, 249));
        tabela.setSelectionBackground(new Color(209, 250, 229));
        tabela.setSelectionForeground(TEXTO);
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cabecalho.setForeground(TEXTO);
        cabecalho.setBackground(new Color(241, 245, 249));
        cabecalho.setPreferredSize(new Dimension(0, 43));
        cabecalho.setReorderingAllowed(false);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(400);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(160);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(200);

        DefaultTableCellRenderer centralRenderer = new DefaultTableCellRenderer();
        centralRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(0).setCellRenderer(centralRenderer);
        tabela.getColumnModel().getColumn(2).setCellRenderer(centralRenderer);

        tabela.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
            ) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));

                String texto = value != null ? value.toString() : "—";
                if (texto.contains(":") && !texto.contains("Pendente")) {
                    label.setForeground(new Color(16, 185, 129));
                } else {
                    label.setForeground(new Color(217, 119, 6));
                }
                return label;
            }
        });
    }

    private void carregarAlunos() {
        if (modelo == null || comboSala == null) {
            return;
        }

        modelo.setRowCount(0);
        Sala salaSelecionada = (Sala) comboSala.getSelectedItem();
        if (salaSelecionada == null) {
            atualizarQuantidade();
            return;
        }

        List<Aluno> alunos = new AlunoDao().listarPorSala(salaSelecionada.getId());
        FrequenciaDao frequenciaDao = new FrequenciaDao();

        if (alunos != null) {
            for (Aluno aluno : alunos) {
                Frequencia f = frequenciaDao.buscarFrequenciaHoje(aluno.getId());
                String situacao = (f != null && Boolean.TRUE.equals(f.isPresente())) ? "Presente" : "Não registrada / Falta";
                String saida = (f != null && f.getSaidaHora() != null) ? f.getSaidaHoraFormatada() : "Pendente (Sem saída)";

                modelo.addRow(new Object[]{
                        aluno.getId(),
                        aluno.getNome(),
                        situacao,
                        saida
                });
            }
        }

        atualizarQuantidade();
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(FUNDO);

        labelQuantidade = new JLabel("Alunos na turma: 0");
        labelQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelQuantidade.setForeground(CINZA);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acoes.setOpaque(false);

        JButton btnRegistrarSelecionado = new JButton("Registrar Saída do Aluno Selecionado");
        btnRegistrarSelecionado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegistrarSelecionado.setForeground(VERDE_ESCURO);
        btnRegistrarSelecionado.setBackground(new Color(236, 253, 245));
        btnRegistrarSelecionado.setFocusPainted(false);
        btnRegistrarSelecionado.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrarSelecionado.setPreferredSize(new Dimension(260, 44));
        btnRegistrarSelecionado.putClientProperty(FlatClientProperties.STYLE, "arc:12;focusWidth:0;borderWidth:0");
        btnRegistrarSelecionado.addActionListener(e -> registrarSaidaSelecionado());

        btnSalvarManual = new JButton("Registrar Saída de Todos");
        btnSalvarManual.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSalvarManual.setForeground(Color.WHITE);
        btnSalvarManual.setBackground(VERDE_ESCURO);
        btnSalvarManual.setFocusPainted(false);
        btnSalvarManual.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalvarManual.setPreferredSize(new Dimension(200, 44));
        btnSalvarManual.putClientProperty(FlatClientProperties.STYLE,
                "arc:12;focusWidth:0;borderWidth:0;hoverBackground:#047857;pressedBackground:#065F46");
        btnSalvarManual.addActionListener(e -> registrarSaidaTodosDaTurma());

        acoes.add(btnRegistrarSelecionado);
        acoes.add(btnSalvarManual);

        rodape.add(labelQuantidade, BorderLayout.WEST);
        rodape.add(acoes, BorderLayout.EAST);

        return rodape;
    }

    private void registrarSaidaSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um aluno na tabela para registrar a saída.",
                    "Nenhum aluno selecionado",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int alunoId = (int) modelo.getValueAt(linha, 0);
        String nomeAluno = (String) modelo.getValueAt(linha, 1);
        Date hoje = Date.valueOf(LocalDate.now());

        FrequenciaDao frequenciaDao = new FrequenciaDao();
        boolean sucesso = frequenciaDao.registrarSaidaManual(alunoId, hoje);

        if (sucesso) {
            String horaAtual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            modelo.setValueAt(horaAtual, linha, 3);
            modelo.setValueAt("Presente", linha, 2);
            JOptionPane.showMessageDialog(
                    this,
                    "Saída do aluno " + nomeAluno + " registrada com sucesso às " + horaAtual + "!",
                    "Saída registrada",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível registrar a saída do aluno.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void registrarSaidaTodosDaTurma() {
        if (modelo.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não existem alunos na turma para registrar saída.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Sala sala = (Sala) comboSala.getSelectedItem();
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja registrar o ponto de saída de todos os alunos da turma " + sala + " neste momento?",
                "Confirmar saída da turma",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        FrequenciaDao frequenciaDao = new FrequenciaDao();
        Date hoje = Date.valueOf(LocalDate.now());
        int salvos = 0;

        for (int i = 0; i < modelo.getRowCount(); i++) {
            int alunoId = (int) modelo.getValueAt(i, 0);
            if (frequenciaDao.registrarSaidaManual(alunoId, hoje)) {
                salvos++;
            }
        }

        carregarAlunos();
        JOptionPane.showMessageDialog(
                this,
                "Saída registrada para " + salvos + " aluno(s) com sucesso!",
                "Concluído",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void atualizarQuantidade() {
        if (labelQuantidade != null && modelo != null) {
            labelQuantidade.setText("Alunos na turma: " + modelo.getRowCount());
        }
    }

    private void atualizarData() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        labelData.setText(LocalDate.now().format(formato));
    }
}
