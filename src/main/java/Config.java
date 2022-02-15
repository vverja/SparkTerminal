import lombok.Cleanup;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static String  getComPort(){
        try {
            @Cleanup InputStream input = Config.class.getResourceAsStream("config.properties");
            Properties properties = new Properties();
            properties.load(input);
            return properties.getOrDefault("comport", "Com1").toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Com1";
        }
    }
}
