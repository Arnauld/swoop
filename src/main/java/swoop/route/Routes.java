package swoop.route;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.util.New;

public class Routes {
    
    private static Logger logger = LoggerFactory.getLogger(Routes.class);
    
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
            if(!r.isFilter()) {
                if(target==null)
                    target = r;
                else
                    logger.error("Multiple target defined, only the first one is kept [{}]", matches);
            }
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
