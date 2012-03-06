package swoop.route;

import java.util.ArrayList;
import java.util.List;

import swoop.Request;
import swoop.Response;

public abstract class Route {
    protected static final String ALL_PATHS = Path.ALL_PATHS;

    private String path;
    
    /**
     * Constructor
     * 
     * @param path The route path which is used for matching. (e.g. /hello, users/:name) 
     */
    protected Route(String path) {
        this.path = path;
    }
    
    /**
     * Indicates whether this route must be considered as a final target or as a filter 
     * (aka interceptor).
     * 
     * @return <code>true</code> if this route must be considered as a filter.
     */
    public abstract boolean isFilter();
    
    /**
     * Invoked when a request is made on this route's corresponding path e.g. '/hello'
     * 
     * @param request The request object providing information about the HTTP request
     * @param response The response object providing functionality for modifying the response
     * @param routeChain The route chain matching the request
     * 
     * @return The content to be set in the response
     */
    public abstract void handle(Request request, Response response, RouteChain routeChain);

    /**
     * Returns this route's path
     */
    public String getPath() {
        return this.path;
    }
    
    public static List<String> convertRouteToList(String route) {
        String[] pathArray = route.split("/");
        List<String> path = new ArrayList<String>();
        for (String p : pathArray) {
            if (p.length() > 0) {
                path.add(p);
            }
        }
        return path;
    }
}
