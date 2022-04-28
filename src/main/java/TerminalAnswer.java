import lombok.Data;

@Data
public class TerminalAnswer {
    private String model, vendor, description, reciept;
    private boolean isError;

    public boolean isInit(){
        return vendor!=null&&model!=null;
    }
}
