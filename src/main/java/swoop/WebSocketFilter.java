package swoop;

import swoop.route.WebSocketRoute;

public abstract class WebSocketFilter extends WebSocketRoute {
    
    /**
     * Constructor
     * 
     * @param path The route path which is used for matching. (e.g. /hello, users/:name) 
     */
    protected WebSocketFilter(String path) {
        super(path);
    }

    @Override
    public final boolean isFilter() {
        return true;
    }

}
