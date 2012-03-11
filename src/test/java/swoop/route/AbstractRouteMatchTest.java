package swoop.route;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import swoop.path.Path;
import swoop.path.PathMatcher;
import swoop.util.Multimap;

public class AbstractRouteMatchTest {
    private Path requestedPath;
    private EntryImpl matchEntry;
    private AbstractRouteMatch<FilterAwareImpl, EntryImpl> routeMatch;

    @BeforeMethod
    public void setUp () {
        requestedPath = Mockito.mock(Path.class);
        matchEntry = Mockito.mock(EntryImpl.class);
        routeMatch = new AbstractRouteMatch<FilterAwareImpl, EntryImpl>(requestedPath, matchEntry);
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
    public void getRequestPath () {
        when(requestedPath.getPathPattern()).thenReturn("Fonzie");
        assertThat(routeMatch.getRequestPath(), equalTo("Fonzie"));
    }
    
    @Test
    public void getRouteParameters () {
        when(requestedPath.getPathPattern()).thenReturn("Dooo!");
        
        PathMatcher pathMatcher = mock(PathMatcher.class);
        when(matchEntry.getPathMatcher()).thenReturn(pathMatcher);

        Multimap<String, String> multimap = new Multimap<String, String>();
        when(pathMatcher.extractParameters("Dooo!")).thenReturn(multimap);

        assertThat(routeMatch.getRouteParameters(), sameInstance(multimap));
    }

    
    public static class EntryImpl extends AbstractEntry<FilterAwareImpl> {
        public EntryImpl(Path path, PathMatcher pathMatcher, FilterAwareImpl target) {
            super(path, pathMatcher, target);
        }
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
