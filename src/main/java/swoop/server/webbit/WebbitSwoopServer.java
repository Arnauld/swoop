package swoop.server.webbit;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.handler.StaticFileHandler;

import swoop.route.RouteRegistry;
import swoop.server.SwoopServer;

public class WebbitSwoopServer implements SwoopServer {

    private Logger log = LoggerFactory.getLogger(WebbitSwoopServer.class);

    private WebServer webServer;
    private RouteRegistry routeMatcher;

    public WebbitSwoopServer(RouteRegistry routeMatcher) {
        this.routeMatcher = routeMatcher;
    }

    @Override
    public void ignite(int port) {
        webServer = createWebServer(port);
        defineStaticDirs(webServer, routeMatcher);
        defineSwoopHandler(webServer, routeMatcher);

        Future<? extends WebServer> start = webServer.start();
        try {
            start.get();
            log.info("Server started");
        } catch (InterruptedException e) {
            log.warn("Interrupted while starting server", e);
        } catch (ExecutionException e) {
            log.warn("Error while starting server", e);
        }
    }
    
    protected void defineSwoopHandler(WebServer webServer, RouteRegistry routeMatcher) {
        webServer.add(new WebbitSwoopHttpHandler(routeMatcher));
    }

    protected void defineStaticDirs(WebServer webServer, RouteRegistry routeMatcher) {
        for (String dir : routeMatcher.getStaticDirs()) {
            webServer.add(new StaticFileHandler(dir));// "/web"
        }
    }

    protected WebServer createWebServer(int port) {
        return WebServers.createWebServer(port);
    }

    @Override
    public void stop() {
        Future<? extends WebServer> stop = webServer.stop();
        try {
            stop.get();
            log.info("Server stopped");
        } catch (InterruptedException e) {
            log.warn("Interrupted while stopping server", e);
        } catch (ExecutionException e) {
            log.warn("Error while stopping server", e);
        }

    }

}
