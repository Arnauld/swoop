package swoop.route;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.testng.annotations.Test;

import swoop.util.New;

public class RoutesTest {
    
    @Test
    public void firstTarget_empty() {
        List<RouteMatch> routeMatches = New.arrayList();
        RouteMatch target = Routes.firstTarget(routeMatches);
        assertThat(target, nullValue());
    }

    @Test
    public void firstTarget_onlyFilters() {
        List<RouteMatch> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(newRouteMatchMock(true));

        RouteMatch target = Routes.firstTarget(routeMatches);
        assertThat(target, nullValue());
    }
    
    @Test
    public void firstTarget_filtersAndOneTarget() {
        RouteMatch expectedTarget = newRouteMatchMock(false);
        List<RouteMatch> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(expectedTarget);
        routeMatches.add(newRouteMatchMock(true));

        RouteMatch target = Routes.firstTarget(routeMatches);
        assertThat(target, sameInstance(expectedTarget));
    }
    
    @Test
    public void firstTarget_filtersAndMultipleTargets_returnFirstTarget() {
        RouteMatch expectedTarget = newRouteMatchMock(false);
        List<RouteMatch> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(expectedTarget);
        routeMatches.add(newRouteMatchMock(false));
        routeMatches.add(newRouteMatchMock(true));

        RouteMatch target = Routes.firstTarget(routeMatches);
        assertThat(target, sameInstance(expectedTarget));
    }
    
    @Test
    public void throwIfMultipleTargets_noTarget_dontThrow() {
        List<RouteMatch> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(newRouteMatchMock(true));

        Routes.throwIfMultipleTargets(routeMatches);
    }
    
    @Test
    public void throwIfMultipleTargets_oneTarget_dontThrow() {
        List<RouteMatch> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(newRouteMatchMock(false));
        routeMatches.add(newRouteMatchMock(true));
        
        Routes.throwIfMultipleTargets(routeMatches);
    }
    
    @Test(expectedExceptions={MultipleTargetException.class})
    public void throwIfMultipleTargets_twoTarget_doThrow() {
        List<RouteMatch> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(newRouteMatchMock(false));
        routeMatches.add(newRouteMatchMock(false));
        
        Routes.throwIfMultipleTargets(routeMatches);
    }
    
    @Test
    public void routesWithTargetAsLast_noTarget() {
        List<Route> routes = New.arrayList();
        List<RouteMatch> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(true, routes));
        routeMatches.add(newRouteMatchMock(true, routes));
        routeMatches.add(newRouteMatchMock(true, routes));
        
        List<Route> modified = Routes.routesWithTargetAsLast(routeMatches);
        for(int i=0;i<routes.size();i++)
            assertThat(modified.get(i), sameInstance(routes.get(i)));
    }
    
    @Test
    public void routesWithTargetAsLast_oneTarget_case1() {
        List<Route> routes = New.arrayList();
        List<RouteMatch> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(false, routes));
        routeMatches.add(newRouteMatchMock(true, routes));
        routeMatches.add(newRouteMatchMock(true, routes));
        
        List<Route> modified = Routes.routesWithTargetAsLast(routeMatches);
        assertThat(modified.get(0), sameInstance(routes.get(1)));
        assertThat(modified.get(1), sameInstance(routes.get(2)));
        assertThat(modified.get(2), sameInstance(routes.get(0)));
    }
    
    @Test
    public void routesWithTargetAsLast_oneTarget_case2() {
        List<Route> routes = New.arrayList();
        List<RouteMatch> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(true, routes));
        routeMatches.add(newRouteMatchMock(false, routes));
        routeMatches.add(newRouteMatchMock(true, routes));
        
        List<Route> modified = Routes.routesWithTargetAsLast(routeMatches);
        assertThat(modified.get(0), sameInstance(routes.get(0)));
        assertThat(modified.get(1), sameInstance(routes.get(2)));
        assertThat(modified.get(2), sameInstance(routes.get(1)));
    }
    
    @Test
    public void routesWithTargetAsLast_oneTarget_case3() {
        List<Route> routes = New.arrayList();
        List<RouteMatch> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(true, routes));
        routeMatches.add(newRouteMatchMock(true, routes));
        routeMatches.add(newRouteMatchMock(false, routes));
        
        List<Route> modified = Routes.routesWithTargetAsLast(routeMatches);
        assertThat(modified.get(0), sameInstance(routes.get(0)));
        assertThat(modified.get(1), sameInstance(routes.get(1)));
        assertThat(modified.get(2), sameInstance(routes.get(2)));
    }
    
    @Test
    public void routesWithTargetAsLast_twoTargets_keepFirstTargetOnly() {
        List<Route> routes = New.arrayList();
        List<RouteMatch> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(false, routes));
        routeMatches.add(newRouteMatchMock(false, routes));
        routeMatches.add(newRouteMatchMock(true, routes));
        routeMatches.add(newRouteMatchMock(true, routes));
        
        List<Route> modified = Routes.routesWithTargetAsLast(routeMatches);
        assertThat(modified.get(0), sameInstance(routes.get(2)));
        assertThat(modified.get(1), sameInstance(routes.get(3)));
        assertThat(modified.get(2), sameInstance(routes.get(0)));
        assertThat(modified.size(), equalTo(3));
    }

    
    private static RouteMatch newRouteMatchMock(boolean isFilter) {
        return newRouteMatchMock(isFilter, null);
    }
    
    private static RouteMatch newRouteMatchMock(boolean isFilter, List<Route> routes) {
        Route route = mock(Route.class);
        if(routes!=null)
            routes.add(route);
        when(route.isFilter()).thenReturn(isFilter);

        RouteMatch routeMatch = mock(RouteMatch.class);
        when(routeMatch.getTarget()).thenReturn(route);
        return routeMatch;
    }
}
