package swoop;

import swoop.path.Verb;
import swoop.route.Route;

public abstract class Filter extends Route {
    
    private final Verb applyOn;

    protected Filter() {
        this(Verb.Any, ALL_PATHS);
    }

    protected Filter(String path) {
        this(Verb.Any, path);
    }
    
    protected Filter(Verb applyOn, String path) {
        super(path);
        this.applyOn = applyOn;
    }
    
    @Override
    public final boolean isFilter() {
        return true;
    }
    
    public Verb getApplyOn() {
        return applyOn;
    }

}
