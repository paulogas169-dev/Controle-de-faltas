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
        String sql = """
            SELECT alunos.id, alunos.nome, alunos.uid_rfid, alunos.sala_id, salas.nome AS sala
            FROM alunos
            LEFT JOIN salas ON alunos.sala_id = salas.id
            ORDER BY alunos.nome
            """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Aluno aluno = new Aluno();
                aluno.setId(rs.getInt("id"));
                aluno.setNome(rs.getString("nome"));
                aluno.setUid_rfid(rs.getString("uid_rfid"));
                aluno.setSala_id(rs.getInt("sala_id"));
                aluno.setSala(rs.getString("sala"));
                alunos.add(aluno);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar todos os alunos: " + e.getMessage());
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


    public List<Aluno> listarPorSala(int salaId) {
        List<Aluno> alunos = new ArrayList<>();

        String sql = """
        SELECT a.id, a.nome, a.sala_id, s.nome AS sala
        FROM alunos a
        LEFT JOIN salas s ON a.sala_id = s.id
        WHERE a.sala_id = ?
        ORDER BY a.nome
        """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Aluno aluno = new Aluno();

                    aluno.setId(rs.getInt("id"));
                    aluno.setNome(rs.getString("nome"));
                    aluno.setSala_id(rs.getInt("sala_id"));
                    aluno.setSala(rs.getString("sala"));

                    alunos.add(aluno);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar alunos: " + e.getMessage());
        }

        return alunos;
    }


    public List<Aluno> buscarPorNome(String nome) {
        List<Aluno> alunos = new ArrayList<>();
        String sql = """
            SELECT alunos.*, salas.nome AS nome_sala
            FROM alunos
            LEFT JOIN salas ON alunos.sala_id = salas.id
            WHERE alunos.nome LIKE ?
            ORDER BY alunos.nome
            """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + (nome != null ? nome : "") + "%");
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    Aluno aluno = new Aluno();
                    aluno.setId(result.getInt("id"));
                    aluno.setNome(result.getString("nome"));
                    aluno.setUid_rfid(result.getString("uid_rfid"));
                    aluno.setSala_id(result.getInt("sala_id"));
                    aluno.setSala(result.getString("nome_sala"));
                    alunos.add(aluno);
                }
            }
        } catch (SQLException e) {
            System.out.println("ERRO ao buscar por nome: " + e.getMessage());
        }
        return alunos;
    }

    public Aluno buscarPorId(int id) {
        String sql = """
            SELECT a.id, a.nome, a.uid_rfid, a.sala_id, s.nome AS sala
            FROM alunos a
            LEFT JOIN salas s ON a.sala_id = s.id
            WHERE a.id = ?
            LIMIT 1
            """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Aluno aluno = new Aluno();
                    aluno.setId(rs.getInt("id"));
                    aluno.setNome(rs.getString("nome"));
                    aluno.setUid_rfid(rs.getString("uid_rfid"));
                    aluno.setSala_id(rs.getInt("sala_id"));
                    aluno.setSala(rs.getString("sala"));
                    return aluno;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar aluno por ID: " + e.getMessage());
        }
        return null;
    }

    public Aluno buscarPorUid(String uid) {
        if (uid == null || uid.isBlank()) return null;
        String uidFormatado = uid.trim().toUpperCase();
        String uidSemEspacos = uidFormatado.replace(" ", "").replace(":", "");

        String sql = """
        SELECT a.id, a.nome, a.uid_rfid, a.sala_id, s.nome AS sala
        FROM alunos a
        LEFT JOIN salas s ON a.sala_id = s.id
        WHERE a.uid_rfid = ? 
           OR UPPER(REPLACE(REPLACE(a.uid_rfid, ' ', ''), ':', '')) = ?
           OR UPPER(a.uid_rfid) = ?
        LIMIT 1
        """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, uidFormatado);
            stmt.setString(2, uidSemEspacos);
            stmt.setString(3, uidSemEspacos);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Aluno aluno = new Aluno();
                    aluno.setId(rs.getInt("id"));
                    aluno.setNome(rs.getString("nome"));
                    aluno.setUid_rfid(rs.getString("uid_rfid"));
                    aluno.setSala_id(rs.getInt("sala_id"));
                    aluno.setSala(rs.getString("sala"));
                    return aluno;
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar UID: " + e.getMessage());
        }

        return null;
    }

    public Aluno buscarOutroPorUid(String uid, int excetoAlunoId) {
        if (uid == null || uid.isBlank()) return null;
        String uidFormatado = uid.trim().toUpperCase();
        String uidSemEspacos = uidFormatado.replace(" ", "").replace(":", "");

        String sql = """
        SELECT a.id, a.nome, a.uid_rfid, a.sala_id, s.nome AS sala
        FROM alunos a
        LEFT JOIN salas s ON a.sala_id = s.id
        WHERE (a.uid_rfid = ? 
           OR UPPER(REPLACE(REPLACE(a.uid_rfid, ' ', ''), ':', '')) = ?
           OR UPPER(a.uid_rfid) = ?)
           AND a.id <> ?
        LIMIT 1
        """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, uidFormatado);
            stmt.setString(2, uidSemEspacos);
            stmt.setString(3, uidSemEspacos);
            stmt.setInt(4, excetoAlunoId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Aluno aluno = new Aluno();
                    aluno.setId(rs.getInt("id"));
                    aluno.setNome(rs.getString("nome"));
                    aluno.setUid_rfid(rs.getString("uid_rfid"));
                    aluno.setSala_id(rs.getInt("sala_id"));
                    aluno.setSala(rs.getString("sala"));
                    return aluno;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar duplicidade de UID: " + e.getMessage());
        }
        return null;
    }

    public boolean vincularCartao(int alunoId, String uid) {
        String sql = "UPDATE alunos SET uid_rfid = ? WHERE id = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (uid == null || uid.isBlank()) {
                stmt.setNull(1, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(1, uid.trim().toUpperCase());
            }
            stmt.setInt(2, alunoId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao vincular cartão ao aluno: " + e.getMessage());
            return false;
        }
    }

    public boolean desvincularCartao(int alunoId) {
        return vincularCartao(alunoId, null);
    }



}

