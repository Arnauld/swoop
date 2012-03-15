package swoop.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.Request;
import swoop.Response;
import swoop.RouteChain;

public class RouteInvoker implements Invoker<RouteMatch<Route>> {
    
    private Logger logger = LoggerFactory.getLogger(RouteInvoker.class);
    
    private final Request request;
    private final Response response;
    
    public RouteInvoker(Request request, Response response) {
        super();
        this.request = request;
        this.response = response;
    }

    /* (non-Javadoc)
     * @see swoop.route.shared.Invoker#invoke(java.lang.Object, swoop.RouteChain)
     */
    @Override
    public void invoke(RouteMatch<Route> routeMatch, RouteChain chain) {
        logger.debug("Invoking route {} (from {})", routeMatch.getTarget(), request.uri());
        try {
            routeMatch.getTarget().handle(request, response, chain);
        }
        catch(RuntimeException re) {
            logger.error("Failed to invoking route " + routeMatch.getTarget() + " (from " + request.uri() + ")", re);
            throw re;
        }
    }
    
}
