package swoop.route;

import swoop.util.Multimap;

public interface PathMatcher {
    boolean matches(String pathInfo);
    Multimap<String, String> extractParameters(String pathInfo);
}
