import lombok.Data;

import java.util.Map;

@Data
public class JsonEntity {
    private String method;
    private int step;
    private Map<String, String> params;
    private boolean error;
    private String errorDescription;
}
