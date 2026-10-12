package Interface;

import DAO.AlunoDao;
import DAO.SalaDao;
import Escola.Aluno;
import Escola.Sala;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.formdev.flatlaf.FlatClientProperties;

public class CadastroAluno extends JFrame {

    private JTextField campoNome;
    private JComboBox<Sala> comboSala;
    private JButton botaoCadastrar;

    private final Color AZUL = new Color(37, 99, 235);
    private final Color TEXTO = Configuracoes.getCorTexto();
    private final Color CINZA = Configuracoes.getCorSubtexto();
    private final Color FUNDO = Configuracoes.getCorFundo();
    private final Color BRANCO = Configuracoes.getCorPainel();
    private final Color BORDA = Configuracoes.getCorBorda();

    public CadastroAluno() {

        setTitle("Controle de Faltas | Cadastro de Aluno");
        setSize(760, 570);
        setMinimumSize(new Dimension(650, 520));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // PAINEL PRINCIPAL
        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(FUNDO);

        // CABEÇALHO
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(
                new BoxLayout(cabecalho, BoxLayout.Y_AXIS)
        );
        cabecalho.setBackground(BRANCO);
        cabecalho.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 0,
                                BORDA
                        ),
                        BorderFactory.createEmptyBorder(
                                25, 35, 25, 35
                        )
                )
        );

        JLabel titulo = new JLabel("Cadastro de aluno");
        titulo.setFont(
                new Font("Segoe UI", Font.BOLD, 26)
        );
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel(
                "Preencha os dados para adicionar um aluno ao sistema."
        );
        subtitulo.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );
        subtitulo.setForeground(CINZA);

        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        cabecalho.add(titulo);
        cabecalho.add(Box.createVerticalStrut(8));
        cabecalho.add(subtitulo);

        // FORMULÁRIO
        JPanel areaFormulario = new JPanel(new GridBagLayout());
        areaFormulario.setBackground(FUNDO);
        areaFormulario.setBorder(
                BorderFactory.createEmptyBorder(25, 35, 25, 35)
        );

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(BRANCO);
        formulario.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDA
                        ),
                        BorderFactory.createEmptyBorder(
                                28, 30, 28, 30
                        )
                )
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel tituloFormulario = new JLabel("Informações do aluno");
        tituloFormulario.setFont(
                new Font("Segoe UI", Font.BOLD, 18)
        );
        tituloFormulario.setForeground(TEXTO);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formulario.add(tituloFormulario, gbc);

        // CAMPO NOME
        JLabel labelNome = criarLabel("Nome completo");

        campoNome = new JTextField();
        estilizarCampo(campoNome);
        campoNome.putClientProperty(
                "JTextField.placeholderText",
                "Digite o nome completo do aluno"
        );

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formulario.add(labelNome, gbc);

        gbc.gridy = 2;
        formulario.add(campoNome, gbc);

        // CAMPO SALA
        JLabel labelSala = criarLabel("Turma / Sala");

        comboSala = new JComboBox<>();
        estilizarCombo(comboSala);

        carregarSalas();

        gbc.gridy = 3;
        formulario.add(labelSala, gbc);

        gbc.gridy = 4;
        formulario.add(comboSala, gbc);

        // TEXTO AUXILIAR
        JLabel dica = new JLabel(
                "Confira o nome e a turma antes de cadastrar."
        );
        dica.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );
        dica.setForeground(CINZA);

        gbc.gridy = 5;
        gbc.insets = new Insets(8, 5, 15, 5);
        formulario.add(dica, gbc);

        // BOTÃO CADASTRAR
        botaoCadastrar = new JButton("Cadastrar aluno");
        botaoCadastrar.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );
        botaoCadastrar.setForeground(BRANCO);
        botaoCadastrar.setBackground(AZUL);
        botaoCadastrar.setFocusPainted(false);
        botaoCadastrar.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
        botaoCadastrar.setPreferredSize(
                new Dimension(220, 45)
        );

        botaoCadastrar.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:12;focusWidth:0;borderWidth:0;"
                        + "hoverBackground:#1D4ED8;"
                        + "pressedBackground:#1E40AF"
        );

        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);
        formulario.add(botaoCadastrar, gbc);

        // CENTRALIZA O FORMULÁRIO
        areaFormulario.add(formulario);

        principal.add(cabecalho, BorderLayout.NORTH);
        principal.add(areaFormulario, BorderLayout.CENTER);

        setContentPane(principal);

        botaoCadastrar.addActionListener(
                e -> cadastrarAluno()
        );

        getRootPane().setDefaultButton(botaoCadastrar);

        setVisible(true);
    }

    private JLabel criarLabel(String texto) {

        JLabel label = new JLabel(texto);
        label.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );
        label.setForeground(TEXTO);

        return label;
    }

    private void estilizarCampo(JTextField campo) {

        campo.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );
        campo.setPreferredSize(
                new Dimension(350, 43)
        );
        campo.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:12;focusWidth:1"
        );
    }

    private void estilizarCombo(JComboBox<Sala> combo) {

        combo.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );
        combo.setPreferredSize(
                new Dimension(350, 43)
        );
        combo.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:12;focusWidth:1"
        );
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
            botaoCadastrar.setEnabled(false);

            JOptionPane.showMessageDialog(
                    this,
                    "Nenhuma turma foi encontrada. "
                            + "Verifique se existem salas cadastradas.",
                    "Turmas indisponíveis",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void cadastrarAluno() {

        String nome = campoNome.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe o nome do aluno.",
                    "Campo obrigatório",
                    JOptionPane.WARNING_MESSAGE
            );

            campoNome.requestFocus();
            return;
        }

        Sala salaSelecionada =
                (Sala) comboSala.getSelectedItem();

        if (salaSelecionada == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma turma para o aluno.",
                    "Turma obrigatória",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        aluno.setSala_id(salaSelecionada.getId());

        AlunoDao alunoDao = new AlunoDao();
        alunoDao.salvar(aluno);

        JOptionPane.showMessageDialog(
                this,
                "Aluno cadastrado com sucesso!",
                "Cadastro concluído",
                JOptionPane.INFORMATION_MESSAGE
        );

        campoNome.setText("");
        comboSala.setSelectedIndex(0);
        campoNome.requestFocus();
    }
}

