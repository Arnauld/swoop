package swoop.server.webbit;

import java.net.HttpCookie;
import java.util.List;

import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import swoop.Cookie;
import swoop.route.RouteParameters;
import swoop.util.New;

public class WebbitAdapters {

    public static WebbitRequestAdapter adaptRequest(HttpRequest httpRequest, RouteParameters routeParameters) {
        return new WebbitRequestAdapter(httpRequest, routeParameters);
    }
    
    public static List<Cookie> adaptCookies(List<HttpCookie> httpCookies) {
        List<Cookie> cookies = New.arrayList(httpCookies.size());
        for(HttpCookie httpCookie : httpCookies)
            cookies.add(adaptCookie(httpCookie));
        return cookies;
    }
    
    public static WebbitCookieAdapter adaptCookie(HttpCookie httpCookie) {
        return new WebbitCookieAdapter(httpCookie);
    }

    public static WebbitResponseAdapter adaptResponse(HttpResponse httpResponse) {
        return new WebbitResponseAdapter(httpResponse);
    }
}
