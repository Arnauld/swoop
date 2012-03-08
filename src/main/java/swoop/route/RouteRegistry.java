package swoop.route;

import java.util.List;

import swoop.Action;
import swoop.After;
import swoop.Before;
import swoop.Filter;
import swoop.WebSocket;
import swoop.path.Path;
import swoop.path.Verb;

/**
 * Route registry
 */
public interface RouteRegistry {

    void addRouteRegistryListener(RouteRegistryListener listener);

    void removeRouteRegistryListener(RouteRegistryListener listener);

    /**
     * Parses, validates and adds a route to handle a {@link WebSocket}
     * 
     * <strong>Note the accepted verbs are {@link Verb#WebSocket} and {@link Verb#Any}.</strong>
     * 
     * @param route
     * @param target
     */
    void addWebSocket(Path path, WebSocketRoute target);

    /**
     * Parses, validates and adds a route
     * 
     * <strong>Note the accepted verbs are {@link Verb#Get}, {@link Verb#Put}, {@link Verb#Delete}, {@link Verb#Post},
     * {@link Verb#Connect}, {@link Verb#Head}, {@link Verb#Options}, {@link Verb#Trace} and {@link Verb#Any}.</strong>
     * 
     * @param route
     * @param target
     */
    void addRoute(String route, Route target);

    /**
     * Adds a route
     * 
     * <strong>Note the accepted verbs are {@link Verb#Get}, {@link Verb#Put}, {@link Verb#Delete}, {@link Verb#Post},
     * {@link Verb#Connect}, {@link Verb#Head}, {@link Verb#Options}, {@link Verb#Trace} and {@link Verb#Any}.</strong>
     * 
     * @param route
     * @param target
     */
    void addRoute(Path route, Route target);

    /**
     * Define a directory containing static content.
     * 
     * @param path
     */
    void addStaticDir(String path);

    /**
     * Return a view of the directories containing static content.
     * 
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
    List<RouteMatch> findRoutes(Path requestedPath);
    
    /**
     * Finds all routes (filters and target) matching the requested route path
     * 
     * If more than one target ({@link WebSocket}) is found an exception should be thrown.
     * 
     * @return the list of the routes matching given in the same order as they were defined
     * @see WebSocket
     */
    List<WebSocketRouteMatch> findWebSocketRoutes(Path requestedPath);

    /**
     * Clear all routes
     */
    void clearRoutes();
}
