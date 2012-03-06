package swoop.server;

import swoop.route.RouteRegistry;
import swoop.server.webbit.WebbitSwoopServerFactory;


public interface SwoopServerFactory {

    SwoopServer create(RouteRegistry routeMatcher);
    
    public static class Default {
        private static SwoopServerFactory defaultFactory = new WebbitSwoopServerFactory();
        public static void setDefaultFactory(SwoopServerFactory defaultFactory) {
            Default.defaultFactory = defaultFactory;
        }
        public static SwoopServerFactory getDefaultFactory() {
            return defaultFactory;
        }
        public static SwoopServer create(RouteRegistry routeMatcher) {
            return getDefaultFactory().create(routeMatcher);
        }
    }
}
