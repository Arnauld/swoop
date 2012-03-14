package swoop.route;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import swoop.path.Path;
import swoop.util.Multimap;

public class RouteMatchTest {
    private Path requestedPath;
    private RouteEntry<FilterAwareImpl> matchEntry;
    private RouteMatch<FilterAwareImpl> routeMatch;

    @SuppressWarnings("unchecked")
    @BeforeMethod
    public void setUp () {
        requestedPath = mock(Path.class);
        matchEntry = mock(RouteEntry.class);
        routeMatch = new RouteMatch<FilterAwareImpl>(requestedPath, matchEntry);
    }
    
    @Test
    public void getMatchEntry () {
        assertThat(routeMatch.getMatchEntry(), sameInstance(matchEntry));
    }

    @Test
    public void getTarget () {
        FilterAwareImpl filterAware = mock(FilterAwareImpl.class);
        when(matchEntry.getTarget()).thenReturn(filterAware);
        assertThat(routeMatch.getTarget(), sameInstance(filterAware));
    }

    @Test
    public void getRouteParameters () {
        when(requestedPath.getPathPattern()).thenReturn("Dooo!");
        
        Multimap<String, String> multimap = new Multimap<String, String>();
        when(matchEntry.extractParameters(requestedPath)).thenReturn(multimap);

        assertThat(routeMatch.getRouteParameters(), sameInstance(multimap));
    }

    public static class FilterAwareImpl implements FilterAware {
        private boolean isFilter;

        public void setFilter(boolean isFilter) {
            this.isFilter = isFilter;
        }

        @Override
        public boolean isFilter() {
            return isFilter;
        }
    }
}
