package swoop.server.webbit;

import java.util.Set;

import swoop.Request;
import swoop.WebSocketConnection;

public class WebbitWebSocketConnection implements WebSocketConnection {

    private final org.webbitserver.WebSocketConnection connection;
    private final WebbitRequestAdapter request;

    public WebbitWebSocketConnection(org.webbitserver.WebSocketConnection connection, WebbitRequestAdapter request) {
        super();
        this.connection = connection;
        this.request = request;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~[DATA]~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public Object data(String key) {
        return connection.data(key);
    }

    @Override
    public void data(String key, Object value) {
        connection.data(key, value);
    }

    @Override
    public Set<String> dataKeys() {
        return connection.dataKeys();
    }

    @Override
    public WebSocketConnection send(String message) {
        connection.send(message);
        return this;
    }

    @Override
    public WebSocketConnection send(byte[] message) {
        connection.send(message);
        return this;
    }

    @Override
    public WebSocketConnection ping(byte[] message) {
        connection.ping(message);
        return this;
    }

    @Override
    public WebSocketConnection pong(byte[] message) {
        connection.pong(message);
        return this;
    }

    @Override
    public Request request() {
        return request;
    }

}
