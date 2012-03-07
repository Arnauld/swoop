package swoop.server.webbit;

import java.net.HttpCookie;

import swoop.Cookie;

public class WebbitCookieAdapter implements Cookie {

    private HttpCookie cookie;

    public WebbitCookieAdapter(HttpCookie cookie) {
        super();
        this.cookie = cookie;
    }
    
    @Override
    public Object raw() {
        return cookie;
    }

    /* (non-Javadoc)
     * @see swoop.Cookie#comment()
     */
    @Override
    public String comment() {
        return cookie.getComment();
    }

    /* (non-Javadoc)
     * @see swoop.Cookie#comment(java.lang.String)
     */
    @Override
    public void comment(String purpose) {
        cookie.setComment(purpose);
    }

    /* (non-Javadoc)
     * @see swoop.Cookie#domain()
     */
    @Override
    public String domain() {
        return cookie.getDomain();
    }

    /* (non-Javadoc)
     * @see swoop.Cookie#domain(java.lang.String)
     */
    @Override
    public void domain(String pattern) {
        cookie.setDomain(pattern);
    }

    /* (non-Javadoc)
     * @see swoop.Cookie#maxAge()
     */
    @Override
    public long maxAge() {
        return cookie.getMaxAge();
    }

    /* (non-Javadoc)
     * @see swoop.Cookie#maxAge(long)
     */
    @Override
    public void maxAge(long expiry) {
        cookie.setMaxAge(expiry);
    }

    /* (non-Javadoc)
     * @see swoop.Cookie#name()
     */
    @Override
    public String name() {
        return cookie.getName();
    }

    /* (non-Javadoc)
     * @see swoop.Cookie#path()
     */
    @Override
    public String path() {
        return cookie.getPath();
    }

    /* (non-Javadoc)
     * @see swoop.Cookie#path(java.lang.String)
     */
    @Override
    public void path(String uri) {
        cookie.setPath(uri);
    }

    /* (non-Javadoc)
     * @see swoop.Cookie#secure()
     */
    @Override
    public boolean secure() {
        return cookie.getSecure();
    }

    /* (non-Javadoc)
     * @see swoop.Cookie#secure(boolean)
     */
    @Override
    public void secure(boolean flag) {
        cookie.setSecure(flag);
    }

    /* (non-Javadoc)
     * @see swoop.Cookie#value()
     */
    @Override
    public String value() {
        return cookie.getValue();
    }

    /* (non-Javadoc)
     * @see swoop.Cookie#value(java.lang.String)
     */
    @Override
    public void value(String newValue) {
        cookie.setValue(newValue);
    }

    /* (non-Javadoc)
     * @see swoop.Cookie#version()
     */
    @Override
    public int version() {
        return cookie.getVersion();
    }

    /* (non-Javadoc)
     * @see swoop.Cookie#version(int)
     */
    @Override
    public void version(int ver) {
        cookie.setVersion(ver);
    }
}
