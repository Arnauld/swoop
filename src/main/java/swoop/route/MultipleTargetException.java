package swoop.route;

import java.util.List;

import swoop.SwoopException;

@SuppressWarnings("serial")
public class MultipleTargetException extends SwoopException {

    private final List<RouteMatch> matchingRoutes;
    public MultipleTargetException(List<RouteMatch> matchingRoutes) {
        super();
        this.matchingRoutes = matchingRoutes;
    }
    
    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder ();
        builder.append("Multiple targets are matching the requested path ")//
        .append(matchingRoutes.get(0).getRequestPath());
        builder.append(" got: [");
        for(RouteMatch e : matchingRoutes)
            builder.append("\n  ").append(e.getMatchEntry()).append(", ");
        builder.append("]");
        return builder.toString();
    }
    
}
