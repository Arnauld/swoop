package swoop.route;

import swoop.path.Path;

public class RouteRegistryListenerAdapter implements RouteRegistryListener {
    
    @Override
    public void staticDirAdded(RouteRegistry registry, String path) {
    }
    
    @Override
    public void routeCleared(RouteRegistry registry) {
    }
    
    @Override
    public void routeAdded(RouteRegistry registry, Path route, Route target) {
    }
    
    @Override
    public void webSocketRouteAdded(RouteRegistry registry, Path path, WebSocketRoute target) {
    }
    
    @Override
    public void eventSourceRouteAdded(RouteRegistry registry, Path path, EventSourceRoute target) {
    }
}
