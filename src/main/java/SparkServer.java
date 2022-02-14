import static spark.Spark.*;

public class SparkServer {
    public static void main(String[] args) {
        port(8888);
        get("/terminal",(req, res)->"Hello world");
    }
}
