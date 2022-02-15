import lombok.extern.java.Log;

import static spark.Spark.*;

@Log
public class SparkServer {
    public static void main(String[] args) {
        port(8888);
        TerminalController terminal = new TerminalController(Config.getComPort());
        if (!terminal.init()) {
            log.severe("error in terminal initialization");
            return;
        }

        get("/terminal/:operation",(req, res)->{
            String operation = req.params(":operation");
            int merchantId = Integer.parseInt(req.queryParamOrDefault("merchant", "0"));
            double summ = Double.parseDouble(req.queryParamOrDefault("sum", "0.00"));
            if ("xbalance".equals(operation)){
                terminal.xbalance(merchantId);
            }else if ("zbalance".equals(operation)){
                terminal.zbalance(merchantId);
            }else if ("purchase".equals(operation)){
                terminal.purchase(summ, merchantId);
            }
            else if("stop".equals(operation)){
                terminal.stop();
                stop();
            }
            res.status(200);
            return terminal.getModel() + " " + terminal.getVendor();
        });

    }
}
