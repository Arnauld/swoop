package swoop.route;

import swoop.RouteChain;
import swoop.WebSocketConnection;
import swoop.WebSocketMessage;
import swoop.path.Verb;

public class WebSocketInvoker implements Invoker<RouteMatch<WebSocketRoute>> {
    private final Verb verb;
    private final WebSocketConnection connection;
    private final WebSocketMessage message;

    public WebSocketInvoker(Verb verb, WebSocketConnection connection, WebSocketMessage message) {
        super();
        switch (verb) {
            case WebSocketOpen:
            case WebSocketClose:
                if (message != null)
                    throw new IllegalArgumentException("Message must be null for " + verb + " mode");
                break;
            case WebSocketMessage:
            case WebSocketPing:
            case WebSocketPong:
                if (message == null)
                    throw new IllegalArgumentException("Message cannot be null for " + verb + " mode");
                break;
            default:
                throw new IllegalArgumentException(verb + " is not an websocket one");
        }
        this.verb = verb;
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
        switch (verb) {
            case WebSocketOpen:
                routeMatch.getTarget().onOpen(connection, chain);
                break;
            case WebSocketClose:
                routeMatch.getTarget().onClose(connection, chain);
                break;
            case WebSocketMessage:
                routeMatch.getTarget().onMessage(connection, message, chain);
                break;
            case WebSocketPing:
                routeMatch.getTarget().onPing(connection, message, chain);
                break;
            case WebSocketPong:
                routeMatch.getTarget().onPong(connection, message, chain);
                break;
        }
    }

}
