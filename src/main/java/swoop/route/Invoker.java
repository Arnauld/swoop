package swoop.route;

import swoop.RouteChain;

public interface Invoker<T> {
    void invoke(T value, RouteChain chain);
}
