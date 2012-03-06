package swoop.route;

import java.util.List;

import swoop.util.New;

public class Routes {
    
    public static RouteMatch firstTarget(List<RouteMatch> routes) {
        for(RouteMatch m : routes) {
            if(!m.getTarget().isFilter()) {
                return m;
            }
        }
        return null;
    }
    
    public static List<Route> routesWithTargetAsLast(List<RouteMatch> matches) {
        List<Route> routes = New.arrayList(matches.size());
        Route target = null;
        for(RouteMatch m : matches) {
            Route r = m.getTarget();
            if(!r.isFilter())
                target = r;
            else
                routes.add(r);
        }
        if(target!=null)
            routes.add(target);
        return routes;
    }

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
