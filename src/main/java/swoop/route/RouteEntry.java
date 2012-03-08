package swoop.route;

import swoop.path.Path;
import swoop.path.PathMatcher;

public class RouteEntry extends AbstractEntry<Route> {
    
    public RouteEntry(Path path, PathMatcher pathMatcher, Route target) {
        super(path, pathMatcher, target);
    }
}