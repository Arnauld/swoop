package swoop.path;

import swoop.util.Multimap;

public interface PathPatternMatcher {
    boolean matches(String pathInfo);
    Multimap<String, String> extractParameters(String pathInfo);
    boolean hasParameters();
}
