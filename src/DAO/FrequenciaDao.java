package DAO;

import Conexao.Conexao;
import Escola.Frequencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FrequenciaDao {

    public void salvar(Frequencia frequencia) {

        try {


            String sql = "INSERT INTO frequencia (aluno_id, data, presente) VALUES (?,?,?)";

            Connection conn = Conexao.conectar();

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, frequencia.getAlunoId());
            stmt.setDate(2, frequencia.getData());
            stmt.setBoolean( 3,frequencia.isPresente());

            stmt.executeUpdate();

            System.out.println("Frequencia salva com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao salvar frequencia" +
                    e.getMessage());
        }
    }

    public List<Frequencia> listarporAluno(int alunoid) {
        List<Frequencia> frequencias = new ArrayList<>();

        String sql = "SELECT * FROM frequencia WHERE aluno_id = ? ORDER BY data";

        try {
            Connection conn = Conexao.conectar();

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, alunoid);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Frequencia frequencia = new Frequencia();

                frequencia.setId(rs.getInt("id"));
                frequencia.setAlunoId(rs.getInt("aluno_id"));
                frequencia.setData(rs.getDate("data"));
                frequencia.setPresente(rs.getBoolean("presente"));

                frequencias.add(frequencia);

            }
        }catch (SQLException e) {
            System.out.println("Erro ao consultar frequencia" + e.getMessage());
        }

        return frequencias;
    }
}
