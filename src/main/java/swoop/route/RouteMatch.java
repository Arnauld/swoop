package swoop.route;

import swoop.path.Path;


/**
 * 
 */
public class RouteMatch {

    private final Path requestedPath;
    private final RouteEntry matchEntry;
    
    public RouteMatch(Path requestedPath, RouteEntry matchEntry) {
        super();
        this.requestedPath = requestedPath;
        this.matchEntry = matchEntry;
    }

    public RouteEntry getMatchEntry() {
        return matchEntry;
    }
    
    /**
     * @return the target
     */
    public Route getTarget() {
        return matchEntry.getTarget();
    }

    /**
     * @return the requestUri
     */
    public String getRequestPath() {
        return requestedPath.getPathPattern();
    }
    
    
}
