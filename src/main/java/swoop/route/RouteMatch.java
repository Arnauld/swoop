package swoop.route;

import swoop.path.Path;
import swoop.util.Multimap;


/**
 * 
 */
public class RouteMatch {

    private final Path requestedPath;
    private final RouteEntry matchEntry;
    private Multimap<String, String> params;
    
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

    public Multimap<String, String> getRouteParameters() {
        if(params==null)
            params = matchEntry.getPathMatcher().extractParameters(requestedPath.getPathPattern());
        return params;
    }
}
