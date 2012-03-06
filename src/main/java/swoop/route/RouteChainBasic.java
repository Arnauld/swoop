package swoop.route;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.Request;
import swoop.Response;

public class RouteChainBasic implements RouteChain {
    
    private Logger logger = LoggerFactory.getLogger(RouteChainBasic.class);
    
    private final Request request;
    private final Response response;
    private final List<Route> routes;
    private int index;
    
    public RouteChainBasic(Request request, Response response, List<Route> routes) {
        super();
        this.request = request;
        this.response = response;
        this.routes = routes;
    }

    @Override
    public void invokeNext() {
        try {
            if (index == routes.size()) {
                logger.warn("No more link in the chain. This usually happens when there is no matching target and only filters");
                return;
            }
    
            try {
                Route route = routes.get(index++);
                route.handle(request, response, this);
            } finally {
                index--;
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
