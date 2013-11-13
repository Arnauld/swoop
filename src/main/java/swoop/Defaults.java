package swoop;

import java.util.concurrent.ThreadFactory;

import swoop.route.RouteRegistry;
import swoop.route.RouteRegistryFactory;
import swoop.route.RouteRegistryBasic;
import swoop.server.SwoopServer;
import swoop.server.SwoopServerFactory;
import swoop.server.webbit.WebbitSwoopServerFactory;
import swoop.util.DefaultThreadFactory;
import swoop.util.Provider;

public class Defaults {
    private static RouteRegistryFactory routeMatcherFactory = new RouteRegistryBasic.Factory();
    private static SwoopServerFactory swoopServerFactory = new WebbitSwoopServerFactory();
    private static ThreadFactory threadFactory = new DefaultThreadFactory("Swoop");
    
    public static void setRouteMatcherFactory(RouteRegistryFactory defaultFactory) {
        Defaults.routeMatcherFactory = defaultFactory;
    }
    public static RouteRegistryFactory getRouteMatcherFactory() {
        return routeMatcherFactory;
    }
    public static RouteRegistry createRouteMatcher() {
        return getRouteMatcherFactory().create();
    }
    
    public static SwoopServerFactory getSwoopServerFactory() {
        return swoopServerFactory;
    }
    public static void setSwoopServerFactory(SwoopServerFactory swoopServerFactory) {
        Defaults.swoopServerFactory = swoopServerFactory;
    }
    public static SwoopServer createSwoopServer(RouteRegistry routeMatcher, Provider<ResponseProcessor> responseProcessorProvider) {
        return getSwoopServerFactory().create(routeMatcher, responseProcessorProvider);
    }
    
    public static ThreadFactory getThreadFactory() {
        return threadFactory;
    }
    public static void setThreadFactory(ThreadFactory threadFactory) {
        Defaults.threadFactory = threadFactory;
    }
    public static void executeAsynchronously(Runnable runnable) {
        getThreadFactory().newThread(runnable).start();
    }
    
}
