package Interface;

import DAO.AlunoDao;
import DAO.FrequenciaDao;
import DAO.SalaDao;
import Escola.Aluno;
import Escola.Frequencia;
import Escola.Sala;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.DefaultCellEditor;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.formdev.flatlaf.FlatClientProperties;

public class FrequenciaTela extends JFrame {

    private JComboBox<Sala> comboSala;
    private JButton btnSalvar;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JLabel labelQuantidade;
    private JLabel labelData;

    private final Color AZUL = new Color(37, 99, 235);
    private final Color TEXTO = new Color(23, 43, 77);
    private final Color CINZA = new Color(100, 116, 139);
    private final Color FUNDO = new Color(248, 250, 253);
    private final Color BRANCO = Color.WHITE;
    private final Color BORDA = new Color(226, 232, 240);

    public FrequenciaTela() {

        setTitle("Controle de Faltas | Frequência Escolar");
        setSize(1050, 700);
        setMinimumSize(new Dimension(850, 580));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(FUNDO);

        // CABEÇALHO
        JPanel cabecalho = criarCabecalho();

        // CONTEÚDO
        JPanel conteudo = new JPanel(new BorderLayout(0, 20));
        conteudo.setBackground(FUNDO);
        conteudo.setBorder(
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        );

        // SELEÇÃO DA TURMA E INFORMAÇÕES
        JPanel painelControle = criarPainelControle();

        // TABELA
        criarTabela();

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(
                BorderFactory.createLineBorder(BORDA)
        );
        scroll.getViewport().setBackground(BRANCO);

        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBackground(BRANCO);
        painelTabela.setBorder(
                BorderFactory.createLineBorder(BORDA)
        );

        painelTabela.add(scroll, BorderLayout.CENTER);

        // RODAPÉ
        JPanel rodape = criarRodape();

        conteudo.add(painelControle, BorderLayout.NORTH);
        conteudo.add(painelTabela, BorderLayout.CENTER);
        conteudo.add(rodape, BorderLayout.SOUTH);

        principal.add(cabecalho, BorderLayout.NORTH);
        principal.add(conteudo, BorderLayout.CENTER);

        setContentPane(principal);

        carregarSalas();

        comboSala.addActionListener(e -> carregarAlunos());

        if (comboSala.getItemCount() > 0) {
            carregarAlunos();
        }

        setVisible(true);
    }

    // CABEÇALHO DA TELA

    private JPanel criarCabecalho() {

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(BRANCO);
        cabecalho.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 0, BORDA
                        ),
                        BorderFactory.createEmptyBorder(
                                23, 30, 23, 30
                        )
                )
        );

        JPanel textos = new JPanel();
        textos.setLayout(
                new BoxLayout(textos, BoxLayout.Y_AXIS)
        );
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Frequência escolar");
        titulo.setFont(
                new Font("Segoe UI", Font.BOLD, 25)
        );
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel(
                "Registre a presença dos alunos de cada turma."
        );
        subtitulo.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );
        subtitulo.setForeground(CINZA);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(7));
        textos.add(subtitulo);

        JLabel etiqueta = new JLabel("  CONTROLE DE PRESENÇA  ");
        etiqueta.setFont(
                new Font("Segoe UI", Font.BOLD, 11)
        );
        etiqueta.setForeground(AZUL);
        etiqueta.setOpaque(true);
        etiqueta.setBackground(new Color(239, 246, 255));
        etiqueta.setBorder(
                BorderFactory.createEmptyBorder(9, 5, 9, 5)
        );

        cabecalho.add(textos, BorderLayout.CENTER);
        cabecalho.add(etiqueta, BorderLayout.EAST);

        return cabecalho;
    }

    // PAINEL DE SELEÇÃO DA TURMA

    private JPanel criarPainelControle() {

        JPanel painel = new JPanel(new BorderLayout(15, 0));
        painel.setBackground(BRANCO);
        painel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA),
                        BorderFactory.createEmptyBorder(
                                18, 20, 18, 20
                        )
                )
        );

        JPanel selecao = new JPanel();
        selecao.setLayout(
                new BoxLayout(selecao, BoxLayout.Y_AXIS)
        );
        selecao.setOpaque(false);

        JLabel titulo = new JLabel("Turma / Sala");
        titulo.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );
        titulo.setForeground(TEXTO);

        comboSala = new JComboBox<>();
        comboSala.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );
        comboSala.setPreferredSize(
                new Dimension(240, 42)
        );
        comboSala.setMaximumSize(
                new Dimension(300, 42)
        );
        comboSala.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboSala.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:12;focusWidth:1"
        );

        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        selecao.add(titulo);
        selecao.add(Box.createVerticalStrut(9));
        selecao.add(comboSala);

        JPanel informacoes = new JPanel();
        informacoes.setLayout(
                new BoxLayout(informacoes, BoxLayout.Y_AXIS)
        );
        informacoes.setOpaque(false);
        informacoes.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel legendaData = new JLabel("DATA DO REGISTRO");
        legendaData.setFont(
                new Font("Segoe UI", Font.BOLD, 10)
        );
        legendaData.setForeground(CINZA);

        labelData = new JLabel();
        labelData.setFont(
                new Font("Segoe UI", Font.BOLD, 16)
        );
        labelData.setForeground(AZUL);

        atualizarData();

        informacoes.add(legendaData);
        informacoes.add(Box.createVerticalStrut(7));
        informacoes.add(labelData);

        painel.add(selecao, BorderLayout.WEST);
        painel.add(informacoes, BorderLayout.EAST);

        return painel;
    }

    // CARREGAR AS TURMAS DO BANCO

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
                    "Nenhuma turma foi encontrada. "
                            + "Verifique as salas cadastradas.",
                    "Turmas indisponíveis",
                    JOptionPane.WARNING_MESSAGE
            );

            btnSalvar.setEnabled(false);
        }
    }

    // CONFIGURAÇÃO DA TABELA

    private void criarTabela() {

        modelo = new DefaultTableModel(
                new Object[]{"ID", "Nome do aluno", "Situação"},
                0
        ) {
            @Override
            public boolean isCellEditable(
                    int linha, int coluna
            ) {
                return coluna == 2;
            }
        };

        tabela = new JTable(modelo);

        tabela.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );
        tabela.setForeground(TEXTO);
        tabela.setBackground(BRANCO);
        tabela.setRowHeight(42);
        tabela.setShowVerticalLines(false);
        tabela.setShowHorizontalLines(true);
        tabela.setGridColor(new Color(241, 245, 249));
        tabela.setSelectionBackground(new Color(219, 234, 254));
        tabela.setSelectionForeground(TEXTO);
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );
        cabecalho.setForeground(TEXTO);
        cabecalho.setBackground(new Color(241, 245, 249));
        cabecalho.setPreferredSize(new Dimension(0, 43));
        cabecalho.setReorderingAllowed(false);

        tabela.getColumnModel().getColumn(0)
                .setPreferredWidth(70);
        tabela.getColumnModel().getColumn(1)
                .setPreferredWidth(450);
        tabela.getColumnModel().getColumn(2)
                .setPreferredWidth(180);

        JComboBox<String> comboPresenca = new JComboBox<>();
        comboPresenca.addItem("Presente");
        comboPresenca.addItem("Falta");

        comboPresenca.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );
        comboPresenca.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:8;focusWidth:1"
        );

        tabela.getColumnModel()
                .getColumn(2)
                .setCellEditor(
                        new DefaultCellEditor(comboPresenca)
                );
    }

    // CARREGAR ALUNOS DA TURMA SELECIONADA

    private void carregarAlunos() {

        if (modelo == null || comboSala == null) {
            return;
        }

        modelo.setRowCount(0);

        Sala salaSelecionada =
                (Sala) comboSala.getSelectedItem();

        if (salaSelecionada == null) {
            atualizarQuantidade();
            return;
        }

        int salaId = salaSelecionada.getId();

        AlunoDao alunoDao = new AlunoDao();
        List<Aluno> alunos =
                alunoDao.listarPorSala(salaId);

        if (alunos != null) {
            for (Aluno aluno : alunos) {
                modelo.addRow(new Object[]{
                        aluno.getId(),
                        aluno.getNome(),
                        "Presente"
                });
            }
        }

        atualizarQuantidade();
    }

    // RODAPÉ COM CONTAGEM E BOTÃO DE SALVAR

    private JPanel criarRodape() {

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(FUNDO);

        labelQuantidade = new JLabel("Alunos na turma: 0");
        labelQuantidade.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );
        labelQuantidade.setForeground(CINZA);

        btnSalvar = new JButton("Salvar frequência");
        btnSalvar.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );
        btnSalvar.setForeground(BRANCO);
        btnSalvar.setBackground(AZUL);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
        btnSalvar.setPreferredSize(
                new Dimension(190, 45)
        );

        btnSalvar.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:12;focusWidth:0;borderWidth:0;"
                        + "hoverBackground:#1D4ED8;"
                        + "pressedBackground:#1E40AF"
        );

        btnSalvar.addActionListener(
                e -> salvarFrequencia()
        );

        rodape.add(labelQuantidade, BorderLayout.WEST);
        rodape.add(btnSalvar, BorderLayout.EAST);

        return rodape;
    }

    private void atualizarQuantidade() {

        if (labelQuantidade != null && modelo != null) {
            labelQuantidade.setText(
                    "Alunos na turma: " + modelo.getRowCount()
            );
        }
    }

    private void atualizarData() {

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        labelData.setText(
                LocalDate.now().format(formato)
        );
    }

    // SALVAR A FREQUÊNCIA NO BANCO

    private void salvarFrequencia() {

        if (tabela.isEditing()) {
            if (!tabela.getCellEditor().stopCellEditing()) {
                return;
            }
        }

        if (modelo.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não existem alunos nesta turma para registrar.",
                    "Lista vazia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Sala salaSelecionada =
                (Sala) comboSala.getSelectedItem();

        if (salaSelecionada == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma turma antes de salvar.",
                    "Turma obrigatória",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Deseja salvar a frequência de "
                        + salaSelecionada
                        + " para hoje?",
                "Confirmar frequência",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        FrequenciaDao frequenciaDao = new FrequenciaDao();

        Date data = Date.valueOf(LocalDate.now());

        int salvos = 0;
        int erros = 0;

        btnSalvar.setEnabled(false);

        try {

            for (int i = 0; i < modelo.getRowCount(); i++) {

                int alunoId = Integer.parseInt(
                        modelo.getValueAt(i, 0).toString()
                );

                String escolha = modelo.getValueAt(i, 2).toString();

                boolean presente = escolha.equals("Presente");

                Frequencia frequencia = new Frequencia();

                frequencia.setAlunoId(alunoId);
                frequencia.setData(data);
                frequencia.setPresente(presente);

                boolean sucesso = frequenciaDao.salvar(frequencia);

                if (sucesso) {
                    salvos++;
                } else {
                    erros++;
                }
            }

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ocorreu um erro durante o registro da frequência.\n"
                            + "Confira a conexão com o banco de dados.",
                    "Erro ao salvar",
                    JOptionPane.ERROR_MESSAGE
            );

            return;

        } finally {
            btnSalvar.setEnabled(true);
        }

        String mensagem =
                "Resultado do registro:\n\n"
                        + "Registros salvos: " + salvos + "\n"
                        + "Registros com falha: " + erros;

        JOptionPane.showMessageDialog(
                this,
                mensagem,
                erros == 0
                        ? "Frequência registrada"
                        : "Registro parcialmente concluído",
                erros == 0
                        ? JOptionPane.INFORMATION_MESSAGE
                        : JOptionPane.WARNING_MESSAGE
        );
    }
}

