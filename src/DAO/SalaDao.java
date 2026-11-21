package DAO;

import Escola.Sala;
import Conexao.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalaDao {

    public boolean cadastrar(Sala sala) {
        String sql = "INSERT INTO salas (nome) VALUES (?)";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, sala.getNome());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar sala! " + e.getMessage());
            return false;
        }
    }

    public boolean atualizar(Sala sala) {
        String sql = "UPDATE salas SET nome = ? WHERE id = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, sala.getNome());
            stmt.setInt(2, sala.getId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar sala! " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM salas WHERE id = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir sala! " + e.getMessage());
            return false;
        }
    }

    public int contarAlunos(int salaId) {
        String sql = "SELECT COUNT(*) FROM alunos WHERE sala_id = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, salaId);
            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao contar alunos da sala! " + e.getMessage());
        }
        return 0;
    }

    public Sala buscarporId(int id) {
        String sql = "SELECT * FROM salas WHERE id = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    Sala sala = new Sala();
                    sala.setId(resultado.getInt("id"));
                    sala.setNome(resultado.getString("nome"));
                    return sala;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar sala! " + e.getMessage());
        }
        return null;
    }

    public List<Sala> listar() {
        List<Sala> salas = new ArrayList<>();
        String sql = "SELECT * FROM salas ORDER BY nome";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {
            while (resultado.next()) {
                Sala sala = new Sala();
                sala.setId(resultado.getInt("id"));
                sala.setNome(resultado.getString("nome"));
                salas.add(sala);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar salas! " + e.getMessage());
        }
        return salas;
    }

    public List<Sala> buscarPorNome(String nome) {
        List<Sala> salas = new ArrayList<>();
        String sql = "SELECT * FROM salas WHERE LOWER(nome) LIKE LOWER(?) ORDER BY nome";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            try (ResultSet resultado = stmt.executeQuery()) {
                while (resultado.next()) {
                    Sala sala = new Sala();
                    sala.setId(resultado.getInt("id"));
                    sala.setNome(resultado.getString("nome"));
                    salas.add(sala);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar salas por nome! " + e.getMessage());
        }
        return salas;
    }
}
