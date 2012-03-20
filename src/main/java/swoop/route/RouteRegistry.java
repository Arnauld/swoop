package swoop.route;

import java.util.List;

import swoop.Action;
import swoop.After;
import swoop.Before;
import swoop.EventSource;
import swoop.Filter;
import swoop.WebSocket;
import swoop.path.Path;
import swoop.path.Verb;
import swoop.path.VerbMatcher;

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
    void addWebSocket(VerbMatcher verbMatcher, String pathPattern, WebSocketRoute target);
    
    /**
     * Parses, validates and adds a route to handle a {@link EventSource}
     * 
     * <strong>Note the accepted verbs are {@link Verb#EventSource} and {@link Verb#Any}.</strong>
     * 
     * @param route
     * @param target
     */
    void addEventSource(VerbMatcher verbMatcher, String pathPattern, EventSourceRoute target);

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
    void addRoute(VerbMatcher verbMatcher, String pathPattern, Route target);

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
    List<RouteMatch<Route>> findRoutes(Path requestedPath);
    
    /**
     * Finds all routes (filters and target) matching the requested route path
     * 
     * If more than one target ({@link WebSocket}) is found an exception should be thrown.
     * 
     * @return the list of the routes matching given in the same order as they were defined
     * @see WebSocket
     */
    List<RouteMatch<WebSocketRoute>> findWebSocketRoutes(Path requestedPath);
    
    /**
     * 
     */
    boolean hasWebSocketRoutes(String pathPattern);
    
    /**
     * Finds all routes (filters and target) matching the requested route path
     * 
     * If more than one target ({@link EventSource}) is found an exception should be thrown.
     * 
     * @return the list of the routes matching given in the same order as they were defined
     * @see WebSocket
     */
    List<RouteMatch<EventSourceRoute>> findEventSourceRoutes(Path requestedPath);

    /**
     * 
     */
    boolean hasEventSourceRoutes(String pathPattern);

    /**
     * Clear all routes
     */
    void clearRoutes();

}
