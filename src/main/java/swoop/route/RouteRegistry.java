package swoop.route;

import java.util.List;

import swoop.Action;
import swoop.After;
import swoop.Before;
import swoop.Filter;

/**
 * Route registry
 */
public interface RouteRegistry {
    
    void addRouteRegistryListener(RouteRegistryListener listener);
    
    void removeRouteRegistryListener(RouteRegistryListener listener);

    
    /**
     * Parses, validates and adds a route
     * 
     * @param route
     * @param target
     */
    void addRoute(String route, Route target);
    
    /**
     * Adds a route
     * 
     * @param route
     * @param target
     */
    void addRoute(Path route, Route target);
    
    /**
     * Define a directory containing static content.
     * @param path
     */
    void addStaticDir(String path);
    
    /**
     * Return a view of the directories containing static content.
     * @return
     */
    List<String> getStaticDirs();
    
    /**
     * Finds all routes (filters and target) matching the requested route path 
     * 
     * If more than one target ({@link Action}) is found an exception should be thrown.
     * 
     * @return the list of the routes matching given in the same order as they were defined
     * @see After
     * @see Before
     * @see Filter
     * @see Action
     */
    List<RouteMatch> findRoutes(Path requestPath);
    
    /**
     * Clear all routes
     */
    void clearRoutes();
}
