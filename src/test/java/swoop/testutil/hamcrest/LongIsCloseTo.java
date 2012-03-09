package swoop.testutil.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;


/**
 * Is the value a number equal to a value within some range of
 * acceptable error?
 */
public class LongIsCloseTo extends TypeSafeMatcher<Long> {
    private final long delta;
    private final long value;

    public LongIsCloseTo(long value, long error) {
        this.delta = error;
        this.value = value;
    }

    @Override
    public boolean matchesSafely(Long item) {
        return actualDelta(item) <= 0.0;
    }

    @Override
    public void describeMismatchSafely(Long item, Description mismatchDescription) {
      mismatchDescription.appendValue(item)
                         .appendText(" differed by ")
                         .appendValue(actualDelta(item));
    }

    public void describeTo(Description description) {
        description.appendText("a numeric value within ")
                .appendValue(delta)
                .appendText(" of ")
                .appendValue(value);
    }

    private long actualDelta(long item) {
      return (Math.abs((item - value)) - delta);
    }


    @Factory
    public static Matcher<Long> closeTo(long operand, long error) {
        return new LongIsCloseTo(operand, error);
    }

}
