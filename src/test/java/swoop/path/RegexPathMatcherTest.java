package swoop.path;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.regex.Pattern;

import org.testng.annotations.Test;

import swoop.util.Multimap;

public class RegexPathMatcherTest {
    
    private RegexPathMatcher matcher;
    private Multimap<String, String> params;

    @Test
    public void sameNumberOfParameterThanMatching() {
        matcher = new RegexPathMatcher (pattern("a(b+)"), asList("name"));
        params = matcher.extractParameters("abbbb");
        assertThat(params.keySet().size(), equalTo(1));
        assertThat(params.get("name"), equalTo(asList("bbbb")));
    }
    
    @Test
    public void sameNumberOfParametersThanMatchings() {
        matcher = new RegexPathMatcher (pattern("a(b+)([0-9]*)"), asList("name", "zipc"));
        params = matcher.extractParameters("abbbb7815");
        assertThat(params.keySet().size(), equalTo(2));
        assertThat(params.get("name"), equalTo(asList("bbbb")));
        assertThat(params.get("zipc"), equalTo(asList("7815")));
    }
    
    @Test
    public void lessParametersThanMatchings() {
        matcher = new RegexPathMatcher (pattern("a(b+)([0-9]*)"), asList("name"));
        params = matcher.extractParameters("abbbb7815");
        assertThat(params.keySet().size(), equalTo(1));
        assertThat(params.get("name"), equalTo(asList("bbbb")));
    }
    
    @Test
    public void moreParametersThanMatchings() {
        matcher = new RegexPathMatcher (pattern("a(b+)([0-9]*)"), asList("name", "zipc", "woot"));
        params = matcher.extractParameters("abbbb7815");
        assertThat(params.keySet().size(), equalTo(2));
        assertThat(params.get("name"), equalTo(asList("bbbb")));
        assertThat(params.get("zipc"), equalTo(asList("7815")));
    }

    private static Pattern pattern(String regex) {
        return Pattern.compile(regex);
    }
}
