package swoop.testutil.hamcrest;

import static java.util.Arrays.asList;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.text.StringContainsInOrder;

public class StringMatchers  {
    
    @Factory
    public static Matcher<String> stringContains(String substring) {
        return new StringContainsInOrder(asList(substring));
    }
    
    @Factory
    public static Matcher<String> stringContainsInOrder(Iterable<String> substrings) {
        return new StringContainsInOrder(substrings);
    }
}
