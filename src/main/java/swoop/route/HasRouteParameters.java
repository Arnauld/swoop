package swoop.route;

import java.util.List;
import java.util.Set;

public interface HasRouteParameters {
    /**
     * Returns the value of the provided route pattern parameter.
     * Example: parameter 'name' from the following pattern: (get '/hello/:name')
     * 
     * @return null if the given param is null or not found 
     * @see #routeParams(String)
     */
    String routeParam(String param);
    
    /**
     * Returns the values of the provided route pattern parameter.
     * Example: parameter 'name' from the following pattern: (get '/hello/:name')
     * 
     * @return null if the given param is null or not found 
     */
    List<String> routeParams(String param);
    
    /**
     * List all route parameter keys.
     *
     * @see #routeParam(String)
     */
    Set<String> routeParamKeys();
}
