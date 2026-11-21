package Interface;

import DAO.AlunoDao;
import Escola.Aluno;
import Servico.FotoServico;
import com.formdev.flatlaf.FlatClientProperties;
import testeserial.LeitorRfid;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CadastroCartaoRfid extends JFrame {

    private final Color AZUL_ESCURO   = new Color(20, 43, 74);
    private final Color AZUL          = new Color(37, 99, 235);
    private final Color AZUL_CLARO    = new Color(239, 246, 255);
    private final Color AZUL_BORDA    = new Color(191, 219, 254);
    private final Color TEXTO         = new Color(23, 43, 77);
    private final Color CINZA         = new Color(100, 116, 139);
    private final Color CINZA_CLARO   = new Color(148, 163, 184);
    private final Color BORDA         = new Color(226, 232, 240);
    private final Color FUNDO         = new Color(248, 250, 253);
    private final Color VERDE         = new Color(22, 163, 74);
    private final Color VERDE_BG      = new Color(240, 253, 244);
    private final Color VERDE_BORDA   = new Color(187, 247, 208);
    private final Color VERMELHO      = new Color(185, 28, 28);
    private final Color VERMELHO_BG   = new Color(254, 242, 242);
    private final Color VERMELHO_BORDA= new Color(254, 202, 202);
    private final Color AMARELO_BG    = new Color(254, 252, 232);
    private final Color AMARELO_TXT   = new Color(161, 98, 7);
    private final Color AMARELO_BORDA = new Color(254, 240, 138);

    private final AlunoDao alunoDao = new AlunoDao();
    private LeitorRfid leitorRfid;
    private volatile boolean telaFechada = false;
    private boolean inicializando = true;
    private List<Aluno> listaAlunosEmMemoria = new ArrayList<>();

    private JComboBox<Aluno> comboAlunos;
    private JLabel labelTurmaInfo;
    private JLabel labelFotoPreview;
    private JTextField campoUid;
    private JLabel labelStatusUid;
    private JLabel labelStatusLeitor;
    private File fotoArquivoSelecionado = null;

    private DefaultTableModel modeloTabela;
    private JTable tabela;
    private JTextField campoBusca;
    private JComboBox<String> comboFiltro;
    private JLabel labelContador;

    private static final int FOTO_LARGURA = 190;
    private static final int FOTO_ALTURA  = 220;

    public CadastroCartaoRfid() {
        this(null);
    }

    public CadastroCartaoRfid(Aluno alunoPreSelecionado) {
        setTitle("Controle de Faltas | Cadastro de Cartões RFID e Fotos");
        setSize(1150, 750);
        setMinimumSize(new Dimension(960, 620));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        Configuracoes.aplicarIcone(this);

        JPanel principal = new JPanel(new BorderLayout(0, 0));
        principal.setBackground(FUNDO);

        principal.add(criarCabecalho(), BorderLayout.NORTH);
        principal.add(criarCorpo(),     BorderLayout.CENTER);

        setContentPane(principal);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                fecharTela();
            }
        });

        try {
            inicializando = true;
            recarregarAlunosDoBanco();
            preencherComboAlunos(alunoPreSelecionado);
            atualizarTabelaVisual();
            inicializando = false;

            if (comboAlunos.getItemCount() > 0) {
                aoSelecionarAlunoNoCombo();
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar dados: " + e.getMessage());
            e.printStackTrace();
        }

        iniciarLeitorRfidAssincrono();
    }

    private void fecharTela() {
        if (!telaFechada) {
            telaFechada = true;
            if (leitorRfid != null) {
                leitorRfid.parar();
            }
        }
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout(15, 0));
        cabecalho.setBackground(AZUL_ESCURO);
        cabecalho.setBorder(new EmptyBorder(18, 28, 18, 28));

        JPanel esquerda = new JPanel();
        esquerda.setLayout(new BoxLayout(esquerda, BoxLayout.Y_AXIS));
        esquerda.setOpaque(false);

        JLabel titulo = new JLabel("💳  Cadastro e Associação de Cartões RFID");
        titulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Aproxime o cartão do leitor ou digite o código UID, escolha o aluno e vincule a foto.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(180, 197, 220));

        esquerda.add(titulo);
        esquerda.add(Box.createVerticalStrut(4));
        esquerda.add(subtitulo);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        direita.setOpaque(false);

        labelStatusLeitor = new JLabel("🟡 Conectando ao leitor...");
        labelStatusLeitor.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        labelStatusLeitor.setForeground(Color.WHITE);
        labelStatusLeitor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        labelContador = new JLabel("0 alunos");
        labelContador.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelContador.setForeground(new Color(148, 196, 255));
        labelContador.setBorder(new EmptyBorder(0, 10, 0, 0));

        direita.add(labelStatusLeitor);
        direita.add(labelContador);

        cabecalho.add(esquerda, BorderLayout.CENTER);
        cabecalho.add(direita,  BorderLayout.EAST);
        return cabecalho;
    }

    private JPanel criarCorpo() {
        JPanel corpo = new JPanel(new GridBagLayout());
        corpo.setBackground(FUNDO);
        corpo.setBorder(new EmptyBorder(18, 24, 18, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 16);
        gbc.weightx = 0.40;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        corpo.add(criarPainelFormulario(), gbc);

        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 0.60;
        gbc.gridx = 1;
        corpo.add(criarPainelTabela(), gbc);
        return corpo;
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new BorderLayout(0, 14));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDA), new EmptyBorder(18, 20, 18, 20)));
        painel.putClientProperty(FlatClientProperties.STYLE, "arc:14");

        JLabel tituloForm = new JLabel("📝 Dados da Associação");
        tituloForm.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        tituloForm.setForeground(TEXTO);
        painel.add(tituloForm, BorderLayout.NORTH);

        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setOpaque(false);

        JLabel lblAluno = new JLabel("Aluno a ser associado:");
        lblAluno.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAluno.setForeground(TEXTO);
        lblAluno.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboAlunos = new JComboBox<>();
        comboAlunos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboAlunos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        comboAlunos.setPreferredSize(new Dimension(360, 38));
        comboAlunos.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboAlunos.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:1");
        comboAlunos.addActionListener(e -> {
            if (!inicializando) aoSelecionarAlunoNoCombo();
        });

        labelTurmaInfo = new JLabel("Turma: —");
        labelTurmaInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelTurmaInfo.setForeground(CINZA);
        labelTurmaInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        conteudo.add(lblAluno);
        conteudo.add(Box.createVerticalStrut(6));
        conteudo.add(comboAlunos);
        conteudo.add(Box.createVerticalStrut(4));
        conteudo.add(labelTurmaInfo);
        conteudo.add(Box.createVerticalStrut(12));

        JLabel lblFoto = new JLabel("Foto do Aluno:");
        lblFoto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFoto.setForeground(TEXTO);
        lblFoto.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel painelFotoBox = new JPanel(new BorderLayout(0, 8));
        painelFotoBox.setOpaque(false);
        painelFotoBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        labelFotoPreview = new JLabel();
        labelFotoPreview.setPreferredSize(new Dimension(FOTO_LARGURA, FOTO_ALTURA));
        labelFotoPreview.setMaximumSize(new Dimension(FOTO_LARGURA, FOTO_ALTURA));
        labelFotoPreview.setHorizontalAlignment(SwingConstants.CENTER);
        labelFotoPreview.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDA, 1, true), new EmptyBorder(2, 2, 2, 2)));
        labelFotoPreview.setOpaque(true);
        labelFotoPreview.setBackground(new Color(248, 250, 252));

        JPanel painelBotoesFoto = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        painelBotoesFoto.setOpaque(false);

        JButton btnEscolherFoto = new JButton("📁 Escolher Foto...");
        btnEscolherFoto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEscolherFoto.addActionListener(e -> escolherFoto());

        JButton btnRemoverFoto = new JButton("🗑️ Remover");
        btnRemoverFoto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRemoverFoto.addActionListener(e -> removerFotoAtual());

        painelBotoesFoto.add(btnEscolherFoto);
        painelBotoesFoto.add(btnRemoverFoto);

        JPanel centroFoto = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centroFoto.setOpaque(false);
        centroFoto.add(labelFotoPreview);

        painelFotoBox.add(centroFoto, BorderLayout.CENTER);
        painelFotoBox.add(painelBotoesFoto, BorderLayout.SOUTH);

        conteudo.add(lblFoto);
        conteudo.add(Box.createVerticalStrut(6));
        conteudo.add(painelFotoBox);
        conteudo.add(Box.createVerticalStrut(12));

        JLabel lblUid = new JLabel("Código do Cartão RFID (UID):");
        lblUid.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUid.setForeground(TEXTO);
        lblUid.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoUid = new JTextField();
        campoUid.setFont(new Font("Segoe UI", Font.BOLD, 15));
        campoUid.putClientProperty("JTextField.placeholderText", "Aproxime do leitor ou digite o UID…");
        campoUid.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:1");
        campoUid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        labelStatusUid = new JLabel("Aproxime o cartão do leitor ou digite o código.");
        labelStatusUid.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelStatusUid.setForeground(CINZA);

        conteudo.add(lblUid);
        conteudo.add(Box.createVerticalStrut(6));
        conteudo.add(campoUid);
        conteudo.add(Box.createVerticalStrut(4));
        conteudo.add(labelStatusUid);
        conteudo.add(Box.createVerticalGlue());

        JPanel painelBotoesAcao = new JPanel(new GridLayout(1, 2, 10, 0));
        painelBotoesAcao.setOpaque(false);
        painelBotoesAcao.setPreferredSize(new Dimension(360, 44));

        JButton btnSalvar = new JButton("💾 Salvar Associação");
        btnSalvar.setBackground(AZUL);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.addActionListener(e -> salvarAssociacao());

        JButton btnDesvincular = new JButton("❌ Desvincular Cartão");
        btnDesvincular.addActionListener(e -> desvincularCartaoAlunoAtual());

        painelBotoesAcao.add(btnSalvar);
        painelBotoesAcao.add(btnDesvincular);

        painel.add(conteudo, BorderLayout.CENTER);
        painel.add(painelBotoesAcao, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout(0, 12));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDA), new EmptyBorder(18, 20, 18, 20)));
        painel.putClientProperty(FlatClientProperties.STYLE, "arc:14");

        campoBusca = new JTextField();
        campoBusca.putClientProperty("JTextField.placeholderText", "🔍 Pesquisar por aluno, turma ou UID…");
        campoBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrarTabela(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrarTabela(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarTabela(); }
        });

        String[] colunas = {"ID", "Foto", "Nome do Aluno", "Turma / Sala", "Cartão RFID (UID)", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(modeloTabela);
        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);
        tabela.getColumnModel().getColumn(1).setCellRenderer(new FotoStatusRenderer());
        tabela.getColumnModel().getColumn(5).setCellRenderer(new BadgeStatusRenderer());
        tabela.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int linha = tabela.getSelectedRow();
                if (linha >= 0) carregarAlunoDaTabelaNoFormulario(tabela.convertRowIndexToModel(linha));
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        painel.add(campoBusca, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    private void iniciarLeitorRfidAssincrono() {
        new Thread(() -> {
            try {
                if (leitorRfid != null) leitorRfid.parar();
                leitorRfid = new LeitorRfid();
                leitorRfid.iniciar(
                    uid -> SwingUtilities.invokeLater(() -> aoReceberUidRfid(uid)),
                    err -> SwingUtilities.invokeLater(() -> labelStatusLeitor.setText("🔴 Leitor erro: " + err))
                );
                SwingUtilities.invokeLater(() -> labelStatusLeitor.setText("🟢 Leitor Ativo"));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> labelStatusLeitor.setText("🔴 Erro no hardware"));
            }
        }).start();
    }

    private void aoReceberUidRfid(String uidLido) {
        if (telaFechada) return;
        String uid = uidLido.trim().toUpperCase();
        campoUid.setText(uid);
        Aluno dono = alunoDao.buscarPorUid(uid);
        if (dono != null) {
            labelStatusUid.setText("⚠️ Cartão já vinculado a: " + dono.getNome());
            labelStatusUid.setForeground(AMARELO_TXT);
        } else {
            labelStatusUid.setText("✨ Cartão novo detectado: " + uid);
            labelStatusUid.setForeground(VERDE);
        }
    }

    private void recarregarAlunosDoBanco() {
        listaAlunosEmMemoria = alunoDao.listarTodos();
    }

    private void preencherComboAlunos(Aluno alunoParaSelecionar) {
        comboAlunos.removeAllItems();
        for (Aluno a : listaAlunosEmMemoria) comboAlunos.addItem(a);
        if (alunoParaSelecionar != null) selecionarAlunoPorId(alunoParaSelecionar.getId());
    }

    private void selecionarAlunoPorId(int id) {
        for (int i = 0; i < comboAlunos.getItemCount(); i++) {
            if (comboAlunos.getItemAt(i).getId() == id) {
                comboAlunos.setSelectedIndex(i);
                break;
            }
        }
    }

    private Aluno getAlunoSelecionadoNoCombo() {
        return (Aluno) comboAlunos.getSelectedItem();
    }

    private void aoSelecionarAlunoNoCombo() {
        Aluno aluno = getAlunoSelecionadoNoCombo();
        if (aluno == null) return;
        labelTurmaInfo.setText("Turma: " + (aluno.getSala() != null ? aluno.getSala() : "—"));
        String uid = aluno.getUid_rfid();
        campoUid.setText(uid != null ? uid : "");
        atualizarPreviewFoto(aluno, uid);
    }

    private void atualizarPreviewFoto(Aluno aluno, String uid) {
        ImageIcon foto = FotoServico.obterFotoAluno(aluno, uid, FOTO_LARGURA, FOTO_ALTURA);
        labelFotoPreview.setIcon(foto != null ? foto : FotoServico.criarAvatarPlaceholder(aluno.getNome(), FOTO_LARGURA, FOTO_ALTURA));
    }

    private void filtrarTabela() {
        modeloTabela.setRowCount(0);
        for (Aluno a : listaAlunosEmMemoria) {
            modeloTabela.addRow(new Object[]{
                    a.getId(),
                    FotoServico.temFoto(a, a.getUid_rfid()) ? "📷 Sim" : "—",
                    a.getNome(),
                    a.getSala(),
                    a.getUid_rfid() != null ? a.getUid_rfid() : "—",
                    a.getUid_rfid() != null ? "Vinculado" : "Pendente"
            });
        }
    }

    private void carregarAlunoDaTabelaNoFormulario(int modelRow) {
        int id = (int) modeloTabela.getValueAt(modelRow, 0);
        selecionarAlunoPorId(id);
    }

    private void escolherFoto() {
        JFileChooser jfc = new JFileChooser();
        if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fotoArquivoSelecionado = jfc.getSelectedFile();
            aoSelecionarAlunoNoCombo();
        }
    }

    private void removerFotoAtual() {
        Aluno a = getAlunoSelecionadoNoCombo();
        if (a != null) {
            FotoServico.removerFotoAluno(a, a.getUid_rfid());
            aoSelecionarAlunoNoCombo();
        }
    }

    private void salvarAssociacao() {
        Aluno a = getAlunoSelecionadoNoCombo();
        if (a == null) return;
        String uid = campoUid.getText().trim().toUpperCase();
        alunoDao.vincularCartao(a.getId(), uid.isEmpty() ? null : uid);
        if (fotoArquivoSelecionado != null) {
            FotoServico.salvarFotoAluno(fotoArquivoSelecionado, a, uid);
            fotoArquivoSelecionado = null;
        }
        recarregarAlunosDoBanco();
        filtrarTabela();
        JOptionPane.showMessageDialog(this, "Salvo com sucesso!");
    }

    private void desvincularCartaoAlunoAtual() {
        Aluno a = getAlunoSelecionadoNoCombo();
        if (a != null) {
            alunoDao.desvincularCartao(a.getId());
            aoSelecionarAlunoNoCombo();
            filtrarTabela();
        }
    }

    private void atualizarTabelaVisual() { filtrarTabela(); }

    private class BadgeStatusRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            return new JLabel(v.toString(), SwingConstants.CENTER);
        }
    }

    private class FotoStatusRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            return new JLabel(v.toString(), SwingConstants.CENTER);
        }
    }
}
