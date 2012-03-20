package swoop.pipeline;

import swoop.path.Path;
import swoop.path.Verb.Category;
import swoop.util.Multimap;

public class HandlerEntry {
    private final Handler handler;
    private final PathMatcher pathMatcher;

    public HandlerEntry(PathMatcher selector, Handler handler) {
        super();
        ensureHandlerTypeIsValid(handler);
        this.pathMatcher = selector;
        this.handler = handler;
    }

    protected void ensureHandlerTypeIsValid(Object handler) {
        if (handler instanceof PipelineDownstreamHandler //
                || handler instanceof PipelineUpstreamHandler //
                || handler instanceof PipelineTargetHandler)
            return;
        throw new IllegalArgumentException("Handler type is not supported");
    }
    
    public Handler getHandler() {
        return handler;
    }
    
    public boolean satisfiedBy(Category category, String pathPattern) {
        return pathMatcher.satisfiedBy(category, pathPattern);
    }

    public boolean matches(Path requestPath) {
        return pathMatcher.matches(requestPath);
    }
    
    public Multimap<String, String> extractParameters(Path requestPath) {
        return pathMatcher.extractParameters(requestPath);
    }

}
