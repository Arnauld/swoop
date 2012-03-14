package swoop.server.webbit;

import static swoop.server.webbit.WebbitAdapters.adaptRequest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbitserver.EventSourceConnection;
import org.webbitserver.EventSourceHandler;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import swoop.path.Path;
import swoop.path.Verb;
import swoop.route.EventSourceInvoker;
import swoop.route.EventSourceRoute;
import swoop.route.HaltException;
import swoop.route.RedirectException;
import swoop.route.RouteChainBasic;
import swoop.route.RouteMatch;
import swoop.route.RouteParameters;
import swoop.route.RouteRegistry;
import swoop.util.ContextBasic;

public class WebbitSwoopEventSourceHandler implements EventSourceHandler, HttpHandler {

    private Logger logger = LoggerFactory.getLogger(WebbitSwoopEventSourceHandler.class);
    private RouteRegistry routeRegistry;

    public WebbitSwoopEventSourceHandler(RouteRegistry routeRegistry) {
        this.routeRegistry = routeRegistry;
    }

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl control)
            throws Exception {
        Path path = Webbits.getPath(httpRequest);
        logger.debug("Analysing path <{}>", path);

        // eventsource is initiated by a 'GET'...
        if (path.getVerb() == Verb.Get) {
            path = path.withVerb(Verb.EventSource);
            List<RouteMatch<EventSourceRoute>> matches = //
            routeRegistry.findEventSourceRoutes(path);
            if (!matches.isEmpty()) {
                logger.debug("Upgrading path <{}> to eventsource", path);
                control.upgradeToEventSourceConnection(this);
                return;
            }
            logger.debug("Path <{}> does not belongs to eventsource", path);
        }
        control.nextHandler();
    }

    @Override
    public void onOpen(EventSourceConnection connection) throws Exception {
        logger.debug("OnOpen <{}>", connection.httpRequest().uri());
        dispatch(connection, EventSourceInvoker.Code.Open, new RouteParameters());
    }

    @Override
    public void onClose(EventSourceConnection connection) throws Exception {
        logger.debug("onClose <{}>", connection.httpRequest().uri());
        dispatch(connection, EventSourceInvoker.Code.Close, new RouteParameters());
    }

    protected void dispatch(EventSourceConnection wConnection, EventSourceInvoker.Code code,
            RouteParameters routeParameters) {
        HttpRequest httpRequest = wConnection.httpRequest();
        Path path = Webbits.getPath(httpRequest).withVerb(Verb.EventSource);

        List<RouteMatch<EventSourceRoute>> matches = routeRegistry.findEventSourceRoutes(path);
        logger.debug("Dispatching EventSource call <{}> through a chain of #{} link(s) (filter and target)",
                wConnection.httpRequest().uri(), matches.size());

        WebbitRequestAdapter request = adaptRequest(httpRequest, routeParameters);
        WebbitEventSourceConnection connection = WebbitAdapters.adaptConnection(wConnection, request);
        ContextBasic context = new ContextBasic()//
                .register(RouteParameters.class, routeParameters);

        try {
            RouteChainBasic.create(new EventSourceInvoker(code, connection), matches, context).invokeNext();
        } catch (HaltException he) {
            logger.info("Processing halted", he);
        } catch (RedirectException re) {
            logger.info("Redirecting to " + re.getLocation(), re);
        } catch (Exception e) {
            logger.error("Processing error <" + httpRequest.uri() + ">", e);
        }
    }
}
