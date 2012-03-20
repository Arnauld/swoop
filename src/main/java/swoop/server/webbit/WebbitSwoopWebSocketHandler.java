package swoop.server.webbit;

import static swoop.server.webbit.WebbitAdapters.adaptConnection;
import static swoop.server.webbit.WebbitAdapters.adaptMessage;
import static swoop.server.webbit.WebbitAdapters.adaptRequest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebSocketConnection;
import org.webbitserver.WebSocketHandler;

import swoop.WebSocketMessage;
import swoop.path.Path;
import swoop.path.Verb;
import swoop.route.HaltException;
import swoop.route.Invoker;
import swoop.route.RedirectException;
import swoop.route.RouteChainBasic;
import swoop.route.RouteMatch;
import swoop.route.RouteParameters;
import swoop.route.RouteRegistry;
import swoop.route.WebSocketInvoker;
import swoop.route.WebSocketRoute;
import swoop.util.ContextBasic;

public class WebbitSwoopWebSocketHandler implements WebSocketHandler, HttpHandler {

    private Logger logger = LoggerFactory.getLogger(WebbitSwoopWebSocketHandler.class);
    private RouteRegistry routeRegistry;

    public WebbitSwoopWebSocketHandler(RouteRegistry routeRegistry) {
        this.routeRegistry = routeRegistry;
    }

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl control)
            throws Exception {
        Path path = Webbits.getPath(httpRequest);
        logger.debug("Analysing path <{}>", path);

        // websocket is initiated by a 'GET'...
        if (path.getVerb() == Verb.Get) {
            if (routeRegistry.hasWebSocketRoutes(path.getPathPattern())) {
                logger.debug("Upgrading path <{}> to websocket", path);
                control.upgradeToWebSocketConnection(this);
                return;
            }
            logger.debug("Path <{}> does not belongs to websocket", path);
        }
        control.nextHandler();
    }

    @Override
    public void onOpen(WebSocketConnection connection) throws Throwable {
        logger.debug("OnOpen <{}>", connection.httpRequest().uri());
        dispatch(connection, Verb.WebSocketOpen, new RouteParameters(), null);
    }

    @Override
    public void onClose(WebSocketConnection connection) throws Throwable {
        logger.debug("onClose <{}>", connection.httpRequest().uri());
        dispatch(connection,Verb.WebSocketClose, new RouteParameters(), null);
    }

    @Override
    public void onMessage(WebSocketConnection connection, String msg) throws Throwable {
        logger.debug("onMessage <{}>", connection.httpRequest().uri());
        RouteParameters routeParameters = new RouteParameters();
        WebSocketMessage message = adaptMessage(msg, routeParameters);
        dispatch(connection, Verb.WebSocketMessage, routeParameters, message);
    }

    @Override
    public void onMessage(WebSocketConnection connection, byte[] msg) throws Throwable {
        logger.debug("onMessage <{}>", connection.httpRequest().uri());
        RouteParameters routeParameters = new RouteParameters();
        WebSocketMessage message = adaptMessage(msg, routeParameters);
        dispatch(connection, Verb.WebSocketMessage, routeParameters, message);
    }

    @Override
    public void onPing(WebSocketConnection connection, byte[] msg) throws Throwable {
        logger.debug("onPing <{}>", connection.httpRequest().uri());
        RouteParameters routeParameters = new RouteParameters();
        WebSocketMessage message = adaptMessage(msg, routeParameters);
        dispatch(connection, Verb.WebSocketPing, routeParameters, message);
    }

    @Override
    public void onPong(WebSocketConnection connection, byte[] msg) throws Throwable {
        logger.debug("onPong <{}>", connection.httpRequest().uri());
        RouteParameters routeParameters = new RouteParameters();
        WebSocketMessage message = adaptMessage(msg, routeParameters);
        dispatch(connection, Verb.WebSocketPing, routeParameters, message);
    }

    protected void dispatch(WebSocketConnection wConnection, Verb verb,
            RouteParameters routeParameters, WebSocketMessage msg) {
        HttpRequest httpRequest = wConnection.httpRequest();
        Path path = Webbits.getPath(httpRequest).withVerb(verb);

        List<RouteMatch<WebSocketRoute>> matches = routeRegistry.findWebSocketRoutes(path);
        logger.debug("Dispatching webSocket call <{}> through a chain of #{} link(s) (filter and target)", wConnection
                .httpRequest().uri(), matches.size());

        WebbitRequestAdapter request = adaptRequest(httpRequest, routeParameters);
        WebbitWebSocketConnection connection = adaptConnection(wConnection, request);
        ContextBasic context = new ContextBasic()//
                .register(RouteParameters.class, routeParameters);

        try {
            Invoker<RouteMatch<WebSocketRoute>> invoker = new WebSocketInvoker(verb, connection, msg);
            RouteChainBasic.create(invoker, matches, context).invokeNext();
        } catch (HaltException he) {
            logger.info("Processing halted", he);
        } catch (RedirectException re) {
            logger.info("Redirecting to " + re.getLocation(), re);
        } catch (Exception e) {
            logger.error("Processing error <" + httpRequest.uri() + ">", e);
        }
    }

}
