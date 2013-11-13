package swoop.server.webbit;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.handler.StaticFileHandler;

import swoop.ResponseProcessor;
import swoop.route.RouteRegistry;
import swoop.route.RouteRegistryListener;
import swoop.route.RouteRegistryListenerAdapter;
import swoop.server.SwoopServer;
import swoop.server.SwoopServerListener;
import swoop.util.New;
import swoop.util.Provider;

public class WebbitSwoopServer implements SwoopServer {

    private final Logger log = LoggerFactory.getLogger(WebbitSwoopServer.class);

    private final RouteRegistry routeRegistry;
    private final Provider<ResponseProcessor> responseProcessorProvider;

    private WebServer webServer;
    private RouteRegistryListener listener;
    private CopyOnWriteArraySet<SwoopServerListener> listeners = New.copyOnWriteArraySet();

    public WebbitSwoopServer(RouteRegistry routeRegistry, Provider<ResponseProcessor> responseProcessorProvider) {
        this.routeRegistry = routeRegistry;
        this.responseProcessorProvider = responseProcessorProvider;
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
    public Object raw() {
        return webServer;
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
        defineSwoopEventSourceHandler(webServer, routeRegistry);
        defineSwoopHttpHandler(webServer, routeRegistry);

        log.info("Starting server: {}", webServer.getUri());
        Future<? extends WebServer> start = webServer.start();
        try {
            start.get();
            log.info("Server started: {}", webServer.getUri());
            for (SwoopServerListener listener : listeners)
                listener.serverStarted(this);
        } catch (InterruptedException e) {
            log.warn("Interrupted while starting server", e);
        } catch (ExecutionException e) {
            log.warn("Error while starting server", e);
        }
    }

    protected void defineSwoopHttpHandler(WebServer webServer, RouteRegistry routeRegistry) {
        webServer.add(new WebbitSwoopHttpHandler(routeRegistry, responseProcessorProvider));
    }
    
    protected void defineSwoopWebSocketHandler(WebServer webServer, RouteRegistry routeRegistry) {
        webServer.add(new WebbitSwoopWebSocketHandler(routeRegistry));
    }
    
    protected void defineSwoopEventSourceHandler(WebServer webServer, RouteRegistry routeRegistry) {
        webServer.add(new WebbitSwoopEventSourceHandler(routeRegistry));
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
