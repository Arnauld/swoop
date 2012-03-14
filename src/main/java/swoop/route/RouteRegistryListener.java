package swoop.route;

import swoop.path.Path;


public interface RouteRegistryListener {
    void routeAdded(RouteRegistry registry, Path route, Route target);
    void webSocketRouteAdded(RouteRegistry registry, Path path, WebSocketRoute target);
    void eventSourceRouteAdded(RouteRegistry registry, Path path, EventSourceRoute target);
    void staticDirAdded(RouteRegistry registry, String path);
    void routeCleared(RouteRegistry registry);
}
