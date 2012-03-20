package swoop.pipeline;

import swoop.path.Path;
import swoop.path.Verb.Category;
import swoop.util.Multimap;

public interface PathMatcher {
    boolean satisfiedBy(Category category, String path);
    
    boolean matches(Path path);
    Multimap<String, String> extractParameters(Path path);
    boolean hasParameters();
}
