package swoop.path;

import swoop.util.Multimap;

public class PathPatternMatchers {

    public static PathPatternMatcher matchAll() {
        return new PathPatternMatcher() {
            
            @Override
            public boolean matches(String pathInfo) {
                return true;
            }
            
            @Override
            public boolean hasParameters() {
                return false;
            }
            
            @Override
            public Multimap<String, String> extractParameters(String pathInfo) {
                return new Multimap<String, String>();
            }
        };
    }

}
