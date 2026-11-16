package Interface;

import Servico.ServicoFaltaAutomatica;

public class Interface {
    public static void main(String[] args) {
        Configuracoes.aplicarTemaSalvo();
        ServicoFaltaAutomatica.iniciar();
        new Tela();
    }
}

