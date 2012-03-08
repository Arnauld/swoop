package swoop.route;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.WebSocketConnection;
import swoop.util.Multimap;

public class WebSocketRouteChainBasic implements WebSocketRouteChain {
private Logger logger = LoggerFactory.getLogger(WebSocketRouteChainBasic.class);
    
    private final WebSocketConnection connection;
    private final WebSocketInvoker invoker;
    private final RouteParameters routeParameters;
    private final List<WebSocketRouteMatch> websocketRoutes;
    private int index;
    
    public WebSocketRouteChainBasic(WebSocketConnection connection, WebSocketInvoker invoker, RouteParameters routeParameters, List<WebSocketRouteMatch> websocketRoutes) {
        super();
        this.connection = connection;
        this.invoker = invoker;
        this.routeParameters = routeParameters;
        this.websocketRoutes = websocketRoutes;
    }

    @Override
    public void invokeNext() {
        try {
            if (index == websocketRoutes.size()) {
                logger.warn("No more link in the chain. This usually happens when there is no matching target and only filters");
                return;
            }
    
            Multimap<String, String> previous = routeParameters.getUnderlying();
            try {
                WebSocketRouteMatch routeMatch = websocketRoutes.get(index++);
                routeParameters.setUnderlying(routeMatch.getRouteParameters());
                invoker.invoke(connection, routeMatch.getTarget(), this);
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
