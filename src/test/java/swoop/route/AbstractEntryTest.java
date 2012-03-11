package swoop.route;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import swoop.path.Path;
import swoop.path.PathMatcher;
import swoop.path.Verb;

public class AbstractEntryTest {
    private FilterAwareImpl impl;
    private PathMatcher pathMatcher;
    private Path path;
    private AbstractEntry<FilterAwareImpl> entry;

    @BeforeMethod
    public void setUp() {
        path = mock(Path.class);
        pathMatcher = mock(PathMatcher.class);
        impl = new FilterAwareImpl();
        //
        entry = new AbstractEntry<FilterAwareImpl>(path, pathMatcher, impl) {
        };
    }

    @Test
    public void getPathPattern_delegateTo_underlyingPath() {
        String DATA = "*pattern*";
        when(path.getPathPattern()).thenReturn(DATA);
        assertThat(entry.getPathPattern(), equalTo(DATA));
        verify(path).getPathPattern();
    }

    @Test
    public void getVerb_delegateTo_underlyingPath() {
        when(path.getVerb()).thenReturn(Verb.Post);
        assertThat(entry.getVerb(), equalTo(Verb.Post));
        verify(path).getVerb();
    }

    @Test
    public void getTarget() {
        assertThat(entry.getTarget(), sameInstance(impl));
    }

    @Test
    public void getPathMatcher() {
        assertThat(entry.getPathMatcher(), sameInstance(pathMatcher));
    }

    @Test
    public void isFilter_delegateTo_underlyingImpl() {
        impl.setFilter(true);
        assertThat(entry.isFilter(), is(true));
        impl.setFilter(false);
        assertThat(entry.isFilter(), is(false));
    }

    @Test(dataProvider = "matchesData")
    public void matches(Verb entryVerb, final String entryPath, Path testedPath, boolean expected) {
        when(pathMatcher.matches(Mockito.anyString())).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return entryPath.equals((String) invocation.getArguments()[0]);
            }
        });
        when(path.getVerb()).thenReturn(entryVerb);
        when(path.getPathPattern()).thenReturn(entryPath);
        assertThat(entry.matches(testedPath), is(expected));
    }

    @DataProvider
    public Object[][] matchesData() {
        return new Object[][] {//
        //
                { Verb.Get, "some", new Path(Verb.Get, "some"), true }, //
                { Verb.Put, "some", new Path(Verb.Put, "some"), true }, //
                { Verb.Get, "sume", new Path(Verb.Get, "some"), false }, //
                { Verb.Any, "some", new Path(Verb.Get, "some"), true }, //
                { Verb.Post, "some", new Path(Verb.Get, "some"), false }, //
        };
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
