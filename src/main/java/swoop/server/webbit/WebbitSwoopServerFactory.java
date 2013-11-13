package swoop.server.webbit;

import swoop.ResponseProcessor;
import swoop.route.RouteRegistry;
import swoop.server.SwoopServer;
import swoop.server.SwoopServerFactory;
import swoop.util.Provider;

public class WebbitSwoopServerFactory implements SwoopServerFactory {

    @Override
    public SwoopServer create(RouteRegistry routeMatcher, Provider<ResponseProcessor> responseProcessorProvider) {
        return new WebbitSwoopServer(routeMatcher, responseProcessorProvider);
    }
}
