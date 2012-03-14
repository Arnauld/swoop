package swoop.route;

import swoop.RouteChain;
import swoop.WebSocketConnection;
import swoop.WebSocketMessage;

public class WebSocketInvoker implements Invoker<RouteMatch<WebSocketRoute>> {
    public enum Code {
        Open, Close, Message, Ping, Pong;
    }

    private final Code code;
    private final WebSocketConnection connection;
    private final WebSocketMessage message;

    public WebSocketInvoker(Code code, WebSocketConnection connection, WebSocketMessage message) {
        super();
        switch (code) {
            case Open:
            case Close:
                if (message != null)
                    throw new IllegalArgumentException("Message must be null for " + code + " mode");
                break;
            case Message:
            case Ping:
            case Pong:
                if (message == null)
                    throw new IllegalArgumentException("Message cannot be null for " + code + " mode");
                break;
        }
        this.code = code;
        this.connection = connection;
        this.message = message;
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.route.Invoker#invoke(java.lang.Object, swoop.RouteChain)
     */
    @Override
    public void invoke(RouteMatch<WebSocketRoute> routeMatch, RouteChain chain) {
        switch (code) {
            case Open:
                routeMatch.getTarget().onOpen(connection, chain);
                break;
            case Close:
                routeMatch.getTarget().onClose(connection, chain);
                break;
            case Message:
                routeMatch.getTarget().onMessage(connection, message, chain);
                break;
            case Ping:
                routeMatch.getTarget().onPing(connection, message, chain);
                break;
            case Pong:
                routeMatch.getTarget().onPong(connection, message, chain);
                break;
        }
    }

}
