package swoop.route;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;
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
    public void oneRoute_withoutRouteParametersInContext() {
        @SuppressWarnings("unchecked")
        RouteMatch<FilterAwareImpl> routeMatch = mock(RouteMatch.class);
        when(routeMatch.getRouteParameters()).thenReturn(multimap);

        routeMatches.add(routeMatch);
        routeChain.invokeNext();

        Mockito.verifyZeroInteractions(routeParameters);
        verify(invoker).invoke(routeMatch, routeChain);
    }
    
    @Test
    public void oneRoute_withRouteParametersInContext() {
        when(context.get(RouteParameters.class)).thenReturn(routeParameters);
        
        @SuppressWarnings("unchecked")
        RouteMatch<FilterAwareImpl> routeMatch = mock(RouteMatch.class);
        when(routeMatch.getRouteParameters()).thenReturn(multimap);

        routeMatches.add(routeMatch);
        routeChain.invokeNext();

        verify(routeParameters).getUnderlying();
        verify(routeParameters).setUnderlying(multimap);
        verify(invoker).invoke(routeMatch, routeChain);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void oneRoute_simulateChaining() {
        invoker = new RouteMatchCollectorInvoker();
        routeChain = RouteChainBasic.create(invoker, routeMatches, context);
        when(context.get(RouteParameters.class)).thenReturn(routeParameters);

        RouteMatch<FilterAwareImpl> routeMatch = mock(RouteMatch.class);
        when(routeMatch.getRouteParameters()).thenReturn(multimap);

        routeMatches.add(routeMatch);
        routeChain.invokeNext();

        verify(routeParameters).getUnderlying();
        verify(routeParameters).setUnderlying(multimap);
        assertThat(getRouteMatchCollected(), contains(routeMatch));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void twoRoutes_simulateChaining() {
        invoker = new RouteMatchCollectorInvoker();
        routeChain = RouteChainBasic.create(invoker, routeMatches, context);
        
        when(context.get(RouteParameters.class)).thenReturn(routeParameters);

        Multimap<String,String> multimap1 = New.multiMap();
        RouteMatch<FilterAwareImpl> routeMatch1 = mock(RouteMatch.class);
        when(routeMatch1.toString()).thenReturn("routeMatch1");
        assertThat(routeMatch1.equals(routeMatch1), is(true));
        when(routeMatch1.getRouteParameters()).thenReturn(multimap1);
        
        Multimap<String,String> multimap2 = New.multiMap();
        RouteMatch<FilterAwareImpl> routeMatch2 = mock(RouteMatch.class);
        when(routeMatch2.toString()).thenReturn("routeMatch2");
        when(routeMatch2.getRouteParameters()).thenReturn(multimap2);

        routeMatches.add(routeMatch1);
        routeMatches.add(routeMatch2);
        routeChain.invokeNext();

        assertThat(getRouteMatchCollected(), contains(routeMatch1, routeMatch2));
        verify(routeParameters, times(2)).setUnderlying(null);
        verify(routeParameters, times(1)).setUnderlying(multimap1);
        verify(routeParameters, times(1)).setUnderlying(multimap2);
    }

    protected List<RouteMatch<FilterAwareImpl>> getRouteMatchCollected() {
        assertThat(invoker, instanceOf(RouteMatchCollectorInvoker.class));
        return ((RouteMatchCollectorInvoker)invoker).getCollected();
    }

    @Test
    public void multipleRoutes_simulateChaining_withoutRouteParametersInContext() {
        invoker = new RouteMatchCollectorInvoker();
        routeChain = RouteChainBasic.create(invoker, routeMatches, context);
        int COUNT = 10;
        for (int i = 0; i < COUNT; i++) {
            @SuppressWarnings("unchecked")
            RouteMatch<FilterAwareImpl> routeMatch = mock(RouteMatch.class);
            when(routeMatch.toString()).thenReturn("routeMatch" + i);
            routeMatches.add(routeMatch);
        }

        // trigger the chain
        routeChain.invokeNext();

        assertThat(getRouteMatchCollected(), equalTo((List<RouteMatch<FilterAwareImpl>>)routeMatches));
    }

    private static class FilterAwareImpl implements FilterAware {
        @Override
        public boolean isFilter() {
            return false;
        }

    }
    
    class RouteMatchCollectorInvoker implements Invoker<RouteMatch<FilterAwareImpl>> {
        private List<RouteMatch<FilterAwareImpl>> matchesInvoked = New.arrayList();
        
        @Override
        public void invoke(RouteMatch<FilterAwareImpl> value, RouteChain chain) {
            matchesInvoked.add(value);
            chain.invokeNext();
        }

        public List<RouteMatch<FilterAwareImpl>> getCollected() {
            return matchesInvoked;
        }
    }
}
