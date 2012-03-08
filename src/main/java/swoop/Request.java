package swoop;

import java.util.List;
import java.util.Set;

import swoop.path.Verb;
import swoop.route.HasRouteParameters;
import swoop.util.HasDataParameters;
import swoop.util.HasHeaders;
import swoop.util.HasQueryParameters;


public interface Request extends HasRouteParameters, HasDataParameters, HasQueryParameters, HasHeaders {
    
    public static final String USER_AGENT = "user-agent";

    Object raw();
    
    String logInfo();
    
    /**
     * uri
     */
    String uri();

    /**
     * Modify uri
     *
     * @param uri new uri
     */
    void uri(String uri);
    
    /**
     * HTTP method (e.g. "GET" or "POST")
     */
    Verb method();
    
    /**
     * Returns the user-agent
     */
    String userAgent();
    
    /**
     * 
     */
    String scheme();
    
    /**
     * Returns the path info
     * Example return: "/example/foo"
     */
    String pathInfo();
    
    /**
     * Returns the query string
     */
    String queryString();
    
    /**
     * Returns the client's IP address
     */
    String ip();
    
    /**
     * The body
     */
    String body();

    /**
     * The body's byte array
     */
    byte[] bodyAsBytes();
    
    /**
     * A unique identifier for this request. This should be treated as an opaque object,
     * that can be used to track the lifecycle of a request.
     */
    Object id();
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~[COOKIE]~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @return all inbound cookies
     */
    List<Cookie> cookies();

    /**
     * Get a cookie with a specific name
     *
     * @param name cookie name
     * @return cookie with that name
     */
    Cookie cookie(String name);
    
    /**
     * Get the value of named cookie
     *
     * @param name cookie name
     * @return cookie value, or null if the cookie does not exist.
     */
    String cookieValue(String name);
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~[PARAMS]~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Get post parameter value.
     *
     * @param key parameter name
     * @return the value of the parameter
     * @see #postParams(String)
     */
    String postParam(String key);

    /**
     * Get all post parameter values.
     *
     * @param key parameter name
     * @return the values of the parameter
     * @see #postParam(String)
     */
    List<String> postParams(String key);

    /**
     * List all post parameter keys.
     *
     * @see #postParam(String)
     */
    Set<String> postParamKeys();
    
}
