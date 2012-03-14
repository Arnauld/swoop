package swoop.route;

import swoop.EventSourceConnection;
import swoop.RouteChain;
import swoop.path.Path;

public abstract class EventSourceRoute implements FilterAware {
    
    protected static final String ALL_PATHS = Path.ALL_PATHS;

    private String path;

    /**
     * Constructor
     * 
     * @param path
     *            The route path which is used for matching. (e.g. /hello, users/:name)
     */
    protected EventSourceRoute(String path) {
        this.path = path;
    }

    /**
     * Returns this route's path
     */
    public String getPath() {
        return this.path;
    }

    public void onOpen(EventSourceConnection connection, RouteChain chain) {
        chain.invokeNext();
    }

    public void onClose(EventSourceConnection connection, RouteChain chain) {
        chain.invokeNext();
    }

}
