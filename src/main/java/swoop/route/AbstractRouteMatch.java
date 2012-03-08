package swoop.route;

import swoop.path.Path;
import swoop.util.Multimap;


/**
 * 
 */
public class AbstractRouteMatch<R extends FilterAware, T extends AbstractEntry<R>> {

    private final Path requestedPath;
    private final T matchEntry;
    private Multimap<String, String> params;
    
    public AbstractRouteMatch(Path requestedPath, T matchEntry) {
        super();
        this.requestedPath = requestedPath;
        this.matchEntry = matchEntry;
    }

    public T getMatchEntry() {
        return matchEntry;
    }
    
    /**
     * @return the target
     */
    public R getTarget() {
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
