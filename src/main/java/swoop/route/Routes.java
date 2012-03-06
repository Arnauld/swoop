package swoop.route;

import java.util.List;

public class Routes {

    public static void throwIfMultipleTargets(List<RouteMatch> routes) {
        int count = 0;
        for(RouteMatch m : routes) {
            if(!m.getTarget().isFilter()) {
                count++;
            }
        }
        if(count>1)
            throw new MultipleTargetException(routes);
    }
}
