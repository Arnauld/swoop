package swoop.pipeline;

import swoop.path.Path;
import swoop.path.Verb.Category;
import fj.F;
import fj.data.List;

public class HandlerRegistry {
    
    private List<HandlerEntry> entries = List.nil();
    
    public void defineEntry(PathMatcher pathMatcher, Handler handler) {
        entries = entries.cons(new HandlerEntry(pathMatcher, handler));
    }
    
    public List<HandlerEntry> entriesFor(final Path requestedPath) {
        List<HandlerEntry> list = entries.filter(entriesMatching(requestedPath));
        return list.reverse();
    }

    protected F<HandlerEntry, Boolean> entriesMatching(final Path requestedPath) {
        return new F<HandlerEntry,Boolean>() {
            @Override
            public Boolean f(HandlerEntry entry) {
                return entry.matches(requestedPath);
            }
        };
    }

    public boolean hasEntryFor(final Category category, final String path) {
        return entries.exists(entriesBelongingTo(category, path));
    }

    protected F<HandlerEntry, Boolean> entriesBelongingTo(final Category category, final String path) {
        return new F<HandlerEntry,Boolean>() {
            @Override
            public Boolean f(HandlerEntry entry) {
                return entry.satisfiedBy(category, path);
            }
        };
    }
}
