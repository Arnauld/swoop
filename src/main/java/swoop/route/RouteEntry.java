package swoop.route;

import swoop.path.Path;
import swoop.path.PathPatternMatcher;
import swoop.path.Verb;
import swoop.path.VerbMatcher;
import swoop.util.Multimap;

public class RouteEntry<T extends FilterAware> {
    
    public static <T extends FilterAware> RouteEntry<T> create(VerbMatcher verbMatcher, PathPatternMatcher pathMatcher, T target) {
        return new RouteEntry<T>(verbMatcher, pathMatcher, target);
    }
    
    private final VerbMatcher verbMatcher;
    private final PathPatternMatcher pathMatcher;
    private final T target;
    public RouteEntry(VerbMatcher verbMatcher, PathPatternMatcher pathMatcher, T target) {
        super();
        this.verbMatcher = verbMatcher;
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
        return verbMatches(requestPath.getVerb()) && pathMatches(requestPath.getPathPattern());
    }

    public boolean pathMatches(String pathPattern) {
        return pathMatcher.matches(pathPattern);
    }

    protected boolean verbMatches(Verb verb) {
        return verbMatcher.matches(verb);
    }
    
    @Override
    public String toString() {
        return "RouteEntry[verbMatcher: " + verbMatcher + ", target: " + target + "]";
    }
}