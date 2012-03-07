package swoop.server.webbit;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.webbitserver.HttpRequest;

import swoop.Cookie;
import swoop.Request;
import swoop.path.Verb;
import swoop.route.RouteParameters;
import swoop.util.Net;

public class WebbitRequestAdapter implements Request {

    private final HttpRequest request;
    private RouteParameters routeParameters;

    public WebbitRequestAdapter(HttpRequest request, RouteParameters routeParameters) {
        super();
        this.request = request;
        this.routeParameters = routeParameters;
    }
    
    @Override
    public Object raw() {
        return request;
    }

    @Override
    public String logInfo() {
        return request.uri() + "@" + request.id();
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#uri()
     */
    @Override
    public String uri() {
        return request.uri();
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#uri(java.lang.String)
     */
    @Override
    public void uri(String uri) {
        request.uri(uri);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#method()
     */
    @Override
    public Verb method() {
        return Webbits.method(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#userAgent()
     */
    @Override
    public String userAgent() {
        return request.header(USER_AGENT);
    }

    @Override
    public String scheme() {
        return URI.create(uri()).getScheme();
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#pathInfo()
     */
    @Override
    public String pathInfo() {
        return Webbits.pathInfo(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#queryString()
     */
    @Override
    public String queryString() {
        return Webbits.query(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#ip()
     */
    @Override
    public String ip() {
        return Net.ip(request.remoteAddress());
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#body()
     */
    @Override
    public String body() {
        return request.body();
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#bodyAsBytes()
     */
    @Override
    public byte[] bodyAsBytes() {
        return request.bodyAsBytes();
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#id()
     */
    @Override
    public Object id() {
        return request.id();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~[DATA]~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#data(java.lang.String)
     */
    @Override
    public Object data(String key) {
        return request.data(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#data(java.lang.String, java.lang.Object)
     */
    @Override
    public void data(String key, Object value) {
        request.data(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#dataKeys()
     */
    @Override
    public Set<String> dataKeys() {
        return request.dataKeys();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~[HEADER]~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#header(java.lang.String)
     */
    @Override
    public String header(String name) {
        return request.header(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#headers(java.lang.String)
     */
    @Override
    public List<String> headers(String name) {
        return request.headers(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#hasHeader(java.lang.String)
     */
    @Override
    public boolean hasHeader(String name) {
        return request.hasHeader(name);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~[COOKIE]~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#cookies()
     */
    @Override
    public List<Cookie> cookies() {
        return WebbitAdapters.adaptCookies(request.cookies());
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#cookie(java.lang.String)
     */
    @Override
    public Cookie cookie(String name) {
        return WebbitAdapters.adaptCookie(request.cookie(name));
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#cookieValue(java.lang.String)
     */
    @Override
    public String cookieValue(String name) {
        return request.cookieValue(name);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~[PARAMS]~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#routeParam(java.lang.String)
     */
    @Override
    public String routeParam(String param) {
        return routeParameters.routeParam(param);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#routeParams(java.lang.String)
     */
    @Override
    public List<String> routeParams(String param) {
        return routeParameters.routeParams(param);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#routeParamKeys()
     */
    @Override
    public Set<String> routeParamKeys() {
        return routeParameters.routeParamKeys();
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#queryParam(java.lang.String)
     */
    @Override
    public String queryParam(String key) {
        return request.queryParam(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#queryParams(java.lang.String)
     */
    @Override
    public List<String> queryParams(String key) {
        return request.queryParams(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#queryParamKeys()
     */
    @Override
    public Set<String> queryParamKeys() {
        return request.queryParamKeys();
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#postParam(java.lang.String)
     */
    @Override
    public String postParam(String key) {
        return request.postParam(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#postParams(java.lang.String)
     */
    @Override
    public List<String> postParams(String key) {
        return request.postParams(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Request#postParamKeys()
     */
    @Override
    public Set<String> postParamKeys() {
        return request.postParamKeys();
    }

}
