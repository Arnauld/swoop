package swoop.route;

import swoop.WebSocketConnection;
import swoop.WebSocketMessage;

public class WebSocketInvokers {
    
    public static WebSocketInvoker onOpen() {
        return new WebSocketInvoker() {
            @Override
            public void invoke(WebSocketConnection connection, WebSocketRoute target,
                    WebSocketRouteChain chain) {
                target.onOpen(connection, chain);
            }
        };
    }

    public static WebSocketInvoker onClose() {
        return new WebSocketInvoker() {
            @Override
            public void invoke(WebSocketConnection connection, WebSocketRoute target,
                    WebSocketRouteChain chain) {
                target.onClose(connection, chain);
            }
        };
    }

    public static WebSocketInvoker onMessage(final WebSocketMessage msg) {
        return new WebSocketInvoker() {
            @Override
            public void invoke(WebSocketConnection connection, WebSocketRoute target,
                    WebSocketRouteChain chain) {
                target.onMessage(connection, msg, chain);
            }
        };
    }

    public static WebSocketInvoker onPing(final WebSocketMessage msg) {
        return new WebSocketInvoker() {
            @Override
            public void invoke(WebSocketConnection connection, WebSocketRoute target,
                    WebSocketRouteChain chain) {
                target.onPing(connection, msg, chain);
            }
        };
    }

    public static WebSocketInvoker onPong(final WebSocketMessage msg) {
        return new WebSocketInvoker() {
            @Override
            public void invoke(WebSocketConnection connection, WebSocketRoute target,
                    WebSocketRouteChain chain) {
                target.onPong(connection, msg, chain);
            }
        };
    }
}
