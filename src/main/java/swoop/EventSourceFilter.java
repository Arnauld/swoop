package swoop;

import swoop.route.EventSourceRoute;

public class EventSourceFilter extends EventSourceRoute {

    /**
     * Constructor
     * 
     * @param path The exact route path which is used for matching (e.g. /hellowebsocket) 
     */
    protected EventSourceFilter(String path) {
        super(path);
    }

    @Override
    public final boolean isFilter() {
        return true;
    }

}
