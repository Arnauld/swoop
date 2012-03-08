package swoop;

import swoop.route.HasRouteParameters;

public interface WebSocketMessage extends HasRouteParameters {
    
    boolean isText();
    
    byte[] binary();
    void binary(byte[] bytes);
    String text();
    void text(String text);
}
