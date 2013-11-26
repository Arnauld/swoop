package swoop.testutil.hamcrest;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.text.StringContainsInOrder;

import static java.util.Arrays.asList;

public class StringMatchers {

    @Factory
    public static Matcher<String> stringContains(String substring) {
        return new StringContainsInOrder(asList(substring));
    }

    @Factory
    public static Matcher<String> stringContainsInOrder(Iterable<String> substrings) {
        return new StringContainsInOrder(substrings);
    }

    public static Matcher<String> uuidMatcher() {
        String hex = "[a-f0-9]";
        return new RegexMatcher(hex + "{8}\\-" + hex + "{4}\\-" + hex + "{4}\\-" + hex + "{4}\\-" + hex + "{12}");
    }

/*
    public static PatternMatcher uuidMatcher() {
        PatternComponent hexa = anyCharacterInCategory("XDigit");
        return new PatternMatcher(separatedBy("-", //
                capture("block1", exactly(8, hexa)), //
                capture("block2", exactly(4, hexa)), //
                capture("block3", exactly(4, hexa)), //
                capture("block4", exactly(4, hexa)), //
                capture("block5", exactly(12, hexa))));
    }
    */
}
