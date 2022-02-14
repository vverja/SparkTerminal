import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.nio.charset.StandardCharsets;

public class Terminal {
    private SerialPort serialPort;
    private String data;
    public Terminal(String port) {
        try {
            serialPort = new SerialPort(port);
            serialPort.setParams(SerialPort.BAUDRATE_115200,
                                    SerialPort.DATABITS_8,
                                    SerialPort.STOPBITS_1,
                                    SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN
                                            |SerialPort.FLOWCONTROL_RTSCTS_OUT);
            serialPort.addEventListener(serialPortEvent -> {
                if(serialPortEvent.isRXCHAR()&&serialPortEvent.getEventValue()>0){
                    int bytesCount = serialPortEvent.getEventValue();
                    try {
                        data = serialPort.readString(bytesCount);
                    } catch (SerialPortException e) {
                        e.printStackTrace();
                        data = null;
                    }
                }
            },SerialPort.MASK_RXCHAR);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }
    public boolean writeCommand(String command){
        if (serialPort!=null) {
            try {
                return serialPort.writeBytes(command.getBytes(StandardCharsets.UTF_8));
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String readCommand(){
        return data;
    }

    public static void main(String[] args) {
    }
}
