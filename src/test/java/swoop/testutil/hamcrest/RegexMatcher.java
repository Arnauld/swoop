package swoop.testutil.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.regex.Pattern;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class RegexMatcher extends TypeSafeMatcher<String> {

    // TODO: Replace String with CharSequence to allow for easy interoperability between
    //       String, StringBuffer, StringBuilder, CharBuffer, etc (joe).

    private final Pattern pattern;

    public RegexMatcher(String regex) {
        if (regex == null) {
            throw new IllegalArgumentException("Non-null value required by RegexMatcher()");
        }
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public boolean matchesSafely(String item) {
        return pattern.matcher(item).matches();
    }

    @Override
    public void describeMismatchSafely(String item, Description mismatchDescription) {
      mismatchDescription.appendText("matches  ").appendText(item);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("regex(")
                .appendValue(pattern.pattern())
                .appendText(")");
    }

    /**
     * Creates a matcher of {@link String} that matches when the examined string matches
     * the specified regex.
     *
     * @param expectedString
     *     the expected value of matched strings
     */
    @Factory
    public static Matcher<String> matches(String expectedString) {
        return new RegexMatcher(expectedString);
    }
}
