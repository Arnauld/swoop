package swoop.route;

import swoop.WebSocketConnection;
import swoop.WebSocketMessage;
import swoop.WebSocketRouteChain;
import swoop.path.Path;

public abstract class WebSocketRoute implements FilterAware {
    
    protected static final String ALL_PATHS = Path.ALL_PATHS;

    private String path;

    /**
     * Constructor
     * 
     * @param path
     *            The route path which is used for matching. (e.g. /hello, users/:name)
     */
    protected WebSocketRoute(String path) {
        this.path = path;
    }

    /**
     * Returns this route's path
     */
    public String getPath() {
        return this.path;
    }

    public void onOpen(WebSocketConnection connection, WebSocketRouteChain chain) {
        chain.invokeNext();
    }

    public void onClose(WebSocketConnection connection, WebSocketRouteChain chain) {
        chain.invokeNext();
    }

    public void onMessage(WebSocketConnection connection, WebSocketMessage msg, WebSocketRouteChain chain) {
        chain.invokeNext();
    }

    public void onPing(WebSocketConnection connection, WebSocketMessage msg, WebSocketRouteChain chain) {
        chain.invokeNext();
    }

    public void onPong(WebSocketConnection connection, WebSocketMessage msg, WebSocketRouteChain chain) {
        chain.invokeNext();
    }

}
