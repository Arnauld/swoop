package swoop.testutil.hamcrest;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class LongMatchers {
    @Factory
    public static Matcher<Long> closeTo(long operand, long error) {
        return new LongIsCloseTo(operand, error);
    }
}
