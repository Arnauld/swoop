package swoop.path;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import swoop.path.Path;
import swoop.path.Verb;

public class PathTest {

    @Test
    public void constants() {
        assertThat(Path.ALL_PATHS, equalTo("*"));
    }
    
    @Test(expectedExceptions={IllegalArgumentException.class})
    public void invalidArgument_nullRoute() {
        new Path((String)null);
    }
    
    @Test(expectedExceptions={IllegalArgumentException.class})
    public void invalidArgument_nullVerb() {
        new Path((Verb)null);
    }
    
    @Test(expectedExceptions={IllegalArgumentException.class})
    public void invalidArgument_nullVerbWithRoute() {
        new Path(null, "*");
    }
    
    @Test(expectedExceptions={IllegalArgumentException.class})
    public void invalidArgument_nullVerbNullRoute() {
        new Path(null, null);
    }
    
    @Test(expectedExceptions={IllegalArgumentException.class})
    public void invalidArgument_withVerbNullRoute() {
        new Path(Verb.Get, null);
    }

    
    @Test(dataProvider="validEntries")
    public void parseValidEntries(String input, Verb expectedVerb, String expectedRoute) {
        Path path = new Path(input);
        assertThat(path.getVerb(), equalTo(expectedVerb));
        assertThat(path.getPathPattern(), equalTo(expectedRoute));
    }
    
    @DataProvider(name = "allVerbs")
    public Object[][] validEntries() {
        return new Object[][] {//
                {"get '*'", Verb.Get, "*"},
                {"post '/'", Verb.Post, "/"},
                {"put '/:service/:id'", Verb.Put, "/:service/:id"},
                {"delete", Verb.Delete, "*"},
                {"get", Verb.Get, "*"},
        };
    }
    
    @Test(dataProvider="invalidEntries", expectedExceptions={InvalidPathException.class})
    public void parseInvalidEntries(String input, String reason) {
        new Path(input);
    }
    
    @DataProvider(name = "allVerbs")
    public Object[][] invalidEntries() {
        return new Object[][] {//
                {"get '*", "unbalanced quote"},
                {"past '/'", "unknown verb"},
                {"got", "unknown verb"}
        };
    }
}
