package swoop.testutil.hamcrest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static swoop.testutil.hamcrest.StringMatchers.uuidMatcher;

import java.util.regex.Pattern;

import org.hamcrest.Matcher;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class StringMatchersTest {

    @Test(dataProvider = "uuidRegex")
    public void uuid_javaRegex(String input) {
        Pattern uuidPattern = Pattern.compile(input);
        assertThat(uuidPattern.matcher("eb880ab6-2b7a-46c0-8a12-71120da869b8").matches(), is(true));
    }

    @DataProvider(name = "uuidRegex")
    public Object[][] uuidRegex() {
        String HEXA = "[a-hA-H0-9]";
        return new Object[][] { //
        //
                { "[a-hA-H0-9]{8}\\-[a-hA-H0-9]{4}\\-[a-hA-H0-9]{4}\\-[a-hA-H0-9]{4}\\-[a-hA-H0-9]{12}" }, //
                { HEXA + "{8}\\-" + HEXA + "{4}\\-" + HEXA + "{4}" + "\\-" + HEXA + "{4}" + "\\-" + HEXA + "{12}" }, //
                // with capturing groups
                { "([a-hA-H0-9]{8})\\-([a-hA-H0-9]{4})\\-([a-hA-H0-9]{4})\\-([a-hA-H0-9]{4})\\-([a-hA-H0-9]{12})" }, //
        };
    }

    @Test
    public void uuidMatcher_matches_validEntries() {
        Matcher<String> validUUID = uuidMatcher();
        assertThat("eb880ab6-2b7a-46c0-8a12-71120da869b8", is(validUUID));
    }

    @Test(dataProvider = "invalidUUIDs")
    public void uuidMatcher_doesntMatch_invalidEntry(String input) {
        Matcher<String> validUUID = uuidMatcher();
        assertThat(input, is(not(validUUID)));
    }

    @DataProvider(name = "invalidUUIDs")
    public Object[][] invalidUUIDs() {
        return new Object[][] { //
        //
                { "gb880ab6-2b7a-46c0-8a12-71120da869b8" }, //
                { "0" }, //
                { "a-a-a-a" }, //
                { "ab880ab6-2b7a-46c0-8a12" } };
    }

    /*
    @Test
    public void uuidMatcher_namedCapture() throws PatternMatchException {
        Parse parse = uuidMatcher().parse("eb880ab6-2b7a-46c0-8a12-71120da869b8");
        assertThat(parse.get("block1"), equalTo("eb880ab6"));
        assertThat(parse.get("block2"), equalTo("2b7a"));
        assertThat(parse.get("block3"), equalTo("46c0"));
        assertThat(parse.get("block4"), equalTo("8a12"));
        assertThat(parse.get("block5"), equalTo("71120da869b8"));
    }
    */

}
