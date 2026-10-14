
package testeserial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortTimeoutException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class LeitorRfid {

    private SerialPort porta;
    private Thread threadLeitura;
    private volatile boolean executando = false;

    public boolean iniciar(
            Consumer<String> aoLerCartao,
            Consumer<String> aoOcorrerErro) {

        if (executando) {
            return true;
        }

        porta = SerialPort.getCommPort("COM4");
        porta.setBaudRate(9600);

        // Aguarda dados sem transformar cada timeout em erro.
        porta.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                500,
                0
        );

        if (!porta.openPort()) {
            aoOcorrerErro.accept(
                    "Não foi possível abrir a COM4. "
                            + "Feche o Monitor Serial do Arduino."
            );
            return false;
        }

        executando = true;

        threadLeitura = new Thread(() -> {
            try (BufferedReader leitor = new BufferedReader(
                    new InputStreamReader(porta.getInputStream()))) {

                while (executando) {
                    String uid;

                    try {
                        uid = leitor.readLine();
                    } catch (SerialPortTimeoutException e) {
                        // Normal: ainda não chegou um cartão.
                        continue;
                    }

                    if (uid == null) {
                        continue;
                    }

                    uid = uid.trim().toUpperCase();

                    if (uid.isEmpty()
                            || uid.equals("LEITOR_PRONTO")) {
                        continue;
                    }

                    if (executando) {
                        aoLerCartao.accept(uid);
                    }
                }

            } catch (Exception e) {
                if (executando) {
                    aoOcorrerErro.accept(
                            "Erro na leitura RFID: " + e.getMessage()
                    );
                }
            } finally {
                executando = false;

                if (porta != null && porta.isOpen()) {
                    porta.closePort();
                }
            }
        });

        threadLeitura.setName("Leitura-RFID");
        threadLeitura.setDaemon(true);
        threadLeitura.start();

        return true;
    }

    public void parar() {
        executando = false;

        if (porta != null && porta.isOpen()) {
            porta.closePort();
        }
    }

    public boolean isExecutando() {
        return executando;
    }
}
