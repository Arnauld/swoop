package swoop.path;

import swoop.SwoopException;

@SuppressWarnings("serial")
public class InvalidPathException extends SwoopException {

    public InvalidPathException() {
        super();
    }

    public InvalidPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPathException(String message) {
        super(message);
    }

    public InvalidPathException(Throwable cause) {
        super(cause);
    }

}
