import lombok.Data;

import java.util.Map;
import java.util.function.BiConsumer;

@Data
public class JsonEntity {
    private String method;
    private int step;
    private Map<String, String> params;
    private boolean error;
    private String errorDescription;

    public String getParamsInString(){
        StringBuilder sb = new StringBuilder();
        params.forEach((key, value)-> sb.append(key).append(": ").append(value).append(",\n"));
        return sb.toString();
    }
}
