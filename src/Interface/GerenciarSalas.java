package Interface;

import DAO.SalaDao;
import Escola.Sala;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

public class GerenciarSalas extends JFrame {

    private final Color AZUL_ESCURO  = new Color(20, 43, 74);
    private final Color AZUL         = new Color(37, 99, 235);
    private final Color TEXTO        = new Color(23, 43, 77);
    private final Color CINZA        = new Color(100, 116, 139);
    private final Color BORDA        = new Color(226, 232, 240);
    private final Color FUNDO        = new Color(248, 250, 253);
    private final Color VERMELHO     = new Color(185, 28, 28);
    private final Color VERMELHO_BG  = new Color(254, 242, 242);
    private final Color VERMELHO_BORDA = new Color(254, 202, 202);
    private final Color AZUL_BG      = new Color(239, 246, 255);
    private final Color AZUL_BORDA   = new Color(191, 219, 254);

    private final SalaDao salaDao = new SalaDao();

    private DefaultTableModel modeloTabela;
    private JTable tabela;
    private JTextField campoBusca;
    private JTextField campoNomeSala;
    private JLabel labelContador;
    private AcoesCellRenderer acoesRenderer;

    public GerenciarSalas() {
        setTitle("Gerenciar Salas");
        setSize(840, 640);
        setMinimumSize(new Dimension(740, 520));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout(0, 0));
        principal.setBackground(FUNDO);

        principal.add(criarCabecalho(), BorderLayout.NORTH);
        principal.add(criarCorpo(),    BorderLayout.CENTER);

        setContentPane(principal);
        carregarSalas();
        setVisible(true);
    }

    // ─── CABEÇALHO ──────────────────────────────────────────────────────────

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(AZUL_ESCURO);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        JPanel esquerda = new JPanel();
        esquerda.setLayout(new BoxLayout(esquerda, BoxLayout.Y_AXIS));
        esquerda.setOpaque(false);

        JLabel titulo = new JLabel("🏫  Gerenciar Salas");
        titulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Cadastre, consulte, edite e exclua salas do sistema.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(180, 197, 220));

        esquerda.add(titulo);
        esquerda.add(Box.createVerticalStrut(5));
        esquerda.add(subtitulo);

        labelContador = new JLabel("0 salas");
        labelContador.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelContador.setForeground(new Color(148, 196, 255));

        cabecalho.add(esquerda,       BorderLayout.CENTER);
        cabecalho.add(labelContador,  BorderLayout.EAST);

        return cabecalho;
    }

    // ─── CORPO ───────────────────────────────────────────────────────────────

    private JPanel criarCorpo() {
        JPanel corpo = new JPanel(new BorderLayout(0, 16));
        corpo.setBackground(FUNDO);
        corpo.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        corpo.add(criarPainelCadastro(), BorderLayout.NORTH);
        corpo.add(criarPainelTabela(),   BorderLayout.CENTER);
        corpo.add(criarPainelAcoesInferior(), BorderLayout.SOUTH);

        return corpo;
    }

    // ─── PAINEL DE CADASTRO ───────────────────────────────────────────────────

    private JPanel criarPainelCadastro() {
        JPanel painel = new JPanel(new BorderLayout(12, 0));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        painel.putClientProperty(FlatClientProperties.STYLE, "arc:14");

        JLabel label = new JLabel("Nova sala:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXTO);
        label.setPreferredSize(new Dimension(80, 42));

        campoNomeSala = new JTextField();
        campoNomeSala.putClientProperty("JTextField.placeholderText", "Ex: Sala 101, Lab. Informática…");
        campoNomeSala.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:1");
        campoNomeSala.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoNomeSala.setPreferredSize(new Dimension(0, 42));

        JButton btnCadastrar = new JButton("+ Cadastrar");
        btnCadastrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCadastrar.setBackground(AZUL);
        btnCadastrar.setForeground(Color.WHITE);
        btnCadastrar.setFocusPainted(false);
        btnCadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCadastrar.setPreferredSize(new Dimension(130, 42));
        btnCadastrar.putClientProperty(FlatClientProperties.STYLE,
                "arc:10;focusWidth:0;borderWidth:0;hoverBackground:#1D4ED8;pressedBackground:#1E40AF");
        btnCadastrar.addActionListener(e -> cadastrarSala());

        // Permite cadastrar com Enter
        campoNomeSala.addActionListener(e -> cadastrarSala());

        painel.add(label,         BorderLayout.WEST);
        painel.add(campoNomeSala, BorderLayout.CENTER);
        painel.add(btnCadastrar,  BorderLayout.EAST);

        return painel;
    }

    // ─── PAINEL DA TABELA ─────────────────────────────────────────────────────

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout(0, 12));
        painel.setOpaque(false);

        // Barra de busca
        JPanel barraBusca = new JPanel(new BorderLayout(10, 0));
        barraBusca.setOpaque(false);

        JLabel iconeBusca = new JLabel("🔍");
        iconeBusca.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        campoBusca = new JTextField();
        campoBusca.putClientProperty("JTextField.placeholderText", "Pesquisar sala pelo nome…");
        campoBusca.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:1");
        campoBusca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoBusca.setPreferredSize(new Dimension(0, 38));
        campoBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrarTabela(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrarTabela(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarTabela(); }
        });

        barraBusca.add(iconeBusca,  BorderLayout.WEST);
        barraBusca.add(campoBusca,  BorderLayout.CENTER);

        // Tabela
        String[] colunas = {"ID", "Nome da Sala", "Ações"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(44);
        tabela.setShowVerticalLines(false);
        tabela.setGridColor(BORDA);
        tabela.setSelectionBackground(new Color(239, 246, 255));
        tabela.setSelectionForeground(TEXTO);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.putClientProperty(FlatClientProperties.STYLE, "selectionArc:8");

        // Coluna ID oculta
        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);
        tabela.getColumnModel().getColumn(0).setWidth(0);

        // Coluna nome
        tabela.getColumnModel().getColumn(1).setPreferredWidth(520);

        // Coluna ações
        tabela.getColumnModel().getColumn(2).setPreferredWidth(190);
        tabela.getColumnModel().getColumn(2).setMaxWidth(220);

        // Header
        JTableHeader header = tabela.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(CINZA);
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);

        // Renderer de botões na coluna Ações
        acoesRenderer = new AcoesCellRenderer();
        tabela.getColumnModel().getColumn(2).setCellRenderer(acoesRenderer);

        // Clique direto nos botões da coluna "Ações" ou duplo clique na linha
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = tabela.rowAtPoint(e.getPoint());
                if (row >= 0 && SwingUtilities.isRightMouseButton(e)) {
                    tabela.setRowSelectionInterval(row, row);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int viewRow = tabela.rowAtPoint(e.getPoint());
                int viewCol = tabela.columnAtPoint(e.getPoint());
                if (viewRow < 0) return;

                int modelRow = tabela.convertRowIndexToModel(viewRow);

                // Duplo clique em qualquer coluna: edita a sala
                if (e.getClickCount() == 2 && viewCol != 2) {
                    editarSala(modelRow);
                    return;
                }

                // Clique na coluna de Ações
                if (viewCol == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    Rectangle cellRect = tabela.getCellRect(viewRow, viewCol, false);
                    int relX = e.getX() - cellRect.x;
                    int relY = e.getY() - cellRect.y;

                    acoesRenderer.setSize(cellRect.width, cellRect.height);
                    acoesRenderer.doLayout();

                    Rectangle rEditar = acoesRenderer.getBtnEditar().getBounds();
                    Rectangle rExcluir = acoesRenderer.getBtnExcluir().getBounds();

                    if (rEditar.contains(relX, relY)) {
                        editarSala(modelRow);
                    } else if (rExcluir.contains(relX, relY)) {
                        excluirSala(modelRow);
                    }
                }
            }
        });

        // Efeito de cursor de mão ao passar o mouse sobre os botões na tabela
        tabela.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int viewRow = tabela.rowAtPoint(e.getPoint());
                int viewCol = tabela.columnAtPoint(e.getPoint());
                if (viewRow >= 0 && viewCol == 2) {
                    Rectangle cellRect = tabela.getCellRect(viewRow, viewCol, false);
                    int relX = e.getX() - cellRect.x;
                    int relY = e.getY() - cellRect.y;

                    acoesRenderer.setSize(cellRect.width, cellRect.height);
                    acoesRenderer.doLayout();

                    if (acoesRenderer.getBtnEditar().getBounds().contains(relX, relY) ||
                        acoesRenderer.getBtnExcluir().getBounds().contains(relX, relY)) {
                        tabela.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        return;
                    }
                }
                tabela.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });

        // Menu de contexto com botão direito
        JPopupMenu popup = new JPopupMenu();
        JMenuItem itemEditar = new JMenuItem("✏️ Editar sala");
        itemEditar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemEditar.addActionListener(e -> editarSalaSelecionada());

        JMenuItem itemExcluir = new JMenuItem("🗑️ Excluir sala");
        itemExcluir.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemExcluir.addActionListener(e -> excluirSalaSelecionada());

        popup.add(itemEditar);
        popup.add(itemExcluir);
        tabela.setComponentPopupMenu(popup);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(BORDA));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.putClientProperty(FlatClientProperties.STYLE, "arc:12");

        painel.add(barraBusca, BorderLayout.NORTH);
        painel.add(scroll,     BorderLayout.CENTER);

        return painel;
    }

    // ─── PAINEL DE AÇÕES INFERIOR ────────────────────────────────────────────

    private JPanel criarPainelAcoesInferior() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painel.setOpaque(false);

        JButton btnEditar = new JButton("✏️ Editar sala selecionada");
        btnEditar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEditar.setBackground(AZUL_BG);
        btnEditar.setForeground(AZUL);
        btnEditar.setFocusPainted(false);
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditar.setPreferredSize(new Dimension(190, 38));
        btnEditar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL_BORDA, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btnEditar.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:0");
        btnEditar.addActionListener(e -> editarSalaSelecionada());

        JButton btnExcluir = new JButton("🗑️ Excluir sala selecionada");
        btnExcluir.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExcluir.setBackground(VERMELHO_BG);
        btnExcluir.setForeground(VERMELHO);
        btnExcluir.setFocusPainted(false);
        btnExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExcluir.setPreferredSize(new Dimension(190, 38));
        btnExcluir.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VERMELHO_BORDA, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btnExcluir.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:0");
        btnExcluir.addActionListener(e -> excluirSalaSelecionada());

        painel.add(btnEditar);
        painel.add(btnExcluir);

        return painel;
    }

    // ─── LÓGICA DE DADOS ─────────────────────────────────────────────────────

    private void carregarSalas() {
        carregarSalas(campoBusca != null ? campoBusca.getText().trim() : null);
    }

    private void carregarSalas(String filtro) {
        modeloTabela.setRowCount(0);
        List<Sala> salas;

        if (filtro == null || filtro.isBlank()) {
            salas = salaDao.listar();
        } else {
            salas = salaDao.buscarPorNome(filtro);
        }

        for (Sala s : salas) {
            modeloTabela.addRow(new Object[]{s.getId(), s.getNome(), ""});
        }

        int total = salas.size();
        labelContador.setText(total + (total == 1 ? " sala" : " salas"));
    }

    private void filtrarTabela() {
        carregarSalas(campoBusca.getText().trim());
    }

    private void cadastrarSala() {
        String nome = campoNomeSala.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Informe o nome da sala antes de cadastrar.",
                    "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
            campoNomeSala.requestFocus();
            return;
        }

        Sala sala = new Sala();
        sala.setNome(nome);

        if (salaDao.cadastrar(sala)) {
            campoNomeSala.setText("");
            carregarSalas();
            JOptionPane.showMessageDialog(this,
                    "Sala \"" + nome + "\" cadastrada com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar sala. Verifique a conexão com o banco de dados.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarSalaSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma sala na tabela primeiro para editar.",
                    "Nenhuma sala selecionada",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        editarSala(tabela.convertRowIndexToModel(linha));
    }

    private void excluirSalaSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma sala na tabela primeiro para excluir.",
                    "Nenhuma sala selecionada",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        excluirSala(tabela.convertRowIndexToModel(linha));
    }

    private void editarSala(int modelRow) {
        if (modelRow < 0 || modelRow >= modeloTabela.getRowCount()) return;

        int id = (int) modeloTabela.getValueAt(modelRow, 0);
        String nomeAtual = (String) modeloTabela.getValueAt(modelRow, 1);

        JTextField campo = new JTextField(nomeAtual);
        campo.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:1");
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(340, 40));

        JPanel painelDialogo = new JPanel(new BorderLayout(0, 10));
        painelDialogo.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));

        JLabel info = new JLabel("Alterar o nome da sala:");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelDialogo.add(info, BorderLayout.NORTH);
        painelDialogo.add(campo, BorderLayout.CENTER);

        // Foca automaticamente no campo e seleciona o texto
        SwingUtilities.invokeLater(() -> {
            campo.requestFocusInWindow();
            campo.selectAll();
        });

        int resultado = JOptionPane.showConfirmDialog(
                this, painelDialogo,
                "✏️  Editar Sala",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultado == JOptionPane.OK_OPTION) {
            String novoNome = campo.getText().trim();
            if (novoNome.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "O nome da sala não pode ficar vazio.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (novoNome.equals(nomeAtual)) {
                return;
            }

            Sala sala = new Sala(id, novoNome);
            if (salaDao.atualizar(sala)) {
                carregarSalas(campoBusca != null ? campoBusca.getText().trim() : null);
                JOptionPane.showMessageDialog(this,
                        "Sala atualizada com sucesso para \"" + novoNome + "\"!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro ao atualizar sala. Verifique a conexão com o banco de dados.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void excluirSala(int modelRow) {
        if (modelRow < 0 || modelRow >= modeloTabela.getRowCount()) return;

        int id = (int) modeloTabela.getValueAt(modelRow, 0);
        String nome = (String) modeloTabela.getValueAt(modelRow, 1);

        // Verifica se existem alunos vinculados antes de tentar excluir
        int totalAlunos = salaDao.contarAlunos(id);
        if (totalAlunos > 0) {
            JOptionPane.showMessageDialog(this,
                    "Não é possível excluir a sala \"" + nome + "\" porque existem "
                            + totalAlunos + " aluno(s) vinculados a ela.\n\n"
                            + "Para excluir esta sala, primeiro transfira ou remova os alunos dela na tela de Consulta de Alunos.",
                    "⚠️ Sala possui alunos vinculados",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirma = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir a sala:\n\"" + nome + "\"?\n\nEsta ação não poderá ser desfeita.",
                "🗑️  Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirma == JOptionPane.YES_OPTION) {
            if (salaDao.excluir(id)) {
                carregarSalas(campoBusca != null ? campoBusca.getText().trim() : null);
                JOptionPane.showMessageDialog(this,
                        "Sala \"" + nome + "\" excluída com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro ao excluir sala. Verifique se existem dependências ou tente novamente.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ─── RENDERER DE BOTÕES NA TABELA ────────────────────────────────────────

    private class AcoesCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JButton btnEditar  = new JButton("✏️ Editar");
        private final JButton btnExcluir = new JButton("🗑️ Excluir");

        AcoesCellRenderer() {
            super(new FlowLayout(FlowLayout.CENTER, 8, 5));
            setOpaque(true);

            btnEditar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
            btnEditar.setBackground(AZUL_BG);
            btnEditar.setForeground(AZUL);
            btnEditar.setFocusPainted(false);
            btnEditar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AZUL_BORDA, 1, true),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnExcluir.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
            btnExcluir.setBackground(VERMELHO_BG);
            btnExcluir.setForeground(VERMELHO);
            btnExcluir.setFocusPainted(false);
            btnExcluir.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(VERMELHO_BORDA, 1, true),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            btnExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));

            add(btnEditar);
            add(btnExcluir);
        }

        public JButton getBtnEditar() {
            return btnEditar;
        }

        public JButton getBtnExcluir() {
            return btnExcluir;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {

            setBackground(isSelected ? new Color(239, 246, 255) : Color.WHITE);
            return this;
        }
    }
}
