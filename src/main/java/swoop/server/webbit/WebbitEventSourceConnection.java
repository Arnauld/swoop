package swoop.server.webbit;

import java.util.Set;

import swoop.EventSourceConnection;
import swoop.EventSourceMessage;
import swoop.Request;

public class WebbitEventSourceConnection implements EventSourceConnection {

    private final org.webbitserver.EventSourceConnection connection;
    private final WebbitRequestAdapter request;

    public WebbitEventSourceConnection(org.webbitserver.EventSourceConnection connection, WebbitRequestAdapter request) {
        this.connection = connection;
        this.request = request;
    }
    
    @Override
    public Object raw() {
        return connection;
    }
    
    @Override
    public EventSourceConnection send(long id, String message) {
        connection.send(new org.webbitserver.EventSourceMessage(message).id(id));
        return this;
    }
    
    @Override
    public EventSourceConnection send(String message) {
        connection.send(new org.webbitserver.EventSourceMessage(message));
        return this;
    }

    @Override
    public EventSourceConnection send(EventSourceMessage message) {
        org.webbitserver.EventSourceMessage msg = new org.webbitserver.EventSourceMessage(message.content());
        Long id = message.id();
        if(id!=null)
            msg.id(id);
        connection.send(msg);
        return this;
    }
    
    @Override
    public Request request() {
        return request;
    }
    
    @Override
    public void close() {
        connection.close();
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

}
