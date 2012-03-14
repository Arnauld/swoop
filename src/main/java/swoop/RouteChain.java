package swoop;

import swoop.util.Context;

public interface RouteChain {
    void invokeNext();
    Context context();
}
