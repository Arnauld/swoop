package swoop.server;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import swoop.util.Tree;

public class QueriesTest {

    private Tree<String, String> tree;

    @Test(dataProvider = "normalizeParameters")
    public void normalizeParametersRegex(String input, String key, String after) {
        Pattern p = Queries.NORMALIZE_PARAMETERS_PATTERN;
        Matcher matcher = p.matcher(input);
        assertThat(matcher.find(), is(true));
        assertThat(matcher.group(1), equalTo(key));
        assertThat(input.substring(matcher.end()), equalTo(after));
    }

    @DataProvider(name = "normalizeParameters")
    public Object[][] normalizeParametersData() {
        return new Object[][] { { "x[y][][z]", "x", "[y][][z]" }, //
                { "[y][][z]", "y", "[][z]" }, //
                { "[][z]", "z", "" } //
        };
    }

    @BeforeMethod
    public void setUp() {
        tree = new Tree<String, String>("/");
    }

    @Test
    public void normalizeParameters_defArray() {
        Queries.normalizeParameters(tree, "foo[]", "1");
        assertThat(tree.get("foo"), equalTo(asList("1")));

        Queries.normalizeParameters(tree, "foo[]", "2");
        assertThat(tree.get("foo"), equalTo(asList("1", "2")));
    }

    @Test
    public void normalizeParameters_noArray() {
        Queries.normalizeParameters(tree, "foo", "1");
        assertThat(tree.get("foo"), equalTo(asList("1")));

        Queries.normalizeParameters(tree, "foo", "2");
        assertThat(tree.get("foo"), equalTo(asList("1", "2")));
    }

    @Test
    public void normalizeParameters_noAndDefArray() {
        Queries.normalizeParameters(tree, "foo[]", "1");
        Queries.normalizeParameters(tree, "foo", "2");
        assertThat(tree.get("foo"), equalTo(asList("1", "2")));
    }

    @Test
    public void normalizeParameters_x_arrY_arrZ_1() {
        Queries.normalizeParameters(tree, "x[y][z]", "1");
        Tree<String, String> subX = tree.sub("x");
        Tree<String, String> subY = subX.sub("y");
        assertThat(subY.get("z"), equalTo(asList("1")));
    }

    @Test
    public void normalizeParameters_x_arrY_arrZ_2() {
        Queries.normalizeParameters(tree, "x[y][z]", "1");
        Queries.normalizeParameters(tree, "x[y][z]", "2");
        Tree<String, String> subX = tree.sub("x");
        Tree<String, String> subY = subX.sub("y");
        assertThat(subY.get("z"), equalTo(asList("1", "2")));
    }

    @Test
    public void normalizeParameters_x_arrY_arrZ_3() {
        Queries.normalizeParameters(tree, "x[y][z][]", "1");
        Queries.normalizeParameters(tree, "x[y][z][]", "2");
        Tree<String, String> subX = tree.sub("x");
        Tree<String, String> subY = subX.sub("y");
        assertThat(subY.get("z"), equalTo(asList("1", "2")));
    }

    @Test
    public void normalizeParameters_x_arrY_arrZ_4() {
        Queries.normalizeParameters(tree, "x[y][][z]", "1");
        Queries.normalizeParameters(tree, "x[y][][w]", "2");
        Tree<String, String> subX = tree.sub("x");
        Tree<String, String> subY = subX.sub("y");
        assertThat(subY.get("z"), equalTo(asList("1")));
        assertThat(subY.get("w"), equalTo(asList("2")));
    }

    @Test
    public void normalizeParameters_x_arrY_arrZ_5() {
        Queries.normalizeParameters(tree, "x[y][][z]", "1");
        Queries.normalizeParameters(tree, "x[y][][w]", "a");
        Queries.normalizeParameters(tree, "x[y][][z]", "2");
        Queries.normalizeParameters(tree, "x[y][][w]", "3");
        Tree<String, String> subX = tree.sub("x");
        Tree<String, String> subY = subX.sub("y");
        assertThat(subY.get("z"), equalTo(asList("1", "2")));
        assertThat(subY.get("w"), equalTo(asList("a", "3")));
    }
}
