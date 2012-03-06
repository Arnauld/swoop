package swoop.route;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class RouteRegistryBasicTest {

    private RouteRegistryBasic registry;
    private Route[] routes;

    @BeforeMethod
    public void setUp() {
        registry = new RouteRegistryBasic();
    }
    
    protected void prepareRoutes(int amount) {
        routes = new Route[amount];
        for(int i=0;i<amount;i++) {
            Route mock = mock(Route.class);
            defineToString(mock, "route" + i);
            routes[i] = mock;
        }
    }

    @Test
    public void defaults() {
        assertThat(new RouteRegistryBasic().getPathMatcherCompiler(),//
                instanceOf(PathMatcherSinatraCompiler.class));
    }
    
    @Test(expectedExceptions={MultipleTargetException.class})
    public void findRoutes_failIfMultipleTargetsMatch() {
        prepareRoutes(4);
        defineAndAddRoute(0, true, "any '/'");
        defineAndAddRoute(1, true, "get '/'");
        defineAndAddRoute(2, false, "get '/'");
        defineAndAddRoute(3, false, "get '/*'");
        
        List<RouteMatch> found = registry.findRoutes(p(Verb.Get, "/"));
        
        // TODO this make the test works: refactor the test and create a new one.
        Routes.throwIfMultipleTargets(found);
    }

    @Test
    public void findRoutes_multipleFiltersOneTarget() {
        prepareRoutes(5);
        defineAndAddRoute(0, true, "any '/'");
        defineAndAddRoute(1, true, "get '/'");
        defineAndAddRoute(2, true, "get '/foo'");
        defineAndAddRoute(3, false, "get '/'");
        defineAndAddRoute(4, true, "get '/*'");
        
        List<RouteMatch> found = registry.findRoutes(p(Verb.Get, "/"));
        assertThat(found.size(), equalTo(4));
        Iterator<RouteMatch> it = found.iterator();
        assertThat(it.next().getTarget(), sameInstance(routes[0]));
        assertThat(it.next().getTarget(), sameInstance(routes[1]));
        assertThat(it.next().getTarget(), sameInstance(routes[3]));
        assertThat(it.next().getTarget(), sameInstance(routes[4]));
    }
    

    @Test
    public void findRoutes_multipleFiltersNoTarget() {
        prepareRoutes(5);
        defineAndAddRoute(0, true, "any '/'");
        defineAndAddRoute(1, true, "get '/'");
        defineAndAddRoute(2, true, "get '/foo'");
        defineAndAddRoute(3, false, "get '/'");
        defineAndAddRoute(4, true, "get '/*'");
        
        List<RouteMatch> found = registry.findRoutes(p(Verb.Post, "/"));
        assertThat(found.size(), equalTo(1));
        assertThat(found.get(0).getTarget(), sameInstance(routes[0]));
    }
    
    private static Path p(Verb verb, String string) {
        return new Path(verb, string);
    }

    private void defineAndAddRoute(int i, boolean isFilter, String path) {
        Route target = routes[i];
        defineIsFilter(target, isFilter);
        registry.addRoute(path, target);
    }

    private static <T> T defineToString(T mock, String toString) {
        when(mock.toString()).thenReturn(toString);
        return mock;
    }
    
    private static void defineIsFilter(Route mock, boolean isFilter) {
        when(mock.isFilter()).thenReturn(isFilter);
    }
}
