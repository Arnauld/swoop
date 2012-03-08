package swoop.server.webbit;

import java.net.HttpCookie;
import java.util.List;

import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebSocketConnection;

import swoop.Cookie;
import swoop.WebSocketMessage;
import swoop.route.RouteParameters;
import swoop.route.WebSocketMessageBasic;
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

    public static WebbitWebSocketConnection adaptConnection(WebSocketConnection wConnection,
            WebbitRequestAdapter request) {
        return new WebbitWebSocketConnection(wConnection, request);
    }

    public static WebSocketMessage adaptMessage(String text, RouteParameters routeParameters) {
        WebSocketMessageBasic message = new WebSocketMessageBasic(routeParameters);
        message.text(text);
        return message;
    }
    
    public static WebSocketMessage adaptMessage(byte[] binary, RouteParameters routeParameters) {
        WebSocketMessageBasic message = new WebSocketMessageBasic(routeParameters);
        message.binary(binary);
        return message;
    }
}
