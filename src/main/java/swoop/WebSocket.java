package swoop;

import swoop.route.WebSocketRoute;

public abstract class WebSocket extends WebSocketRoute {
    
    /**
     * Constructor
     * 
     * @param path The exact route path which is used for matching (e.g. /hellowebsocket) 
     */
    protected WebSocket(String path) {
        super(path);
    }

    @Override
    public final boolean isFilter() {
        return false;
    }

    @Override
    public final void onOpen(WebSocketConnection connection, RouteChain chain) {
        onOpen(connection);
    }
    
    public void onOpen(WebSocketConnection connection) {
    }

    @Override
    public final void onClose(WebSocketConnection connection, RouteChain chain) {
        onClose(connection);
    }
    
    public void onClose(WebSocketConnection connection) {
    }

    @Override
    public final void onMessage(WebSocketConnection connection, WebSocketMessage msg, RouteChain chain) {
        onMessage(connection, msg);
    }
    
    public void onMessage(WebSocketConnection connection, WebSocketMessage msg) {
    }

    @Override
    public final void onPing(WebSocketConnection connection, WebSocketMessage msg, RouteChain chain) {
        onPing(connection, msg);
    }
    
    public void onPing(WebSocketConnection connection, WebSocketMessage msg) {
        connection.pong(msg.binary());
    }
    
    @Override
    public final void onPong(WebSocketConnection connection, WebSocketMessage msg, RouteChain chain) {
        onPong(connection, msg);
    }
    
    public void onPong(WebSocketConnection connection, WebSocketMessage msg) {
    }
    
}
