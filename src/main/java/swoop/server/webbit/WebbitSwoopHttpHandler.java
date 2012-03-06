package swoop.server.webbit;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import swoop.route.RouteMatcher;

public class WebbitSwoopHttpHandler implements HttpHandler {
    
    private RouteMatcher routeMatcher;

    public WebbitSwoopHttpHandler(RouteMatcher routeMatcher) {
    }

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        sd
    }

}
