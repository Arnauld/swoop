package swoop.route;

import swoop.path.Path;

/**
 * 
 */
public class RouteMatch extends AbstractRouteMatch<Route, RouteEntry> {

    public RouteMatch(Path requestedPath, RouteEntry matchEntry) {
        super(requestedPath, matchEntry);
    }
}
