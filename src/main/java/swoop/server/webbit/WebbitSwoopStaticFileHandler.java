package swoop.server.webbit;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.handler.AbstractResourceHandler;
import org.webbitserver.handler.FileEntry;
import org.webbitserver.handler.StaticFileHandler;
import org.webbitserver.helpers.ClassloaderResourceHelper;
import swoop.ResourceContent;
import swoop.ResourceHandler;
import swoop.ResponseProcessor;
import swoop.StatusCode;
import swoop.path.Path;
import swoop.path.Verb;
import swoop.route.*;
import swoop.util.Context;
import swoop.util.ContextBasic;
import swoop.util.Provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executor;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static swoop.server.webbit.WebbitAdapters.adaptRequest;
import static swoop.server.webbit.WebbitAdapters.adaptResponse;

public class WebbitSwoopStaticFileHandler implements HttpHandler {

    private Logger logger = LoggerFactory.getLogger(WebbitSwoopStaticFileHandler.class);
    private final RouteRegistry routeRegistry;
    private final Provider<ResponseProcessor> responseProcessorProvider;
    private final InternalResourceHandler delegate;

    public WebbitSwoopStaticFileHandler(RouteRegistry routeRegistry, Provider<ResponseProcessor> responseProcessorProvider, Executor ioThread) {
        this.routeRegistry = routeRegistry;
        this.responseProcessorProvider = responseProcessorProvider;
        this.delegate = new InternalResourceHandler(ioThread, 0);
    }

    public WebbitSwoopStaticFileHandler(RouteRegistry routeRegistry, Provider<ResponseProcessor> responseProcessorProvider) {
        this(routeRegistry, responseProcessorProvider, newFixedThreadPool(4));
    }

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        Path path = Webbits.getPath(request);
        logger.debug("Analysing path <{}>", path);

        // static is initiated by a 'GET'...
        if (path.getVerb() == Verb.Get) {
            if (dispatch(path, request, response))
                return;
        }
        control.nextHandler();
    }

    protected boolean dispatch(Path path, HttpRequest httpRequest, HttpResponse httpResponse) {
        List<RouteMatch<Route>> matches = routeRegistry.findRoutes(path);
        if (matches.isEmpty()) {
            return false;
        }
        httpRequest.data("RouteMatches", matches);

        RouteParameters routeParameters = new RouteParameters();
        WebbitRequestAdapter request = adaptRequest(httpRequest, routeParameters);
        WebbitResponseAdapter response = adaptResponse(request, httpResponse, responseProcessorProvider.get());
        ContextBasic context = new ContextBasic()//
                .register(RouteParameters.class, routeParameters)
                .register(ResourceHandler.class, delegate);

        try {
            RouteChainBasic.create(new RouteInvoker(request, response), matches, context).invokeNext();
        } catch (HaltException he) {
            logger.info("Processing halted", he);
            response.body(he.getBody());
            response.status(he.getStatusCode());
        } catch (RedirectException re) {
            logger.info("Redirecting to " + re.getLocation(), re);
            response.redirect(re.getLocation());
        } catch (Exception e) {
            logger.error("Processing error <" + httpRequest.uri() + ">", e);
            response.body(ExceptionUtils.getStackTrace(e));
            response.status(StatusCode.SERVICE_UNAVAILABLE);
        } finally {
            response.end();
        }
        return true;
    }

    class InternalResourceHandler extends AbstractResourceHandler implements ResourceHandler {

        private long maxAge;

        public InternalResourceHandler(Executor ioThread, long maxAge) {
            super(ioThread);
            this.maxAge = maxAge;
        }

        @Override
        public void writeResource(String path, File dir, Context context) {
            HttpRequest request = context.get(HttpRequest.class);
            HttpResponse response = context.get(HttpResponse.class);
            HttpControl control = context.get(HttpControl.class);
            ioThread.execute(new FileWorker(dir, path, request, response, control, maxAge));
        }

        @Override
        protected StaticFileHandler.IOWorker createIOWorker(HttpRequest request, HttpResponse response,
                                                            HttpControl control) {
            String path = (String) request.data(ResourceContent.RESOURCE_PATH);
            File dir = (File) request.data(ResourceContent.RESOURCE_DIR);
            return new FileWorker(dir, path, request, response, control, maxAge);
        }

        protected class FileWorker extends IOWorker {

            private File file;

            private final HttpResponse response;

            private final HttpRequest request;

            private final File dir;
            private final String path;
            private final long maxAge;

            private String mimeType(String uri) {
                String ext = uri.lastIndexOf(".") != -1 ? uri.substring(uri.lastIndexOf(".")) : null;
                String currentMimeType = mimeTypes.get(ext);
                if (currentMimeType == null) currentMimeType = "text/plain";
                return currentMimeType;
            }

            //based on: http://m2tec.be/blog/2010/02/03/java-md5-hex-0093
            private String MD5(String md5) {
                try {
                    java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
                    byte[] array = md.digest(md5.getBytes("UTF-8"));
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < array.length; ++i) {
                        sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return null;
                }
            }

            private String toHeader(Date date) {
                SimpleDateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                return httpDateFormat.format(date);
            }

            private Date fromHeader(String date) {
                SimpleDateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                try {
                    return httpDateFormat.parse(date);
                } catch (Exception ex) {
                    return new Date();
                }
            }

            protected FileWorker(File dir, String path, HttpRequest request, HttpResponse response, HttpControl control, long maxAge) {
                super(request.uri(), request, response, control);
                this.dir = dir;
                this.path = path;
                this.maxAge = maxAge;
                this.response = response;
                this.request = request;
            }

            @Override
            protected boolean exists() throws IOException {
                file = resolveFile(path);
                return file != null && file.exists();
            }

            @Override
            protected boolean isDirectory() throws IOException {
                return file.isDirectory();
            }

            @Override
            protected byte[] fileBytes() throws IOException {
                byte[] raw = file.isFile() ? read(file) : null;
                //add cache control headers if needed
                if (raw != null) {
                    Date lastModified = new Date(file.lastModified());
                    String hashtext = MD5(Long.toString(lastModified.getTime()));
                    if (hashtext != null) response.header("ETag", "\"" + hashtext + "\"");

                    response.header("Last-Modified", toHeader(lastModified));
                    //is there an incoming If-Modified-Since?
                    if (request.header("If-Modified-Since") != null) {
                        if (fromHeader(request.header("If-Modified-Since")).getTime() >= lastModified.getTime()) {
                            response.status(304);
                        }
                    }
                    //is setting cache control necessary?
                    if (maxAge != 0) {
                        response.header("Expires", toHeader(new Date(new Date().getTime() + maxAge * 1000)));
                        response.header("Cache-Control", "max-age=" + maxAge + ", public");
                    }
                }
                return raw;
            }

            @Override
            protected byte[] welcomeBytes() throws IOException {
                File welcome = new File(file, welcomeFileName);
                return welcome.isFile() ? read(welcome) : null;
            }

            @Override
            protected byte[] directoryListingBytes() throws IOException {
                if (!isDirectory()) {
                    return null;
                }
                Iterable<FileEntry> files = ClassloaderResourceHelper.fileEntriesFor(file.listFiles());
                return directoryListingFormatter.formatFileListAsHtml(files);
            }

            private byte[] read(File file) throws IOException {
                return read((int) file.length(), new FileInputStream(file));
            }

            protected File resolveFile(String path) throws IOException {
                // Find file, relative to root
                File result = new File(dir, path).getCanonicalFile();

                // For security, check file really does exist under root.
                String fullPath = result.getPath();
                if (!fullPath.startsWith(dir.getCanonicalPath() + File.separator) && !fullPath.equals(dir.getCanonicalPath())) {
                    // Prevent paths like http://foo/../../etc/passwd
                    return null;
                }
                return result;
            }
        }
    }
}
