package swoop;

@SuppressWarnings("serial")
public class SwoopException extends RuntimeException {

    public SwoopException() {
        super();
    }

    public SwoopException(String message, Throwable cause) {
        super(message, cause);
    }

    public SwoopException(String message) {
        super(message);
    }

    public SwoopException(Throwable cause) {
        super(cause);
    }
}
