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
}