package swoop;

import swoop.server.SwoopServerListener;

public class Swoop {

    private static ThreadLocal<SwoopBuilder> contextRef = new ThreadLocal<SwoopBuilder>() {
        protected SwoopBuilder initialValue() {
            return new SwoopBuilder();
        }
    };

    private static SwoopBuilder context() {
        return contextRef.get();
    }

    /**
     * Set the port that Swoop should listen on. If not called the default port is 4567. This has to be called before
     * any route mapping is done.
     * 
     * @param port
     *            The port number
     */
    public synchronized static void setPort(int port) {
        context().setPort(port);
    }

    public static void listener(SwoopServerListener serverListener) {
        context().serverListener(serverListener);
    }

    public static void staticDir(String dir) {
        context().staticDir(dir);
    }

    /**
     * Map the route for HTTP GET requests
     * 
     * @param route
     *            The route
     */
    public static void get(Action action) {
        context().get(action);
    }

    /**
     * Map the route for HTTP POST requests
     * 
     * @param route
     *            The route
     */
    public static void post(Action action) {
        context().post(action);
    }

    /**
     * Map the route for HTTP PUT requests
     * 
     * @param route
     *            The route
     */
    public static void put(Action action) {
        context().put(action);
    }

    /**
     * Map the route for HTTP DELETE requests
     * 
     * @param route
     *            The route
     */
    public static void delete(Action action) {
        context().delete(action);
    }

    /**
     * Map the route for HTTP HEAD requests
     * 
     * @param route
     *            The route
     */
    public static void head(Action action) {
        context().head(action);
    }

    /**
     * Map the route for HTTP TRACE requests
     * 
     * @param route
     *            The route
     */
    public static void trace(Action action) {
        context().trace(action);
    }

    /**
     * Map the route for HTTP CONNECT requests
     * 
     * @param route
     *            The route
     */
    public static void connect(Action action) {
        context().connect(action);
    }

    /**
     * Map the route for HTTP OPTIONS requests
     * 
     * @param route
     *            The route
     */
    public static void options(Action action) {
        context().options(action);
    }

    /**
     * Maps a filter to be executed before any matching routes
     * 
     * @param filter
     *            The filter
     */
    public static void before(Before before) {
        context().before(before);
    }

    /**
     * Maps a filter to be executed after any matching routes
     * 
     * @param filter
     *            The filter
     */
    public static void after(After after) {
        context().after(after);
    }

    /**
     * Maps an interceptor to be executed around any matching routes
     * 
     * @param interceptor
     *            The interceptor
     */
    public static void around(Filter interceptor) {
        context().around(interceptor);
    }

    public static void define(WebSocket webSocket) {
        context().define(webSocket);
    }

    public static void define(WebSocketFilter filter) {
        context().define(filter);
    }

    /**
     * 
     * @param filter
     */
    public static void webSocket(WebSocket webSocket) {
        context().webSocket(webSocket);
    }

    /**
     * 
     * @param filter
     */
    public static void aroundWebSocket(WebSocketFilter filter) {
        context().aroundWebSocket(filter);
    }

    /**
     * @param eventSource
     */
    public static void define(EventSource eventSource) {
        context().define(eventSource);
    }

    /**
     * @param filter
     */
    public static void define(EventSourceFilter filter) {
        context().define(filter);
    }

    /**
     * 
     * @param filter
     */
    public static void eventSource(EventSource eventSource) {
        context().eventSource(eventSource);
    }

    /**
     * 
     * @param filter
     */
    public static void aroundEventSource(EventSourceFilter filter) {
        context().aroundEventSource(filter);
    }

    /**
     * 
     */
    public synchronized static void stop() {
        context().stop();
    }

}
