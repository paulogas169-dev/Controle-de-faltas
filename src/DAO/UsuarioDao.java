package DAO;

import Conexao.Conexao;
import Escola.Usuario;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDao {
    public boolean salvar(Usuario usuario) {

        String sql = "INSERT INTO usuarios (nome, login, senha) VALUES ( ?, ?, ?)";

        try {
            Connection conexao = Conexao.conectar();

            PreparedStatement stmt = conexao.prepareStatement(sql);

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, usuario.getSenha());

            stmt.executeUpdate();




            stmt.close();
            conexao.close();

            return true;
        } catch (SQLException e){

            System.out.println("Erro ao cadastrar usuário: " + e.getMessage());

            if (e.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(null, "Esse login já está cadastrado! ");
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao cadastrar usuário");
            }
            return false;
        }
    }

    public Usuario login (String login, String senha) {

        String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ?";

        try{
            Connection conexao = Conexao.conectar();

            PreparedStatement stmt = conexao.prepareStatement(sql);

            stmt.setString(1, login);
            stmt.setString(2, senha);

            ResultSet resultado = stmt.executeQuery();

            if (resultado.next()) {
                Usuario usuario = new Usuario();

                usuario.setId(resultado.getInt("id"));
                usuario.setNome(resultado.getString("nome"));
                usuario.setLogin(resultado.getString("login"));
                usuario.setSenha(resultado.getString("senha"));

                return usuario;
            }

            stmt.close();
            conexao.close();

        } catch (SQLException e) {
            System.out.println("Erro ao realizar Login: " + e.getMessage());
        }
        return null;
    }
}
