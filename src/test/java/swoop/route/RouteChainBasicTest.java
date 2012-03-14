package swoop.route;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import swoop.RouteChain;
import swoop.util.Context;
import swoop.util.Multimap;
import swoop.util.New;

public class RouteChainBasicTest {

    private Invoker<RouteMatch<FilterAwareImpl>> invoker;
    private Context context;
    private RouteParameters routeParameters;
    private ArrayList<RouteMatch<FilterAwareImpl>> routeMatches;
    //
    private RouteChainBasic<FilterAwareImpl> routeChain;
    //
    private Multimap<String, String> multimap;

    @SuppressWarnings("unchecked")
    @BeforeMethod
    public void setUp() {
        invoker = mock(Invoker.class);
        routeParameters = mock(RouteParameters.class);
        context = mock(Context.class);
        routeMatches = New.arrayList();
        routeChain = RouteChainBasic.create(invoker, routeMatches, context);
        //
        multimap = new Multimap<String, String>();
        //

    }

    @Test
    public void emptyRoutes_mustDoNothing() {
        routeChain.invokeNext();
    }

    @Test
    public void oneRoute() {
        @SuppressWarnings("unchecked")
        RouteMatch<FilterAwareImpl> routeMatch = mock(RouteMatch.class);
        when(routeMatch.getRouteParameters()).thenReturn(multimap);

        routeMatches.add(routeMatch);
        routeChain.invokeNext();

        verify(routeParameters).getUnderlying();
        verify(routeParameters).setUnderlying(multimap);
        verify(invoker).invoke(routeMatch, routeChain);
    }

    @Test
    public void oneRoute_simulateChaining() {
        @SuppressWarnings("unchecked")
        RouteMatch<FilterAwareImpl> routeMatch = mock(RouteMatch.class);
        when(routeMatch.getRouteParameters()).thenReturn(multimap);

        doAnswer(invokeNextOnChain()).when(invoker).invoke(routeMatch, routeChain);

        routeMatches.add(routeMatch);
        routeChain.invokeNext();

        verify(routeParameters).getUnderlying();
        verify(routeParameters).setUnderlying(multimap);
        verify(invoker).invoke(routeMatch, routeChain);
    }

    @Test
    public void twoRoutes_simulateChaining() {
        @SuppressWarnings("unchecked")
        RouteMatch<FilterAwareImpl> routeMatch1 = mock(RouteMatch.class);
        @SuppressWarnings("unchecked")
        RouteMatch<FilterAwareImpl> routeMatch2 = mock(RouteMatch.class);
        routeMatches.add(routeMatch1);
        routeMatches.add(routeMatch2);
        routeChain.invokeNext();

        doAnswer(invokeNextOnChain()).when(invoker).invoke(routeMatch1, routeChain);

        verify(routeParameters, times(2)).setUnderlying(multimap);
        verify(invoker).invoke(routeMatch1, routeChain);
        verify(invoker).invoke(routeMatch2, routeChain);
    }

    @Test
    public void multipleRoutes_simulateChaining() {
        int COUNT = 10;
        for (int i = 0; i < COUNT; i++) {
            routeMatches.add(mockRouteMatchInvokingNextOnChain());
        }

        // trigger the chain
        routeChain.invokeNext();

        verify(routeParameters, times(COUNT)).setUnderlying(multimap);
        for (int i = 0; i < COUNT; i++) {
            verify(invoker).invoke(routeMatches.get(i), routeChain);
        }
    }

    private Multimap<String, String> underlyingLastDefined;

    @Test
    public void twoRoutes_routeParametersIsUpdatedOnEachLink() {
        // doAnswer(defineAsUnderlyingLastDefined()).when(routeParameters).setUnderlying(Mockito.any(Multimap.class));
        // when(routeParameters.getUnderlying()).then(returnUnderlyingLastDefined());
        // Multimap<String, String> multimap1 = New.multiMap();
        // Multimap<String, String> multimap2 = New.multiMap();
        //
        // Route route1 = mock(Route.class);
        // RouteMatch routeMatch1 = createRouteMatchMock(multimap1, route1);
        // doAnswer(checkRouteParametersAroundInvokeNext(multimap1)).when(route1).handle(request, response, routeChain);
        //
        // Route route2 = mock(Route.class);
        // RouteMatch routeMatch2 = createRouteMatchMock(multimap2, route2);
        // doAnswer(checkRouteParametersAroundInvokeNext(multimap2)).when(route2).handle(request, response, routeChain);
        //
        // routeMatches.add(routeMatch1);
        // routeMatches.add(routeMatch2);
        // routeChain.invokeNext();
        //
        // verify(route1).handle(request, response, routeChain);
        // verify(route2).handle(request, response, routeChain);
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

    protected RouteMatch<FilterAwareImpl> mockRouteMatchInvokingNextOnChain() {
        @SuppressWarnings("unchecked")
        RouteMatch<FilterAwareImpl> routeMatch = mock(RouteMatch.class);
        when(routeMatch.getRouteParameters()).thenReturn(multimap);
        doAnswer(invokeNextOnChain()).when(invoker).invoke(routeMatch, routeChain);
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

    private static class FilterAwareImpl implements FilterAware {
        @Override
        public boolean isFilter() {
            return false;
        }

    }
}
