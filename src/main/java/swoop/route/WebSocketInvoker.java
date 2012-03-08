package swoop.route;

import swoop.WebSocketConnection;
import swoop.WebSocketRouteChain;

public interface WebSocketInvoker {
    void invoke(WebSocketConnection connection, WebSocketRoute target, WebSocketRouteChain chain);
}
