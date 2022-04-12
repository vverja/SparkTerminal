import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Log
public class TerminalController {
    private final Terminal terminal;
    @Getter
    private final TerminalAnswer terminalAnswer = new TerminalAnswer();
    @Getter
    private boolean isInit;
    private final Gson json = new Gson();
    private JsonEntity entity = new JsonEntity();


    public TerminalController(String port) {
        terminal = new Terminal(port);
        entity.setStep(0);
    }
    public boolean init(){
        entity.setMethod("GetTerminalInfo");
        terminal.writeCommand("\0" + json.toJson(entity, JsonEntity.class) + "\0");
        String answer = terminal.readCommand();
        JsonReader reader = new JsonReader(new StringReader(answer.trim()));
        reader.setLenient(true);
        entity = json.fromJson(reader, JsonEntity.class);
        if (entity.isError()) {
            log.severe(entity.getErrorDescription());
            return false;
        }
        entity.setMethod("ServiceMessage");
        Map<String,String> params = new HashMap<>();
        params.put("msgType", "identify");
        entity.setParams(params);
        terminal.writeCommand(json.toJson(entity, JsonEntity.class) + "\0");
        answer=terminal.readCommand();
        reader = new JsonReader(new StringReader(answer.trim()));
        reader.setLenient(true);
        entity = json.fromJson(reader, JsonEntity.class);
        if (entity.isError()) {
            isInit = false;
            log.severe(entity.getErrorDescription());
            return false;
        }
        terminalAnswer.setVendor(entity.getParams().get("vendor"));
        terminalAnswer.setModel(entity.getParams().get("model"));
        terminalAnswer.setDescription(entity.getErrorDescription());
        isInit = terminalAnswer.isInit();
        return isInit;
    }
    public void stop(){
        terminal.close();
    }

    public boolean xbalance(int merchantId){
        entity.setMethod("Audit");
        Map<String, String> params = new HashMap<>();
        if (merchantId==0)
            params.put("merchantId", "");
        else
            params.put("merchantId", String.valueOf(merchantId));
        terminal.writeCommand(json.toJson(entity, JsonEntity.class) + "\0");
        String answer = terminal.readCommand();
        JsonReader reader = new JsonReader(new StringReader(answer.trim()));
        reader.setLenient(true);
        entity = json.fromJson(reader, JsonEntity.class);
        if (entity.isError()) {
            log.severe(entity.getErrorDescription());
            terminalAnswer.setDescription(entity.getErrorDescription());
            return false;
        }
        log.info(entity.getParams().get("receipt"));
        terminalAnswer.setReciept(entity.getParams().get("receipt"));
        return true;
    }
    public boolean zbalance(int merchantId){
        entity.setMethod("Verify");
        Map<String, String> params = new HashMap<>();
        if (merchantId==0)
            params.put("merchantId", "");
        else
            params.put("merchantId", String.valueOf(merchantId));
        terminal.writeCommand(json.toJson(entity, JsonEntity.class) + "\0");
        String answer = terminal.readCommand();
        JsonReader reader = new JsonReader(new StringReader(answer.trim()));
        reader.setLenient(true);
        entity = json.fromJson(reader, JsonEntity.class);
        if (entity.isError()) {
            log.severe(entity.getErrorDescription());
            terminalAnswer.setDescription(entity.getErrorDescription());
            return false;
        }
        log.info(entity.getParams().get("receipt"));
        terminalAnswer.setReciept(entity.getParams().get("receipt"));
        return true;
    }

    public boolean purchase(double summ, int merchantId){
        Gson json = new Gson();
        JsonEntity entity = new JsonEntity();
        entity.setStep(0);
        entity.setMethod("Purchase");
        Map<String, String> params = new HashMap<>();
        params.put("amount", String.valueOf(summ));
        params.put("discount", "");
        params.put("merchantId", String.valueOf(merchantId));
        params.put("facepay", "false");
        entity.setParams(params);

        terminal.writeCommand(json.toJson(entity, JsonEntity.class) + "\0");
        String answer = terminal.readCommand();
        JsonReader reader = new JsonReader(new StringReader(answer.trim()));
        reader.setLenient(true);
        entity = json.fromJson(reader, JsonEntity.class);
        if (entity.isError()) {
            log.severe(entity.getErrorDescription());
            terminalAnswer.setDescription(entity.getErrorDescription());
            return false;
        }
        if (!"0000".equals(entity.getParams().get("responseCode"))) {
            log.severe(entity.getErrorDescription());
            terminalAnswer.setDescription(entity.getErrorDescription());
            return false;
        }
        terminalAnswer.setReciept(entity.getParamsInString());
        return true;
    }

    public boolean refund(double summ, int merchantId){
        Gson json = new Gson();
        JsonEntity entity = new JsonEntity();
        entity.setStep(0);
        entity.setMethod("Purchase");
        Map<String, String> params = new HashMap<>();
        params.put("amount", String.valueOf(summ));
        params.put("discount", "");
        params.put("merchantId", String.valueOf(merchantId));
        params.put("facepay", "false");

        terminal.writeCommand(json.toJson(entity, JsonEntity.class) + "\0");
        String answer = terminal.readCommand();
        JsonReader reader = new JsonReader(new StringReader(answer.trim()));
        reader.setLenient(true);
        entity = json.fromJson(reader, JsonEntity.class);
        if (entity.isError()) {
            log.severe(entity.getErrorDescription());
            terminalAnswer.setDescription(entity.getErrorDescription());
            return false;
        }
        if (!"0000".equals(entity.getParams().get("responseCode"))) {
            log.severe(entity.getErrorDescription());
            return false;
        }
        return true;
    }
}
