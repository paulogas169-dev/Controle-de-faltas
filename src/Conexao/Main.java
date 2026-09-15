package Conexao;

import DAO.SalaDao;
import DAO.UsuarioDao;
import Escola.Aluno;
import DAO.AlunoDao;
import Escola.Sala;
import Escola.Usuario;

import java.util.Scanner;

public class Main {
    public static void main (String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("------ MENU -----");
            System.out.println("1- Cadastrar aluno");
            System.out.println("2- Consultar Sala");
            System.out.println("3- Cadastrar Usuario");
            System.out.println("4- Fazer login");
            System.out.println("0- Sair");

            System.out.println("Escola oque deseja cadastrar: ");
            int opcao = sc.nextInt();
            sc.nextLine();

            if (opcao == 1){
                System.out.println("Digite o nome do aluno: ");
                String nome = sc.nextLine();

                System.out.println("Digite o ID da sala");
                int sala_id = sc.nextInt();

                Aluno aluno = new Aluno();
                aluno.setNome(nome);
                aluno.setSala_id(sala_id);

                AlunoDao alunoDao = new AlunoDao();

                alunoDao.salvar(aluno);
            } else if (opcao == 2) {

                System.out.println("Digite o id da sala: ");
                int salaId = sc.nextInt();

                SalaDao salaDao = new SalaDao();

                Sala sala = salaDao.buscarporId(salaId);

                if (sala != null){
                    System.out.println("Sala: " + sala.getNome());
                } else {
                    System.out.println("Sala não encontrada");
                }
            } else if (opcao == 3) {
                System.out.println("Digite o nome de Usuário: ");
                String nome = sc.nextLine();

                System.out.println("Digite o Login para Cadastrar: ");
                String login = sc.nextLine();

                System.out.println("Digite a senha para cadastro: ");
                String senha = sc.nextLine();

                Usuario usuario = new Usuario();

                usuario.setNome(nome);
                usuario.setLogin(login);
                usuario.setSenha(senha);

                UsuarioDao usuarioDao = new UsuarioDao();

                usuarioDao.salvar(usuario);
            } else if (opcao == 4) {
                System.out.println("\n ---------LOGIN-----------");

                System.out.println("Login: ");
                String login = sc.nextLine();

                System.out.println("Senha: ");
                String senha = sc.nextLine();

                UsuarioDao usuarioDao = new UsuarioDao();

                Usuario usuario = usuarioDao.login(login, senha);

                if (usuario != null) {
                    System.out.println("Login Realizado com sucesso!!");

                    System.out.println("Bem-vinda," + usuario.getNome());
                } else {
                    System.out.println("Login ou Senha incorreto");
                }

            } else if (opcao == 0) {
                System.out.println("Saindo do sistema");
                break;

            }
        }

    }
}


