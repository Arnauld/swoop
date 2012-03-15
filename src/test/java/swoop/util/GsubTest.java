package swoop.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.regex.Matcher;

import org.testng.annotations.Test;

import swoop.util.Gsub.Replacement;

public class GsubTest {
    @Test
    public void simpleCase () {
        String input = "hello World";
        
        String replaced = Gsub.gsub("([a-h]+)", input, new Replacement() {
            @Override
            public String replacement(String content, Matcher matcher) {
                return matcher.group(1).toUpperCase();
            }
        });
        
        assertThat(replaced, equalTo("HEllo WorlD"));
    }
}
