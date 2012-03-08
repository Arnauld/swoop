package swoop.route;

import swoop.path.Path;

public class WebSocketRouteMatch extends AbstractRouteMatch<WebSocketRoute, WebSocketRouteEntry> {

    public WebSocketRouteMatch(Path requestedPath, WebSocketRouteEntry matchEntry) {
        super(requestedPath, matchEntry);
    }

}
