package swoop.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.Filter;
import swoop.Request;
import swoop.Response;
import swoop.route.RouteChain;

public class PerfFilter extends Filter {
    
    private Logger logger = LoggerFactory.getLogger(PerfFilter.class);

    @Override
    public void handle(Request request, Response response, RouteChain routeChain) {
        logger.info("Request " + request.logInfo() + " started");
        long t0 = System.currentTimeMillis();
        try  {
            routeChain.invokeNext();
        }
        finally {
            long t1 = System.currentTimeMillis();
            logger.info("Request " + request.logInfo() + " executed in " + (t1-t0) + "ms");
        }
    }
}
