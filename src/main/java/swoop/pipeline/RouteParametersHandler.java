package swoop.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.path.Path;
import swoop.route.RouteParameters;
import swoop.util.Multimap;

public class RouteParametersHandler {
    
    private Logger logger = LoggerFactory.getLogger(RouteParametersHandler.class);

    public RouteParametersHandler() {
        super();
    }

    public RouteParametersHandler adjustFor(Pipeline pipeline, HandlerEntry entry) {
        Path path = pipeline.get(Path.class);
        RouteParameters routeParameters = pipeline.get(RouteParameters.class);
        if(path==null || routeParameters==null) {
            logger.debug("No <RouteParameters> or <Path> defined in pipeline to adjust");
            return this;
        }
        Multimap<String, String> params = entry.extractParameters(path);
        routeParameters.setUnderlying(params);
        return this;
    }

}
