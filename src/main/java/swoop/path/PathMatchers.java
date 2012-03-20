package swoop.path;

import swoop.util.Multimap;

public class PathMatchers {

    public static PathMatcher matchAll() {
        return new PathMatcher() {
            
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
