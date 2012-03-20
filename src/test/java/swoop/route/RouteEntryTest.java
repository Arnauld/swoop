package swoop.route;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import swoop.path.Path;
import swoop.path.PathPatternMatcher;
import swoop.path.Verb;
import swoop.path.VerbMatcher;

public class RouteEntryTest {
    private FilterAwareImpl impl;
    private PathPatternMatcher pathMatcher;
    private RouteEntry<FilterAwareImpl> entry;
    private VerbMatcher verbMatcher;

    @BeforeMethod
    public void setUp() {
        verbMatcher = mock(VerbMatcher.class);
        pathMatcher = mock(PathPatternMatcher.class);
        impl = new FilterAwareImpl();
        //
        entry = new RouteEntry<FilterAwareImpl>(verbMatcher, pathMatcher, impl) {
        };
    }

    @Test
    public void getTarget() {
        assertThat(entry.getTarget(), sameInstance(impl));
    }

    @Test
    public void isFilter_delegateTo_underlyingImpl() {
        impl.setFilter(true);
        assertThat(entry.isFilter(), is(true));
        impl.setFilter(false);
        assertThat(entry.isFilter(), is(false));
    }

    @Test(dataProvider = "matchesData")
    public void matches(final Verb entryVerb, final String entryPath, Path testedPath, boolean expected) {
        when(pathMatcher.matches(Mockito.anyString())).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return entryPath.equals((String) invocation.getArguments()[0]);
            }
        });
        when(verbMatcher.matches(Mockito.any(Verb.class))).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return entryVerb == ((Verb) invocation.getArguments()[0]);
            }
        });
        assertThat(""+testedPath+"? " + expected, entry.matches(testedPath), is(expected));
    }

    @DataProvider
    public Object[][] matchesData() {
        return new Object[][] {//
        //
                { Verb.Get, "some", new Path(Verb.Get, "some"), true }, //
                { Verb.Put, "some", new Path(Verb.Put, "some"), true }, //
                { Verb.Get, "sume", new Path(Verb.Get, "some"), false }, //
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
