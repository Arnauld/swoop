package swoop.route;

import swoop.path.Path;
import swoop.util.Multimap;

/**
 * 
 */
public class RouteMatch<R extends FilterAware> {

    public static <R extends FilterAware> RouteMatch<R> create(Path requestedPath, RouteEntry<R> entry) {
        return new RouteMatch<R>(requestedPath, entry);
    }
    
    private final Path requestedPath;
    private final RouteEntry<R> matchEntry;
    private Multimap<String, String> params;
    
    public RouteMatch(Path requestedPath, RouteEntry<R> matchEntry) {
        super();
        this.requestedPath = requestedPath;
        this.matchEntry = matchEntry;
    }

    public RouteEntry<R> getMatchEntry() {
        return matchEntry;
    }
    
    /**
     * @return the target
     */
    public R getTarget() {
        return matchEntry.getTarget();
    }

    public Multimap<String, String> getRouteParameters() {
        if(params==null)
            params = matchEntry.extractParameters(requestedPath);
        return params;
    }

}
