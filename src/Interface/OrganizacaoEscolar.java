package Interface;

import DAO.FrequenciaDao;
import DAO.SalaDao;
import Escola.Frequencia;
import Escola.Sala;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.formdev.flatlaf.FlatClientProperties;

public class OrganizacaoEscolar extends JFrame {

    private final Color AZUL = new Color(37, 99, 235);
    private final Color AZUL_ESCURO = new Color(20, 43, 74);
    private final Color TEXTO = Configuracoes.getCorTexto();
    private final Color CINZA = Configuracoes.getCorSubtexto();
    private final Color FUNDO = Configuracoes.getCorFundo();
    private final Color BRANCO = Configuracoes.getCorPainel();
    private final Color BORDA = Configuracoes.getCorBorda();
    private final Color VERDE = new Color(22, 163, 74);
    private final Color VERDE_FUNDO = Configuracoes.isModoEscuro() ? new Color(20, 83, 45) : new Color(220, 252, 231);
    private final Color VERMELHO = new Color(220, 38, 38);
    private final Color VERMELHO_FUNDO = Configuracoes.isModoEscuro() ? new Color(127, 29, 29) : new Color(254, 226, 226);

    private final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Componentes de filtro
    private JComboBox<String> comboDatas;
    private JTextField campoDataManual;
    private JComboBox<Object> comboSala;
    private JTextField campoBuscaAluno;
    private JComboBox<String> comboSituacao;

    // Componentes de métricas / cartões
    private JLabel labelTotalRegistros;
    private JLabel labelTotalPresentes;
    private JLabel labelTotalFaltas;
    private JLabel labelTaxaPresenca;

    // Tabela e Modelo
    private JTable tabela;
    private DefaultTableModel modelo;
    private JLabel labelRodapeInfo;

    // DAOs
    private final FrequenciaDao frequenciaDao = new FrequenciaDao();
    private final SalaDao salaDao = new SalaDao();

    public OrganizacaoEscolar() {
        setTitle("Controle de Faltas | Organização Escolar - Consulta de Frequência");
        setSize(1300, 750);
        setMinimumSize(new Dimension(950, 640));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(FUNDO);

        // CABEÇALHO
        JPanel cabecalho = criarCabecalho();

        // ÁREA CENTRAL
        JPanel areaCentral = new JPanel(new BorderLayout(0, 16));
        areaCentral.setBackground(FUNDO);
        areaCentral.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // PAINEL SUPERIOR: MÉTRICAS + FILTROS
        JPanel painelSuperior = new JPanel(new BorderLayout(0, 16));
        painelSuperior.setOpaque(false);

        JPanel painelMetricas = criarPainelMetricas();
        JPanel painelFiltros = criarPainelFiltros();

        painelSuperior.add(painelMetricas, BorderLayout.NORTH);
        painelSuperior.add(painelFiltros, BorderLayout.CENTER);

        // TABELA
        criarTabela();

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(BORDA));
        scroll.getViewport().setBackground(BRANCO);

        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBackground(BRANCO);
        painelTabela.setBorder(BorderFactory.createLineBorder(BORDA));
        painelTabela.add(scroll, BorderLayout.CENTER);

        // RODAPÉ / AÇÕES
        JPanel rodape = criarRodape();

        areaCentral.add(painelSuperior, BorderLayout.NORTH);
        areaCentral.add(painelTabela, BorderLayout.CENTER);
        areaCentral.add(rodape, BorderLayout.SOUTH);

        principal.add(cabecalho, BorderLayout.NORTH);
        principal.add(areaCentral, BorderLayout.CENTER);

        setContentPane(principal);

        carregarFiltrosIniciais();
        executarConsulta();

        setVisible(true);
    }

    // CABEÇALHO
    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(BRANCO);
        cabecalho.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDA),
                        BorderFactory.createEmptyBorder(20, 30, 20, 30)
                )
        );

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Organização escolar");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 25));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel("Consulta de frequência e acompanhamento diário das turmas.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(CINZA);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(subtitulo);

        JLabel etiqueta = new JLabel("  CONSULTA DE FREQUÊNCIA  ");
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 11));
        etiqueta.setForeground(Configuracoes.getCorTextoDestaque());
        etiqueta.setOpaque(true);
        etiqueta.setBackground(Configuracoes.getCorDestaque());
        etiqueta.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        etiqueta.putClientProperty(FlatClientProperties.STYLE, "arc:8");

        cabecalho.add(textos, BorderLayout.CENTER);
        cabecalho.add(etiqueta, BorderLayout.EAST);

        return cabecalho;
    }

    // CARDS DE MÉTRICAS / RESUMO
    private JPanel criarPainelMetricas() {
        JPanel painel = new JPanel(new GridLayout(1, 4, 15, 0));
        painel.setOpaque(false);

        labelTotalRegistros = new JLabel("0");
        labelTotalPresentes = new JLabel("0");
        labelTotalFaltas = new JLabel("0");
        labelTaxaPresenca = new JLabel("0%");

        painel.add(criarCardMetrica("Total de registros", labelTotalRegistros, AZUL, Configuracoes.getCorDestaque()));
        painel.add(criarCardMetrica("Presenças", labelTotalPresentes, VERDE, VERDE_FUNDO));
        painel.add(criarCardMetrica("Faltas", labelTotalFaltas, VERMELHO, VERMELHO_FUNDO));
        painel.add(criarCardMetrica("Índice de presença", labelTaxaPresenca, new Color(99, 102, 241), Configuracoes.isModoEscuro() ? new Color(30, 41, 59) : new Color(238, 242, 255)));

        return painel;
    }

    private JPanel criarCardMetrica(String titulo, JLabel valorLabel, Color corDestaque, Color corFundoBadge) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(BRANCO);
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA),
                        BorderFactory.createEmptyBorder(12, 16, 12, 16)
                )
        );

        JLabel tituloLabel = new JLabel(titulo.toUpperCase());
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tituloLabel.setForeground(CINZA);

        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valorLabel.setForeground(corDestaque);

        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setOpaque(false);
        barraSuperior.add(tituloLabel, BorderLayout.WEST);

        card.add(barraSuperior, BorderLayout.NORTH);
        card.add(valorLabel, BorderLayout.CENTER);

        return card;
    }

    // PAINEL DE FILTROS COM DATAS DO DIA, SALAS, ALUNOS E SITUAÇÃO
    private JPanel criarPainelFiltros() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BRANCO);
        container.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA),
                        BorderFactory.createEmptyBorder(16, 18, 16, 18)
                )
        );

        JPanel linhaFiltros = new JPanel(new GridBagLayout());
        linhaFiltros.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);

        // 1. Data do Dia (Combo com datas cadastradas + opção Hoje)
        JPanel grupoData = new JPanel();
        grupoData.setLayout(new BoxLayout(grupoData, BoxLayout.Y_AXIS));
        grupoData.setOpaque(false);

        JLabel lblData = new JLabel("Data do dia");
        lblData.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblData.setForeground(TEXTO);

        comboDatas = new JComboBox<>();
        comboDatas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboDatas.setPreferredSize(new Dimension(190, 38));
        comboDatas.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:1");

        JButton btnHoje = new JButton("Hoje");
        btnHoje.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnHoje.setForeground(Configuracoes.getCorTextoDestaque());
        btnHoje.setBackground(Configuracoes.getCorDestaque());
        btnHoje.setFocusPainted(false);
        btnHoje.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHoje.setPreferredSize(new Dimension(60, 38));
        btnHoje.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:0;borderWidth:0");
        btnHoje.setToolTipText("Filtrar para a data de hoje");
        btnHoje.addActionListener(e -> selecionarDataHoje());

        JPanel painelDataEBotao = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        painelDataEBotao.setOpaque(false);
        painelDataEBotao.add(comboDatas);
        painelDataEBotao.add(btnHoje);

        grupoData.add(lblData);
        grupoData.add(Box.createVerticalStrut(6));
        grupoData.add(painelDataEBotao);

        // 2. Data Manual (opcional para digitar qualquer data)
        JPanel grupoDataManual = new JPanel();
        grupoDataManual.setLayout(new BoxLayout(grupoDataManual, BoxLayout.Y_AXIS));
        grupoDataManual.setOpaque(false);

        JLabel lblDataManual = new JLabel("Ou data específica");
        lblDataManual.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDataManual.setForeground(TEXTO);

        campoDataManual = new JTextField();
        campoDataManual.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoDataManual.setPreferredSize(new Dimension(120, 38));
        campoDataManual.putClientProperty("JTextField.placeholderText", "dd/mm/aaaa");
        campoDataManual.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:1");
        campoDataManual.addActionListener(e -> executarConsulta());

        grupoDataManual.add(lblDataManual);
        grupoDataManual.add(Box.createVerticalStrut(6));
        grupoDataManual.add(campoDataManual);

        // 3. Turma / Sala
        JPanel grupoSala = new JPanel();
        grupoSala.setLayout(new BoxLayout(grupoSala, BoxLayout.Y_AXIS));
        grupoSala.setOpaque(false);

        JLabel lblSala = new JLabel("Turma / Sala");
        lblSala.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSala.setForeground(TEXTO);

        comboSala = new JComboBox<>();
        comboSala.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboSala.setPreferredSize(new Dimension(160, 38));
        comboSala.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:1");

        grupoSala.add(lblSala);
        grupoSala.add(Box.createVerticalStrut(6));
        grupoSala.add(comboSala);

        // 4. Nome do Aluno
        JPanel grupoAluno = new JPanel();
        grupoAluno.setLayout(new BoxLayout(grupoAluno, BoxLayout.Y_AXIS));
        grupoAluno.setOpaque(false);

        JLabel lblAluno = new JLabel("Nome do aluno");
        lblAluno.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAluno.setForeground(TEXTO);

        campoBuscaAluno = new JTextField();
        campoBuscaAluno.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoBuscaAluno.setPreferredSize(new Dimension(170, 38));
        campoBuscaAluno.putClientProperty("JTextField.placeholderText", "Buscar aluno...");
        campoBuscaAluno.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:1");
        campoBuscaAluno.addActionListener(e -> executarConsulta());
        campoBuscaAluno.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    executarConsulta();
                }
            }
        });

        grupoAluno.add(lblAluno);
        grupoAluno.add(Box.createVerticalStrut(6));
        grupoAluno.add(campoBuscaAluno);

        // 5. Situação (Presente / Falta / Todos)
        JPanel grupoSituacao = new JPanel();
        grupoSituacao.setLayout(new BoxLayout(grupoSituacao, BoxLayout.Y_AXIS));
        grupoSituacao.setOpaque(false);

        JLabel lblSituacao = new JLabel("Situação");
        lblSituacao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSituacao.setForeground(TEXTO);

        comboSituacao = new JComboBox<>(new String[]{"Todos os status", "Presente", "Falta"});
        comboSituacao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboSituacao.setPreferredSize(new Dimension(130, 38));
        comboSituacao.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:1");

        grupoSituacao.add(lblSituacao);
        grupoSituacao.add(Box.createVerticalStrut(6));
        grupoSituacao.add(comboSituacao);

        // 6. Botões Filtrar / Limpar
        JPanel grupoBotoes = new JPanel();
        grupoBotoes.setLayout(new BoxLayout(grupoBotoes, BoxLayout.Y_AXIS));
        grupoBotoes.setOpaque(false);

        JLabel lblVazio = new JLabel(" ");
        lblVazio.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnFiltrar.setForeground(BRANCO);
        btnFiltrar.setBackground(AZUL);
        btnFiltrar.setFocusPainted(false);
        btnFiltrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFiltrar.setPreferredSize(new Dimension(85, 38));
        btnFiltrar.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:0;borderWidth:0");
        btnFiltrar.addActionListener(e -> executarConsulta());

        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLimpar.setForeground(Configuracoes.getCorTextoDestaque());
        btnLimpar.setBackground(Configuracoes.getCorDestaque());
        btnLimpar.setFocusPainted(false);
        btnLimpar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpar.setPreferredSize(new Dimension(75, 38));
        btnLimpar.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:0;borderWidth:0");
        btnLimpar.addActionListener(e -> limparFiltros());

        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        painelBotoesAcao.setOpaque(false);
        painelBotoesAcao.add(btnFiltrar);
        painelBotoesAcao.add(btnLimpar);

        grupoBotoes.add(lblVazio);
        grupoBotoes.add(Box.createVerticalStrut(6));
        grupoBotoes.add(painelBotoesAcao);

        // Montagem no layout
        gbc.gridx = 0; gbc.weightx = 0; linhaFiltros.add(grupoData, gbc);
        gbc.gridx = 1; gbc.weightx = 0; linhaFiltros.add(grupoDataManual, gbc);
        gbc.gridx = 2; gbc.weightx = 0; linhaFiltros.add(grupoSala, gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; linhaFiltros.add(grupoAluno, gbc);
        gbc.gridx = 4; gbc.weightx = 0; linhaFiltros.add(grupoSituacao, gbc);
        gbc.gridx = 5; gbc.weightx = 0; linhaFiltros.add(grupoBotoes, gbc);

        container.add(linhaFiltros, BorderLayout.CENTER);

        // Eventos imediatos para mudanças em combos
        comboDatas.addActionListener(e -> {
            if (comboDatas.getSelectedIndex() > 0 && !campoDataManual.getText().isEmpty()) {
                campoDataManual.setText("");
            }
            executarConsulta();
        });
        comboSala.addActionListener(e -> executarConsulta());
        comboSituacao.addActionListener(e -> executarConsulta());

        return container;
    }

    // CONFIGURAÇÃO DA TABELA
    private void criarTabela() {
        modelo = new DefaultTableModel(
                new Object[]{"ID", "Data", "Nome do aluno", "Turma / Sala", "Situação"},
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
        tabela.setRowHeight(38);
        tabela.setShowVerticalLines(false);
        tabela.setShowHorizontalLines(true);
        tabela.setGridColor(new Color(241, 245, 249));
        tabela.setSelectionBackground(new Color(219, 234, 254));
        tabela.setSelectionForeground(TEXTO);
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoCreateRowSorter(true);

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cabecalho.setForeground(TEXTO);
        cabecalho.setBackground(new Color(241, 245, 249));
        cabecalho.setPreferredSize(new Dimension(0, 42));
        cabecalho.setReorderingAllowed(false);

        // Larguras das colunas
        tabela.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        tabela.getColumnModel().getColumn(1).setPreferredWidth(110);  // Data
        tabela.getColumnModel().getColumn(2).setPreferredWidth(380);  // Nome
        tabela.getColumnModel().getColumn(3).setPreferredWidth(210);  // Turma
        tabela.getColumnModel().getColumn(4).setPreferredWidth(140);  // Situação

        // Alinhamento central para ID e Data
        DefaultTableCellRenderer centralRenderer = new DefaultTableCellRenderer();
        centralRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(0).setCellRenderer(centralRenderer);
        tabela.getColumnModel().getColumn(1).setCellRenderer(centralRenderer);

        // Renderer customizado para a coluna Situação (Badges verde/vermelho)
        tabela.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
            ) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));

                String situacao = value != null ? value.toString() : "";
                if ("Presente".equalsIgnoreCase(situacao)) {
                    label.setForeground(new Color(21, 128, 61));
                    if (!isSelected) {
                        label.setBackground(VERDE_FUNDO);
                    }
                } else if ("Falta".equalsIgnoreCase(situacao)) {
                    label.setForeground(new Color(185, 28, 28));
                    if (!isSelected) {
                        label.setBackground(VERMELHO_FUNDO);
                    }
                }

                label.setOpaque(true);
                return label;
            }
        });
    }

    // RODAPÉ COM RESUMO E BOTÕES DE AÇÃO
    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(FUNDO);

        labelRodapeInfo = new JLabel("Mostrando 0 registros de frequência.");
        labelRodapeInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelRodapeInfo.setForeground(CINZA);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acoes.setOpaque(false);

        JButton btnAlternar = new JButton("Alternar Presença/Falta");
        btnAlternar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAlternar.setForeground(AZUL);
        btnAlternar.setBackground(new Color(239, 246, 255));
        btnAlternar.setFocusPainted(false);
        btnAlternar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAlternar.setPreferredSize(new Dimension(185, 40));
        btnAlternar.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:0;borderWidth:0");
        btnAlternar.addActionListener(e -> alternarPresencaSelecionada());

        JButton btnExcluir = new JButton("Excluir registro");
        btnExcluir.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExcluir.setForeground(VERMELHO);
        btnExcluir.setBackground(new Color(254, 242, 242));
        btnExcluir.setFocusPainted(false);
        btnExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExcluir.setPreferredSize(new Dimension(140, 40));
        btnExcluir.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:0;borderWidth:0");
        btnExcluir.addActionListener(e -> excluirRegistroSelecionado());

        JButton btnAtualizar = new JButton("Atualizar dados");
        btnAtualizar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAtualizar.setForeground(BRANCO);
        btnAtualizar.setBackground(AZUL);
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAtualizar.setPreferredSize(new Dimension(140, 40));
        btnAtualizar.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:0;borderWidth:0");
        btnAtualizar.addActionListener(e -> {
            carregarDatasNoCombo();
            executarConsulta();
        });

        acoes.add(btnAlternar);
        acoes.add(btnExcluir);
        acoes.add(btnAtualizar);

        rodape.add(labelRodapeInfo, BorderLayout.WEST);
        rodape.add(acoes, BorderLayout.EAST);

        return rodape;
    }

    // CARREGAR DADOS INICIAIS NOS FILTROS
    private void carregarFiltrosIniciais() {
        carregarDatasNoCombo();
        carregarSalasNoCombo();
    }

    private void carregarDatasNoCombo() {
        Object itemSelecionado = comboDatas.getSelectedItem();
        comboDatas.removeAllItems();

        comboDatas.addItem("Todas as datas");

        String hojeFormatado = LocalDate.now().format(FORMATO_BR);
        String labelHoje = "Hoje (" + hojeFormatado + ")";
        comboDatas.addItem(labelHoje);

        List<Date> datasRegistradas = frequenciaDao.listarDatasDisponiveis();
        if (datasRegistradas != null) {
            for (Date d : datasRegistradas) {
                String dFormatada = d.toLocalDate().format(FORMATO_BR);
                String item = dFormatada;
                if (dFormatada.equals(hojeFormatado)) {
                    continue; // Já adicionado como Hoje
                }
                comboDatas.addItem(item);
            }
        }

        if (itemSelecionado != null) {
            comboDatas.setSelectedItem(itemSelecionado);
        } else {
            // Se hoje estiver na lista com registros ou por padrão selecionamos "Todas as datas"
            comboDatas.setSelectedIndex(0);
        }
    }

    private void carregarSalasNoCombo() {
        comboSala.removeAllItems();
        comboSala.addItem("Todas as turmas");

        List<Sala> salas = salaDao.listar();
        if (salas != null) {
            for (Sala sala : salas) {
                comboSala.addItem(sala);
            }
        }
    }

    private void selecionarDataHoje() {
        campoDataManual.setText("");
        String hojeFormatado = LocalDate.now().format(FORMATO_BR);
        String itemHoje = "Hoje (" + hojeFormatado + ")";

        for (int i = 0; i < comboDatas.getItemCount(); i++) {
            if (comboDatas.getItemAt(i).startsWith("Hoje (")) {
                comboDatas.setSelectedIndex(i);
                return;
            }
        }
        campoDataManual.setText(hojeFormatado);
        executarConsulta();
    }

    private void limparFiltros() {
        comboDatas.setSelectedIndex(0);
        campoDataManual.setText("");
        comboSala.setSelectedIndex(0);
        campoBuscaAluno.setText("");
        comboSituacao.setSelectedIndex(0);
        executarConsulta();
    }

    // EXECUTAR CONSULTA NO BANCO COM OS FILTROS SELECIONADOS
    private void executarConsulta() {
        Date dataFiltro = resolverDataFiltro();
        Integer salaId = resolverSalaFiltro();
        String nomeAluno = campoBuscaAluno.getText().trim();
        Boolean presente = resolverSituacaoFiltro();

        List<Frequencia> resultados = frequenciaDao.consultar(dataFiltro, salaId, nomeAluno, presente);

        preencherTabela(resultados);
    }

    private Date resolverDataFiltro() {
        String textoManual = campoDataManual.getText().trim();
        if (!textoManual.isEmpty()) {
            try {
                LocalDate localDate = LocalDate.parse(textoManual, FORMATO_BR);
                return Date.valueOf(localDate);
            } catch (DateTimeParseException e) {

            }
        }

        String dataSelecionada = (String) comboDatas.getSelectedItem();
        if (dataSelecionada == null || "Todas as datas".equals(dataSelecionada)) {
            return null;
        }

        if (dataSelecionada.startsWith("Hoje (")) {
            return Date.valueOf(LocalDate.now());
        }

        try {
            LocalDate localDate = LocalDate.parse(dataSelecionada, FORMATO_BR);
            return Date.valueOf(localDate);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private Integer resolverSalaFiltro() {
        Object item = comboSala.getSelectedItem();
        if (item instanceof Sala sala) {
            return sala.getId();
        }
        return null;
    }

    private Boolean resolverSituacaoFiltro() {
        int index = comboSituacao.getSelectedIndex();
        if (index == 1) return true;  // Presente
        if (index == 2) return false; // Falta
        return null; // Todos
    }

    private void preencherTabela(List<Frequencia> registros) {
        modelo.setRowCount(0);

        int total = 0;
        int presentes = 0;
        int faltas = 0;

        if (registros != null) {
            total = registros.size();
            for (Frequencia f : registros) {
                String dataStr = f.getData() != null ? f.getData().toLocalDate().format(FORMATO_BR) : "-";
                String situacao = Boolean.TRUE.equals(f.isPresente()) ? "Presente" : "Falta";

                if (Boolean.TRUE.equals(f.isPresente())) {
                    presentes++;
                } else {
                    faltas++;
                }

                modelo.addRow(new Object[]{
                        f.getId(),
                        dataStr,
                        f.getAlunoNome() != null ? f.getAlunoNome() : "Aluno ID " + f.getAlunoId(),
                        f.getSalaNome() != null ? f.getSalaNome() : "-",
                        situacao
                });
            }
        }

        // Atualiza métricas
        labelTotalRegistros.setText(String.valueOf(total));
        labelTotalPresentes.setText(String.valueOf(presentes));
        labelTotalFaltas.setText(String.valueOf(faltas));

        if (total > 0) {
            double percentual = (presentes * 100.0) / total;
            labelTaxaPresenca.setText(String.format("%.1f%%", percentual));
        } else {
            labelTaxaPresenca.setText("0%");
        }

        labelRodapeInfo.setText(
                "Mostrando " + total + " registro(s) (" + presentes + " presenças, " + faltas + " faltas)."
        );
    }

    // AÇÕES: ALTERNAR PRESENÇA / EXCLUIR REGISTRO
    private void alternarPresencaSelecionada() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um registro na tabela para alternar a frequência.",
                    "Nenhum registro selecionado",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int linhaModelo = tabela.convertRowIndexToModel(linhaSelecionada);
        int frequenciaId = (int) modelo.getValueAt(linhaModelo, 0);
        String alunoNome = String.valueOf(modelo.getValueAt(linhaModelo, 2));
        String situacaoAtual = String.valueOf(modelo.getValueAt(linhaModelo, 4));

        boolean novaSituacao = !"Presente".equalsIgnoreCase(situacaoAtual);
        String novoTexto = novaSituacao ? "Presente" : "Falta";

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja alterar o status de " + alunoNome + " para '" + novoTexto + "'?",
                "Alterar presença",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean sucesso = frequenciaDao.atualizarPresenca(frequenciaId, novaSituacao);
            if (sucesso) {
                executarConsulta();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Não foi possível atualizar a presença no banco de dados.",
                        "Erro ao atualizar",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void excluirRegistroSelecionado() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um registro na tabela para excluir.",
                    "Nenhum registro selecionado",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int linhaModelo = tabela.convertRowIndexToModel(linhaSelecionada);
        int frequenciaId = (int) modelo.getValueAt(linhaModelo, 0);
        String dataStr = String.valueOf(modelo.getValueAt(linhaModelo, 1));
        String alunoNome = String.valueOf(modelo.getValueAt(linhaModelo, 2));

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir o registro de frequência do aluno:\n"
                        + alunoNome + " referente ao dia " + dataStr + "?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean sucesso = frequenciaDao.deletar(frequenciaId);
            if (sucesso) {
                carregarDatasNoCombo();
                executarConsulta();
                JOptionPane.showMessageDialog(
                        this,
                        "Registro de frequência excluído com sucesso.",
                        "Exclusão concluída",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Erro ao excluir registro de frequência.",
                        "Falha na exclusão",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
