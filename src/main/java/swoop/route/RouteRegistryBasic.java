package swoop.route;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import swoop.path.Path;
import swoop.path.PathPatternMatcher;
import swoop.path.PathMatcherCompiler;
import swoop.path.PathMatcherSinatraCompiler;
import swoop.path.Verb.Category;
import swoop.path.VerbMatcher;
import swoop.path.VerbMatchers;
import swoop.util.New;

/**
 * Basic route registry 
 */
public class RouteRegistryBasic implements RouteRegistry {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RouteRegistryBasic.class);
    public static final String ROOT = "/";

    private List<RouteEntry<Route>> routes;
    private List<RouteEntry<WebSocketRoute>> webSocketRoutes;
    private List<RouteEntry<EventSourceRoute>> eventSourceRoutes;
    private List<String> staticPaths;
    private PathMatcherCompiler pathMatcherCompiler;
    private CopyOnWriteArraySet<RouteRegistryListener> listeners;

    public RouteRegistryBasic() {
        routes = New.arrayList();
        webSocketRoutes = New.arrayList();
        eventSourceRoutes = New.arrayList();
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
    public List<RouteMatch<Route>> findRoutes(Path requestedPath) {
        List<RouteMatch<Route>> matchSet = New.arrayList();
        for (RouteEntry<Route> entry : routes) {
            if (entry.matches(requestedPath)) {
                matchSet.add(RouteMatch.create(requestedPath, entry));
            }
        }
        return matchSet;
    }

    @Override
    public void addRoute(String route, Route target) {
        try {
            VerbMatcher verbMatcher = VerbMatchers.fromExpression(Path.verbPart(route));
            String path = Path.pathPart(route);
            addRoute(verbMatcher, path, target);
        } catch (Exception e) {
            logger.error("The @Route value: " + route + " is not in the correct format", e);
        }
    }

    @Override
    public void addRoute(VerbMatcher verbMatcher, String pathPattern, Route target) {
        if(!verbMatcher.belongsTo(Category.HttpMethod)) {
            throw new IllegalArgumentException("WebSocket or EventSource verb is not allowed for route");
        }
        
        // Adds to end of list
        routes.add(RouteEntry.create(verbMatcher, pathMatcherCompiler.compile(pathPattern), target));

        for (RouteRegistryListener listener : listeners)
            listener.routeAdded(this, verbMatcher, pathPattern, target);
    }
    
    @Override
    public boolean hasWebSocketRoutes(String pathPattern) {
        for (RouteEntry<WebSocketRoute> entry : webSocketRoutes) {
            if (entry.pathMatches(pathPattern)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<RouteMatch<WebSocketRoute>> findWebSocketRoutes(Path requestedPath) {
        List<RouteMatch<WebSocketRoute>> matchSet = New.arrayList();
        for (RouteEntry<WebSocketRoute> entry : webSocketRoutes) {
            if (entry.matches(requestedPath)) {
                matchSet.add(RouteMatch.create(requestedPath, entry));
            }
        }
        return matchSet;
    }
    
    @Override
    public void addWebSocket(VerbMatcher verbMatcher, String pathPattern, WebSocketRoute target) {
        if(!verbMatcher.belongsTo(Category.WebSocket)) {
            throw new IllegalArgumentException("HttpMethod or EventSource verb is not allowed for route");
        }
        
        PathPatternMatcher pathMatcher = pathMatcherCompiler.compile(pathPattern);
        // Adds to end of list
        webSocketRoutes.add(RouteEntry.create(verbMatcher, pathMatcher, target));
        
        for (RouteRegistryListener listener : listeners)
            listener.webSocketRouteAdded(this, verbMatcher, pathPattern, target);
    }
    
    @Override
    public boolean hasEventSourceRoutes(String pathPattern) {
        for (RouteEntry<EventSourceRoute> entry : eventSourceRoutes) {
            if (entry.pathMatches(pathPattern)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<RouteMatch<EventSourceRoute>> findEventSourceRoutes(Path requestedPath) {
        List<RouteMatch<EventSourceRoute>> matchSet = New.arrayList();
        for (RouteEntry<EventSourceRoute> entry : eventSourceRoutes) {
            if (entry.matches(requestedPath)) {
                matchSet.add(RouteMatch.create(requestedPath, entry));
            }
        }
        return matchSet;
    }
    
    @Override
    public void addEventSource(VerbMatcher verbMatcher, String pathPattern, EventSourceRoute target) {
        if(!verbMatcher.belongsTo(Category.EventSource))
            throw new IllegalArgumentException("HttpMethod or WebSocket verb is not allowed for route");

        PathPatternMatcher pathMatcher = pathMatcherCompiler.compile(pathPattern);
        // Adds to end of list
        eventSourceRoutes.add(RouteEntry.create(verbMatcher, pathMatcher, target));
        
        for (RouteRegistryListener listener : listeners)
            listener.eventSourceRouteAdded(this, verbMatcher, pathPattern, target);
    }
    
    @Override
    public void clearRoutes() {
        eventSourceRoutes.clear();
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
