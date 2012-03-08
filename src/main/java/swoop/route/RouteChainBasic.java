package swoop.route;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.Request;
import swoop.Response;
import swoop.RouteChain;
import swoop.util.Multimap;

public class RouteChainBasic implements RouteChain {
    
    private Logger logger = LoggerFactory.getLogger(RouteChainBasic.class);
    
    private final Request request;
    private final Response response;
    private final RouteParameters routeParameters;
    private final List<RouteMatch> routes;
    private int index;
    
    public RouteChainBasic(Request request, Response response, RouteParameters routeParameters, List<RouteMatch> routes) {
        super();
        this.request = request;
        this.response = response;
        this.routeParameters = routeParameters;
        this.routes = routes;
    }

    @Override
    public void invokeNext() {
        try {
            if (index == routes.size()) {
                logger.warn("No more link in the chain. This usually happens when there is no matching target and only filters");
                return;
            }
    
            Multimap<String, String> previous = routeParameters.getUnderlying();
            try {
                RouteMatch routeMatch = routes.get(index++);
                routeParameters.setUnderlying(routeMatch.getRouteParameters());
                routeMatch.getTarget().handle(request, response, this);
            } finally {
                index--;
                routeParameters.setUnderlying(previous);
            }
        }
        catch(HaltException he) {
            throw he;
        }
        catch(RuntimeException re) {
            logger.error("Oops", re);
            throw re;
        }
    }
}
