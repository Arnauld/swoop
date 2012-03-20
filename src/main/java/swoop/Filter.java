package swoop;

import swoop.path.Verb;
import swoop.path.VerbMatcher;
import swoop.path.VerbMatchers;
import swoop.route.Route;

public abstract class Filter extends Route {
    
    private final VerbMatcher applyOn;

    protected Filter() {
        this(VerbMatchers.any(), ALL_PATHS);
    }

    protected Filter(String path) {
        this(VerbMatchers.any(), path);
    }
    
    protected Filter(Verb applyOn, String path) {
        this(VerbMatchers.on(applyOn), path);
    }
    
    protected Filter(VerbMatcher applyOn, String path) {
        super(path);
        this.applyOn = applyOn;
    }
    
    @Override
    public final boolean isFilter() {
        return true;
    }
    
    public VerbMatcher getApplyOn() {
        return applyOn;
    }

}
