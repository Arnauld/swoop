package swoop.route;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import swoop.path.Path;
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
    private List<String> staticPaths;
    private PathMatcherCompiler pathMatcherCompiler;
    private CopyOnWriteArraySet<RouteRegistryListener> listeners;

    public RouteRegistryBasic() {
        routes = new ArrayList<RouteEntry>();
        staticPaths = new ArrayList<String>();
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
        List<RouteMatch> matchSet = new ArrayList<RouteMatch>();
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
        if(path.getVerb().isAny() && !target.isFilter())
            logger.debug("Be aware that you define a catch all ('any' verb) on a target");
        
        RouteEntry entry = new RouteEntry(path, pathMatcherCompiler.compile(path.getPathPattern()), target);
        // Adds to end of list
        routes.add(entry);

        for (RouteRegistryListener listener : listeners)
            listener.routeAdded(this, path, target);
    }

    @Override
    public void clearRoutes() {
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
