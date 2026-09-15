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

    public Sala buscarporId (int id) {

        String sql = "SELECT * FROM salas WHERE id = ?";

        try{
            Connection conexao = Conexao.conectar();

            PreparedStatement stmt = conexao.prepareStatement(sql);

            stmt.setInt(1, id );

            ResultSet resultado = stmt.executeQuery();

            if (resultado.next()){
                Sala sala = new Sala();
                sala.setId(resultado.getInt("id"));
                sala.setNome(resultado.getString("nome"));

                return sala;

            }

        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar sala!" + e.getMessage());
        }
        return null;

    }

    public List<Sala> listar() {

        List<Sala> salas = new ArrayList<>();

        String sql = "SELECT * FROM salas";

        try {
            Connection conexao = Conexao.conectar();

            PreparedStatement stmt = conexao.prepareStatement(sql);

            ResultSet resultado = stmt.executeQuery();

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
}
