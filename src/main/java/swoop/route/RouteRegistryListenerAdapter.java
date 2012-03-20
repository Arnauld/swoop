package swoop.route;

import swoop.path.VerbMatcher;

public class RouteRegistryListenerAdapter implements RouteRegistryListener {
    
    @Override
    public void staticDirAdded(RouteRegistry registry, String path) {
    }
    
    @Override
    public void routeCleared(RouteRegistry registry) {
    }

    @Override
    public void routeAdded(RouteRegistry registry, VerbMatcher verbMatcher, String pathPattern, Route target) {
    }

    @Override
    public void webSocketRouteAdded(RouteRegistry registry, VerbMatcher verbMatcher, String pathPattern,
            WebSocketRoute target) {
    }

    @Override
    public void eventSourceRouteAdded(RouteRegistry registry, VerbMatcher verbMatcher, String pathPattern,
            EventSourceRoute target) {
    }
    
}
