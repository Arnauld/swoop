package swoop.server.webbit;

import static swoop.server.webbit.WebbitAdapters.adaptConnection;
import static swoop.server.webbit.WebbitAdapters.adaptMessage;
import static swoop.server.webbit.WebbitAdapters.adaptRequest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbitserver.HttpRequest;
import org.webbitserver.WebSocketConnection;
import org.webbitserver.WebSocketHandler;

import swoop.WebSocketMessage;
import swoop.path.Path;
import swoop.route.HaltException;
import swoop.route.RedirectException;
import swoop.route.RouteParameters;
import swoop.route.RouteRegistry;
import swoop.route.WebSocketInvoker;
import swoop.route.WebSocketInvokers;
import swoop.route.WebSocketRouteChainBasic;
import swoop.route.WebSocketRouteMatch;

public class WebbitSwoopWebSocketHandler implements WebSocketHandler {

    private Logger logger = LoggerFactory.getLogger(WebbitSwoopWebSocketHandler.class);
    private RouteRegistry routeRegistry;

    public WebbitSwoopWebSocketHandler(RouteRegistry routeRegistry) {
        this.routeRegistry = routeRegistry;
    }

    @Override
    public void onOpen(WebSocketConnection connection) throws Throwable {
        dispatch(connection, WebSocketInvokers.onOpen(), new RouteParameters());
    }

    @Override
    public void onClose(WebSocketConnection connection) throws Throwable {
        dispatch(connection, WebSocketInvokers.onClose(), new RouteParameters());
    }

    @Override
    public void onMessage(WebSocketConnection connection, String msg) throws Throwable {
        RouteParameters routeParameters = new RouteParameters();
        WebSocketMessage message = adaptMessage(msg, routeParameters);
        dispatch(connection, WebSocketInvokers.onMessage(message), routeParameters);
    }

    @Override
    public void onMessage(WebSocketConnection connection, byte[] msg) throws Throwable {
        RouteParameters routeParameters = new RouteParameters();
        WebSocketMessage message = adaptMessage(msg, routeParameters);
        dispatch(connection, WebSocketInvokers.onMessage(message), routeParameters);
    }

    @Override
    public void onPing(WebSocketConnection connection, byte[] msg) throws Throwable {
        RouteParameters routeParameters = new RouteParameters();
        WebSocketMessage message = adaptMessage(msg, routeParameters);
        dispatch(connection, WebSocketInvokers.onPing(message), routeParameters);
    }

    @Override
    public void onPong(WebSocketConnection connection, byte[] msg) throws Throwable {
        RouteParameters routeParameters = new RouteParameters();
        WebSocketMessage message = adaptMessage(msg, routeParameters);
        dispatch(connection, WebSocketInvokers.onPong(message), routeParameters);
    }
    
    protected void dispatch(WebSocketConnection wConnection, WebSocketInvoker invoker, RouteParameters routeParameters) {
        HttpRequest httpRequest = wConnection.httpRequest();
        Path path = Webbits.getPath(httpRequest);
        List<WebSocketRouteMatch> matches = routeRegistry.findWebSocketRoutes(path);
        WebbitRequestAdapter request = adaptRequest(httpRequest, routeParameters);
        WebbitWebSocketConnection connection = adaptConnection(wConnection, request);
     
        try {
            WebSocketRouteChainBasic chain = new WebSocketRouteChainBasic(
                    connection, 
                    invoker, routeParameters, matches);
            chain.invokeNext();
        }
        catch(HaltException he) {
            logger.info("Processing halted", he);
        }
        catch(RedirectException re) {
            logger.info("Redirecting to " + re.getLocation(), re);
        }
        catch(Exception e) {
            logger.error("Processing error <" + httpRequest.uri() + ">", e);
        }
    }

}
