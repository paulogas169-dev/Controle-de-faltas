package Servico;

import DAO.FrequenciaDao;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Serviço em segundo plano responsável por monitorar os registros de presença.
 * Alunos que registraram presença mas não registraram saída até o fim do dia recebem
 * falta automaticamente (presente = 0).
 */
public class ServicoFaltaAutomatica {

    private static final FrequenciaDao frequenciaDao = new FrequenciaDao();
    private static ScheduledExecutorService executor;
    private static volatile boolean iniciado = false;

    // Horário de corte no fim do dia (a partir de 23:00 ou fim do dia)
    private static final LocalTime HORARIO_CORTE_FIM_DO_DIA = LocalTime.of(23, 0);

    public static synchronized void iniciar() {
        if (iniciado) {
            return;
        }

        // Execução imediata para garantir dias passados
        verificarDiasPassados();

        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ServicoFaltaAutomatica-Monitor");
            t.setDaemon(true);
            return t;
        });

        // Executa a verificação a cada 10 minutos
        executor.scheduleAtFixedRate(
                ServicoFaltaAutomatica::executarRotinaVerificacao,
                1,
                10,
                TimeUnit.MINUTES
        );

        iniciado = true;
        System.out.println("ServicoFaltaAutomatica iniciado com sucesso.");
    }

    public static void verificarDiasPassados() {
        try {
            int alterados = frequenciaDao.aplicarFaltaAutomaticaDiasAnteriores();
            if (alterados > 0) {
                System.out.println("ServicoFaltaAutomatica: " + alterados + " registro(s) anterior(es) sem saída receberam falta.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar faltas automáticas de dias passados: " + e.getMessage());
        }
    }

    public static void executarRotinaVerificacao() {
        verificarDiasPassados();

        // Se já passou do horário de corte no fim do dia, aplica também para hoje
        LocalTime agora = LocalTime.now();
        if (agora.isAfter(HORARIO_CORTE_FIM_DO_DIA)) {
            try {
                Date hoje = Date.valueOf(LocalDate.now());
                int faltasAplicadas = frequenciaDao.aplicarFaltaSemSaidaNaData(hoje);
                if (faltasAplicadas > 0) {
                    System.out.println("ServicoFaltaAutomatica (Fim do dia): "
                            + faltasAplicadas + " aluno(s) sem registro de saída hoje receberam falta.");
                }
            } catch (Exception e) {
                System.err.println("Erro ao aplicar faltas de fim de dia hoje: " + e.getMessage());
            }
        }
    }

    public static int aplicarParaHojeAgora() {
        Date hoje = Date.valueOf(LocalDate.now());
        return frequenciaDao.aplicarFaltaSemSaidaNaData(hoje);
    }

    public static synchronized void parar() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
        iniciado = false;
    }
}
