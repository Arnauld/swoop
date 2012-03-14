package swoop;

import swoop.path.Path;
import swoop.path.Verb;
import swoop.route.Route;
import swoop.route.RouteRegistry;
import swoop.route.RouteRegistryFactory;
import swoop.server.SwoopServer;
import swoop.server.SwoopServerFactory;
import swoop.server.SwoopServerListener;

public class SwoopBuilder {
    
    private RouteRegistryFactory routeMatcherFactory;
    private SwoopServerFactory swoopServerFactory;
    //
    private boolean initialized = false;
    private SwoopServer server;
    private SwoopServerListener serverListener;
    private RouteRegistry routeRegistry;
    private int port = 4567;

    public void stop() {
        if (server != null) {
            server.stop();
        }
        initialized = false;
    }
    
    public void routeMatcherFactory(RouteRegistryFactory routeMatcherFactory) {
        this.routeMatcherFactory = routeMatcherFactory;
    }
    
    protected RouteRegistryFactory getOrDefaultRouteMatcherFactory() {
        if(routeMatcherFactory==null)
            routeMatcherFactory = Defaults.getRouteMatcherFactory();
        return routeMatcherFactory;
    }
    
    public void swoopServerFactory(SwoopServerFactory swoopServerFactory) {
        this.swoopServerFactory = swoopServerFactory;
    }
    
    protected SwoopServerFactory getOrDefaultSwoopServerFactory() {
        if(swoopServerFactory==null)
            swoopServerFactory = Defaults.getSwoopServerFactory();
        return swoopServerFactory;
    }
    
    public SwoopServer getServer() {
        return server;
    }

    /**
     * Set the port that Swoop should listen on. If not called the default port is 4567. This has to be called before
     * any route mapping is done.
     * 
     * @param port
     *            The port number
     */
    public synchronized void setPort(int port) {
        if (initialized) {
            throw new IllegalStateException("This must be done before route mapping has begun");
        }
        this.port = port;
    }

    public void serverListener(SwoopServerListener serverListener) {
        this.serverListener = serverListener;
    }

    public void staticDir(String dir) {
        init();
        this.routeRegistry.addStaticDir(dir);
    }

    /**
     * Map the route for HTTP GET requests
     * 
     * @param route
     *            The route
     */
    public void get(Action action) {
        addRoute(Verb.Get, action);
    }

    /**
     * Map the route for HTTP POST requests
     * 
     * @param route
     *            The route
     */
    public void post(Action action) {
        addRoute(Verb.Post, action);
    }

    /**
     * Map the route for HTTP PUT requests
     * 
     * @param route
     *            The route
     */
    public void put(Action action) {
        addRoute(Verb.Put, action);
    }

    /**
     * Map the route for HTTP DELETE requests
     * 
     * @param route
     *            The route
     */
    public void delete(Action action) {
        addRoute(Verb.Delete, action);
    }

    /**
     * Map the route for HTTP HEAD requests
     * 
     * @param route
     *            The route
     */
    public void head(Action action) {
        addRoute(Verb.Head, action);
    }

    /**
     * Map the route for HTTP TRACE requests
     * 
     * @param route
     *            The route
     */
    public void trace(Action action) {
        addRoute(Verb.Trace, action);
    }

    /**
     * Map the route for HTTP CONNECT requests
     * 
     * @param route
     *            The route
     */
    public void connect(Action action) {
        addRoute(Verb.Connect, action);
    }

    /**
     * Map the route for HTTP OPTIONS requests
     * 
     * @param route
     *            The route
     */
    public void options(Action action) {
        addRoute(Verb.Options, action);
    }

    /**
     * Maps a filter to be executed before any matching routes
     * 
     * @param filter
     *            The filter
     */
    public void before(Before before) {
        addFilter(before);
    }

    /**
     * Maps a filter to be executed after any matching routes
     * 
     * @param filter
     *            The filter
     */
    public void after(After after) {
        addFilter(after);
    }

    /**
     * Maps an interceptor to be executed around any matching routes
     * 
     * @param interceptor
     *            The interceptor
     */
    public void around(Filter interceptor) {
        addFilter(interceptor);
    }

    public void define(WebSocket webSocket) {
        webSocket(webSocket);
    }

    public void define(WebSocketFilter filter) {
        aroundWebSocket(filter);
    }

    /**
     * 
     * @param filter
     */
    public void webSocket(WebSocket webSocket) {
        init();
        this.routeRegistry.addWebSocket(new Path(Verb.WebSocket, webSocket.getPath()), webSocket);
    }

    /**
     * 
     * @param filter
     */
    public void aroundWebSocket(WebSocketFilter filter) {
        init();
        this.routeRegistry.addWebSocket(new Path(Verb.WebSocket, filter.getPath()), filter);
    }

    /**
     * @param eventSource
     */
    public void define(EventSource eventSource) {
        eventSource(eventSource);
    }

    /**
     * @param filter
     */
    public void define(EventSourceFilter filter) {
        aroundEventSource(filter);
    }

    /**
     * 
     * @param filter
     */
    public void eventSource(EventSource eventSource) {
        init();
        this.routeRegistry.addEventSource(new Path(Verb.EventSource, eventSource.getPath()), eventSource);
    }

    /**
     * 
     * @param filter
     */
    public void aroundEventSource(EventSourceFilter filter) {
        init();
        this.routeRegistry.addEventSource(new Path(Verb.EventSource, filter.getPath()), filter);
    }

    /**
     * @param filter
     */
    private void addFilter(Filter filter) {
        init();
        this.routeRegistry.addRoute(new Path(filter.getApplyOn(), filter.getPath()), filter);
    }

    /**
     * @param verb
     * @param route
     */
    private void addRoute(Verb verb, Route route) {
        init();
        this.routeRegistry.addRoute(new Path(verb, route.getPath()), route);
    }

    private synchronized final void init() {
        if (!initialized) {
            routeRegistry = getOrDefaultRouteMatcherFactory().create();
            server = getOrDefaultSwoopServerFactory().create(routeRegistry);
            startServer();
            initialized = true;
        }
    }
    
    /**
     * Call {@link #asyncStartServer()} by default.
     * 
     * @see #execStartServer()
     * @see #asyncStartServer()
     */
    protected void startServer() {
        asyncStartServer();
    }
    
    protected void asyncStartServer() {
        Defaults.executeAsynchronously(new Runnable() {
            @Override
            public void run() {
                execStartServer();
            }
        });
    }
    
    protected void execStartServer() {
        if (serverListener != null)
            server.addListener(serverListener);
        server.ignite(port);
    }
}
