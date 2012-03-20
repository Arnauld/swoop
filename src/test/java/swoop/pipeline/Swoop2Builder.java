package swoop.pipeline;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.path.PathMatcherCompiler;
import swoop.path.PathMatcherSinatraCompiler;
import swoop.path.PathPatternMatcher;
import swoop.path.Verb;
import swoop.path.VerbMatcher;
import swoop.path.VerbMatchers;
import swoop.pipeline.webbit.WebbitPipelineServer;

public class Swoop2Builder {
    private Logger logger = LoggerFactory.getLogger(Swoop2Builder.class);
    
    private PathMatcherCompiler compiler = new PathMatcherSinatraCompiler();
    private HandlerRegistry registry;
    private boolean initialized;
    private WebbitPipelineServer server;
    private int port;
    private CountDownLatch latch;
    
    public synchronized WebbitPipelineServer server() throws InterruptedException {
        if(latch==null)
            throw new IllegalStateException("Server not started: define at least one route");
        logger.info("Waiting for the server to be in starting mode");
        latch.await();
        return server;
    }
    
    public synchronized Swoop2Builder port(int port) {
        if(initialized)
            throw new IllegalStateException("Server already initialized");
        this.port = port;
        return this;
    }

    public Swoop2Builder get(String route, Handler handler) {
        return on(Verb.Get, route, handler);
    }
    
    public Swoop2Builder post(String route, Handler handler) {
        return on(Verb.Post, route, handler);
    }
    
    public Swoop2Builder put(String route, Handler handler) {
        return on(Verb.Put, route, handler);
    }
    
    public Swoop2Builder delete(String route, Handler handler) {
        return on(Verb.Delete, route, handler);
    }
    
    public Swoop2Builder webSocket(String route, Handler handler) {
        return on(VerbMatchers.webSocket, route, handler);
    }
    
    public Swoop2Builder eventSource(String route, Handler handler) {
        return on(VerbMatchers.eventSource, route, handler);
    }
    
    public Swoop2Builder on(Verb verb, String route, Handler handler) {
        return on(VerbMatchers.on(verb), route, handler);
    }
    
    public Swoop2Builder on(VerbMatcher verbMatcher, String route, Handler handler) {
        PathPatternMatcher pathPatternMatcher = compiler.compile(route);
        return on(new PathMatcherDefault(verbMatcher, pathPatternMatcher), handler);
    }

    public Swoop2Builder on(PathMatcher pathMatcher, Handler handler) {
        init();
        registry.defineEntry(pathMatcher, handler);
        return this;
    }

    private synchronized void init() {
        if(initialized)
            return;
        initialized = true;
        registry = new HandlerRegistry();
        server = new WebbitPipelineServer(registry);
        latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                server.ignite(port);
                latch.countDown();
            }
        }).start();
    }

}
