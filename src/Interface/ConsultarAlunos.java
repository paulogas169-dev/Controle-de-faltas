package Interface;

import DAO.AlunoDao;
import DAO.SalaDao;
import Escola.Aluno;
import Escola.Sala;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import com.formdev.flatlaf.FlatClientProperties;

public class ConsultarAlunos extends JFrame {

    private JTable tabela;
    private DefaultTableModel modelo;
    private JTextField buscarAluno;

    private final Color AZUL = new Color(37, 99, 235);
    private final Color AZUL_ESCURO = new Color(20, 43, 74);
    private final Color FUNDO = Configuracoes.getCorFundo();
    private final Color TEXTO = Configuracoes.getCorTexto();
    private final Color CINZA = Configuracoes.getCorSubtexto();
    private final Color BRANCO = Configuracoes.getCorPainel();
    private final Color BORDA = Configuracoes.getCorBorda();

    public ConsultarAlunos() {

        setTitle("Controle de Faltas | Consultar Alunos");
        setSize(1050, 700);
        setMinimumSize(new Dimension(850, 580));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(FUNDO);

        // CABEÇALHO
        JPanel cabecalho = criarCabecalho();

        // ÁREA DA TABELA
        JPanel areaCentral = new JPanel(new BorderLayout(0, 18));
        areaCentral.setBackground(FUNDO);
        areaCentral.setBorder(
                BorderFactory.createEmptyBorder(25, 30, 20, 30)
        );

        JPanel painelPesquisa = criarPainelPesquisa();

        // MODELO DA TABELA
        modelo = new DefaultTableModel(
                new Object[]{"ID", "Nome do aluno", "Turma / Sala"},
                0
        ) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };

        tabela = new JTable(modelo);
        estilizarTabela();

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

        // BOTÕES INFERIORES
        JPanel painelAcoes = criarPainelAcoes();

        areaCentral.add(painelPesquisa, BorderLayout.NORTH);
        areaCentral.add(painelTabela, BorderLayout.CENTER);
        areaCentral.add(painelAcoes, BorderLayout.SOUTH);

        principal.add(cabecalho, BorderLayout.NORTH);
        principal.add(areaCentral, BorderLayout.CENTER);

        setContentPane(principal);

        carregarAlunos();

        setVisible(true);
    }

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

        JLabel titulo = new JLabel("Alunos cadastrados");
        titulo.setFont(
                new Font("Segoe UI", Font.BOLD, 25)
        );
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel(
                "Consulte, pesquise e gerencie os alunos da escola."
        );
        subtitulo.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );
        subtitulo.setForeground(CINZA);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(7));
        textos.add(subtitulo);

        JLabel etiqueta = new JLabel("  GESTÃO DE ALUNOS  ");
        etiqueta.setFont(
                new Font("Segoe UI", Font.BOLD, 11)
        );
        etiqueta.setForeground(Configuracoes.getCorTextoDestaque());
        etiqueta.setOpaque(true);
        etiqueta.setBackground(Configuracoes.getCorDestaque());
        etiqueta.setBorder(
                BorderFactory.createEmptyBorder(9, 5, 9, 5)
        );

        cabecalho.add(textos, BorderLayout.CENTER);
        cabecalho.add(etiqueta, BorderLayout.EAST);

        return cabecalho;
    }

    private JPanel criarPainelPesquisa() {

        JPanel painel = new JPanel(new BorderLayout(12, 0));
        painel.setBackground(FUNDO);

        JPanel textos = new JPanel();
        textos.setLayout(
                new BoxLayout(textos, BoxLayout.Y_AXIS)
        );
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Lista de alunos");
        titulo.setFont(
                new Font("Segoe UI", Font.BOLD, 17)
        );
        titulo.setForeground(TEXTO);

        JLabel descricao = new JLabel(
                "Pesquise pelo nome para encontrar um aluno."
        );
        descricao.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );
        descricao.setForeground(CINZA);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(descricao);

        JPanel busca = new JPanel(new BorderLayout(8, 0));
        busca.setOpaque(false);

        buscarAluno = new JTextField();
        buscarAluno.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );
        buscarAluno.putClientProperty(
                "JTextField.placeholderText",
                "Digite o nome do aluno..."
        );
        buscarAluno.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:12;focusWidth:1"
        );
        buscarAluno.setPreferredSize(
                new Dimension(250, 42)
        );

        JButton botaoBuscar = criarBotao(
                "Buscar", AZUL, BRANCO
        );

        JButton botaoAtualizar = criarBotao(
                "Atualizar", Configuracoes.getCorDestaque(), Configuracoes.getCorTextoDestaque()
        );

        botaoBuscar.addActionListener(e -> buscarAlunos());

        buscarAluno.addActionListener(e -> buscarAlunos());

        botaoAtualizar.addActionListener(e -> {
            buscarAluno.setText("");
            carregarAlunos();
        });

        busca.add(buscarAluno, BorderLayout.CENTER);
        busca.add(botaoBuscar, BorderLayout.EAST);

        JPanel ladoDireito = new JPanel(new BorderLayout(0, 7));
        ladoDireito.setOpaque(false);
        ladoDireito.add(busca, BorderLayout.CENTER);
        ladoDireito.add(botaoAtualizar, BorderLayout.EAST);

        painel.add(textos, BorderLayout.WEST);
        painel.add(ladoDireito, BorderLayout.EAST);

        return painel;
    }

    private void estilizarTabela() {

        tabela.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );
        tabela.setForeground(TEXTO);
        tabela.setBackground(BRANCO);
        tabela.setRowHeight(39);
        tabela.setShowVerticalLines(false);
        tabela.setShowHorizontalLines(true);
        tabela.setGridColor(Configuracoes.isModoEscuro() ? new Color(45, 45, 52) : new Color(241, 245, 249));
        tabela.setSelectionBackground(Configuracoes.isModoEscuro() ? new Color(30, 58, 138) : new Color(219, 234, 254));
        tabela.setSelectionForeground(Configuracoes.isModoEscuro() ? Color.WHITE : TEXTO);
        tabela.setIntercellSpacing(new Dimension(0, 1));
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        tabela.setAutoCreateRowSorter(true);

        tabela.setBorder(
                BorderFactory.createEmptyBorder(3, 5, 3, 5)
        );

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );
        cabecalho.setForeground(TEXTO);
        cabecalho.setBackground(Configuracoes.isModoEscuro() ? new Color(35, 35, 40) : new Color(241, 245, 249));
        cabecalho.setPreferredSize(new Dimension(0, 43));
        cabecalho.setReorderingAllowed(false);

        tabela.getColumnModel().getColumn(0)
                .setPreferredWidth(70);
        tabela.getColumnModel().getColumn(1)
                .setPreferredWidth(430);
        tabela.getColumnModel().getColumn(2)
                .setPreferredWidth(220);
    }

    private JPanel criarPainelAcoes() {

        JPanel painel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        painel.setBackground(FUNDO);

        JButton botaoEditar = criarBotao(
                "Editar aluno",
                Configuracoes.getCorDestaque(),
                Configuracoes.getCorTextoDestaque()
        );

        JButton botaoExcluir = criarBotao(
                "Excluir aluno",
                Configuracoes.isModoEscuro() ? new Color(69, 10, 10) : new Color(254, 242, 242),
                new Color(220, 38, 38)
        );

        botaoEditar.addActionListener(
                e -> editarAlunoSelecionado()
        );

        botaoExcluir.addActionListener(
                e -> deletarAlunoSelecionado()
        );

        painel.add(botaoEditar);
        painel.add(botaoExcluir);

        return painel;
    }

    private JButton criarBotao(
            String texto,
            Color fundo,
            Color corTexto
    ) {

        JButton botao = new JButton(texto);

        botao.setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );
        botao.setBackground(fundo);
        botao.setForeground(corTexto);
        botao.setFocusPainted(false);
        botao.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
        botao.setPreferredSize(
                new Dimension(125, 40)
        );

        botao.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:11;focusWidth:0;borderWidth:0"
        );

        return botao;
    }

    // CARREGAR TODOS OS ALUNOS

    private void carregarAlunos() {

        AlunoDao alunoDao = new AlunoDao();
        List<Aluno> alunos = alunoDao.listarTodos();

        preencherTabela(alunos);
    }

    // PREENCHER A TABELA SEM DUPLICAR REGISTROS

    private void preencherTabela(List<Aluno> alunos) {

        modelo.setRowCount(0);

        if (alunos == null) {
            return;
        }

        for (Aluno aluno : alunos) {
            modelo.addRow(new Object[]{
                    aluno.getId(),
                    aluno.getNome(),
                    aluno.getSala()
            });
        }
    }

    // PESQUISAR ALUNOS

    public void buscarAlunos() {

        String nome = buscarAluno.getText().trim();

        AlunoDao alunoDao = new AlunoDao();
        List<Aluno> alunos = alunoDao.buscarPorNome(nome);

        preencherTabela(alunos);
    }

    // EXCLUIR ALUNO SELECIONADO

    private void deletarAlunoSelecionado() {

        int linhaSelecionada = tabela.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um aluno na tabela primeiro.",
                    "Nenhum aluno selecionado",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int linhaModelo = tabela.convertRowIndexToModel(
                linhaSelecionada
        );

        int id = ((Number) modelo.getValueAt(
                linhaModelo, 0
        )).intValue();

        String nome = String.valueOf(
                modelo.getValueAt(linhaModelo, 1)
        );

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir o aluno "
                        + nome + "?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        AlunoDao alunoDao = new AlunoDao();
        alunoDao.deletar(id);

        atualizarPesquisaAtual();

        JOptionPane.showMessageDialog(
                this,
                "Solicitação de exclusão realizada.",
                "Gerenciamento de alunos",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // EDITAR ALUNO SELECIONADO

    private void editarAlunoSelecionado() {

        int linhaSelecionada = tabela.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um aluno na tabela primeiro.",
                    "Nenhum aluno selecionado",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int linhaModelo = tabela.convertRowIndexToModel(
                linhaSelecionada
        );

        int id = ((Number) modelo.getValueAt(
                linhaModelo, 0
        )).intValue();

        String nomeAtual = String.valueOf(
                modelo.getValueAt(linhaModelo, 1)
        );

        String salaAtual = String.valueOf(
                modelo.getValueAt(linhaModelo, 2)
        );

        JTextField campoNome = new JTextField(nomeAtual);
        campoNome.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );
        campoNome.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:10;focusWidth:1"
        );

        JComboBox<Sala> comboSala = new JComboBox<>();

        SalaDao salaDao = new SalaDao();
        List<Sala> salas = salaDao.listar();

        if (salas != null) {
            for (Sala sala : salas) {
                comboSala.addItem(sala);

                if (String.valueOf(sala).equals(salaAtual)) {
                    comboSala.setSelectedItem(sala);
                }
            }
        }

        if (comboSala.getItemCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não há turmas disponíveis para edição.",
                    "Turmas indisponíveis",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        comboSala.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );

        JPanel painel = new JPanel(new GridLayout(0, 1, 0, 9));
        painel.setPreferredSize(new Dimension(340, 125));

        painel.add(new JLabel("Nome completo:"));
        painel.add(campoNome);
        painel.add(new JLabel("Turma / Sala:"));
        painel.add(comboSala);

        int resultado = JOptionPane.showConfirmDialog(
                this,
                painel,
                "Editar aluno",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }

        String novoNome = campoNome.getText().trim();
        Sala salaSelecionada =
                (Sala) comboSala.getSelectedItem();

        if (novoNome.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "O nome do aluno não pode ficar vazio.",
                    "Campo obrigatório",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (salaSelecionada == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma turma.",
                    "Campo obrigatório",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setNome(novoNome);
        aluno.setSala_id(salaSelecionada.getId());

        AlunoDao alunoDao = new AlunoDao();
        alunoDao.atualizar(aluno);

        atualizarPesquisaAtual();

        JOptionPane.showMessageDialog(
                this,
                "Solicitação de atualização realizada.",
                "Gerenciamento de alunos",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // MANTER A PESQUISA ATUAL APÓS EDITAR OU EXCLUIR

    private void atualizarPesquisaAtual() {

        String pesquisa = buscarAluno.getText().trim();

        if (pesquisa.isEmpty()) {
            carregarAlunos();
        } else {
            buscarAlunos();
        }
    }
}

