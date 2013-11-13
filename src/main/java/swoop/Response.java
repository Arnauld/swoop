package swoop;

import java.nio.charset.Charset;

public interface Response {

    public final static String ACCEPT_RANGES = "Accept-Ranges",//
            AGE = "Age",//
            ETAG = "ETag",//
            LOCATION = "Location",//
            PROXY_AUTHENTICATE = "Proxy-Authenticate",//
            WWW_AUTHENTICATE = "WWW-Authenticate";

    /**
     * Gets the raw response object handed in the underlying HTTP server
     */
    Object raw();

    /**
     * Sets the status code for the response
     */
    void status(int statusCode);

    /**
     * Sets the content type for the response
     */
    void contentType(String contentType);

    /**
     * Sets the body
     */
    void body(Object body);

    Object body();

    /**
     * Trigger a browser redirect
     *
     * @param location Where to redirect
     */
    void redirect(String location);

    /**
     * Adds/Sets a response header
     */
    void header(String header, String value);

    /**
     * Create a new cookie but does not add it to the response.
     *
     * @see #cookie(Cookie)
     */
    Cookie createCookie(String name, String value);

    /**
     * Adds the specified cookie to the response. This method can be called multiple times to set more than one cookie.
     *
     * @see #createCookie(String, String)
     */
    void cookie(Cookie cookie);

    /**
     * Tells the client to delete a cookie "now", add it to the response as above. It usually set its maximum age to
     * zero.
     */
    void discardCookie(String name);

    /**
     * For text based responses, sets the Charset to encode the response as.
     * <p/>
     * If not set, defaults to UTF8.
     */
    void charset(Charset charset);

    /**
     * Current Charset used to encode to response as.
     *
     * @see #charset(Charset)
     */
    Charset charset();
}
