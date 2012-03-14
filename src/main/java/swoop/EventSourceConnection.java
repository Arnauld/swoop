package swoop;

import swoop.util.HasDataParameters;

public interface EventSourceConnection extends HasDataParameters {
    
    Object raw();
    
    EventSourceConnection send(String message);
    
    EventSourceConnection send(long id, String message);
    
    EventSourceConnection send(EventSourceMessage message);
    
    /**
     * Return the underlying request
     */
    Request request();
    
    /**
     * 
     */
    void close();
}
