package swoop.server.webbit;

import org.webbitserver.HttpResponse;
import swoop.*;

import java.net.HttpCookie;
import java.nio.charset.Charset;

public class WebbitResponseAdapter implements Response {

    private final Request request;
    private final HttpResponse response;
    private final ResponseProcessor responseProcessor;
    private Object body;
    private String redirectLocation;

    public WebbitResponseAdapter(Request request, HttpResponse response, ResponseProcessor responseProcessor) {
        super();
        this.request = request;
        this.response = response;
        this.responseProcessor = responseProcessor;
    }

    public String redirectLocation() {
        return redirectLocation;
    }

    @Override
    public Object raw() {
        return response;
    }

    @Override
    public void charset(Charset charset) {
        response.charset(charset);
    }

    @Override
    public Charset charset() {
        return response.charset();
    }

    /*
     * (non-Javadoc)
     *
     * @see swoop.Response#status(int)
     */
    @Override
    public void status(int statusCode) {
        response.status(statusCode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Response#contentType(java.lang.String)
     */
    @Override
    public void contentType(String contentType) {
        response.header("Content-type", contentType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Response#body(java.lang.String)
     */
    @Override
    public void body(Object body) {
        this.body = body;
        // don't write body yet, since filter can still modify it
        // response.content(body);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Response#body()
     */
    @Override
    public Object body() {
        return this.body;
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Response#redirect(java.lang.String)
     */
    @Override
    public void redirect(String location) {
        this.redirectLocation = location;
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Response#header(java.lang.String, java.lang.String)
     */
    @Override
    public void header(String header, String value) {
        response.header(header, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Response#createCookie(java.lang.String, java.lang.String)
     */
    @Override
    public Cookie createCookie(String name, String value) {
        return WebbitAdapters.adaptCookie(new HttpCookie(name, value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Response#cookie(Cookie)
     */
    @Override
    public void cookie(Cookie cookie) {
        response.cookie((HttpCookie) cookie.raw());
    }

    /*
     * (non-Javadoc)
     * 
     * @see swoop.Response#deleteCookie(java.lang.String)
     */
    @Override
    public void discardCookie(String name) {
        HttpCookie httpCookie = new HttpCookie(name, "");
        httpCookie.setDiscard(true);
        httpCookie.setMaxAge(0);
        response.cookie(httpCookie);
    }

    public void end() {

        if (redirectLocation != null) {
            response.header(Response.LOCATION, redirectLocation);
            response.status(StatusCode.MOVED_TEMPORARILY);
        } else {
            if (body != null) {
                responseProcessor.process(body, request, wrap(this));
            }
        }
        response.end();
    }

    private static Response wrap(final WebbitResponseAdapter r) {
        return new Response() {
            @Override
            public Object raw() {
                return r.raw();
            }

            @Override
            public void status(int statusCode) {
                r.status(statusCode);
            }

            @Override
            public void contentType(String contentType) {
                r.contentType(contentType);
            }

            @Override
            public void body(Object body) {
                if (body instanceof String) {
                    r.body(body);
                    r.response.content((String) body);
                } else if (body instanceof byte[]) {
                    r.body(body);
                    r.response.content((byte[]) body);
                } else
                    throw new SwoopException("Only String or byte[] can be set as body");
            }

            @Override
            public Object body() {
                return r.body();
            }

            @Override
            public void redirect(String location) {
                throw new SwoopException("Too late!");
            }

            @Override
            public void header(String header, String value) {
                r.header(header, value);
            }

            @Override
            public Cookie createCookie(String name, String value) {
                return r.createCookie(name, value);
            }

            @Override
            public void cookie(Cookie cookie) {
                r.cookie(cookie);
            }

            @Override
            public void discardCookie(String name) {
                r.discardCookie(name);
            }

            @Override
            public void charset(Charset charset) {
                r.charset(charset);
            }

            @Override
            public Charset charset() {
                return r.charset();
            }
        };
    }

// @formatter:off
//    public void sendRedirect(String location) throws IOException
//    {
//        if (_connection.isIncluding())
//            return;
//
//        if (location==null)
//            throw new IllegalArgumentException();
//
//        if (!URIUtil.hasScheme(location))
//        {
//            StringBuilder buf = _connection.getRequest().getRootURL();
//            if (location.startsWith("/"))
//                buf.append(location);
//            else
//            {
//                String path=_connection.getRequest().getRequestURI();
//                String parent=(path.endsWith("/"))?path:URIUtil.parentPath(path);
//                location=URIUtil.addPaths(parent,location);
//                if(location==null)
//                    throw new IllegalStateException("path cannot be above root");
//                if (!location.startsWith("/"))
//                    buf.append('/');
//                buf.append(location);
//            }
//
//            location=buf.toString();
//            HttpURI uri = new HttpURI(location);
//            String path=uri.getDecodedPath();
//            String canonical=URIUtil.canonicalPath(path);
//            if (canonical==null)
//                throw new IllegalArgumentException();
//            if (!canonical.equals(path))
//            {
//                buf = _connection.getRequest().getRootURL();
//                buf.append(canonical);
//                if (uri.getQuery()!=null)
//                {
//                    buf.append('?');
//                    buf.append(uri.getQuery());
//                }
//                if (uri.getFragment()!=null)
//                {
//                    buf.append('#');
//                    buf.append(uri.getFragment());
//                }
//                location=buf.toString();
//            }
//        }
//        resetBuffer();
//
//        setHeader(HttpHeaders.LOCATION,location);
//        setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
//        complete();
//
//    }
// @formatter:on
}
