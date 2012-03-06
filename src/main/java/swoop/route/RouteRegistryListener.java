package swoop.route;


public interface RouteRegistryListener {
    void routeAdded(RouteRegistry registry, Path route, Route target);
    void staticDirAdded(RouteRegistry registry, String path);
    void routeCleared(RouteRegistry registry);
}
