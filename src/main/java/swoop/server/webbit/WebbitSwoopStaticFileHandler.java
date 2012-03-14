package swoop.server.webbit;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static swoop.server.webbit.WebbitAdapters.adaptRequest;
import static swoop.server.webbit.WebbitAdapters.adaptResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.handler.AbstractResourceHandler;
import org.webbitserver.handler.StaticFileHandler;

import swoop.ResourceContent;
import swoop.ResourceHandler;
import swoop.StatusCode;
import swoop.path.Path;
import swoop.path.Verb;
import swoop.route.HaltException;
import swoop.route.RedirectException;
import swoop.route.Route;
import swoop.route.RouteChainBasic;
import swoop.route.RouteInvoker;
import swoop.route.RouteMatch;
import swoop.route.RouteParameters;
import swoop.route.RouteRegistry;
import swoop.util.Context;
import swoop.util.ContextBasic;

public class WebbitSwoopStaticFileHandler implements HttpHandler {

    private Logger logger = LoggerFactory.getLogger(WebbitSwoopStaticFileHandler.class);
    private final RouteRegistry routeRegistry;
    private final InternalResourceHandler delegate;

    public WebbitSwoopStaticFileHandler(RouteRegistry routeRegistry, Executor ioThread) {
        this.delegate = new InternalResourceHandler(ioThread);
        this.routeRegistry = routeRegistry;
    }

    public WebbitSwoopStaticFileHandler(RouteRegistry routeRegistry) {
        this(routeRegistry, newFixedThreadPool(4));
    }

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        Path path = Webbits.getPath(request);
        logger.debug("Analysing path <{}>", path);

        // static is initiated by a 'GET'...
        if (path.getVerb() == Verb.Get) {
            path = path.withVerb(Verb.StaticContent);
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
        WebbitResponseAdapter response = adaptResponse(httpResponse);
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
        
        public InternalResourceHandler(Executor ioThread) {
            super(ioThread);
        }
        
        @Override
        public void writeResource(String path, File dir, Context context) {
            HttpRequest request = context.adaptTo(HttpRequest.class);
            HttpResponse response = context.adaptTo(HttpResponse.class);
            HttpControl control = context.adaptTo(HttpControl.class);
            ioThread.execute(new FileWorker(dir, path, request, response, control));
        }

        @Override
        protected StaticFileHandler.IOWorker createIOWorker(HttpRequest request, HttpResponse response,
                HttpControl control) {
            String path = (String)request.data(ResourceContent.RESOURCE_PATH);
            File dir = (File)request.data(ResourceContent.RESOURCE_DIR);
            return new FileWorker(dir, path, request, response, control);
        }

        protected class FileWorker extends IOWorker {
            private File dir;
            private File file;

            private FileWorker(File dir, String path, HttpRequest request, HttpResponse response, HttpControl control) {
                super(path, request, response, control);
                this.dir = dir;
            }

            @Override
            protected boolean exists() throws IOException {
                file = resolveFile(path);
                return file != null && file.exists();
            }

            @Override
            protected ByteBuffer fileBytes() throws IOException {
                return file.isFile() ? read(file) : null;
            }

            @Override
            protected ByteBuffer welcomeBytes() throws IOException {
                File welcome = new File(file, welcomeFileName);
                return welcome.isFile() ? read(welcome) : null;
            }

            private ByteBuffer read(File file) throws IOException {
                return read((int) file.length(), new FileInputStream(file));
            }

            private File resolveFile(String path) throws IOException {
                // Find file, relative to roo
                File result = new File(dir, path).getCanonicalFile();

                // For security, check file really does exist under root.
                String fullPath = result.getPath();
                if (!fullPath.startsWith(dir.getCanonicalPath() + File.separator)
                        && !fullPath.equals(dir.getCanonicalPath())) {
                    // Prevent paths like http://foo/../../etc/passwd
                    return null;
                }
                return result;
            }
        }
    }
}
