package swoop.route;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.path.Path;
import swoop.util.New;

public class Routes {
    
    private static Logger logger = LoggerFactory.getLogger(Routes.class);
    
    public static <R extends FilterAware> List<RouteMatch<R>> reorderRoutes(List<RouteMatch<R>> matches) {
        List<RouteMatch<R>> routes = New.arrayList(matches.size());
        RouteMatch<R> target = null;
        for(RouteMatch<R> m : matches) {
            R r = m.getTarget();
            if(!r.isFilter()) {
                if(target==null)
                    target = m;
                else
                    logger.error("Multiple target defined, only the first one is kept [{}]", matches);
            }
            else
                routes.add(m);
        }
        if(target!=null)
            routes.add(target);
        return routes;
    }

    public static void throwIfMultipleTargets(Path requestedPath, List<RouteMatch<?>> routes) {
        int count = 0;
        for(RouteMatch<?> m : routes) {
            if(!m.getTarget().isFilter()) {
                count++;
            }
        }
        if(count>1)
            throw new MultipleTargetException(requestedPath, routes);
    }
}
