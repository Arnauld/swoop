package swoop.route;

import swoop.path.VerbMatcher;


public interface RouteRegistryListener {
    void routeAdded(RouteRegistry registry, VerbMatcher verbMatcher, String pathPattern, Route target);
    void webSocketRouteAdded(RouteRegistry registry, VerbMatcher verbMatcher, String pathPattern, WebSocketRoute target);
    void eventSourceRouteAdded(RouteRegistry registry, VerbMatcher verbMatcher, String pathPattern, EventSourceRoute target);
    void staticDirAdded(RouteRegistry registry, String path);
    void routeCleared(RouteRegistry registry);
}
