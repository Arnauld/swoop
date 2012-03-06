package swoop;

import swoop.path.Path;
import swoop.path.Verb;
import swoop.route.Route;
import swoop.route.RouteRegistry;
import swoop.server.SwoopServer;
import swoop.server.SwoopServerFactory;

public class Swoop {

    private static boolean initialized = false;

    private static SwoopServer server;
    private static RouteRegistry routeMatcher;
    private static int port = 4567;

    /**
     * Set the port that Swoop should listen on. If not called the default port is 4567. This has to be called before
     * any route mapping is done.
     * 
     * @param port
     *            The port number
     */
    public synchronized static void setPort(int port) {
        if (initialized) {
            throw new IllegalStateException("This must be done before route mapping has begun");
        }
        Swoop.port = port;
    }
    
    public static void staticDir(String dir) {
        init();
        routeMatcher.addStaticDir(dir);
    }

    /**
     * Map the route for HTTP GET requests
     * 
     * @param route
     *            The route
     */
    public static void get(Action action) {
        addRoute(Verb.Get, action);
    }

    /**
     * Map the route for HTTP POST requests
     * 
     * @param route
     *            The route
     */
    public static void post(Action action) {
        addRoute(Verb.Post, action);
    }

    /**
     * Map the route for HTTP PUT requests
     * 
     * @param route
     *            The route
     */
    public static void put(Action action) {
        addRoute(Verb.Put, action);
    }

    /**
     * Map the route for HTTP DELETE requests
     * 
     * @param route
     *            The route
     */
    public static void delete(Action action) {
        addRoute(Verb.Delete, action);
    }

    /**
     * Map the route for HTTP HEAD requests
     * 
     * @param route
     *            The route
     */
    public static void head(Action action) {
        addRoute(Verb.Head, action);
    }

    /**
     * Map the route for HTTP TRACE requests
     * 
     * @param route
     *            The route
     */
    public static void trace(Action action) {
        addRoute(Verb.Trace, action);
    }

    /**
     * Map the route for HTTP CONNECT requests
     * 
     * @param route
     *            The route
     */
    public static void connect(Action action) {
        addRoute(Verb.Connect, action);
    }

    /**
     * Map the route for HTTP OPTIONS requests
     * 
     * @param route
     *            The route
     */
    public static void options(Action action) {
        addRoute(Verb.Options, action);
    }

    /**
     * Maps a filter to be executed before any matching routes
     * 
     * @param filter
     *            The filter
     */
    public static void before(Before before) {
        addFilter(before);
    }

    /**
     * Maps a filter to be executed after any matching routes
     * 
     * @param filter
     *            The filter
     */
    public static void after(After after) {
        addFilter(after);
    }

    /**
     * Maps an interceptor to be executed around any matching routes
     * 
     * @param interceptor
     *            The interceptor
     */
    public static void around(Filter interceptor) {
        addFilter(interceptor);
    }
    
    private static void addFilter(Filter filter) {
        init();
        routeMatcher.addRoute(new Path(filter.getApplyOn(), filter.getPath()), filter);
    }

    private static void addRoute(Verb verb, Route route) {
        init();
        routeMatcher.addRoute(new Path(verb, route.getPath()), route);
    }
    
    // WARNING, used for jUnit testing only!!!
    synchronized static void clearRoutes() {
        routeMatcher.clearRoutes();
    }

    // Used for jUnit testing!
    synchronized static void stop() {
        if (server != null) {
            server.stop();
        }
        initialized = false;
    }

    private synchronized static final void init() {
        if (!initialized) {
            routeMatcher = Defaults.createRouteMatcher();
            Defaults.executeAsynchronously(new Runnable() {
                @Override
                public void run() {
                    server = SwoopServerFactory.Default.create(routeMatcher);
                    server.ignite(port);
                }
            });
            initialized = true;
        }
    }
}
