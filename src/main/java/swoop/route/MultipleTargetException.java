package swoop.route;

import java.util.List;

import swoop.SwoopException;
import swoop.path.Path;

@SuppressWarnings("serial")
public class MultipleTargetException extends SwoopException {

    private final Path requestedPath;
    private final List<RouteMatch<? extends FilterAware>> matchingRoutes;

    public MultipleTargetException(Path requestedPath, List<RouteMatch<? extends FilterAware>> matchingRoutes) {
        super();
        this.requestedPath = requestedPath;
        this.matchingRoutes = matchingRoutes;
    }

    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("Multiple targets are matching the requested path ").append(requestedPath);
        builder.append(" got: [");
        for (RouteMatch<?> e : matchingRoutes)
            builder.append("\n  ").append(e.getMatchEntry()).append(", ");
        builder.append("]");
        return builder.toString();
    }

}
