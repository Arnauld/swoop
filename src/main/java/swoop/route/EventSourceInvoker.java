package swoop.route;

import swoop.EventSourceConnection;
import swoop.RouteChain;
import swoop.path.Verb;

public class EventSourceInvoker implements Invoker<RouteMatch<EventSourceRoute>> {

    private final Verb verb;
    private final EventSourceConnection connection;

    public EventSourceInvoker(Verb verb, EventSourceConnection connection) {
        switch (verb) {
            case EventSourceOpen:
            case EventSourceClose:
                break;
            default:
                throw new IllegalArgumentException(verb + " is not an eventsource one");
        }
        this.verb = verb;
        this.connection = connection;
    }

    @Override
    public void invoke(RouteMatch<EventSourceRoute> routeMatch, RouteChain chain) {
        switch (verb) {
            case EventSourceOpen:
                routeMatch.getTarget().onOpen(connection, chain);
                break;
            case EventSourceClose:
                routeMatch.getTarget().onClose(connection, chain);
                break;
        }
    }

}
