package swoop.path;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.Test;

public class VerbMatchersTest {

    private VerbMatcher matcher;

    @Test
    public void fromExpression_empty() {
        matcher = VerbMatchers.fromExpression("");
        for (Verb verb : Verb.values())
            assertThat("Testing " + verb, matcher.matches(verb), is(false));
    }
    
    @Test
    public void fromExpression_singleVerb() {
        matcher = VerbMatchers.fromExpression("post");
        for (Verb verb : Verb.values())
            assertThat("Testing " + verb, matcher.matches(verb), is(verb==Verb.Post));
    }
    
    @Test
    public void fromExpression_threeVerbs() {
        matcher = VerbMatchers.fromExpression("post,put,websocketOpen");
        for (Verb verb : Verb.values())
            assertThat("Testing " + verb, matcher.matches(verb), is(verb==Verb.Post || verb==Verb.Put || verb==Verb.WebSocketOpen));
    }
    
    @Test
    public void fromExpression_any() {
        matcher = VerbMatchers.fromExpression("any");
        for (Verb verb : Verb.values())
            assertThat("Testing " + verb, matcher.matches(verb), is(true));

        matcher = VerbMatchers.fromExpression("*");
        for (Verb verb : Verb.values())
            assertThat("Testing " + verb, matcher.matches(verb), is(true));

        matcher = VerbMatchers.fromExpression("get,any,post");
        for (Verb verb : Verb.values())
            assertThat("Testing " + verb, matcher.matches(verb), is(true));
    }
    
}
