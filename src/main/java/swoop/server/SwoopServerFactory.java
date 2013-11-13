package swoop.server;

import swoop.ResponseProcessor;
import swoop.route.RouteRegistry;
import swoop.server.webbit.WebbitSwoopServerFactory;
import swoop.util.Provider;


public interface SwoopServerFactory {

    SwoopServer create(RouteRegistry routeMatcher, Provider<ResponseProcessor> responseProcessorProvider);
    
    public static class Default {
        private static SwoopServerFactory defaultFactory = new WebbitSwoopServerFactory();
        public static void setDefaultFactory(SwoopServerFactory defaultFactory) {
            Default.defaultFactory = defaultFactory;
        }
        public static SwoopServerFactory getDefaultFactory() {
            return defaultFactory;
        }
        public static SwoopServer create(RouteRegistry routeMatcher, Provider<ResponseProcessor> responseProcessorProvider) {
            return getDefaultFactory().create(routeMatcher, responseProcessorProvider);
        }
    }
}
