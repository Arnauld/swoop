[hamcrest-text-patterns](http://code.google.com/p/hamcrest-text-patterns/)

> A library for writing readable, composable regular expression matchers that integrates cleanly with Hamcrest.

# Features

* Easier to read (although more long-winded) than regular expressions
* Named capture groups: captured text is identified by name, not by the index of the group.
* Composable: patterns can be easily combined into more complex patterns without worrying about breaking regex syntax or changing group identifiers.
* Refactoring friendly: patterns can be refactored with your favourite IDE.

# Parsing

Here's a simple example of using Hamcrest Text Patterns to parse date ranges, such as "12 Apr 1994 - 16 Mar 2005".

We can define components of the final text pattern using the factory methods defined in the Patterns class:

```java
PatternComponent month = either(
    "jan", "feb", "mar", "apr", "may", "jun", 
    "jul", "aug", "sep", "oct", "nov", "dec");
        
PatternComponent digit = anyCharacterInCategory("Digit");
    
PatternComponent date = separatedBy(whitespace(),
    capture("day", exactly(2, digit)), 
    capture("month", caseInsensitive(month)), 
    capture("year", exactly(4, digit)));
```

We can combine these into a PatternMatcher for matching date ranges:

```java
PatternMatcher dateRange = new PatternMatcher(separatedBy(whitespace(),
    capture("from", date), "-", capture("to", date)));
```

We can now parse date ranges from input text and extract the dates using the names of the capture groups we defined in the dateRange pattern: "from" and "to".

```java
String input = "31 Dec 2003 - 16 aug 2008";
        
Parse parse = dateRange.parse(input);
        
assertThat(parse.get("from"), equalTo("31 Dec 2003"));
assertThat(parse.get("to"), equalTo("16 aug 2008"));
```

And, because we defined capture groups in our date pattern, we can extract the parts of the "from" and "to" dates:

```java
assertThat(parse.get("from.day"), equalTo("31"));
assertThat(parse.get("from.month"), equalTo("Dec"));
assertThat(parse.get("from.year"), equalTo("2003"));
        
assertThat(parse.get("to.day"), equalTo("16"));
assertThat(parse.get("to.month"), equalTo("aug"));
assertThat(parse.get("to.year"), equalTo("2008"));
```

# Matching

A `PatternMatcher` is a Hamcrest matcher, and so text patterns can be used with libraries that build upon Hamcrest, such as JUnit or jMock. For example:

```java
String input = "31 Dec 2003 - 16 aug 2008";
assertThat(input, dateRange);
        
String badInput = "31 12 2003 - 16 08 2008";
assertThat(badInput, not(dateRange));
```

# `PatternMatcherTests.java`

```java
package org.hamcrest.text.pattern;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.text.pattern.PatternMatcher.matchesPattern;
import static org.hamcrest.text.pattern.Patterns.anyCharacter;
import static org.hamcrest.text.pattern.Patterns.anyCharacterIn;
import static org.hamcrest.text.pattern.Patterns.anyCharacterInCategory;
import static org.hamcrest.text.pattern.Patterns.anyCharacterNotIn;
import static org.hamcrest.text.pattern.Patterns.anyCharacterNotInCategory;
import static org.hamcrest.text.pattern.Patterns.capture;
import static org.hamcrest.text.pattern.Patterns.caseInsensitive;
import static org.hamcrest.text.pattern.Patterns.caseSensitive;
import static org.hamcrest.text.pattern.Patterns.either;
import static org.hamcrest.text.pattern.Patterns.exactly;
import static org.hamcrest.text.pattern.Patterns.from;
import static org.hamcrest.text.pattern.Patterns.listOf;
import static org.hamcrest.text.pattern.Patterns.oneOrMore;
import static org.hamcrest.text.pattern.Patterns.optional;
import static org.hamcrest.text.pattern.Patterns.separatedBy;
import static org.hamcrest.text.pattern.Patterns.sequence;
import static org.hamcrest.text.pattern.Patterns.text;
import static org.hamcrest.text.pattern.Patterns.valueOf;
import static org.hamcrest.text.pattern.Patterns.whitespace;
import static org.hamcrest.text.pattern.Patterns.zeroOrMore;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

public class PatternMatcherTests {
    @Test
    public void matchesPlainText() {
        PatternMatcher matcher = new PatternMatcher(text("text"));

        assertThat("text", matchesPattern(matcher));
        assertThat("xxxtextxxx", not(matchesPattern(matcher)));
        assertThat("tex", not(matchesPattern(matcher)));
        assertThat("blah", not(matchesPattern(matcher)));
    }

    @Test
    public void matchesPlainTextContainingSpecialRegexCharacters() {
        assertThat("*star*", matchesPattern(
                new PatternMatcher(text("*star*"))));
        
        assertThat("-", matchesPattern(
                new PatternMatcher(text("-"))));
    }

    @Test
    public void matchesSequenceOfText() {
        PatternMatcher matcher = new PatternMatcher(sequence("hello", " ", "world"));

        assertThat("hello world", matchesPattern(matcher));
    }

    @Test
    public void matchesAlternatives() {
        PatternMatcher matcher = new PatternMatcher(either(text("hello"), text("world")));

        assertThat("hello", matchesPattern(matcher));
        assertThat("world", matchesPattern(matcher));
        assertThat("hello world", not(matchesPattern(matcher)));
    }

    @Test
    public void matchesOptionalPattern() {
        PatternMatcher matcher = new PatternMatcher(sequence(text("hello"), optional(text(" world"))));

        assertThat("hello", matchesPattern(matcher));
        assertThat("hello world", matchesPattern(matcher));
        assertThat(" world", not(matchesPattern(matcher)));
    }

    @Test
    public void matchesRepetitionZeroOrMoreTimes() {
        PatternMatcher matcher = new PatternMatcher(zeroOrMore(text("x")));

        assertThat("", matchesPattern(matcher));
        assertThat("x", matchesPattern(matcher));
        assertThat("xxx", matchesPattern(matcher));
        assertThat(" xx", not(matchesPattern(matcher)));
        assertThat("x x", not(matchesPattern(matcher)));
        assertThat("xx ", not(matchesPattern(matcher)));
    }

    @Test
    public void matchesRepetitionOneOrMoreTimes() {
        PatternMatcher matcher = new PatternMatcher(oneOrMore(text("x")));

        assertThat("", not(matchesPattern(matcher)));
        assertThat("x", matchesPattern(matcher));
        assertThat("xxx", matchesPattern(matcher));
        assertThat(" xx", not(matchesPattern(matcher)));
        assertThat("x x", not(matchesPattern(matcher)));
        assertThat("xx ", not(matchesPattern(matcher)));
    }

    @Test
    public void testCanMatchAnyCharacter() {
        PatternMatcher matcher = new PatternMatcher(sequence(text("x"), anyCharacter(), text("y")));

        assertThat("x.y", matchesPattern(matcher));
        assertThat("xzy", matchesPattern(matcher));
        assertThat("xy", not(matchesPattern(matcher)));
    }

    @Test
    public void testCapturesMatchedGroups() throws Exception {
        PatternMatcher matcher = new PatternMatcher(sequence(capture("xs", oneOrMore(text("x"))), capture("ys", oneOrMore(text("y")))));

        Parse parse;

        parse = matcher.parse("xxxyyy");
        assertEquals("xxx", parse.get("xs"));
        assertEquals("yyy", parse.get("ys"));

        parse = matcher.parse("xxyyyyyy");
        assertEquals("xx", parse.get("xs"));
        assertEquals("yyyyyy", parse.get("ys"));
    }

    @Test
    public void testFailsIfDoesNotMatchParseInput() {
        PatternMatcher matcher = new PatternMatcher(text("expected input"));

        try {
            matcher.parse("input that doesn't match");
            fail("should have thrown ParseException");
        } catch (PatternMatchException ex) {
            // expected
        }
    }

    @Test
    public void testCanReferToContentOfMatchedGroups() throws Exception {
        PatternMatcher matcher = new PatternMatcher(sequence(capture("first", oneOrMore(text("x"))), text("-"), valueOf("first")));

        assertThat("x-x", matchesPattern(matcher));
        assertThat("xx-xx", matchesPattern(matcher));

        assertThat("x-xx", not(matchesPattern(matcher)));
        assertThat("xx-x", not(matchesPattern(matcher)));
    }

    PatternMatcher scopedMatcher = new PatternMatcher(sequence(capture("xs", oneOrMore(text("x"))), capture("inside", sequence(capture("xs", oneOrMore(text("X"))), valueOf("xs"))), valueOf("xs")));

    @Test
    public void testReferencesToGroupsAreLexicallyScoped() throws Exception {
        assertThat("xxXXXXxx", matchesPattern(scopedMatcher));
        assertThat("xXXx", matchesPattern(scopedMatcher));
        assertThat("xXxx", not(matchesPattern(scopedMatcher)));
        assertThat("xXXX", not(matchesPattern(scopedMatcher)));
    }

    @Test
    public void testNamesInInnerScopesCanBeQueriedUsingDottedPathNotation() throws Exception {
        Parse parse = scopedMatcher.parse("xxXXXXXXxx");
        assertEquals("xx", parse.get("xs"));
        assertEquals("XXX", parse.get("inside.xs"));
    }

    @Test
    public void testCanReferToValuesOfGroupsInInnerScopesUsingDottedPathNotation() {
        PatternMatcher matcher = new PatternMatcher(sequence(capture("xs", oneOrMore(text("x"))), capture("inside", sequence(capture("xs", oneOrMore(text("X"))), valueOf("xs"))), valueOf("xs"), valueOf("inside.xs")));

        assertThat("xXXXXxXX", matchesPattern(matcher));
        assertThat("xxXXxxX", matchesPattern(matcher));
    }

    @Test
    public void testCanDefinePatternsInTermsOfExistingPatterns() {
        PatternMatcher emailAddressMatcher = new PatternMatcher(sequence(capture("user", oneOrMore(anyCharacter())), "@", capture("host", oneOrMore(anyCharacter()))));

        PatternMatcher mailToURLMatcher = new PatternMatcher(sequence(capture("scheme", text("mailto")), ":", capture("email", emailAddressMatcher)));

        assertThat("mailto:npryce@users.sf.net", matchesPattern(mailToURLMatcher));
    }

    @Test
    public void matchesCharacterInList() {
        PatternMatcher matcher = new PatternMatcher(anyCharacterIn("0123456789"));

        for (int i = 0; i < 9; i++) {
            String input = String.valueOf(i);

            assertThat(input, matchesPattern(matcher));
        }
        assertThat("X", not(matchesPattern(matcher)));
    }

    @Test
    public void matchesCharacterRange() {
        PatternMatcher matcher = new PatternMatcher(anyCharacterIn("0-9"));

        for (int i = 0; i < 9; i++) {
            String input = String.valueOf(i);

            assertThat(input, matchesPattern(matcher));
        }
        assertThat("X", not(matchesPattern(matcher)));
    }

    @Test
    public void matchesCharacterNotInRange() {
        PatternMatcher matcher = new PatternMatcher(anyCharacterNotIn("0-9"));

        for (int i = 0; i < 9; i++) {
            String input = String.valueOf(i);

            assertThat(input, not(matchesPattern(matcher)));
        }
        assertThat("X", matchesPattern(matcher));
    }

    @Test
    public void matchesCharactersInUnicodeCategories() {
        PatternMatcher matcher = new PatternMatcher(anyCharacterInCategory("Lu"));

        assertThat("A", matchesPattern(matcher));
        assertThat("a", not(matchesPattern(matcher)));
        assertThat("B", matchesPattern(matcher));
        assertThat("b", not(matchesPattern(matcher)));
    }

    @Test
    public void matchesCharactersNotInUnicodeCategories() {
        PatternMatcher matcher = new PatternMatcher(anyCharacterNotInCategory("Lu"));

        assertThat("A", not(matchesPattern(matcher)));
        assertThat("a", matchesPattern(matcher));
        assertThat("B", not(matchesPattern(matcher)));
        assertThat("b", matchesPattern(matcher));
    }

    @Test
    public void matchesExactlyTheSpecifiedNumberOfRepetitions() {
        PatternMatcher matcher = new PatternMatcher(exactly(3, "x"));

        assertThat("xx", not(matchesPattern(matcher)));
        assertThat("xxx", matchesPattern(matcher));
        assertThat("xxxx", not(matchesPattern(matcher)));
    }

    @Test
    public void matchesARangeOfAllowableRepetitions() {
        PatternMatcher matcher = new PatternMatcher(from(3, 5, "x"));

        assertThat("xx", not(matchesPattern(matcher)));
        assertThat("xxx", matchesPattern(matcher));
        assertThat("xxxx", matchesPattern(matcher));
        assertThat("xxxxx", matchesPattern(matcher));
        assertThat("xxxxxx", not(matchesPattern(matcher)));
    }

    @Test
    public void matchesAListOfMatchedThings() {
        PatternMatcher matcher = new PatternMatcher(listOf("x"));

        assertThat("", matchesPattern(matcher));
        assertThat("x", matchesPattern(matcher));
        assertThat("x,x", matchesPattern(matcher));
        assertThat("x,x,x,x,x", matchesPattern(matcher));

        assertThat(",", not(matchesPattern(matcher)));
        assertThat("x,x,x,", not(matchesPattern(matcher)));
    }

    @Test
    public void matchesAListWithSpecificSeparator() {
        PatternMatcher matcher = new PatternMatcher(listOf("x").separatedBy(":"));

        assertThat("", matchesPattern(matcher));
        assertThat("x", matchesPattern(matcher));
        assertThat("x:x", matchesPattern(matcher));
        assertThat("x:x:x:x:x", matchesPattern(matcher));

        assertThat("x,x,x", not(matchesPattern(matcher)));
    }

    @Test
    public void matchesWhitespace() {
        PatternMatcher matcher = new PatternMatcher(sequence("a", whitespace(), "z"));

        assertThat("az", matchesPattern(matcher));
        assertThat("a z", matchesPattern(matcher));
        assertThat("a \t z", matchesPattern(matcher));
    }
    
    @Test
    public void matchesSequenceSeparatedByPattern() {
        PatternMatcher matcher = new PatternMatcher(
                separatedBy(",", "a", "b", "c"));
        
        assertThat("a,b,c", matchesPattern(matcher));
    }
    
    @Test
    public void canControlCaseSensitivity() {
        PatternMatcher matcher = new PatternMatcher(
            sequence("a",caseInsensitive(sequence("b",caseSensitive("c"))))
        );
        
        assertThat("abc", matchesPattern(matcher));
        assertThat("aBc", matchesPattern(matcher));
        assertThat("aBC", not(matchesPattern(matcher)));
    }
}
```
