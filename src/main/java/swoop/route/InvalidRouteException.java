package swoop.route;

import swoop.SwoopException;

@SuppressWarnings("serial")
public class InvalidRouteException extends SwoopException {

    public InvalidRouteException() {
        super();
    }

    public InvalidRouteException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRouteException(String message) {
        super(message);
    }

    public InvalidRouteException(Throwable cause) {
        super(cause);
    }

}
