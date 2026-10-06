package Interface;

import DAO.AlunoDao;
import DAO.SalaDao;
import Escola.Aluno;
import Escola.Sala;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultCellEditor;
import java.awt.*;
import java.util.List;

public class FrequenciaTela extends JFrame {

    private JComboBox<Sala> combosala;
    private JButton btnsalvar;
    private JTable tabela;
    private DefaultTableModel modelo;

    public FrequenciaTela() {

        setTitle("Controle de Frequência");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painel = new JPanel();
        painel.setLayout(new BorderLayout());

        // Painel superior
        JPanel paineltopo = new JPanel();
        paineltopo.add(new JLabel("Sala: "));

        combosala = new JComboBox<>();

        SalaDao salaDao = new SalaDao();
        List<Sala> salas = salaDao.listar();

        for (Sala sala : salas) {
            combosala.addItem(sala);
        }

        paineltopo.add(combosala);
        painel.add(paineltopo, BorderLayout.NORTH);

        // Botão salvar
        btnsalvar = new JButton("Salvar frequência");
        painel.add(btnsalvar, BorderLayout.SOUTH);

        // Modelo da tabela
        modelo = new DefaultTableModel(
                new Object[]{"ID", "Aluno", "Presença"},
                0
        ) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return coluna == 2;
            }
        };

        tabela = new JTable(modelo);

        // Opções de frequência
        JComboBox<String> comboPresenca = new JComboBox<>();
        comboPresenca.addItem("Presente");
        comboPresenca.addItem("Falta");

        // Editor da terceira coluna
        tabela.getColumnModel()
                .getColumn(2)
                .setCellEditor(new DefaultCellEditor(comboPresenca));

        JScrollPane scroll = new JScrollPane(tabela);
        painel.add(scroll, BorderLayout.CENTER);

        // Carregar alunos ao selecionar uma sala
        combosala.addActionListener(e -> carregarAlunos());

        add(painel);

        // Carrega a primeira sala automaticamente
        if (combosala.getItemCount() > 0) {
            carregarAlunos();
        }
    }

    private void carregarAlunos() {

        modelo.setRowCount(0);

        Sala salaSelecionada =
                (Sala) combosala.getSelectedItem();

        if (salaSelecionada == null) {
            return;
        }

        int sala_id = salaSelecionada.getId();

        AlunoDao alunoDao = new AlunoDao();
        List<Aluno> alunos = alunoDao.listarPorSala(sala_id);

        for (Aluno aluno : alunos) {
            modelo.addRow(new Object[]{
                    aluno.getId(),
                    aluno.getNome(),
                    "Presente"
            });
        }
    }
}

