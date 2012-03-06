package swoop.route;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static swoop.route.PathMatcherSinatraCompiler.encoded;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import swoop.util.Multimap;

public class PathMatcherSinatraCompilerTest {

    @Test(dataProvider = "matchAll")
    public void matches(String mustMatch) {
        assertThat(compile("*").matches(mustMatch), is(true));
    }
    
    @DataProvider(name = "matchAll")
    public Object[][] matchAll() {
        return new Object[][] { //
                { "/" }, //
                { "/hello/bar" }, //
                { "/say/hello/to/world" }, //
                { "/download/path/to/file.xml" }, //
                { "/posts" }, //
                { "/posts.json" }, //
        };
    }

    @Test(dataProvider = "validPaths")
    public void matches_validInput(String routeUri, String uri) {
        assertThat(compile(routeUri).matches(uri), is(true));
    }

    @DataProvider(name = "validPaths")
    public Object[][] validPaths() {
        return new Object[][] { { "/hello/:name", "/hello/foo" }, //
                { "/hello/:name", "/hello/bar" }, //
                { "/say/*/to/*", "/say/hello/to/world" }, //
                { "/download/*.*", "/download/path/to/file.xml" }, //
                { "/posts.?:format?", "/posts" }, //
                { "/posts.?:format?", "/posts.json" }, //
                { "/posts.?:format?", "/posts.xml" } //
        };
    }

    @Test
    public void encoded_b() throws UnsupportedEncodingException {
        assertThat(encoded('a'), equalTo("(?:\\Qa\\E|%61)"));
        assertThat(encoded(' '), equalTo("(?:%20|\\+|%2B)"));
        assertThat(encoded('+'), equalTo("(?:\\+|%2B)"));
    }
    
    @Test
    public void regex_from_space() {
        assertThat(Pattern.matches("(?:%20|\\+|%2B)", "%20"), is(true));
        assertThat(Pattern.matches("^(?:%20|\\+|%2B)$", "%20"), is(true));
        assertThat(Pattern.matches("^\\/path(?:%20|\\+|%2B)with(?:%20|\\+|%2B)spaces$", "/path%20with%20spaces"), is(true));
        assertThat(Pattern.matches("^\\/path(?:%20|\\+|%2B)with(?:%20|\\+|%2B)spaces$", "/path+with+spaces"), is(true));
    }

    @Test
    public void extractParameters_handlesEncodedSlash() throws UnsupportedEncodingException {
        // it handles encoded slashes correctly
        parametersMatch("/:a", "/foo%2Fbar", param("a", "foo/bar"));
    }

    @Test
    public void extractParameters_supportNamedParameter() throws UnsupportedEncodingException {
        // supports named params like /hello/:person
        parametersMatch("/hello/:person", "/hello/Frank", param("person", "Frank"));
    }

    @Test
    public void extractParameters_supportOptionalParameters() throws UnsupportedEncodingException {
        // supports optional named params like /?:foo?/?:bar?
        parametersMatch("/?:foo?/?:bar?", "/hello/world", param("foo", "hello"), param("bar", "world"));
        parametersMatch("/?:foo?/?:bar?", "/hello", param("foo", "hello"));
        parametersMatch("/?:foo?/?:bar?", "/");
    }

    @Test
    public void extractParameters_supportSingleSplatParameter() throws UnsupportedEncodingException {
        // supports single splat params like /*
        parametersMatch("/*", "/foo", param("splat", "foo"));
        parametersMatch("/*", "/foo/bar/baz", param("splat", "foo/bar/baz"));
    }

    @Test
    public void extractParameters_supportMultipleSplatParams() {
        // supports mixing multiple splat params like /*/foo/*/*
        parametersMatch("/*/foo/*/*", "/bar/foo/bling/baz/boom", param("splat", "bar", "bling", "baz/boom"));
    }

    @Test
    public void extractParameters_supports_mixing_named_and_splat_params() {
        // supports mixing named and splat params like /:foo/*
        parametersMatch("/:foo/*", "/foo/bar/baz", param("foo", "foo"), param("splat", "bar/baz"));
    }

    @Test
    public void extractParameters_matches_a_dot_as_part_of_a_named_param() {
        // matches a dot ('.') as part of a named param
        parametersMatch("/:foo/:bar", "/user@example.com/name", param("foo", "user@example.com"), param("bar", "name"));
    }

    @Test
    public void extractParameters_matches_a_literal_dot_outside_of_named_params() {
        // matches a literal dot ('.') outside of named params
        parametersMatch("/:file.:ext", "/pony.jpg", param("file", "pony"), param("ext", "jpg"));
    }

    @Test
    public void extractParameters_literally_matches_dot_in_paths() {
        // literally matches dot in paths
        parametersMatch("/test.bar", "/test.bar");
        parametersNotMatch("/test.bar", "/test0bar");
    }

    @Test
    public void extractParameters_literally_matches_dollar_sign_in_paths() {
        // literally matches dollar sign in paths
        parametersMatch("/test$/", "/test$/");
    }

    @Test
    public void extractParameters_literally_matches_plus_sign_in_paths() {
        // literally matches plus sign in paths
        parametersMatch("/te+st/", "/te%2Bst/");
        parametersNotMatch("/te+st/", "/teeeeeeest/");
    }

    @Test
    public void extractParameters_converts_plus_sign_into_space_as_the_value_of_a_named_param() {
        // converts plus sign into space as the value of a named param
        parametersMatch("/:test", "/bob+ross", param("test", "bob ross"));
    }

    @Test
    public void extractParameters_literally_matches_parens_in_paths() {
        // literally matches parens in paths
        parametersMatch("/test(bar)/", "/test(bar)/");
    }

    @Test
    public void extractParameters_matches_paths_that_include_spaces_encoded_with_percent20() {
        // matches paths that include spaces encoded with %20
        parametersMatch("/path with spaces", "/path%20with%20spaces");
    }

    @Test
    public void extractParameters_matches_paths_that_include_spaces_encoded_with_plus() {
        // matches paths that include spaces encoded with +
        parametersMatch("/path with spaces", "/path+with+spaces");
    }

    @Test
    public void extractParameters_matches_paths_that_include_ampersands() {
        // matches paths that include ampersands
        parametersMatch("/:name", "/foo&bar", param("name", "foo&bar"));
    }

    @Test
    public void extractParameters_URL_decodes_named_parameters_and_splats() {
        // URL decodes named parameters and splats
        parametersMatch("/:foo/*", "/hello%20world/how%20are%20you", param("foo", "hello world"),
                param("splat", "how are you"));
    }

    protected static PathMatcher compile(String routeExpr) {
        return new PathMatcherSinatraCompiler().compile(routeExpr);
    }

    protected static void parametersNotMatch(String expr, String value) {
        PathMatcher compiled = compile(expr);
        assertThat(compiled.matches(value), is(false));
    }

    protected static void parametersMatch(String expr, String value, Param... expectedParams) {
        PathMatcher compiled = compile(expr);
        assertThat("[" + compiled + "] does not match [" + value + "]", compiled.matches(value), is(true));
        Multimap<String, String> params = compiled.extractParameters(value);
        for (Param p : expectedParams) {
            assertThat(params.toMap(), hasEntry(p.name, Arrays.asList(p.values)));
        }
        assertThat("There are more parameters than the number of checked", params.numberOfKeys(),
                equalTo(expectedParams.length));
    }

    @Test(enabled = false)
    public void compile_d() throws UnsupportedEncodingException {
        // merges named params and query string params in params
        PathMatcher compiled = compile("/:foo");
        Multimap<String, String> params = compiled.extractParameters("/bar?baz=biz");
        System.out.println(params);
    }

    private static Param param(String name, String... values) {
        return new Param(name, values);
    }

    private static class Param {
        public final String name;
        public final String[] values;

        public Param(String name, String... values) {
            super();
            this.name = name;
            this.values = values;
        }
    }
}
