package swoop.route;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import swoop.Request;
import swoop.Response;
import swoop.RouteChain;
import swoop.util.Multimap;
import swoop.util.New;

public class RouteChainBasicTest {

    private Request request;
    private Response response;
    private RouteParameters routeParameters;
    private ArrayList<RouteMatch> routeMatches;
    //
    private RouteChainBasic routeChain;
    //
    private Multimap<String, String> multimap;

    @BeforeMethod
    public void setUp() {
        request = mock(Request.class);
        response = mock(Response.class);
        routeParameters = mock(RouteParameters.class);
        routeMatches = New.arrayList();
        routeChain = new RouteChainBasic(request, response, routeParameters, routeMatches);
        //
        multimap = new Multimap<String, String>();
    }

    @Test
    public void emptyRoutes_mustDoNothing() {
        routeChain.invokeNext();
    }

    @Test
    public void oneRoute() {
        Route route = mock(Route.class);
        RouteMatch routeMatch = mock(RouteMatch.class);
        when(routeMatch.getRouteParameters()).thenReturn(multimap);
        when(routeMatch.getTarget()).thenReturn(route);

        routeMatches.add(routeMatch);
        routeChain.invokeNext();

        verify(routeParameters).setUnderlying(multimap);
        verify(route).handle(request, response, routeChain);
    }

    @Test
    public void oneRoute_simulateChaining() {
        Route route = mock(Route.class);
        RouteMatch routeMatch = mock(RouteMatch.class);
        when(routeMatch.getRouteParameters()).thenReturn(multimap);
        when(routeMatch.getTarget()).thenReturn(route);

        doAnswer(invokeNextOnChain()).when(route).handle(request, response, routeChain);

        routeMatches.add(routeMatch);
        routeChain.invokeNext();

        verify(routeParameters).setUnderlying(multimap);
        verify(route).handle(request, response, routeChain);
    }

    @Test
    public void twoRoutes_simulateChaining() {
        Route route1 = mock(Route.class);
        Route route2 = mock(Route.class);

        routeMatches.add(mockRouteMatchInvokingNextOnChain(route1));
        routeMatches.add(mockRouteMatchInvokingNextOnChain(route2));
        routeChain.invokeNext();

        verify(routeParameters, times(2)).setUnderlying(multimap);
        verify(route1).handle(request, response, routeChain);
        verify(route2).handle(request, response, routeChain);
    }

    @Test
    public void multipleRoutes_simulateChaining() {
        int COUNT = 10;
        List<Route> routes = New.arrayList();
        for (int i = 0; i < COUNT; i++) {
            Route route = mock(Route.class);
            routes.add(route);
            routeMatches.add(mockRouteMatchInvokingNextOnChain(route));
        }

        // trigger the chain
        routeChain.invokeNext();

        verify(routeParameters, times(COUNT)).setUnderlying(multimap);
        for (int i = 0; i < COUNT; i++) {
            verify(routes.get(i)).handle(request, response, routeChain);
        }
    }

    private Multimap<String, String> underlyingLastDefined;

    @SuppressWarnings("unchecked")
    @Test
    public void twoRoutes_routeParametersIsUpdatedOnEachLink() {
        doAnswer(defineAsUnderlyingLastDefined()).when(routeParameters).setUnderlying(Mockito.any(Multimap.class));
        when(routeParameters.getUnderlying()).then(returnUnderlyingLastDefined());
        Multimap<String, String> multimap1 = New.multiMap();
        Multimap<String, String> multimap2 = New.multiMap();

        Route route1 = mock(Route.class);
        RouteMatch routeMatch1 = createRouteMatchMock(multimap1, route1);
        doAnswer(checkRouteParametersAroundInvokeNext(multimap1)).when(route1).handle(request, response, routeChain);

        Route route2 = mock(Route.class);
        RouteMatch routeMatch2 = createRouteMatchMock(multimap2, route2);
        doAnswer(checkRouteParametersAroundInvokeNext(multimap2)).when(route2).handle(request, response, routeChain);

        routeMatches.add(routeMatch1);
        routeMatches.add(routeMatch2);
        routeChain.invokeNext();

        verify(route1).handle(request, response, routeChain);
        verify(route2).handle(request, response, routeChain);
    }

    protected Answer<Object> defineAsUnderlyingLastDefined() {
        return new Answer<Object>() {
            @SuppressWarnings("unchecked")
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                underlyingLastDefined = (Multimap<String, String>) invocation.getArguments()[0];
                return null;
            }
        };
    }

    protected Answer<Multimap<String, String>> returnUnderlyingLastDefined() {
        return new Answer<Multimap<String, String>>() {
            @Override
            public Multimap<String, String> answer(InvocationOnMock invocation) throws Throwable {
                return underlyingLastDefined;
            }
        };
    }

    protected static RouteMatch createRouteMatchMock(Multimap<String, String> multimap, Route route) {
        RouteMatch routeMatch = mock(RouteMatch.class);
        when(routeMatch.getRouteParameters()).thenReturn(multimap);
        when(routeMatch.getTarget()).thenReturn(route);
        return routeMatch;
    }

    protected RouteMatch mockRouteMatchInvokingNextOnChain(Route route) {
        RouteMatch routeMatch = mock(RouteMatch.class);
        when(routeMatch.getRouteParameters()).thenReturn(multimap);
        when(routeMatch.getTarget()).thenReturn(route);

        doAnswer(invokeNextOnChain()).when(route).handle(request, response, routeChain);
        return routeMatch;
    }

    protected Answer<Object> checkRouteParametersAroundInvokeNext(final Multimap<String, String> multimap) {
        return new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                RouteChain chain = (RouteChain) args[2];
                assertThat("Map not updated @before", multimap, sameInstance(underlyingLastDefined));
                chain.invokeNext();
                assertThat("Map not updated @after", multimap, sameInstance(underlyingLastDefined));
                return null;
            }
        };
    }

    protected static Answer<Object> invokeNextOnChain() {
        return new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                RouteChain chain = (RouteChain) args[2];
                chain.invokeNext();
                return null;
            }
        };
    }

}
