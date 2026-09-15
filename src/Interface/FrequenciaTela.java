package Interface;

import DAO.AlunoDao;
import DAO.SalaDao;
import Escola.Aluno;
import Escola.Sala;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FrequenciaTela extends JFrame {

    private JComboBox<Sala> combosala;
    private JButton btnsalvar;
    private JTable tabela;
    private DefaultTableModel modelo;

    public FrequenciaTela() {
        setTitle("Controle de Frequencia");
        setSize(600,500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLocationRelativeTo(null);

        JPanel painel = new JPanel();
        painel.setLayout(new BorderLayout());

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

        btnsalvar = new JButton("Salvar frequência");

        painel.add(btnsalvar, BorderLayout.SOUTH);

        modelo = new DefaultTableModel(
                new Object[]{"ID", "Aluno", "Presença"},
                0
        );

        tabela = new JTable(modelo);

        JScrollPane scroll = new JScrollPane(tabela);

        painel.add(scroll, BorderLayout.CENTER);

        combosala.addActionListener(e -> carregarAlunos() );



        add(painel);


    }

    private void carregarAlunos() {

        modelo.setRowCount(0);

        Sala salaSelecionada = (Sala) combosala.getSelectedItem();

        int sala_id = salaSelecionada.getId();

        AlunoDao alunoDao = new AlunoDao();

        List<Aluno> alunos = alunoDao.listarPorSala(sala_id);

        for (Aluno aluno : alunos) {
            modelo.addRow(new Object[] {
                    aluno.getId(),
                    aluno.getNome(),
                    "Presente"
            });
        }
    }
}
