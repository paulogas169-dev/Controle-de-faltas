package testeserial;
import com.fazecast.jSerialComm.SerialPort;

public class TesteSerial {

    public static void main(String[] args) {
        SerialPort[] portas = SerialPort.getCommPorts();

        for (SerialPort porta : portas) {
            System.out.println(porta.getSystemPortName());
        }
    }
}
