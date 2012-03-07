package swoop;

import java.util.List;
import java.util.Set;

import swoop.path.Verb;


public interface Request {
    
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
    // ~~~~~~~~~~~~~~~~~~~~~~~~[DATA]~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Arbitrary data that can be stored for the lifetime of the connection.
     * Retrieve data value by key.
     *
     * @see #data(String,String)
     */
    Object data(String key);
    
    /**
     * Arbitrary data that can be stored for the lifetime of the connection.
     * Store data value by key.
     *
     * @see #data(String)
     */
    void data(String key, Object value);
    
    /**
     * Arbitrary data that can be stored for the lifetime of the connection.
     * List data keys.
     *
     * @see #data()
     */
    Set<String> dataKeys();
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~[HEADER]~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    /**
     * Retrieve the value single HTTP header.
     * <p/>
     * If the header is not found, null is returned.
     * <p/>
     * If there are multiple headers with the same name, it will return one of them, but it is not
     * defined which one. Instead, use {@link #headers(String)}.
     */
    String header(String name);

    /**
     * Retrieve all values for an HTTP header. If no values are found, an empty List is returned.
     */
    List<String> headers(String name);

    /**
     * Whether a specific HTTP header was present in the request.
     */
    boolean hasHeader(String name);
    
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
    
    /**
     * Get query parameter value.
     *
     * @param key parameter name
     * @return the value of the parameter
     * @see #queryParams(String)
     */
    String queryParam(String key);

    /**
     * Get all query parameter values.
     *
     * @param key parameter name
     * @return the values of the parameter
     * @see #queryParam(String)
     */
    List<String> queryParams(String key);

    /**
     * List all query parameter keys.
     *
     * @see #queryParam(String)
     */
    Set<String> queryParamKeys();

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
