package swoop.route;

import swoop.Request;
import swoop.Response;
import swoop.RouteChain;

public class RouteInvoker implements Invoker<RouteMatch<Route>> {
    
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
        routeMatch.getTarget().handle(request, response, chain);
    }
    
}
