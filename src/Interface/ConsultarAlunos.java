package Interface;

import DAO.AlunoDao;
import DAO.SalaDao;
import Escola.Aluno;
import Escola.Sala;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class ConsultarAlunos extends JFrame {
    private JTable tabela;
    private DefaultTableModel modelo;
    private JTextField buscarAluno;
    private JButton botaoBuscar;

    public ConsultarAlunos(){

        setTitle("Consultar Alunos");
        setSize(600,400);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLocationRelativeTo(null);

        JLabel titulo = new JLabel("Alunos Cadastrados");

        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        add(titulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel();

        modelo.addColumn("ID");
        modelo.addColumn("Nome");
        modelo.addColumn("Sala");

        tabela = new JTable(modelo);

        JScrollPane scroll = new JScrollPane(tabela);

        add(scroll);

        JButton btnexcluir = new JButton("Excluir");

        btnexcluir.addActionListener(e -> deletarAlunoSelecionado());

        JButton btnEditar = new JButton("Editar");

        btnEditar.addActionListener(e -> editarAlunoSelecionado() );

        botaoBuscar = new JButton("Buscar");

        botaoBuscar.addActionListener(e -> buscarAlunos());

        JPanel botoes = new JPanel();

        buscarAluno = new JTextField(20);


        botoes.add(botaoBuscar);
        botoes.add(buscarAluno);
        botoes.add(btnEditar);
        botoes.add(btnexcluir);


        add(botoes, BorderLayout.SOUTH);



        carregarAlunos();


    }

    private void carregarAlunos() {
        AlunoDao alunoDao = new AlunoDao();

        List<Aluno> alunos = alunoDao.listarTodos();

        System.out.println("Quantidade de Alunos: " + alunos.size());

        for (Aluno aluno : alunos) {
            modelo.addRow(new Object[]{
                    aluno.getId(),
                    aluno.getNome(),
                    aluno.getSala()
            });
        }

    }

    private void deletarAlunoSelecionado() {

        int linha = tabela.getSelectedRow();

        if (linha == -1) {

            JOptionPane.showMessageDialog(this,"Selecione um aluno primeiro!");
            return;
        }

        int id = (int) modelo.getValueAt(linha, 0 );

        AlunoDao alunoDao = new AlunoDao();
        alunoDao.deletar(id);

        modelo.removeRow(linha);
    }

    private void editarAlunoSelecionado() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um aluno primeiro");
            return;
        }
        int id = (int)
        modelo.getValueAt(linha, 0);
        String nomeAtual = (String)
                modelo.getValueAt(linha, 1);
        String salaAtual = (String)
                modelo.getValueAt(linha,2);

      JTextField camponome = new JTextField(nomeAtual);

      JComboBox<Sala> combosala = new JComboBox<>();

        SalaDao salaDao = new SalaDao();

        List<Sala> salas = salaDao.listar();

        for (Sala sala : salas) {
            combosala.addItem(sala);
        }

      String numeroSala = salaAtual.replaceAll("\\D+" , "");

      if (!numeroSala.isEmpty()) {
          combosala.setSelectedItem(Integer.parseInt(numeroSala));
      }

        JPanel painel = new JPanel(new GridLayout(0,1));

      painel.add(new JLabel("Nome:"));
      painel.add(camponome);

      painel.add(new JLabel("Sala"));
      painel.add(combosala);

      int resultado = JOptionPane.showConfirmDialog(this, painel, "Editar aluno" , JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

      if (resultado == JOptionPane.OK_OPTION) {
          String novonome = camponome.getText().trim();
          Sala salaSelecionada = (Sala) combosala.getSelectedItem();

          int sala_id = salaSelecionada.getId();

          if (novonome.isEmpty()) {
              JOptionPane.showMessageDialog(this,"O nome não pode estar vazio!");
              return;
          }

          Aluno aluno = new Aluno();

          aluno.setId(id);
          aluno.setNome(novonome);

          aluno.setSala_id(sala_id);

          AlunoDao alunoDao = new AlunoDao();

          alunoDao.atualizar(aluno);

          carregarAlunos();

          JOptionPane.showMessageDialog(this, "Aluno atualizado com sucesso!");
    }
}

public void buscarAlunos() {
        String nome = buscarAluno.getText().trim();

        AlunoDao alunoDao = new AlunoDao();

        List<Aluno> alunos = alunoDao.buscarPorNome(nome);

        modelo.setRowCount(0);

        for (Aluno aluno : alunos){
            modelo.addRow(new Object[]{
                    aluno.getId(),
                    aluno.getNome(),
                    aluno.getSala()
            });
        }
}
}
