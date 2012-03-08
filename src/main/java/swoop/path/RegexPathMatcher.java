package swoop.path;

import static swoop.util.Objects.o;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.SwoopException;
import swoop.util.Multimap;
import swoop.util.New;
import swoop.util.Objects;

public class RegexPathMatcher implements PathMatcher {

    private Logger log = LoggerFactory.getLogger(RegexPathMatcher.class);

    private final List<String> keys;
    private final Pattern pattern;

    public RegexPathMatcher(Pattern pattern, List<String> keys) {
        super();
        this.keys = keys;
        this.pattern = pattern;
    }

    @Override
    public boolean matches(String uri) {
        Matcher matcher = pattern.matcher(uri);
        boolean matches = matcher.matches();
        log.debug("Does <{}> match <{}>? {}", Objects.o(pattern.pattern(), uri, matches));
        return matches;
    }

    @Override
    public Multimap<String, String> extractParameters(String uri) {
        Matcher matcher = pattern.matcher(uri);
        if (matcher.matches()) {
            Multimap<String, String> params = New.multiMap();
            int cnt = Math.min(matcher.groupCount(), keys.size());
            for (int i = 0; i < cnt; i++) {
                String key = keys.get(i);
                String value = matcher.group(i + 1);

                log.debug("Extracted from <{}> got <{}: {}>", o(uri, key, value));
                // case of optional parameter in regex...
                if (value != null)
                    params.put(key, postProcessValue(value));
            }
            return params;
        } else {
            log.debug("Path <{}> does not match: no parameter extracted", uri);
        }
        return null;
    }

    /**
     * Be default invoke {@link URLDecoder#decode(String)} on value.
     */
    protected String postProcessValue(String value) {
        try {
            return URLDecoder.decode(value, "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new SwoopException(e);
        }
    }

    @Override
    public String toString() {
        return "<" + pattern.pattern() + ">::[" + keys + "]";
    }
}