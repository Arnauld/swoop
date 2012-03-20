package swoop.pipeline;

import swoop.path.Path;
import swoop.path.PathPatternMatcher;
import swoop.path.Verb.Category;
import swoop.path.VerbMatcher;
import swoop.util.Multimap;

public class PathMatcherDefault implements PathMatcher {

    private final VerbMatcher verbMatcher;
    private final PathPatternMatcher patternMatcher;
    
    public PathMatcherDefault(VerbMatcher verbMatcher, PathPatternMatcher patternMatcher) {
        super();
        this.verbMatcher = verbMatcher;
        this.patternMatcher = patternMatcher;
    }
    
    @Override
    public boolean satisfiedBy(Category category, String path) {
        return verbMatcher.belongsTo(category) && patternMatcher.matches(path);
    }

    @Override
    public boolean matches(Path path) {
        return verbMatcher.matches(path.getVerb()) && patternMatcher.matches(path.getPathPattern());
    }
    
    @Override
    public boolean hasParameters() {
        return patternMatcher.hasParameters();
    }
    
    @Override
    public Multimap<String, String> extractParameters(Path path) {
        return patternMatcher.extractParameters(path.getPathPattern());
    }
    
}
