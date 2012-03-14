package swoop.route;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import swoop.path.Path;
import swoop.path.PathMatcher;
import swoop.path.Verb;
import swoop.util.New;

public class RoutesTest {
    
    private Path path;

    @BeforeMethod
    public void setUp () {
        path = new Path(Verb.Get, "Bob");
    }
    
    @Test
    public void throwIfMultipleTargets_noTarget_dontThrow() {
        List<RouteMatch<?>> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(newRouteMatchMock(true));

        Routes.throwIfMultipleTargets(path, routeMatches);
    }
    
    @Test
    public void throwIfMultipleTargets_oneTarget_dontThrow() {
        List<RouteMatch<?>> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(newRouteMatchMock(false));
        routeMatches.add(newRouteMatchMock(true));
        
        Routes.throwIfMultipleTargets(path, routeMatches);
    }
    
    @Test(expectedExceptions={MultipleTargetException.class})
    public void throwIfMultipleTargets_twoTarget_doThrow() {
        List<RouteMatch<?>> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(newRouteMatchMock(false));
        routeMatches.add(newRouteMatchMock(false));
        
        Routes.throwIfMultipleTargets(path, routeMatches);
    }
    
    @Test
    public void routesWithTargetAsLast_noTarget() {
        List<RouteMatch<FilterAwareImpl>> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(newRouteMatchMock(true));
        
        List<RouteMatch<FilterAwareImpl>> modified = Routes.reorderRoutes(routeMatches);
        for(int i=0;i<routeMatches.size();i++)
            assertThat(modified.get(i), sameInstance(routeMatches.get(i)));
    }
    
    @Test
    public void routesWithTargetAsLast_oneTarget_case1() {
        List<RouteMatch<FilterAwareImpl>> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(false));
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(newRouteMatchMock(true));
        
        List<RouteMatch<FilterAwareImpl>> modified = Routes.reorderRoutes(routeMatches);
        assertThat(modified.get(0), sameInstance(routeMatches.get(1)));
        assertThat(modified.get(1), sameInstance(routeMatches.get(2)));
        assertThat(modified.get(2), sameInstance(routeMatches.get(0)));
    }
    
    @Test
    public void routesWithTargetAsLast_oneTarget_case2() {
        List<RouteMatch<FilterAwareImpl>> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(newRouteMatchMock(false));
        routeMatches.add(newRouteMatchMock(true));
        
        List<RouteMatch<FilterAwareImpl>> modified = Routes.reorderRoutes(routeMatches);
        assertThat(modified.get(0), sameInstance(routeMatches.get(0)));
        assertThat(modified.get(1), sameInstance(routeMatches.get(2)));
        assertThat(modified.get(2), sameInstance(routeMatches.get(1)));
    }
    
    @Test
    public void routesWithTargetAsLast_oneTarget_case3() {
        List<RouteMatch<FilterAwareImpl>> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(newRouteMatchMock(false));
        
        List<RouteMatch<FilterAwareImpl>> modified = Routes.reorderRoutes(routeMatches);
        assertThat(modified.get(0), sameInstance(routeMatches.get(0)));
        assertThat(modified.get(1), sameInstance(routeMatches.get(1)));
        assertThat(modified.get(2), sameInstance(routeMatches.get(2)));
    }
    
    @Test
    public void routesWithTargetAsLast_twoTargets_keepFirstTargetOnly() {
        List<RouteMatch<FilterAwareImpl>> routeMatches = New.arrayList();
        routeMatches.add(newRouteMatchMock(false));
        routeMatches.add(newRouteMatchMock(false));
        routeMatches.add(newRouteMatchMock(true));
        routeMatches.add(newRouteMatchMock(true));
        
        List<RouteMatch<FilterAwareImpl>> modified = Routes.reorderRoutes(routeMatches);
        assertThat(modified.get(0), sameInstance(routeMatches.get(2)));
        assertThat(modified.get(1), sameInstance(routeMatches.get(3)));
        assertThat(modified.get(2), sameInstance(routeMatches.get(0)));
        assertThat(modified.size(), equalTo(3));
    }

    
    private static RouteMatch<FilterAwareImpl> newRouteMatchMock(boolean isFilter) {

        Path requestedPath = mock(Path.class);
        Path entryPath = mock(Path.class);
        PathMatcher pathMatcher = mock(PathMatcher.class);
        RouteEntry<FilterAwareImpl> entry = RouteEntry.create(entryPath, pathMatcher, new FilterAwareImpl(isFilter));
        return RouteMatch.create(requestedPath, entry);
    }
    
    private static class FilterAwareImpl implements FilterAware {
        private boolean isFilter;
        public FilterAwareImpl(boolean isFilter) {
            super();
            this.isFilter = isFilter;
        }
        @Override
        public boolean isFilter() {
            return isFilter;
        }
    }
}
