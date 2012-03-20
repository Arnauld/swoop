package swoop.pipeline.webbit;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

import swoop.pipeline.HandlerRegistry;
import swoop.pipeline.PipelineExecutorDefault;
import swoop.server.webbit.WebbitSwoopServer;

public class WebbitPipelineServer {
    private Logger log = LoggerFactory.getLogger(WebbitSwoopServer.class);
    
    private PipelineExecutorDefault webThread;
    private WebServer webServer;
    private HandlerRegistry handlerRegistry;

    private Future<? extends WebServer> startFuture;
    
    public WebbitPipelineServer(HandlerRegistry handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
    }

    public synchronized void ignite(int port) {
        if(webThread==null)
            webThread = initWebThread();
        webServer = createWebServer(webThread, port);
        webServer.connectionExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log.error("Uncaught Exception [" + t + "]", e);
            }
        });
        definePipelineHandler(webServer, handlerRegistry);

        log.info("Starting server: {}", webServer.getUri());
        startFuture = webServer.start();
    }
    
    public synchronized void awaitServerStarted() {
        try {
            startFuture.get();
            log.info("Server started: {}", webServer.getUri());
        } catch (InterruptedException e) {
            log.warn("Interrupted while starting server", e);
        } catch (ExecutionException e) {
            log.warn("Error while starting server", e);
        }
    }
    
    public void shutdown() {
        webServer.stop();
    }

    protected void definePipelineHandler(WebServer webServer, HandlerRegistry handlerRegistry) {
        webServer.add(new WebbitPipelineHttpHandler(webThread, handlerRegistry));
    }

    protected PipelineExecutorDefault initWebThread() {
        return new PipelineExecutorDefault();
    }
    
    protected WebServer createWebServer(PipelineExecutorDefault webThread, int port) {
        return WebServers.createWebServer(webThread.getPipelineThread(), port);
    }
}
