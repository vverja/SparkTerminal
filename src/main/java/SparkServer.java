import com.google.gson.Gson;
import lombok.extern.java.Log;

import java.nio.charset.StandardCharsets;

import static spark.Spark.*;

@Log
public class SparkServer {


    public static void main(String[] args) {
        port(8888);
        TerminalController terminal = new TerminalController(Config.getComPort());

        System.out.println("Terminal server started");
        get("/terminal/:operation",(req, res)->{
            TerminalAnswer terminalAnswer = terminal.getTerminalAnswer();
            String operation = req.params(":operation");
            Gson json = new Gson();
            res.status(200);
            if (!terminal.isInit())
                if (!terminal.init()) {
                    log.severe("error in terminal initialization");
                    return json.toJson(terminalAnswer);
                }
            int merchantId = Integer.parseInt(req.queryParamOrDefault("merchant", "0"));
            double summ = Double.parseDouble(req.queryParamOrDefault("sum", "0.00"));
            if ("xbalance".equals(operation)){
                terminal.xbalance(merchantId);
            }else if ("zbalance".equals(operation)){
                terminal.zbalance(merchantId);
            }else if ("purchase".equals(operation)){
                terminal.purchase(summ, merchantId);
            }else if ("emulation".equals(operation)){
                terminal.setEmulationOnOf();
            }
            return json.toJson(terminalAnswer);
        });
    }
}
