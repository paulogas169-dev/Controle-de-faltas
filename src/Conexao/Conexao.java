package Conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    public static final String URL = "jdbc:mysql://localhost:3306/controle_faltas";
    private static final String USUARIO = "root";
    private static final String SENHA = "24312345";

    public static Connection conectar() {

        try {
         Connection conexao = DriverManager.getConnection(URL, USUARIO, SENHA);

            System.out.println("Conextado com sucesso!!");

            return conexao;

        } catch (SQLException e){
            System.out.println("ERRO: " + e.getMessage());

            return null;
        }
    }
}
