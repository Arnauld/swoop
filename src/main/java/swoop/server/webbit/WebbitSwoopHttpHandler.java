package swoop.server.webbit;

import static swoop.server.webbit.WebbitAdapters.adaptRequest;
import static swoop.server.webbit.WebbitAdapters.adaptResponse;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import swoop.StatusCode;
import swoop.path.Path;
import swoop.route.HaltException;
import swoop.route.Invoker;
import swoop.route.RedirectException;
import swoop.route.Route;
import swoop.route.RouteChainBasic;
import swoop.route.RouteInvoker;
import swoop.route.RouteMatch;
import swoop.route.RouteParameters;
import swoop.route.RouteRegistry;
import swoop.util.ContextBasic;

public class WebbitSwoopHttpHandler implements HttpHandler {

    private Logger logger = LoggerFactory.getLogger(WebbitSwoopHttpHandler.class);
    private RouteRegistry routeRegistry;

    public WebbitSwoopHttpHandler(RouteRegistry routeRegistry) {
        this.routeRegistry = routeRegistry;
    }

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl control)
            throws Exception {
        Path path = Webbits.getPath(httpRequest);
        logger.debug("Analysing path <{}>", path);
        List<RouteMatch<Route>> matches = routeRegistry.findRoutes(path);
        if (matches.isEmpty()) {
            logger.debug("Path <{}> does not belongs to http handler", path);
            control.nextHandler();
            return;
        }

        RouteParameters routeParameters = new RouteParameters();
        WebbitRequestAdapter request = adaptRequest(httpRequest, routeParameters);
        WebbitResponseAdapter response = adaptResponse(httpResponse);
        ContextBasic context = new ContextBasic()//
                .register(RouteParameters.class, routeParameters);

        try {
            Invoker<RouteMatch<Route>> invoker = new RouteInvoker(request, response);
            RouteChainBasic.create(invoker, matches, context).invokeNext();
        } catch (HaltException he) {
            logger.info("Processing halted", he);
            response.body(he.getBody());
            response.status(he.getStatusCode());
        } catch (RedirectException re) {
            logger.info("Redirecting to " + re.getLocation(), re);
            response.redirect(re.getLocation());
        } catch (Exception e) {
            logger.error("Processing error <" + httpRequest.uri() + ">", e);
            response.body(ExceptionUtils.getStackTrace(e));
            response.status(StatusCode.SERVICE_UNAVAILABLE);
        } finally {
            response.end();
        }
    }

}
