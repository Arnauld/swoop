package swoop.server.webbit;

import swoop.route.RouteRegistry;
import swoop.server.SwoopServer;
import swoop.server.SwoopServerFactory;

public class WebbitSwoopServerFactory implements SwoopServerFactory {

    @Override
    public SwoopServer create(RouteRegistry routeMatcher) {
        return new WebbitSwoopServer(routeMatcher);
    }
}
