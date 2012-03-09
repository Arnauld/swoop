package swoop.route;

import swoop.path.Path;
import swoop.path.PathMatcher;
import swoop.path.Verb;

public abstract class AbstractEntry<T extends FilterAware> {
    private final Path path;
    private final PathMatcher pathMatcher;
    private final T target;
    public AbstractEntry(Path path, PathMatcher pathMatcher, T target) {
        super();
        this.path = path;
        this.pathMatcher = pathMatcher;
        this.target = target;
    }
    
    public String getPathPattern() {
        return path.getPathPattern();
    }
    
    public Verb getVerb() {
        return path.getVerb();
    }
    
    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }
    
    public T getTarget() {
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
