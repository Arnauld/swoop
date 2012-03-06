package swoop;

import swoop.route.Route;
import swoop.route.Verb;

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
    public boolean isFilter() {
        return true;
    }
    
    public Verb getApplyOn() {
        return applyOn;
    }

}
