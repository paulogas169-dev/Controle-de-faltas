package DAO;

import Conexao.Conexao;
import Escola.Frequencia;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FrequenciaDao {

    public boolean salvar(Frequencia frequencia) {

        String sql = """
        INSERT INTO frequencia (aluno_id, data, presente)
        VALUES (?, ?, ?)
        """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, frequencia.getAlunoId());
            stmt.setDate(2, frequencia.getData());
            stmt.setBoolean(3, frequencia.isPresente());

            stmt.executeUpdate();

            System.out.println("Frequência salva com sucesso!");
            return true;

        } catch (SQLException e) {
            System.out.println(
                    "Erro ao salvar frequência: " + e.getMessage()
            );
            return false;
        }
    }

    public List<Frequencia> listarporAluno(int alunoid) {
        List<Frequencia> frequencias = new ArrayList<>();

        String sql = "SELECT * FROM frequencia WHERE aluno_id = ? ORDER BY data";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, alunoid);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Frequencia frequencia = new Frequencia();

                    frequencia.setId(rs.getInt("id"));
                    frequencia.setAlunoId(rs.getInt("aluno_id"));
                    frequencia.setData(rs.getDate("data"));
                    frequencia.setPresente(rs.getBoolean("presente"));

                    frequencias.add(frequencia);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar frequencia: " + e.getMessage());
        }

        return frequencias;
    }

    public List<Frequencia> listarTodas() {
        List<Frequencia> lista = new ArrayList<>();
        String sql = """
            SELECT f.id, f.aluno_id, f.data, f.presente, a.nome AS aluno_nome, s.nome AS sala_nome
            FROM frequencia f
            JOIN alunos a ON f.aluno_id = a.id
            JOIN salas s ON a.sala_id = s.id
            ORDER BY f.data DESC, s.nome ASC, a.nome ASC
            """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Frequencia f = new Frequencia();
                f.setId(rs.getInt("id"));
                f.setAlunoId(rs.getInt("aluno_id"));
                f.setData(rs.getDate("data"));
                f.setPresente(rs.getBoolean("presente"));
                f.setAlunoNome(rs.getString("aluno_nome"));
                f.setSalaNome(rs.getString("sala_nome"));
                lista.add(f);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar frequencias: " + e.getMessage());
        }

        return lista;
    }

    public List<Date> listarDatasDisponiveis() {
        List<Date> datas = new ArrayList<>();
        String sql = "SELECT DISTINCT data FROM frequencia ORDER BY data DESC";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                datas.add(rs.getDate("data"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar datas: " + e.getMessage());
        }

        return datas;
    }

    public List<Frequencia> consultar(Date data, Integer salaId, String nomeAluno, Boolean presente) {
        List<Frequencia> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT f.id, f.aluno_id, f.data, f.presente, a.nome AS aluno_nome, s.nome AS sala_nome
            FROM frequencia f
            JOIN alunos a ON f.aluno_id = a.id
            JOIN salas s ON a.sala_id = s.id
            WHERE 1=1
            """);

        List<Object> parametros = new ArrayList<>();

        if (data != null) {
            sql.append(" AND f.data = ?");
            parametros.add(data);
        }
        if (salaId != null && salaId > 0) {
            sql.append(" AND s.id = ?");
            parametros.add(salaId);
        }
        if (nomeAluno != null && !nomeAluno.trim().isEmpty()) {
            sql.append(" AND a.nome LIKE ?");
            parametros.add("%" + nomeAluno.trim() + "%");
        }
        if (presente != null) {
            sql.append(" AND f.presente = ?");
            parametros.add(presente);
        }

        sql.append(" ORDER BY f.data DESC, s.nome ASC, a.nome ASC");

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                Object param = parametros.get(i);
                if (param instanceof Date) {
                    stmt.setDate(i + 1, (Date) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof Boolean) {
                    stmt.setBoolean(i + 1, (Boolean) param);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Frequencia f = new Frequencia();
                    f.setId(rs.getInt("id"));
                    f.setAlunoId(rs.getInt("aluno_id"));
                    f.setData(rs.getDate("data"));
                    f.setPresente(rs.getBoolean("presente"));
                    f.setAlunoNome(rs.getString("aluno_nome"));
                    f.setSalaNome(rs.getString("sala_nome"));
                    lista.add(f);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar frequencias: " + e.getMessage());
        }

        return lista;
    }

    public boolean atualizarPresenca(int id, boolean presente) {
        String sql = "UPDATE frequencia SET presente = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, presente);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar presenca: " + e.getMessage());
            return false;
        }
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM frequencia WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao deletar frequencia: " + e.getMessage());
            return false;
        }
    }


    public boolean registrarPresencaRFID(int alunoId) {
        Date hoje = Date.valueOf(java.time.LocalDate.now());

        String consulta = """
        SELECT id FROM frequencia
        WHERE aluno_id = ? AND data = ?
        """;

        try (Connection conn = Conexao.conectar()) {

            // Verifica se o aluno já tem registro hoje
            try (PreparedStatement stmt = conn.prepareStatement(consulta)) {
                stmt.setInt(1, alunoId);
                stmt.setDate(2, hoje);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Presença já registrada hoje.");
                        return false;
                    }
                }
            }

            // Registra a presença
            String sql = """
            INSERT INTO frequencia (aluno_id, data, presente)
            VALUES (?, ?, ?)
            """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, alunoId);
                stmt.setDate(2, hoje);
                stmt.setBoolean(3, true);

                stmt.executeUpdate();

                System.out.println("Presença registrada com sucesso!");
                return true;
            }

        } catch (SQLException e) {
            // O índice UNIQUE também pode impedir duplicidade
            if ("23000".equals(e.getSQLState())) {
                System.out.println("Presença já registrada hoje.");
            } else {
                System.out.println("Erro ao registrar presença: " + e.getMessage());
            }

            return false;
        }
    }

}
