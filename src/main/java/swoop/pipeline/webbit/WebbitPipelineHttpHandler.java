package swoop.pipeline.webbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbitserver.EventSourceConnection;
import org.webbitserver.EventSourceHandler;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebSocketConnection;
import org.webbitserver.WebSocketHandler;
import org.webbitserver.wrapper.HttpResponseWrapper;

import swoop.path.Path;
import swoop.path.Verb;
import swoop.path.Verb.Category;
import swoop.pipeline.Flusher;
import swoop.pipeline.HandlerEntry;
import swoop.pipeline.HandlerRegistry;
import swoop.pipeline.MessageReceived;
import swoop.pipeline.Pipeline;
import swoop.pipeline.PipelineBuilder;
import swoop.pipeline.PipelineExecutor;
import swoop.pipeline.RouteParametersHandler;
import swoop.route.RouteParameters;
import swoop.server.webbit.Webbits;
import fj.data.List;

public class WebbitPipelineHttpHandler implements HttpHandler, WebSocketHandler, EventSourceHandler {

    private Logger logger = LoggerFactory.getLogger(WebbitPipelineHttpHandler.class);
    private final HandlerRegistry handlerRegistry;
    private final PipelineExecutor executor;

    public WebbitPipelineHttpHandler(PipelineExecutor executor, HandlerRegistry handlerRegistry) {
        this.executor = executor;
        this.handlerRegistry = handlerRegistry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.webbitserver.HttpHandler#handleHttpRequest(org.webbitserver.HttpRequest, org.webbitserver.HttpResponse,
     * org.webbitserver.HttpControl)
     */
    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl httpControl)
            throws Exception {
        Path requestedPath = Webbits.getPath(httpRequest);
        logger.debug("Analysing path <{}>", requestedPath);

        // websocket and eventsource are initiated by a 'GET'...
        if (requestedPath.getVerb() == Verb.Get) {
            if (handlerRegistry.hasEntryFor(Category.WebSocket, requestedPath.getPathPattern())) {
                logger.debug("Upgrading path <{}> to websocket", requestedPath);
                httpControl.upgradeToWebSocketConnection(this);
                return;
            } else if (handlerRegistry.hasEntryFor(Category.EventSource, requestedPath.getPathPattern())) {
                logger.debug("Upgrading path <{}> to eventsource", requestedPath);
                httpControl.upgradeToEventSourceConnection(this);
                return;
            }
            logger.debug("Path <{}> does not belongs to either websocket or eventsource", requestedPath);
        }

        List<HandlerEntry> entries = handlerRegistry.entriesFor(requestedPath);
        if (entries.isEmpty()) {
            logger.debug("No entries matching path <{}>", requestedPath);
            httpControl.nextHandler();
        } else {
            Pipeline pipeline = new PipelineBuilder()//
                    .handlers(entries)//
                    .executor(executor)//
                    .ensureFlushIsCalled()//
                    .buildPipeline();

            // fill pipeline context
            pipeline.with(HttpRequest.class, httpRequest)//
                    .with(HttpResponse.class, allowMultipleEndCall(httpResponse))//
                    .with(HttpControl.class, httpControl)//
                    .with(RouteParametersHandler.class, new RouteParametersHandler())//
                    .with(RouteParameters.class, new RouteParameters())//
                    .with(Verb.class, requestedPath.getVerb())//
                    .with(Path.class, requestedPath)//
                    .with(Flusher.class, new Flusher() {
                        @Override
                        public void flush(Pipeline pipeline) {
                            // TODO
                            // Payload payload = pipeline.get(Payload.class);
                            // applyPayloadOn(payload, pipeline);
                            pipeline.get(HttpResponse.class).end();
                        }
                    })//
                    .invokeNext();
        }
    }

    private HttpResponse allowMultipleEndCall(HttpResponse httpResponse) {
        return new HttpResponseWrapper(httpResponse) {
            private boolean alreadyEnded = false;
            @Override
            public HttpResponseWrapper end() {
                if(alreadyEnded)
                    return this;
                alreadyEnded=true;
                return super.end();
            }
            @Override
            public HttpResponseWrapper error(Throwable error) {
                if(alreadyEnded)
                    return this;
                alreadyEnded=true;
                return super.error(error);
            }
        };
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~[WebSocket]~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void onOpen(WebSocketConnection connection) throws Throwable {
        handleWebSocket(Verb.WebSocketOpen, connection, new MessageReceived());
    }

    @Override
    public void onClose(WebSocketConnection connection) throws Throwable {
        handleWebSocket(Verb.WebSocketClose, connection, new MessageReceived());
    }

    @Override
    public void onMessage(WebSocketConnection connection, String msg) throws Throwable {
        handleWebSocket(Verb.WebSocketMessage, connection, new MessageReceived(msg));
    }

    @Override
    public void onMessage(WebSocketConnection connection, byte[] msg) throws Throwable {
        handleWebSocket(Verb.WebSocketMessage, connection, new MessageReceived(msg));
    }

    @Override
    public void onPing(WebSocketConnection connection, byte[] msg) throws Throwable {
        handleWebSocket(Verb.WebSocketPing, connection, new MessageReceived(msg));
    }

    @Override
    public void onPong(WebSocketConnection connection, byte[] msg) throws Throwable {
        handleWebSocket(Verb.WebSocketPong, connection, new MessageReceived(msg));
    }

    private void handleWebSocket(Verb verb, WebSocketConnection connection, MessageReceived message) {
        Path requestedPath = Webbits.getPath(connection.httpRequest());
        requestedPath = requestedPath.withVerb(verb);
        List<HandlerEntry> entries = handlerRegistry.entriesFor(requestedPath);
        if (entries.isEmpty()) {
            logger.warn("No entries matching event source path <{}>", requestedPath);
        } else {
            Pipeline pipeline = new PipelineBuilder()//
                    .handlers(entries)//
                    .executor(executor)//
                    .buildPipeline();

            // fill pipeline context
            pipeline.with(WebSocketConnection.class, connection)//
                    .with(MessageReceived.class, message)//
                    .with(RouteParameters.class, new RouteParameters())//
                    .with(Verb.class, verb)//
                    .with(Path.class, requestedPath)//
                    .invokeNext();
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~[EventSource]~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void onOpen(EventSourceConnection connection) throws Exception {
        handleEventSource(Verb.EventSourceOpen, connection);
    }

    @Override
    public void onClose(EventSourceConnection connection) throws Exception {
        handleEventSource(Verb.EventSourceClose, connection);
    }

    private void handleEventSource(Verb verb, EventSourceConnection connection) {
        Path requestedPath = Webbits.getPath(connection.httpRequest());
        requestedPath = requestedPath.withVerb(verb);
        List<HandlerEntry> entries = handlerRegistry.entriesFor(requestedPath);
        if (entries.isEmpty()) {
            logger.warn("No entries matching event source path <{}>", requestedPath);
        } else {
            Pipeline pipeline = new PipelineBuilder()//
                    .handlers(entries)//
                    .executor(executor)//
                    .buildPipeline();

            // fill pipeline context
            pipeline.with(EventSourceConnection.class, connection)//
                    .with(RouteParameters.class, new RouteParameters())//
                    .with(Verb.class, verb)//
                    .with(Path.class, requestedPath)//
                    .invokeNext();
        }
    }
}
