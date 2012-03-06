package swoop;

import swoop.route.Route;
import swoop.route.RouteChain;

public abstract class Action extends Route {

    protected Action() {
        super(ALL_PATHS);
    }

    protected Action(String path) {
        super(path);
    }
    
    @Override
    public boolean isFilter() {
        return false;
    }
    
    @Override
    public final void handle(Request request, Response response, RouteChain routeChain) {
        handle(request, response);
    }
    
    public abstract void handle(Request request, Response response);
}
