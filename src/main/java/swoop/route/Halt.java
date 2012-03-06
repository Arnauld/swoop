package swoop.route;

public class Halt {

    public static void halt(int statusCode) {
        throw new HaltException(statusCode);
    }
    
    public static void halt(int statusCode, String body) {
        throw new HaltException(statusCode, body);
    }

    public static void halt(String body) {
        throw new HaltException(body);
    }
}
