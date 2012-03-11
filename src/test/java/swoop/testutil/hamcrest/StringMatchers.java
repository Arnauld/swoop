package swoop.testutil.hamcrest;

import static java.util.Arrays.asList;
import static org.hamcrest.text.pattern.Patterns.anyCharacterInCategory;
import static org.hamcrest.text.pattern.Patterns.capture;
import static org.hamcrest.text.pattern.Patterns.exactly;
import static org.hamcrest.text.pattern.Patterns.separatedBy;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.text.StringContainsInOrder;
import org.hamcrest.text.pattern.PatternComponent;
import org.hamcrest.text.pattern.PatternMatcher;

public class StringMatchers {

    @Factory
    public static Matcher<String> stringContains(String substring) {
        return new StringContainsInOrder(asList(substring));
    }

    @Factory
    public static Matcher<String> stringContainsInOrder(Iterable<String> substrings) {
        return new StringContainsInOrder(substrings);
    }

    public static PatternMatcher uuidMatcher() {
        PatternComponent hexa = anyCharacterInCategory("XDigit");
        return new PatternMatcher(separatedBy("-", //
                capture("block1", exactly(8, hexa)), //
                capture("block2", exactly(4, hexa)), //
                capture("block3", exactly(4, hexa)), //
                capture("block4", exactly(4, hexa)), //
                capture("block5", exactly(12, hexa))));
    }
}
