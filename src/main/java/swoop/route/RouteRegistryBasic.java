package swoop.route;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import swoop.path.Path;
import swoop.path.PathMatcher;
import swoop.path.PathMatcherCompiler;
import swoop.path.PathMatcherSinatraCompiler;
import swoop.util.New;

/**
 * Basic route registry 
 */
public class RouteRegistryBasic implements RouteRegistry {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RouteRegistryBasic.class);
    public static final String ROOT = "/";

    private List<RouteEntry> routes;
    private List<WebSocketRouteEntry> webSocketRoutes;
    private List<String> staticPaths;
    private PathMatcherCompiler pathMatcherCompiler;
    private CopyOnWriteArraySet<RouteRegistryListener> listeners;

    public RouteRegistryBasic() {
        routes = New.arrayList();
        webSocketRoutes = New.arrayList();
        staticPaths = New.arrayList();
        pathMatcherCompiler = new PathMatcherSinatraCompiler();
        listeners = New.copyOnWriteArraySet();
    }

    public void setPathMatcherCompiler(PathMatcherCompiler pathMatcherCompiler) {
        this.pathMatcherCompiler = pathMatcherCompiler;
    }

    public PathMatcherCompiler getPathMatcherCompiler() {
        return pathMatcherCompiler;
    }

    @Override
    public void addRouteRegistryListener(RouteRegistryListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeRouteRegistryListener(RouteRegistryListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void addStaticDir(String path) {
        staticPaths.add(path);
        for (RouteRegistryListener listener : listeners)
            listener.staticDirAdded(this, path);
    }

    @Override
    public List<String> getStaticDirs() {
        return staticPaths;
    }

    @Override
    public List<RouteMatch> findRoutes(Path requestedPath) {
        List<RouteMatch> matchSet = New.arrayList();
        for (RouteEntry entry : routes) {
            if (entry.matches(requestedPath)) {
                matchSet.add(new RouteMatch(requestedPath, entry));
            }
        }
        return matchSet;
    }

    @Override
    public void addRoute(String route, Route target) {
        try {
            Path path = Path.parse(route);
            addRoute(path, target);
        } catch (Exception e) {
            logger.error("The @Route value: " + route + " is not in the correct format", e);
        }
    }

    @Override
    public void addRoute(Path path, Route target) {
        if(path.getVerb().isWebSocket())
            throw new IllegalArgumentException("WebSocket verb is not allowed for route");
        if(path.getVerb().isAny() && !target.isFilter())
            logger.debug("Be aware that you define a catch all ('any' verb) on a target");
        
        RouteEntry entry = new RouteEntry(path, pathMatcherCompiler.compile(path.getPathPattern()), target);
        // Adds to end of list
        routes.add(entry);

        for (RouteRegistryListener listener : listeners)
            listener.routeAdded(this, path, target);
    }
    
    @Override
    public void addWebSocket(Path path, WebSocketRoute target) {
        if(path.getVerb().isHttpMethod())
            throw new IllegalArgumentException("HttpMethod verb is not allowed for websocket route");
        if(path.getVerb().isAny() && !target.isFilter())
            throw new IllegalArgumentException("Target must be defined on an exact path");

        String pathPattern = path.getPathPattern();
        PathMatcher pathMatcher = pathMatcherCompiler.compile(pathPattern);

        if(!target.isFilter() && pathMatcher.hasParameters())
            throw new IllegalArgumentException("Target must be defined on an exact path got: <" + pathPattern + ">");
        
        WebSocketRouteEntry entry = new WebSocketRouteEntry(path, pathMatcher, target);
        // Adds to end of list
        webSocketRoutes.add(entry);
        
        for (RouteRegistryListener listener : listeners)
            listener.webSocketRouteAdded(this, path, target);
    }
    
    @Override
    public List<WebSocketRouteMatch> findWebSocketRoutes(Path requestedPath) {
        List<WebSocketRouteMatch> matchSet = New.arrayList();
        for (WebSocketRouteEntry entry : webSocketRoutes) {
            if (entry.matches(requestedPath)) {
                matchSet.add(new WebSocketRouteMatch(requestedPath, entry));
            }
        }
        return matchSet;
    }
    
    @Override
    public List<WebSocketRouteEntry> listWebSocketTargets() {
        List<WebSocketRouteEntry> matchSet = New.arrayList();
        for (WebSocketRouteEntry entry : webSocketRoutes) {
            if (!entry.isFilter()) {
                matchSet.add(entry);
            }
        }
        return matchSet;
    }

    @Override
    public void clearRoutes() {
        webSocketRoutes.clear();
        routes.clear();
        for (RouteRegistryListener listener : listeners)
            listener.routeCleared(this);
    }

    public static class Factory implements RouteRegistryFactory {
        @Override
        public RouteRegistry create() {
            return new RouteRegistryBasic();
        }
    }

}
