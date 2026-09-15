package Interface;

import DAO.AlunoDao;
import DAO.SalaDao;
import Escola.Aluno;
import Escola.Sala;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CadastroAluno extends JFrame {

    private JTextField campoNome;
    private JTextField campoId;
    private JButton botaoCadastrar;
    private JComboBox<Sala> combosala;


    public CadastroAluno(){
        setTitle("Cadastro de Aluno");
        setSize(400,300);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLocationRelativeTo(null);

        JLabel titulo = new JLabel("CADASTRO DE ALUNO");
        JLabel labelNome = new JLabel("Nome: ");

        campoNome = new JTextField(20);
        campoId = new JTextField(20);
        combosala = new JComboBox<>();
       SalaDao salaDao = new SalaDao();

       List<Sala> salas = salaDao.listar();

       for (Sala sala : salas) {
           combosala.addItem(sala);
       }


        botaoCadastrar = new JButton("Cadastrar");

        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();



        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        painel.add(titulo,gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        painel.add(labelNome,gbc);

        gbc.gridx = 1;
        painel.add(campoNome,gbc);



        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;

        painel.add(botaoCadastrar, gbc);

        painel.add(new JLabel("Sala: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;

        painel.add(combosala, gbc);

        add(painel);

        botaoCadastrar.addActionListener(e -> cadastrarAluno());





    }

    private void cadastrarAluno(){


        String nome = campoNome.getText();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Erro!! Campo Vazio" , "Atenção" , JOptionPane.WARNING_MESSAGE);
            return;
        }

        Sala salaSelecionada = (Sala) combosala.getSelectedItem();

        int sala_id = salaSelecionada.getId();

        Aluno aluno = new Aluno();

        aluno.setNome(nome);
        aluno.setSala_id(sala_id);

        AlunoDao alunoDao = new AlunoDao();

        alunoDao.salvar(aluno);

        JOptionPane.showMessageDialog(this,"Aluno Cadastrado Com Sucesso!!");

    }
}
