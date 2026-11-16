package DAO;

import Conexao.Conexao;
import Escola.Frequencia;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FrequenciaDao {

    static {
        verificarEstruturaTabela();
    }

    public static void verificarEstruturaTabela() {
        try (Connection conn = Conexao.conectar()) {
            if (conn != null) {
                var meta = conn.getMetaData();
                try (ResultSet rs = meta.getColumns(null, null, "frequencia", "entrada_hora")) {
                    if (!rs.next()) {
                        try (PreparedStatement stmt = conn.prepareStatement(
                                "ALTER TABLE frequencia ADD COLUMN entrada_hora DATETIME NULL AFTER presente")) {
                            stmt.executeUpdate();
                            System.out.println("Coluna 'entrada_hora' adicionada à tabela 'frequencia'.");
                        }
                    }
                }
                try (ResultSet rs = meta.getColumns(null, null, "frequencia", "saida_hora")) {
                    if (!rs.next()) {
                        try (PreparedStatement stmt = conn.prepareStatement(
                                "ALTER TABLE frequencia ADD COLUMN saida_hora DATETIME NULL")) {
                            stmt.executeUpdate();
                            System.out.println("Coluna 'saida_hora' adicionada à tabela 'frequencia'.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Aviso ao verificar estrutura da tabela: " + e.getMessage());
        }
    }

    public boolean salvar(Frequencia frequencia) {
        String sql = """
        INSERT INTO frequencia (aluno_id, data, presente, entrada_hora, saida_hora)
        VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Timestamp entrada = frequencia.getEntradaHora();
            if (entrada == null && Boolean.TRUE.equals(frequencia.isPresente())) {
                entrada = Timestamp.valueOf(LocalDateTime.now());
            }

            stmt.setInt(1, frequencia.getAlunoId());
            stmt.setDate(2, frequencia.getData());
            stmt.setBoolean(3, frequencia.isPresente());
            stmt.setTimestamp(4, entrada);
            stmt.setTimestamp(5, frequencia.getSaidaHora());

            stmt.executeUpdate();
            System.out.println("Frequência salva com sucesso!");
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao salvar frequência: " + e.getMessage());
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
                    frequencia.setEntradaHora(rs.getTimestamp("entrada_hora"));
                    frequencia.setSaidaHora(rs.getTimestamp("saida_hora"));
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
            SELECT f.id, f.aluno_id, f.data, f.presente, f.entrada_hora, f.saida_hora, a.nome AS aluno_nome, s.nome AS sala_nome,
                   (SELECT COUNT(*) FROM frequencia f2 WHERE f2.aluno_id = f.aluno_id AND f2.presente = 0) AS total_faltas
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
                f.setEntradaHora(rs.getTimestamp("entrada_hora"));
                f.setSaidaHora(rs.getTimestamp("saida_hora"));
                f.setAlunoNome(rs.getString("aluno_nome"));
                f.setSalaNome(rs.getString("sala_nome"));
                f.setTotalFaltasAluno(rs.getInt("total_faltas"));
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
            SELECT f.id, f.aluno_id, f.data, f.presente, f.entrada_hora, f.saida_hora, a.nome AS aluno_nome, s.nome AS sala_nome,
                   (SELECT COUNT(*) FROM frequencia f2 WHERE f2.aluno_id = f.aluno_id AND f2.presente = 0) AS total_faltas
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
                    f.setEntradaHora(rs.getTimestamp("entrada_hora"));
                    f.setSaidaHora(rs.getTimestamp("saida_hora"));
                    f.setAlunoNome(rs.getString("aluno_nome"));
                    f.setSalaNome(rs.getString("sala_nome"));
                    f.setTotalFaltasAluno(rs.getInt("total_faltas"));
                    lista.add(f);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar frequencias: " + e.getMessage());
        }

        return lista;
    }

    public int contarPresentesNaData(Date data) {
        if (data == null) {
            return 0;
        }
        String sql = "SELECT COUNT(*) FROM frequencia WHERE data = ? AND presente = 1";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, data);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao contar presentes na data: " + e.getMessage());
        }
        return 0;
    }

    public int contarFaltasNaData(Date data) {
        if (data == null) {
            return 0;
        }
        String sql = "SELECT COUNT(*) FROM frequencia WHERE data = ? AND presente = 0";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, data);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao contar faltas na data: " + e.getMessage());
        }
        return 0;
    }

    public int contarSaidasNaData(Date data) {
        if (data == null) {
            return 0;
        }
        String sql = "SELECT COUNT(*) FROM frequencia WHERE data = ? AND saida_hora IS NOT NULL";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, data);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao contar saídas na data: " + e.getMessage());
        }
        return 0;
    }

    public int contarPresentesSemSaidaNaData(Date data) {
        if (data == null) {
            return 0;
        }
        String sql = "SELECT COUNT(*) FROM frequencia WHERE data = ? AND presente = 1 AND saida_hora IS NULL";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, data);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao contar presentes sem saída: " + e.getMessage());
        }
        return 0;
    }

    public int contarPresentesHoje() {
        return contarPresentesNaData(Date.valueOf(LocalDate.now()));
    }

    public int contarFaltasHoje() {
        return contarFaltasNaData(Date.valueOf(LocalDate.now()));
    }

    public int contarSaidasHoje() {
        return contarSaidasNaData(Date.valueOf(LocalDate.now()));
    }

    public int contarFaltasPorAluno(int alunoId) {
        String sql = "SELECT COUNT(*) FROM frequencia WHERE aluno_id = ? AND presente = 0";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, alunoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao contar faltas do aluno: " + e.getMessage());
        }
        return 0;
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

    public boolean limparTodosOsRegistros() {
        String sql = "DELETE FROM frequencia";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
            System.out.println("Todos os registros de frequência foram excluídos.");
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao limpar registros: " + e.getMessage());
            return false;
        }
    }

    public static class ResultadoPresenca {
        private final boolean sucesso;
        private final boolean jaRegistrada;
        private final String mensagem;
        private final Timestamp horaEntrada;

        public ResultadoPresenca(boolean sucesso, boolean jaRegistrada, String mensagem, Timestamp horaEntrada) {
            this.sucesso = sucesso;
            this.jaRegistrada = jaRegistrada;
            this.mensagem = mensagem;
            this.horaEntrada = horaEntrada;
        }

        public boolean isSucesso() {
            return sucesso;
        }

        public boolean isJaRegistrada() {
            return jaRegistrada;
        }

        public String getMensagem() {
            return mensagem;
        }

        public Timestamp getHoraEntrada() {
            return horaEntrada;
        }

        public String getHoraFormatada() {
            if (horaEntrada == null) return "—";
            return horaEntrada.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
    }

    public ResultadoPresenca registrarPresencaRFIDDetalhado(int alunoId) {
        Date hoje = Date.valueOf(LocalDate.now());
        String consulta = "SELECT id, entrada_hora FROM frequencia WHERE aluno_id = ? AND data = ?";

        try (Connection conn = Conexao.conectar()) {
            if (conn == null) {
                return new ResultadoPresenca(false, false, "Falha de conexão com o banco de dados.", null);
            }

            try (PreparedStatement stmt = conn.prepareStatement(consulta)) {
                stmt.setInt(1, alunoId);
                stmt.setDate(2, hoje);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Timestamp existente = rs.getTimestamp("entrada_hora");
                        String horaStr = existente != null
                                ? existente.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                                : "—";
                        return new ResultadoPresenca(false, true, "Presença já registrada hoje às " + horaStr + ".", existente);
                    }
                }
            }

            Timestamp agora = Timestamp.valueOf(LocalDateTime.now());
            String sql = "INSERT INTO frequencia (aluno_id, data, presente, entrada_hora) VALUES (?, ?, 1, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, alunoId);
                stmt.setDate(2, hoje);
                stmt.setTimestamp(3, agora);
                stmt.executeUpdate();

                String horaStr = agora.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                return new ResultadoPresenca(true, false, "Presença registrada com sucesso às " + horaStr + "!", agora);
            }

        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState())) {
                return new ResultadoPresenca(false, true, "Presença já registrada hoje.", null);
            }
            return new ResultadoPresenca(false, false, "Erro ao registrar presença: " + e.getMessage(), null);
        }
    }

    public boolean registrarPresencaRFID(int alunoId) {
        ResultadoPresenca res = registrarPresencaRFIDDetalhado(alunoId);
        return res.isSucesso();
    }

    public static class ResultadoSaida {
        private final boolean sucesso;
        private final boolean jaRegistrada;
        private final String mensagem;
        private final Timestamp horaSaida;

        public ResultadoSaida(boolean sucesso, boolean jaRegistrada, String mensagem, Timestamp horaSaida) {
            this.sucesso = sucesso;
            this.jaRegistrada = jaRegistrada;
            this.mensagem = mensagem;
            this.horaSaida = horaSaida;
        }

        public boolean isSucesso() {
            return sucesso;
        }

        public boolean isJaRegistrada() {
            return jaRegistrada;
        }

        public String getMensagem() {
            return mensagem;
        }

        public Timestamp getHoraSaida() {
            return horaSaida;
        }

        public String getHoraFormatada() {
            if (horaSaida == null) return "—";
            return horaSaida.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
    }

    public ResultadoSaida registrarSaidaRFID(int alunoId) {
        Date hoje = Date.valueOf(LocalDate.now());
        String consulta = "SELECT id, presente, saida_hora FROM frequencia WHERE aluno_id = ? AND data = ?";

        try (Connection conn = Conexao.conectar()) {
            if (conn == null) {
                return new ResultadoSaida(false, false, "Falha de conexão com o banco de dados.", null);
            }

            try (PreparedStatement stmt = conn.prepareStatement(consulta)) {
                stmt.setInt(1, alunoId);
                stmt.setDate(2, hoje);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        Timestamp saidaExistente = rs.getTimestamp("saida_hora");

                        if (saidaExistente != null) {
                            String horaStr = saidaExistente.toLocalDateTime()
                                    .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                            return new ResultadoSaida(false, true, "Saída já registrada hoje às " + horaStr + ".", saidaExistente);
                        }

                        Timestamp agora = Timestamp.valueOf(LocalDateTime.now());
                        String sqlUpdate = "UPDATE frequencia SET saida_hora = ?, presente = 1 WHERE id = ?";
                        try (PreparedStatement stmtUp = conn.prepareStatement(sqlUpdate)) {
                            stmtUp.setTimestamp(1, agora);
                            stmtUp.setInt(2, id);
                            stmtUp.executeUpdate();
                            String horaStr = agora.toLocalDateTime()
                                    .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                            return new ResultadoSaida(true, false, "Saída registrada com sucesso às " + horaStr + "!", agora);
                        }
                    } else {
                        Timestamp agora = Timestamp.valueOf(LocalDateTime.now());
                        String sqlInsert = "INSERT INTO frequencia (aluno_id, data, presente, entrada_hora, saida_hora) VALUES (?, ?, 1, ?, ?)";
                        try (PreparedStatement stmtIn = conn.prepareStatement(sqlInsert)) {
                            stmtIn.setInt(1, alunoId);
                            stmtIn.setDate(2, hoje);
                            stmtIn.setTimestamp(3, agora);
                            stmtIn.setTimestamp(4, agora);
                            stmtIn.executeUpdate();
                            String horaStr = agora.toLocalDateTime()
                                    .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                            return new ResultadoSaida(true, false, "Presença e saída registradas com sucesso às " + horaStr + "!", agora);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            return new ResultadoSaida(false, false, "Erro ao registrar saída: " + e.getMessage(), null);
        }
    }

    public boolean registrarSaidaManual(int alunoId, Date data) {
        String consulta = "SELECT id, saida_hora FROM frequencia WHERE aluno_id = ? AND data = ?";
        try (Connection conn = Conexao.conectar()) {
            if (conn == null) return false;

            try (PreparedStatement stmt = conn.prepareStatement(consulta)) {
                stmt.setInt(1, alunoId);
                stmt.setDate(2, data);
                try (ResultSet rs = stmt.executeQuery()) {
                    Timestamp agora = Timestamp.valueOf(LocalDateTime.now());
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        String sqlUpdate = "UPDATE frequencia SET saida_hora = ?, presente = 1 WHERE id = ?";
                        try (PreparedStatement stmtUp = conn.prepareStatement(sqlUpdate)) {
                            stmtUp.setTimestamp(1, agora);
                            stmtUp.setInt(2, id);
                            return stmtUp.executeUpdate() > 0;
                        }
                    } else {
                        String sqlInsert = "INSERT INTO frequencia (aluno_id, data, presente, entrada_hora, saida_hora) VALUES (?, ?, 1, ?, ?)";
                        try (PreparedStatement stmtIn = conn.prepareStatement(sqlInsert)) {
                            stmtIn.setInt(1, alunoId);
                            stmtIn.setDate(2, data);
                            stmtIn.setTimestamp(3, agora);
                            stmtIn.setTimestamp(4, agora);
                            return stmtIn.executeUpdate() > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao registrar saída manual: " + e.getMessage());
            return false;
        }
    }

    public Frequencia buscarFrequenciaHoje(int alunoId) {
        Date hoje = Date.valueOf(LocalDate.now());
        String sql = "SELECT * FROM frequencia WHERE aluno_id = ? AND data = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, alunoId);
            stmt.setDate(2, hoje);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Frequencia f = new Frequencia();
                    f.setId(rs.getInt("id"));
                    f.setAlunoId(rs.getInt("aluno_id"));
                    f.setData(rs.getDate("data"));
                    f.setPresente(rs.getBoolean("presente"));
                    f.setEntradaHora(rs.getTimestamp("entrada_hora"));
                    f.setSaidaHora(rs.getTimestamp("saida_hora"));
                    return f;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar frequência hoje: " + e.getMessage());
        }
        return null;
    }

    public int aplicarFaltaAutomaticaDiasAnteriores() {
        String sql = """
            UPDATE frequencia 
            SET presente = 0 
            WHERE data < CURDATE() 
              AND presente = 1 
              AND saida_hora IS NULL
        """;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int linhas = stmt.executeUpdate();
            if (linhas > 0) {
                System.out.println("Faltas automáticas aplicadas a " + linhas + " registro(s) anterior(es) sem saída.");
            }
            return linhas;
        } catch (SQLException e) {
            System.out.println("Erro ao aplicar faltas automáticas de dias anteriores: " + e.getMessage());
            return 0;
        }
    }

    public int aplicarFaltaSemSaidaNaData(Date data) {
        if (data == null) return 0;
        String sql = """
            UPDATE frequencia 
            SET presente = 0 
            WHERE data = ? 
              AND presente = 1 
              AND saida_hora IS NULL
        """;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, data);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao aplicar falta sem saída na data: " + e.getMessage());
            return 0;
        }
    }
}
