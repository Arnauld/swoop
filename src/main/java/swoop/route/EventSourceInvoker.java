package swoop.route;

import swoop.EventSourceConnection;
import swoop.RouteChain;

public class EventSourceInvoker implements Invoker<RouteMatch<EventSourceRoute>> {
    public enum Code {
        Open,
        Close;
    }
    
    private final Code code;
    private final EventSourceConnection connection;
    
    public EventSourceInvoker(Code code, EventSourceConnection connection) {
        super();
        this.code = code;
        this.connection = connection;
    }

    @Override
    public void invoke(RouteMatch<EventSourceRoute> routeMatch, RouteChain chain) {
        switch(code) {
            case Open:
                routeMatch.getTarget().onOpen(connection, chain);
                break;
            case Close:
                routeMatch.getTarget().onClose(connection, chain);
                break;
        }
    }
    
}
