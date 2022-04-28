import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import jssc.*;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class Terminal {
    private SerialPort serialPort;
    private String data;
    private boolean isDataReaded;
    private String errorMessage;
    private boolean error;

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isError() {
        return error;
    }
    public Terminal(String port) {
        try {
            serialPort = new SerialPort(port);
            serialPort.openPort();
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
                        isDataReaded = true;
                    } catch (SerialPortException e) {
                        e.printStackTrace();
                    }
                }
            },SerialPort.MASK_RXCHAR);
        } catch (SerialPortException e) {
            System.out.println("Port not opened!");
            e.printStackTrace();
            error = true;
            errorMessage = e.getMessage();
        }
    }
    public boolean writeCommand(String command){
        if (serialPort!=null) {
            try {
                data=null;
                isDataReaded = false;
                return serialPort.writeBytes(command.getBytes(StandardCharsets.UTF_8));
            } catch (SerialPortException e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getMessage();
            }
        }
        return false;
    }

    public String readCommand(){
        while (!isDataReaded){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public void close(){
        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            error = true;
            errorMessage = e.getMessage();
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        Terminal terminal = new Terminal("COM3");
        JsonEntity command = new JsonEntity();
        command.setMethod("GetTerminalInfo");
        command.setStep(0);
        Gson gson = new Gson();


        terminal.writeCommand("\0"+ gson.toJson(command, JsonEntity.class) +"\0");
        String answer = terminal.readCommand();

        JsonReader reader = new JsonReader(new StringReader(answer.trim()));
        reader.setLenient(true);
        JsonEntity jsonEntityObject = gson.fromJson(reader, JsonEntity.class);

        System.out.println(jsonEntityObject.isError());
        System.out.println(jsonEntityObject.getErrorDescription());
        command.setMethod("ServiceMessage");
        Map<String,String> params = new HashMap<>();
        params.put("msgType", "identify");
        command.setParams(params);
        //terminal.writeCommand("{\"method\":\"ServiceMessage\",\"step\":0,\"params\":{\"msgType\":\"identify\"}}\0");

        terminal.writeCommand(gson.toJson(command, JsonEntity.class) + "\0");
        answer=terminal.readCommand();

        reader = new JsonReader(new StringReader(answer.trim()));
        jsonEntityObject = gson.fromJson(reader, JsonEntity.class);

        System.out.println("model - " + jsonEntityObject.getParams().get("model"));
        System.out.println("vendor - " + jsonEntityObject.getParams().get("vendor"));

        System.out.println("++");
        terminal.close();
    }
}
