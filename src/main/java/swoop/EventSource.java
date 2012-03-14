package swoop;

import swoop.route.EventSourceRoute;

public class EventSource extends EventSourceRoute {

    /**
     * Constructor
     * 
     * @param path The exact route path which is used for matching (e.g. /hellowebsocket) 
     */
    protected EventSource(String path) {
        super(path);
    }

    @Override
    public final boolean isFilter() {
        return false;
    }

    /* (non-Javadoc)
     * @see swoop.route.eventsource.EventSourceRoute#onOpen(swoop.EventSourceConnection, swoop.EventSourceRouteChain)
     */
    @Override
    public final void onOpen(EventSourceConnection connection, RouteChain chain) {
        onOpen(connection);
    }
    
    public void onOpen(EventSourceConnection connection) {
    }

    /* (non-Javadoc)
     * @see swoop.route.eventsource.EventSourceRoute#onClose(swoop.EventSourceConnection, swoop.EventSourceRouteChain)
     */
    @Override
    public final void onClose(EventSourceConnection connection, RouteChain chain) {
        onClose(connection);
    }
    
    public void onClose(EventSourceConnection connection) {
    }
    
}
