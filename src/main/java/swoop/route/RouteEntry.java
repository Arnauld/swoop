package swoop.route;

public class RouteEntry {
    private final Path path;
    private final PathMatcher pathMatcher;
    private final Route target;
    public RouteEntry(Path path, PathMatcher pathMatcher, Route target) {
        super();
        this.path = path;
        this.pathMatcher = pathMatcher;
        this.target = target;
    }
    
    public Verb getVerb() {
        return path.getVerb();
    }
    
    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }
    
    public Route getTarget() {
        return target;
    }

    public boolean isFilter() {
        return target.isFilter();
    }

    public boolean matches(Path requestPath) {
        return verbMatches(requestPath) && pathMatches(requestPath);
    }

    protected boolean pathMatches(Path requestPath) {
        return pathMatcher.matches(requestPath.getPathPattern());
    }

    protected boolean verbMatches(Path requestPath) {
        return getVerb().matches(requestPath.getVerb());
    }
}