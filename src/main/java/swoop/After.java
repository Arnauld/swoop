package swoop;

import swoop.path.Verb;

public abstract class After extends Filter {

    protected After() {
        super();
    }

    protected After(String path) {
        super(path);
    }
    
    protected After(Verb applyOn, String path) {
        super(applyOn, path);
    }

    @Override
    public final void handle(Request request, Response response, RouteChain routeChain) {
        routeChain.invokeNext();
        handle(request, response);
    }
    
    public abstract void handle(Request request, Response response);
}
