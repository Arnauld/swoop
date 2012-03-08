package swoop.route;

import swoop.WebSocketConnection;

public interface WebSocketInvoker {
    void invoke(WebSocketConnection connection, WebSocketRoute target, WebSocketRouteChain chain);
}
