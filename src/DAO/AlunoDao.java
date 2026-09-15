package DAO;

import Escola.Aluno;
import Conexao.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlunoDao {

    public void salvar(Aluno aluno) {

        String sql = "INSERT INTO alunos (nome, sala_id) VALUES (?, ?)";

        try {
            Connection conexao = Conexao.conectar();

            PreparedStatement stmt = conexao.prepareStatement(sql);

            stmt.setString(1, aluno.getNome());
            stmt.setInt(2, aluno.getSala_id());

            stmt.executeUpdate();

            System.out.println("Aluno Cadastrado com sucesso!");

            stmt.close();
            conexao.close();
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar aluno: " + e.getMessage());
        }

    }

    public List<Aluno> listarTodos() {

        List<Aluno> alunos = new ArrayList<>();

        try {

            Connection conexao = Conexao.conectar();
            String sql =
                    "SELECT alunos.id, alunos.nome, salas.nome AS sala " +
                            "FROM alunos " +
                            "INNER JOIN salas " +
                            "ON alunos.sala_id = salas.id";

            PreparedStatement stmt = conexao.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Aluno aluno = new Aluno();

                aluno.setId(rs.getInt("id"));

                aluno.setNome(rs.getString("nome"));

                aluno.setSala(rs.getString("Sala"));

                alunos.add(aluno);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alunos;
    }

    public void deletar(int id) {
        String sql = "DELETE FROM alunos WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

            System.out.println("Aluno deletado com sucesso!!");

        } catch (SQLException e) {
            System.out.println("Erro ao deletar aluno: " + e.getMessage());
        }
    }

    public void atualizar(Aluno aluno ) {
        String sql = "UPDATE alunos SET nome = ?, sala_id = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1,aluno.getNome());
            stmt.setInt(2,aluno.getSala_id());
            stmt.setInt(3, aluno.getId());

            stmt.executeUpdate();

            System.out.println("Aluno Atualizado com Sucesso");
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar aluno: " + e.getMessage());
        }
    }

    public List<Aluno> listarPorSala (int salaId) {

        List<Aluno> alunos = new ArrayList<>();

        String sql = "SELECT * FROM alunos WHERE sala_id = ?";

        try {
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1,salaId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Aluno aluno = new Aluno();

                aluno.setId(rs.getInt("id"));
                aluno.setNome(rs.getString("nome"));
                aluno.setSala_id(rs.getInt("sala_id"));

                alunos.add(aluno);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar aluno: " + e.getMessage());
        }
        return alunos;
    }

    public List<Aluno> buscarPorNome(String nome) {

        List<Aluno> alunos = new ArrayList<>();

        String sql = """
        SELECT alunos.*, salas.nome AS nome_sala
        FROM alunos
        JOIN salas ON alunos.sala_id = salas.id
        WHERE alunos.nome LIKE ?
        """;

        try {
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql);

          stmt.setString(1, "%" + nome + "%" );

         ResultSet result = stmt.executeQuery();

         while (result.next()) {
             Aluno aluno = new Aluno();

             aluno.setId(result.getInt("id"));
             aluno.setNome(result.getString("nome"));
             aluno.setSala_id(result.getInt("sala_id"));
             aluno.setSala(result.getString("nome_sala"));

             alunos.add(aluno);

             System.out.println(result.getString("nome"));

         }
        } catch (SQLException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
        return alunos;
    }

}

