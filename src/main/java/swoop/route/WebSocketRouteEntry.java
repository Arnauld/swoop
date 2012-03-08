package swoop.route;

import swoop.path.Path;
import swoop.path.PathMatcher;

public class WebSocketRouteEntry extends AbstractEntry<WebSocketRoute> {
    
    public WebSocketRouteEntry(Path path, PathMatcher pathMatcher, WebSocketRoute target) {
        super(path, pathMatcher, target);
    }

}
