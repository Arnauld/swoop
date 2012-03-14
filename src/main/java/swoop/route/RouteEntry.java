package swoop.route;

import swoop.path.Path;
import swoop.path.PathMatcher;
import swoop.util.Multimap;

public class RouteEntry<T extends FilterAware> {
    
    public static <T extends FilterAware> RouteEntry<T> create(Path path, PathMatcher pathMatcher, T target) {
        return new RouteEntry<T>(path, pathMatcher, target);
    }
    
    private final Path path;
    private final PathMatcher pathMatcher;
    private final T target;
    public RouteEntry(Path path, PathMatcher pathMatcher, T target) {
        super();
        this.path = path;
        this.pathMatcher = pathMatcher;
        this.target = target;
    }
    
    public T getTarget() {
        return target;
    }

    public boolean isFilter() {
        return target.isFilter();
    }
    
    public Multimap<String, String> extractParameters(Path requestPath) {
        return pathMatcher.extractParameters(requestPath.getPathPattern());
    }

    public boolean matches(Path requestPath) {
        return verbMatches(requestPath) && pathMatches(requestPath);
    }

    protected boolean pathMatches(Path requestPath) {
        return pathMatcher.matches(requestPath.getPathPattern());
    }

    protected boolean verbMatches(Path requestPath) {
        return path.getVerb().matches(requestPath.getVerb());
    }
    
}