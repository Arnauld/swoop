package swoop.server.webbit;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.handler.StaticFileHandler;

import swoop.route.RouteRegistry;
import swoop.route.RouteRegistryListener;
import swoop.route.RouteRegistryListenerAdapter;
import swoop.server.SwoopServer;
import swoop.server.SwoopServerListener;
import swoop.util.New;

public class WebbitSwoopServer implements SwoopServer {

    private Logger log = LoggerFactory.getLogger(WebbitSwoopServer.class);

    private WebServer webServer;
    private RouteRegistry routeRegistry;

    private RouteRegistryListener listener;
    private CopyOnWriteArraySet<SwoopServerListener> listeners = New.copyOnWriteArraySet();

    public WebbitSwoopServer(RouteRegistry routeRegistry) {
        this.routeRegistry = routeRegistry;
        registerListener();
    }

    private void registerListener() {
        listener = new RouteRegistryListenerAdapter() {
            @Override
            public void staticDirAdded(RouteRegistry registry, String dir) {
                addStaticDir(dir);
            }
        };
        routeRegistry.addRouteRegistryListener(listener);
    }
    
    @Override
    public void addListener(SwoopServerListener listener) {
        listeners.add(listener);
    }
    
    @Override
    public void removeListener(SwoopServerListener listener) {
        listeners.remove(listener);
    }

    private synchronized void addStaticDir(String dir) {
        if (webServer != null)
            webServer.add(new StaticFileHandler(dir));
    }
    
    @Override
    public synchronized void ignite(int port) {
        for (SwoopServerListener listener : listeners)
            listener.serverStarting(this);

        webServer = createWebServer(port);
        
        webServer.connectionExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log.error("Uncaught Exception [" + t + "]", e);
            }
        });
        defineStaticDirs(webServer, routeRegistry);
        defineSwoopWebSocketHandler(webServer, routeRegistry);
        defineSwoopHttpHandler(webServer, routeRegistry);

        log.info("Starting server on port {}", port);
        Future<? extends WebServer> start = webServer.start();
        try {
            start.get();
            log.info("Server started: 0.0.0.0:{}", port);
        } catch (InterruptedException e) {
            log.warn("Interrupted while starting server", e);
        } catch (ExecutionException e) {
            log.warn("Error while starting server", e);
        }

        for (SwoopServerListener listener : listeners)
            listener.serverStarted(this);
    }

    protected void defineSwoopHttpHandler(WebServer webServer, RouteRegistry routeRegistry) {
        webServer.add(new WebbitSwoopHttpHandler(routeRegistry));
    }
    
    protected synchronized void defineSwoopWebSocketHandler(WebServer webServer, RouteRegistry routeRegistry) {
        webServer.add(new WebbitSwoopWebSocketHandler(routeRegistry));
    }

    protected void defineStaticDirs(WebServer webServer, RouteRegistry routeMatcher) {
        // TODO refactor this: remove listener based behavior
        // and rely on something comparable to webSocket and Route...
        // ~~> rename method to 'defineStaticDirsHandler'
        // ~~> use only one and shared executor for all underlying StaticFileHandler! 
        for (String dir : routeMatcher.getStaticDirs()) {
            webServer.add(new StaticFileHandler(dir));// "/web"
        }
    }

    protected WebServer createWebServer(int port) {
        return WebServers.createWebServer(port);
    }

    @Override
    public void stop() {
        for (SwoopServerListener listener : listeners)
            listener.serverStopping(this);

        Future<? extends WebServer> stop = webServer.stop();
        try {
            stop.get();
            log.info("Server stopped");
        } catch (InterruptedException e) {
            log.warn("Interrupted while stopping server", e);
        } catch (ExecutionException e) {
            log.warn("Error while stopping server", e);
        }

        for (SwoopServerListener listener : listeners)
            listener.serverStopped(this);
    }

}
