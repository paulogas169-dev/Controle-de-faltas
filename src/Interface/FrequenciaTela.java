
package Interface;

import DAO.AlunoDao;
import DAO.FrequenciaDao;
import DAO.SalaDao;
import Escola.Aluno;
import Escola.Frequencia;
import Escola.Sala;
import testeserial.LeitorRfid;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.DefaultCellEditor;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class FrequenciaTela extends JFrame {

    private JComboBox<Sala> comboSala;
    private JButton btnSalvar;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JLabel labelQuantidade;
    private JLabel labelData;

    private CardLayout layoutConteudo;
    private JPanel painelCartoes;

    private JLabel labelFoto;
    private JLabel labelNomeAluno;
    private JLabel labelSalaAluno;
    private JLabel labelStatusRFID;

    private JButton btnManual;
    private JButton btnVoltarLeitor;

    private LeitorRfid leitorRfid;

    private volatile boolean telaFechada = false;

    private final Color AZUL = new Color(37, 99, 235);
    private final Color TEXTO = Configuracoes.getCorTexto();
    private final Color CINZA = Configuracoes.getCorSubtexto();
    private final Color FUNDO = Configuracoes.getCorFundo();
    private final Color BRANCO = Configuracoes.getCorPainel();
    private final Color BORDA = Configuracoes.getCorBorda();

    public FrequenciaTela() {

        setTitle("Controle de Faltas | Frequência Escolar");
        setSize(1220, 800);
        setMinimumSize(new Dimension(980, 680));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(FUNDO);

        JPanel cabecalho = criarCabecalho();

        // Painel da frequência manual.
        JPanel conteudoManual = criarPainelManual();

        // Painel que alterna entre RFID e frequência manual.
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

        // Encerra a comunicação serial ao fechar a janela.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                telaFechada = true;

                if (leitorRfid != null) {
                    leitorRfid.parar();
                }
            }
        });

        // Abre a tela já aguardando um cartão.
        layoutConteudo.show(painelCartoes, "RFID");
        setVisible(true);

        iniciarLeitorRFID();
    }

    // CABEÇALHO

    private JPanel criarCabecalho() {

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(new Color(15, 31, 58));
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
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Frequência escolar");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel(
                "Sistema inteligente de registro de presença • RFID e modo manual"
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(203, 213, 225));

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(7));
        textos.add(subtitulo);

        JLabel etiqueta = new JLabel("  CONTROLE DE PRESENÇA  ");
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 11));
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setOpaque(true);
        etiqueta.setBackground(new Color(37, 99, 235));
        etiqueta.setBorder(
                BorderFactory.createEmptyBorder(9, 5, 9, 5)
        );

        cabecalho.add(textos, BorderLayout.CENTER);
        cabecalho.add(etiqueta, BorderLayout.EAST);

        return cabecalho;
    }

    // PAINEL RFID — LAYOUT RENOVADO
    private JPanel criarPainelRFID() {
        JPanel pagina = new JPanel(new BorderLayout(0, 22));
        pagina.setBackground(new Color(241, 245, 249));
        pagina.setBorder(BorderFactory.createEmptyBorder(24, 30, 28, 30));

        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        JPanel textosTopo = new JPanel();
        textosTopo.setOpaque(false);
        textosTopo.setLayout(new BoxLayout(textosTopo, BoxLayout.Y_AXIS));
        JLabel tituloPagina = new JLabel("Identificação do estudante");
        tituloPagina.setFont(new Font("Segoe UI", Font.BOLD, 23));
        tituloPagina.setForeground(new Color(15, 31, 58));
        JLabel descricaoPagina = new JLabel("Aproxime o cartão do leitor para registrar a presença automaticamente.");
        descricaoPagina.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descricaoPagina.setForeground(new Color(100, 116, 139));
        textosTopo.add(tituloPagina);
        textosTopo.add(Box.createVerticalStrut(5));
        textosTopo.add(descricaoPagina);

        btnManual = new JButton("Registrar Frequência Manualmente?");
        btnManual.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnManual.setForeground(Color.WHITE);
        btnManual.setBackground(new Color(37, 99, 235));
        btnManual.setFocusPainted(false);
        btnManual.setBorder(BorderFactory.createEmptyBorder(13, 19, 13, 19));
        btnManual.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnManual.putClientProperty(FlatClientProperties.STYLE,
                "arc:12;focusWidth:0;borderWidth:0;hoverBackground:#1D4ED8;pressedBackground:#1E40AF");
        btnManual.addActionListener(e -> layoutConteudo.show(painelCartoes, "MANUAL"));
        barra.add(textosTopo, BorderLayout.WEST);
        barra.add(btnManual, BorderLayout.EAST);

        JPanel cartao = new JPanel(new BorderLayout(28, 0));
        cartao.setBackground(Color.WHITE);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(28, 30, 28, 30)));

        // Coluna da fotografia: tamanho real maior e fundo neutro.
        JPanel colunaFoto = new JPanel(new BorderLayout(0, 14));
        colunaFoto.setOpaque(false);
        colunaFoto.setPreferredSize(new Dimension(340, 430));
        JLabel legendaFoto = new JLabel("FOTO DO ALUNO", SwingConstants.CENTER);
        legendaFoto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        legendaFoto.setForeground(new Color(100, 116, 139));
        labelFoto = new JLabel("Aguardando cartão", SwingConstants.CENTER);
        labelFoto.setPreferredSize(new Dimension(320, 360));
        labelFoto.setMinimumSize(new Dimension(320, 360));
        labelFoto.setOpaque(true);
        labelFoto.setBackground(new Color(248, 250, 252));
        labelFoto.setForeground(new Color(100, 116, 139));
        labelFoto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelFoto.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        colunaFoto.add(legendaFoto, BorderLayout.NORTH);
        colunaFoto.add(labelFoto, BorderLayout.CENTER);

        // Coluna de dados e estado da leitura.
        JPanel colunaDados = new JPanel();
        colunaDados.setOpaque(false);
        colunaDados.setLayout(new BoxLayout(colunaDados, BoxLayout.Y_AXIS));
        colunaDados.setBorder(BorderFactory.createEmptyBorder(18, 0, 10, 0));

        JLabel selo = new JLabel("  LEITURA RFID  ");
        selo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        selo.setForeground(new Color(29, 78, 216));
        selo.setOpaque(true);
        selo.setBackground(new Color(219, 234, 254));
        selo.setBorder(BorderFactory.createEmptyBorder(8, 3, 8, 3));
        selo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tituloRFID = new JLabel("Aproxime seu cartão");
        tituloRFID.setFont(new Font("Segoe UI", Font.BOLD, 27));
        tituloRFID.setForeground(new Color(15, 31, 58));
        tituloRFID.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel instrucao = new JLabel("O sistema identifica o estudante e registra a presença.");
        instrucao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instrucao.setForeground(new Color(100, 116, 139));
        instrucao.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel legendaNome = criarLegendaRFID("NOME DO ESTUDANTE");
        labelNomeAluno = new JLabel("Aguardando identificação...");
        labelNomeAluno.setFont(new Font("Segoe UI", Font.BOLD, 23));
        labelNomeAluno.setForeground(new Color(15, 31, 58));
        labelNomeAluno.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel legendaTurma = criarLegendaRFID("TURMA");
        labelSalaAluno = new JLabel("—");
        labelSalaAluno.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        labelSalaAluno.setForeground(new Color(71, 85, 105));
        labelSalaAluno.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel caixaStatus = new JPanel(new BorderLayout(12, 0));
        caixaStatus.setBackground(new Color(239, 246, 255));
        caixaStatus.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254)),
                BorderFactory.createEmptyBorder(15, 16, 15, 16)));
        JLabel ponto = new JLabel("●");
        ponto.setFont(new Font("Segoe UI", Font.BOLD, 18));
        ponto.setForeground(new Color(37, 99, 235));
        labelStatusRFID = new JLabel("Conectando ao leitor RFID...");
        labelStatusRFID.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelStatusRFID.setForeground(new Color(29, 78, 216));
        caixaStatus.add(ponto, BorderLayout.WEST);
        caixaStatus.add(labelStatusRFID, BorderLayout.CENTER);
        caixaStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        caixaStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        colunaDados.add(selo);
        colunaDados.add(Box.createVerticalStrut(20));
        colunaDados.add(tituloRFID);
        colunaDados.add(Box.createVerticalStrut(8));
        colunaDados.add(instrucao);
        colunaDados.add(Box.createVerticalStrut(36));
        colunaDados.add(legendaNome);
        colunaDados.add(Box.createVerticalStrut(8));
        colunaDados.add(labelNomeAluno);
        colunaDados.add(Box.createVerticalStrut(24));
        colunaDados.add(legendaTurma);
        colunaDados.add(Box.createVerticalStrut(8));
        colunaDados.add(labelSalaAluno);
        colunaDados.add(Box.createVerticalStrut(34));
        colunaDados.add(caixaStatus);
        colunaDados.add(Box.createVerticalGlue());

        cartao.add(colunaFoto, BorderLayout.WEST);
        cartao.add(colunaDados, BorderLayout.CENTER);

        JLabel dica = new JLabel("Dica: mantenha o cartão próximo ao leitor até a confirmação na tela.");
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
        labelStatusRFID.setForeground(AZUL);

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
            labelStatusRFID.setText("Leitor conectado. Aproxime o cartão.");
            labelStatusRFID.setForeground(AZUL);
        }
    }

    // PROCESSA O UID LIDO

    private void processarCartao(String uid) {

        if (telaFechada) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            if (!telaFechada) {
                labelStatusRFID.setText("Cartão lido! Identificando aluno...");
                labelStatusRFID.setForeground(AZUL);
            }
        });

        AlunoDao alunoDao = new AlunoDao();
        Aluno aluno = alunoDao.buscarPorUid(uid);

        if (aluno == null) {
            SwingUtilities.invokeLater(() -> {
                if (!telaFechada) {
                    limparIdentificacao();
                    labelNomeAluno.setText("Aluno não encontrado");
                    labelSalaAluno.setText("Cartão não cadastrado");
                    labelStatusRFID.setText(
                            "Verifique o cadastro do cartão no sistema."
                    );
                    labelStatusRFID.setForeground(Color.RED);
                }
            });
            return;
        }

        // A consulta do DAO deve preencher o nome da turma.
        String nomeSala = aluno.getSala();

        if (nomeSala == null || nomeSala.isBlank()) {
            nomeSala = "Sala ID: " + aluno.getSala_id();
        }

        final String salaExibicao = nomeSala;

        FrequenciaDao frequenciaDao = new FrequenciaDao();
        boolean registrado = frequenciaDao.registrarPresencaRFID(
                aluno.getId()
        );

        final boolean sucesso = registrado;

        SwingUtilities.invokeLater(() -> {
            if (telaFechada) {
                return;
            }

            mostrarAluno(aluno, salaExibicao);

            if (sucesso) {
                labelStatusRFID.setText(
                        "Presença registrada com sucesso!"
                );
                labelStatusRFID.setForeground(
                        new Color(22, 163, 74)
                );
            } else {
                labelStatusRFID.setText(
                        "Presença já registrada ou falha ao salvar."
                );
                labelStatusRFID.setForeground(
                        new Color(217, 119, 6)
                );
            }
        });
    }

    // MOSTRA FOTO E DADOS DO ALUNO

    private void mostrarAluno(Aluno aluno, String nomeSala) {
        String nome = aluno.getNome() == null ? "Aluno" : aluno.getNome();
        labelNomeAluno.setText(
                "<html><div style='text-align:center; width:420px;'>"
                        + escaparHtml(nome)
                        + "</div></html>"
        );
        labelSalaAluno.setText("Turma: " + escaparHtml(nomeSala));

        ImageIcon foto = carregarFoto(nome);
        if (foto != null) {
            labelFoto.setIcon(foto);
            labelFoto.setText("");
        } else {
            labelFoto.setIcon(null);
            labelFoto.setText("Foto não encontrada");
        }
    }

    private String escaparHtml(String texto) {
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private ImageIcon carregarFoto(String nomeAluno) {
        if (nomeAluno == null) return null;
        String nome = nomeAluno.toUpperCase(Locale.ROOT);
        String arquivo;
        if (nome.contains("JULIO")) arquivo = "Julio.jpeg";
        else if (nome.contains("MARCELO")) arquivo = "Marcelo.jpeg";
        else if (nome.contains("PAULO")) arquivo = "Paulo.jpeg";
        else return null;

        BufferedImage original = null;
        java.net.URL recurso = getClass().getResource("/Imagens/" + arquivo);
        if (recurso != null) {
            try { original = ImageIO.read(recurso); }
            catch (IOException ex) { System.err.println("Erro lendo recurso " + arquivo + ": " + ex.getMessage()); }
        }
        if (original == null) {
            String[] caminhos = {"src/Imagens/" + arquivo, "Imagens/" + arquivo,
                    "src/imagens/" + arquivo, "imagens/" + arquivo,
                    "Controle de faltas/src/Imagens/" + arquivo};
            for (String caminho : caminhos) {
                File f = new File(caminho);
                if (f.isFile()) {
                    try { original = ImageIO.read(f); if (original != null) break; }
                    catch (IOException ex) { System.err.println("Erro lendo " + caminho + ": " + ex.getMessage()); }
                }
            }
        }
        if (original == null) {
            System.err.println("Foto não encontrada: " + arquivo + ". Verifique src/Imagens.");
            return null;
        }
        System.out.println("Foto carregada: " + arquivo + " (" + original.getWidth() + "x" + original.getHeight() + ")");
        return criarFotoAjustada(original, 320, 360);
    }

    // Redimensionamento em alta qualidade. Isso melhora a nitidez da ampliação,
    // mas não cria detalhes que não existam na fotografia original.
    private ImageIcon criarFotoAjustada(BufferedImage original, int largura, int altura) {
        double escala = Math.max((double) largura / original.getWidth(),
                (double) altura / original.getHeight());
        int larguraNova = Math.max(largura, (int) Math.round(original.getWidth() * escala));
        int alturaNova = Math.max(altura, (int) Math.round(original.getHeight() * escala));
        BufferedImage ampliada = new BufferedImage(larguraNova, alturaNova, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = ampliada.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g.drawImage(original, 0, 0, larguraNova, alturaNova, null);
        } finally { g.dispose(); }

        int x = (larguraNova - largura) / 2;
        int y = (alturaNova - altura) / 2;
        BufferedImage recorte = ampliada.getSubimage(x, y, largura, altura);
        BufferedImage finalImagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D gFinal = finalImagem.createGraphics();
        try {
            gFinal.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            gFinal.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            gFinal.drawImage(recorte, 0, 0, null);
        } finally { gFinal.dispose(); }
        return new ImageIcon(finalImagem);
    }

    private void limparIdentificacao() {
        labelFoto.setIcon(null);
        labelFoto.setText("Foto do aluno");
        labelNomeAluno.setText("Aguardando identificação...");
        labelSalaAluno.setText("Turma: —");
    }

    // PAINEL DA FREQUÊNCIA MANUAL

    private JPanel criarPainelManual() {

        JPanel conteudo = new JPanel(new BorderLayout(0, 20));
        conteudo.setBackground(FUNDO);
        conteudo.setBorder(
                BorderFactory.createEmptyBorder(20, 30, 25, 30)
        );

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

    // SELEÇÃO DA TURMA E DATA

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
        comboSala.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:12;focusWidth:1"
        );

        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        selecao.add(titulo);
        selecao.add(Box.createVerticalStrut(9));
        selecao.add(comboSala);

        JPanel informacoes = new JPanel();
        informacoes.setLayout(new BoxLayout(informacoes, BoxLayout.Y_AXIS));
        informacoes.setOpaque(false);
        informacoes.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel legendaData = new JLabel("DATA DO REGISTRO");
        legendaData.setFont(new Font("Segoe UI", Font.BOLD, 10));
        legendaData.setForeground(CINZA);

        labelData = new JLabel();
        labelData.setFont(new Font("Segoe UI", Font.BOLD, 16));
        labelData.setForeground(AZUL);

        atualizarData();

        informacoes.add(legendaData);
        informacoes.add(Box.createVerticalStrut(7));
        informacoes.add(labelData);

        btnVoltarLeitor = new JButton("Voltar ao leitor RFID");
        btnVoltarLeitor.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVoltarLeitor.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarLeitor.addActionListener(e -> {
            layoutConteudo.show(painelCartoes, "RFID");
        });

        painel.add(selecao, BorderLayout.WEST);
        painel.add(informacoes, BorderLayout.EAST);
        painel.add(btnVoltarLeitor, BorderLayout.SOUTH);

        return painel;
    }

    // CARREGAR TURMAS

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

            if (btnSalvar != null) {
                btnSalvar.setEnabled(false);
            }
        }
    }

    // TABELA MANUAL

    private void criarTabela() {

        modelo = new DefaultTableModel(
                new Object[]{"ID", "Nome do aluno", "Situação"},
                0
        ) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return coluna == 2;
            }
        };

        tabela = new JTable(modelo);

        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setForeground(TEXTO);
        tabela.setBackground(BRANCO);
        tabela.setRowHeight(42);
        tabela.setShowVerticalLines(false);
        tabela.setShowHorizontalLines(true);
        tabela.setGridColor(
                Configuracoes.isModoEscuro()
                        ? new Color(45, 45, 52)
                        : new Color(241, 245, 249)
        );
        tabela.setSelectionBackground(
                Configuracoes.isModoEscuro()
                        ? new Color(30, 58, 138)
                        : new Color(219, 234, 254)
        );
        tabela.setSelectionForeground(
                Configuracoes.isModoEscuro() ? Color.WHITE : TEXTO
        );
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cabecalho.setForeground(TEXTO);
        cabecalho.setBackground(
                Configuracoes.isModoEscuro()
                        ? new Color(35, 35, 40)
                        : new Color(241, 245, 249)
        );
        cabecalho.setPreferredSize(new Dimension(0, 43));
        cabecalho.setReorderingAllowed(false);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(450);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(180);

        JComboBox<String> comboPresenca = new JComboBox<>();
        comboPresenca.addItem("Presente");
        comboPresenca.addItem("Falta");

        comboPresenca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboPresenca.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:8;focusWidth:1"
        );

        tabela.getColumnModel()
                .getColumn(2)
                .setCellEditor(new DefaultCellEditor(comboPresenca));
    }

    // CARREGAR ALUNOS DA TURMA

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

        List<Aluno> alunos =
                new AlunoDao().listarPorSala(salaSelecionada.getId());

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

    // RODAPÉ MANUAL

    private JPanel criarRodape() {

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(FUNDO);

        labelQuantidade = new JLabel("Alunos na turma: 0");
        labelQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelQuantidade.setForeground(CINZA);

        btnSalvar = new JButton("Salvar frequência");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSalvar.setForeground(BRANCO);
        btnSalvar.setBackground(AZUL);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalvar.setPreferredSize(new Dimension(190, 45));

        btnSalvar.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:12;focusWidth:0;borderWidth:0;"
                        + "hoverBackground:#1D4ED8;"
                        + "pressedBackground:#1E40AF"
        );

        btnSalvar.addActionListener(e -> salvarFrequencia());

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

        labelData.setText(LocalDate.now().format(formato));
    }

    // SALVAR FREQUÊNCIA MANUAL

    private void salvarFrequencia() {

        if (tabela.isEditing()
                && !tabela.getCellEditor().stopCellEditing()) {
            return;
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

        Sala salaSelecionada = (Sala) comboSala.getSelectedItem();

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

                String escolha =
                        modelo.getValueAt(i, 2).toString();

                boolean presente = escolha.equals("Presente");

                Frequencia frequencia = new Frequencia();
                frequencia.setAlunoId(alunoId);
                frequencia.setData(data);
                frequencia.setPresente(presente);

                if (frequenciaDao.salvar(frequencia)) {
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

        String mensagem = "Resultado do registro:\n\n"
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