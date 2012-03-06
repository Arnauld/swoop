package swoop;

import swoop.path.Verb;
import swoop.route.RouteChain;

public abstract class Before extends Filter {

    protected Before() {
        super();
    }

    protected Before(String path) {
        super(path);
    }
    
    protected Before(Verb applyOn, String path) {
        super(applyOn, path);
    }
    
    @Override
    public final void handle(Request request, Response response, RouteChain routeChain) {
        handle(request, response);
        routeChain.invokeNext();
    }
    
    public abstract void handle(Request request, Response response);
}
